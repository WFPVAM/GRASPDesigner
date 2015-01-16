/*
 * FrontlineSMS <http://www.frontlinesms.com>
 * Copyright 2007, 2008 kiwanja
 * 
 * This file is part of FrontlineSMS.
 * 
 * FrontlineSMS is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 * 
 * FrontlineSMS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FrontlineSMS. If not, see <http://www.gnu.org/licenses/>.
 */
package net.frontlinesms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import net.frontlinesms.data.domain.*;
import net.frontlinesms.email.EmailUtils;
import net.frontlinesms.listener.EmailListener;

/**
 * This class is used to send e-mails.
 * 
 * TODO refactor to email package; UNIT TEST
 *
 * @author Carlos Eduardo Genz
 * <li> kadu(at)masabi(dot)com
 */
public class EmailSender extends Thread {
	private static final boolean DEBUG_SESSION = false;
	
	private boolean running;
	private final ConcurrentLinkedQueue<Email> outbox = new ConcurrentLinkedQueue<Email>();
	private EmailListener emailListener;
	
	private int retries = 6;
	private long SLEEP_TIME = 30000;
	
	private String server;
	private String account;
	private int serverPort;
	private String password;
	private boolean isSSL;
	
	private EmailAccount sender;
	
	private static Logger LOG = FrontlineUtils.getLogger(EmailSender.class);
	
	public EmailSender(EmailAccount account, EmailListener listener) {
		super("EmailSender :: " + account.getAccountName());
		
		this.sender = account;
		this.emailListener = listener;
	}
	
	/**
	 * Adds the supplied email to the outbox.
	 * 
	 * @param email
	 */
	public void sendEmail(Email email) {
		LOG.trace("ENTER");
		email.setStatus(Email.Status.PENDING);
		outbox.add(email);
		LOG.debug("E-mail added to outbox. Size is [" + outbox.size() + "]");
		if (emailListener != null) {
			emailListener.outgoingEmailEvent(this, email);
		}
		LOG.trace("EXIT");
	}
	
	public void run() {
		LOG.trace("ENTER");
		running = true;
		while (running) {
			if (outbox.size() > 0) {
				this.server = sender.getAccountServer();
				this.account = sender.getAccountName();
				this.password = sender.getAccountPassword();
				this.isSSL = sender.useSsl();
				if (sender.getAccountServerPort() == -1) {
					// This is to fix the old email settings on db, when we created the new column, the default value is -1.
					if (isSSL) sender.setAccountServerPort(EmailAccount.DEFAULT_SMTPS_PORT);
					else sender.setAccountServerPort(EmailAccount.DEFAULT_SMTP_PORT);
				}
				this.serverPort = sender.getAccountServerPort();
				send();
			}
			FrontlineUtils.sleep_ignoreInterrupts(5000);
		}
		LOG.trace("EXIT");
	}
	
	/**
	 * Stops this thread
	 */
	public void stopRunning() {
		running = false;
	}
	
	/**
	 * Sends an Email using the supplied information.
	 */
	private void send() {
		LOG.trace("ENTER");
		java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		Session session = getSession();
		Transport transport = null;
		boolean everythingOk = true;
		boolean connectionOk = true;
		
		LOG.debug("Number of retries [" + retries + "]");
		if (retries > 0) {
			//If this server has more retries, try to connect.
			try {
				transport = connect(session);
			} catch (MessagingException e) {
				LOG.info("Fail to connect to server [" + server + "]");
				LOG.debug("Fail to connect to server [" + server + "]", e);
				connectionOk = false;
				everythingOk = false;
				retries--;
			}
		}

		List<Email> toAdd = new ArrayList<Email>();
		
		Email email;
		while ((email = outbox.poll()) != null) {
			//Construct the message
			long sent = FrontlineSMSConstants.DEFAULT_END_DATE;
			try {
				if (connectionOk) {
					Message msg = new MimeMessage(session);
					msg.setFrom(new InternetAddress(account));
					String recipients[] = email.getEmailRecipients().split(";");
					for (String recipient : recipients) {
						msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
					}
					msg.setSubject(email.getEmailSubject());
					msg.setText(email.getEmailContent());
					Date date = new Date();
					msg.setSentDate(date);
					sent = date.getTime();
					LOG.debug("Sending e-mail [" + email + "]");
					transport.sendMessage(msg, msg.getAllRecipients());
				}
			} catch (AddressException e) {
				LOG.info("Fail to send e-mail [" + email.getEmailContent() + "] to [" + email.getEmailRecipients() + "]");
				LOG.debug("Fail to send e-mail [" + email.getEmailContent() + "] to [" + email.getEmailRecipients() + "]", e);
				everythingOk = false;
			} catch (IllegalArgumentException e) {
				LOG.info("Fail to send e-mail [" + email.getEmailContent() + "] to [" + email.getEmailRecipients() + "]");
				LOG.debug("Fail to send e-mail [" + email.getEmailContent() + "] to [" + email.getEmailRecipients() + "]", e);
				everythingOk = false;
			} catch (MessagingException e) {
				LOG.info("Fail to send e-mail [" + email.getEmailContent() + "] to [" + email.getEmailRecipients() + "]");
				LOG.debug("Fail to send e-mail [" + email.getEmailContent() + "] to [" + email.getEmailRecipients() + "]", e);
				everythingOk = false;
			}
			
			if (everythingOk) {
				email.setDate(sent);
				email.setStatus(Email.Status.SENT);
				LOG.debug("E-mail [" + email + "] was sent!");
			} else if (connectionOk || retries == 0) {
				//Failed to send this e-mail, due to either sending failure or no more retries.
				LOG.debug("E-mail [" + email + "] was not sent! Setting status to FAILED.");
				email.setStatus(Email.Status.FAILED);
			} else {
				//We still have some retries for this server, so we should re-try to send e-mails.
				LOG.debug("E-mail [" + email + "] was not sent! Setting status to RE-TRYING.");
				email.setStatus(Email.Status.RETRYING);
				toAdd.add(email);
			}
			
			if (emailListener != null) {
				emailListener.outgoingEmailEvent(this, email);
			}
		}
		
		if (!connectionOk && retries == 0) {
			//Connection errors
			LOG.debug("This server has run out fo connection re-tries. Removing thread!");
			stopRunning();
		} else if (!connectionOk && retries > 0) {
			LOG.debug("This server could not be reached. Waiting [" + SLEEP_TIME + "ms] to try again!");
			FrontlineUtils.sleep_ignoreInterrupts(SLEEP_TIME);
			for (Email mail : toAdd) {
				outbox.add(mail);
			}
			LOG.debug("Thread is back to try connection again!");
		}
		LOG.trace("EXIT");
	}
	
	/**
	 * Connects to the server
	 * 
	 * @param session
	 * @return
	 * @throws MessagingException 
	 * @throws MessagingException
	 */
	private Transport connect(Session session) throws MessagingException {
		LOG.trace("ENTER");
		LOG.debug("Host [" + server + "]");
		LOG.debug("Port [" + serverPort + "]");
		LOG.debug("Account [" + account + "]");
		Transport transport = session.getTransport();
        transport.connect(server, serverPort, account, password);
        LOG.debug("Connection OK!");
        LOG.trace("EXIT");
        return transport;
	}

	/**
	 * Returns a new session for this server.
	 * 
	 * @return
	 */
	private Session getSession() {
		LOG.trace("ENTER");
		LOG.debug("Host [" + server + "]");
		LOG.debug("Use SSL [" + isSSL + "]");
		Properties props = EmailUtils.getPropertiesForHost(server, serverPort, isSSL);
		Session session;
		if (isSSL) {
			session = Session.getInstance(props);
		} else {
			session = Session.getInstance(props, new EmailSender.FrontlineEmailAuthenticator(account, password));
		}
		session.setDebug(DEBUG_SESSION);
		LOG.trace("EXIT");
		return session;
	}
	
	/**
	 * SMTPAuthenticator is used to do simple authentication when the SMTP
	 * server requires it.
	 */
	public static class FrontlineEmailAuthenticator extends Authenticator {
		private String user;
		private String pass;

		public FrontlineEmailAuthenticator(String user, String pass) {
			this.user = user;
			this.pass = pass;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, pass);
		}
	}
}

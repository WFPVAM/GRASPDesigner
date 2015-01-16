/**
 * GRASP(Geo-referential Real-time Acquisition Statistics Platform) Designer Tool <http://www.fabaris.it>
 * Copyright © 2012 ,Fabaris s.r.l
 * This file is part of GRASP Designer Tool.  
 *  GRASP Designer Tool is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.  
 *  GRASP Designer Tool is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser  General Public License for more details.  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with GRASP Designer Tool. 
 *  If not, see <http://www.gnu.org/licenses/>
 */
package net.frontlinesms.plugins.httptrigger.httplistener;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.plugins.httptrigger.HttpTriggerEventListener;
import net.frontlinesms.plugins.httptrigger.HttpTriggerListener;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;

/**
 * Thread that handle the Jetty server
 * @author Alex
 * @author Fabaris Srl: Attila Aknai
 * www.fabaris.it <http://www.fabaris.it/>  <http://www.fabaris.it/>  
 *
 */
public class HttpTriggerServer extends Thread implements HttpTriggerListener {
//> STATIC CONSTANTS

//> INSTANCE PROPERTIES
	/** Logging object */
	private final Logger log = FrontlineUtils.getLogger(this.getClass());
	/** The listener for HTTP events */
	private final HttpTriggerEventListener eventListener;
	/** The port on which we will listen for incoming HTTP connections */
	private final int port;
	/** The Jetty server that will listen for HTTP connections. */
	private final Server server;

	private final String I18N_STARTING_ON_PORT = "plugins.httptrigger.starting.on.port";
	private final String I18N_TERMINATED_ON_PORT = "plugins.httptrigger.terminated.on.port";
	private final String I18N_TERMINATING_ON_PORT = "plugins.httptrigger.terminating.on.port";

//> CONSTRUCTORS
	/**
	 * Create a new {@link HttpTriggerServer}.
	 * @param eventListener value for {@link #eventListener}
	 * @param ignoreList 
	 * @param port value for {@link #port}
	 */
	public HttpTriggerServer(HttpTriggerEventListener eventListener, String[] ignoreList, SimpleUrlRequestHandler groovyRequestHandler, int port) {
		// Give this Thread a meaningful name
		super(HttpTriggerServer.class.getSimpleName() + "; port: " + port);
		
		this.port = port;
		this.eventListener = eventListener;
		
		this.server = new Server(port);
		server.setAttribute("org.mortbay.jetty.Request.maxFormContentSize", -1);
		Connector connector = new SocketConnector();
		connector.setMaxIdleTime(15000);
		connector.setPort(port);
		server.setConnectors(new Connector[]{connector});
		
		Handler handler = new FormRequestsHandler(this.eventListener);
		server.setHandler(handler);
	}

//> ACCESSORS
	/** @return {@link #port} */
	public int getPort() {
		return this.port;
	}

//> INSTANCE METHODS
	/** Start listening. */
	public void run() {
		this.eventListener.log(InternationalisationUtils.getI18nString(I18N_STARTING_ON_PORT, String.valueOf(this.getPort())));
		try {
			server.start();
			//server.setAttribute("", attribute)
		} catch (Exception ex) {
			log.warn("Problem starting listener.", ex);
			try {
				this.server.stop();
			} catch (Exception e) {
				log.info("Problem stopping listener which failed to start.", e);
			}
		}
		try {
			server.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.eventListener.log(InternationalisationUtils.getI18nString(I18N_TERMINATED_ON_PORT, String.valueOf(this.getPort())));
	}

	/** Request the server to stop listening. */
	public void pleaseStop() {
		this.eventListener.log(InternationalisationUtils.getI18nString(I18N_TERMINATING_ON_PORT, String.valueOf(this.getPort())));
		try {
			this.server.stop();
		} catch (Exception ex) {
			log.warn(ex);
		}
	}

//> STATIC FACTORIES

//> STATIC HELPER METHODS
}

/**
 * Implementation of {@link HttpTriggerEventListener} which logs all messages to {@link System#out}
 * @author Alex
 */
class CommandLineTriggerEventListener implements HttpTriggerEventListener {
	/** @see net.frontlinesms.plugins.httptrigger.HttpTriggerEventListener#log(java.lang.String) */
	public void log(String message) {
		System.out.println("LOG: " + message);
	}

	/** Handle request to send an SMS. */
	public void sendSms(String toPhoneNumber, String message) {
		System.out.println("SEND SMS: to=" + toPhoneNumber + "; message=" + message);
	}

	public String testConnection(String phone, String data) {
		System.out.println("TEST CONNECTION:"+phone);
		return "";
	}

	public String formResponse(String phone, String data) {
		System.out.println("RECEIVED RESPOMSE: from=" + phone + "; message=" + data);
		return null;
	}

	public String syncUserForms(String phone, String data) {
		System.out.println("SYNC REQUEST: from=" + phone + "; message=" + data);
		return null;
	}
}

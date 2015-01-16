package net.frontlinesms.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.frontlinesms.EmailServerHandler;
import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.XMLReader;
import net.frontlinesms.data.domain.*;
import net.frontlinesms.data.domain.KeywordAction.ExternalCommandResponseActionType;
import net.frontlinesms.data.domain.KeywordAction.ExternalCommandResponseType;
import net.frontlinesms.data.domain.FrontlineMessage.Status;
import net.frontlinesms.data.repository.*;
import net.frontlinesms.data.*;
import net.frontlinesms.listener.IncomingMessageListener;
import net.frontlinesms.listener.UIListener;
import net.frontlinesms.messaging.mms.MmsUtils;
import net.frontlinesms.messaging.sms.SmsService;
import net.frontlinesms.mms.MmsMessage;


import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.smslib.CIncomingMessage;
import org.smslib.CStatusReportMessage;
import org.smslib.CMessage.MessageType;
import org.smslib.sms.SmsMessageEncoding;

/**
 * Processor of incoming messages for {@link FrontlineSMS}.
 * @author Alex
 */
public class IncomingMessageProcessor extends Thread {
	/** Time, in millis, thread should sleep for after message processing failed. */
	private static final int THREAD_SLEEP_AFTER_PROCESSING_FAILED = 5000;

	private static final Logger LOG = FrontlineUtils.getLogger(IncomingMessageProcessor.class);
	
	/** Set hi when the thread should terminate. */
	private boolean keepAlive;
	/** Queue of messages to process. */
	private final BlockingQueue<IncomingMessageProcessorQueueItem> incomingMessageQueue = new LinkedBlockingQueue<IncomingMessageProcessorQueueItem>();
	
//> DATA ACCESS OBJECTS
	private final FrontlineSMS frontline;
	private final ContactDao contactDao;
	private final KeywordDao keywordDao;
	private final KeywordActionDao keywordActionDao;
	private final GroupDao groupDao;
	private final GroupMembershipDao groupMembershipDao;
	private final MessageDao messageDao;
	private EmailDao emailDao;

	private UIListener uiListener;
	/** Set of listeners for incoming message events. */
	private Set<IncomingMessageListener> incomingMessageListeners = new HashSet<IncomingMessageListener>();
	
	private final EmailServerHandler emailServerHandler;

	/** Create a new {@link IncomingMessageProcessor}, and initialise properties. */
	public IncomingMessageProcessor(FrontlineSMS frontline) {
		super("Incoming message processor");
		this.frontline = frontline;
		this.contactDao = frontline.getContactDao();
		this.keywordDao = frontline.getKeywordDao();
		this.keywordActionDao = frontline.getKeywordActionDao();
		this.groupDao = frontline.getGroupDao();
		this.groupMembershipDao = frontline.getGroupMembershipDao();
		this.messageDao = frontline.getMessageDao();
		this.emailDao = frontline.getEmailDao();
		this.emailServerHandler = frontline.getEmailServerHandler();
	}
	
	public void setUiListener(UIListener uiListener) {
		this.uiListener = uiListener;
	}

	
	public void queue(SmsService receiver, CIncomingMessage incomingMessage) {
		LOG.trace("Adding message to queue: " + receiver.hashCode() + ":" + incomingMessage.hashCode());
		incomingMessageQueue.add(new IncomingMessageDetails(receiver, incomingMessage));
	}
	
	public void queue(MmsMessage mms) {
		LOG.trace("Adding MMS to queue:" + mms.hashCode());
		incomingMessageQueue.add(new IncomingMms(mms));
	}
	
	public void die() {
		keepAlive = false;
		incomingMessageQueue.add(new IncomingMessageProcessorQueueKiller());
	}
	
	public void run() {
		this.keepAlive = true;
		while(keepAlive) {
			IncomingMessageProcessorQueueItem queueItem = null;
			LOG.trace("Getting incoming message from queue.");
			try {
				queueItem = incomingMessageQueue.take();
			} catch(InterruptedException ex) {
				LOG.warn("Thread interrupted.", ex);
			}
		
			if (queueItem == null) {
				// we may have popped out when queue was notified, which means job may be null
				LOG.trace("There were no messages in the queue.");
				continue;
			} else {
				if(queueItem instanceof IncomingMessageProcessorQueueKiller) {
					// We have been given a "poisoned" item so must terminate this thread
					keepAlive = false;
				} else {
					try {
						// We've got a new message, so process it.
						processIncomingMessageDetails(queueItem);
					} catch(Throwable t) {
						// There was a problem processing the message.  At this stage, any issue should be a database
						// connectivity issue.  Stop processing messages for a while, and re-queue this one.
						LOG.warn("Error processing message.  It will be queued for re-processing.", t);
						incomingMessageQueue.add(queueItem);
						FrontlineUtils.sleep_ignoreInterrupts(THREAD_SLEEP_AFTER_PROCESSING_FAILED);
					}
				} 
			}
		}
		LOG.trace("EXIT");
	}
	
	private void processIncomingMessageDetails(IncomingMessageProcessorQueueItem queueItem) {
		if (queueItem instanceof IncomingMms) {
			// Creates the FrontlineMultimediaMessage
			FrontlineMultimediaMessage mms = MmsUtils.create(((IncomingMms) queueItem).getMessage());
			this.messageDao.saveMessage(mms);
			handleMessage(mms);
		} else if (queueItem instanceof IncomingMessageDetails) {
			IncomingMessageDetails incomingMessageDetails = (IncomingMessageDetails) queueItem;
			CIncomingMessage incomingMessage = incomingMessageDetails.getMessage();
			SmsService receiver = incomingMessageDetails.getReceiver();
			LOG.trace("Got message from queue: " + receiver.hashCode() + ":" + incomingMessage.hashCode());
			
			// Check the incoming message details with the KeywordFactory to make sure there are no details
			// that should be hidden before creating the message object...
			String incomingSenderMsisdn = incomingMessage.getOriginator();
			LOG.debug("Sender [" + incomingSenderMsisdn + "]");
			if (incomingMessage.getType() == CIncomingMessage.MessageType.StatusReport) {
				handleStatusReport(incomingMessage);
			} else {
				// This is an incoming message, so process accordingly
				FrontlineMessage incoming;
				if (incomingMessage.getMessageEncoding() == SmsMessageEncoding.GSM_7BIT || incomingMessage.getMessageEncoding() == SmsMessageEncoding.UCS2) {
					if(LOG.isDebugEnabled()) LOG.debug("Incoming text message [" + incomingMessage.getText() + "]");
					incoming = FrontlineMessage.createIncomingMessage(incomingMessage.getDate(), incomingSenderMsisdn, receiver.getMsisdn(), incomingMessage.getText());
					messageDao.saveMessage(incoming);
					//Fabaris_a.zanchi insert here code to unzip-decode message so it can be parsed for keywords
					String temp = incoming.getTextContent();
					try {
						incoming.unzipDecodeTextContent();
					} catch (Exception e) {
						LOG.debug("Error decoding incoming message: "+e.getMessage());
						incoming.setTextMessageContent(temp);
					}
					handleMessage(incoming);
				} else {
					if(LOG.isDebugEnabled()) LOG.debug("Incoming binary message: " + incomingMessage.getBinary().length + "b");
					
					// Save the binary message
					incoming = FrontlineMessage.createBinaryIncomingMessage(incomingMessage.getDate(), incomingSenderMsisdn, receiver.getMsisdn(), -1, incomingMessage.getBinary());
					messageDao.saveMessage(incoming);
				}
	
				for(IncomingMessageListener listener : this.incomingMessageListeners) {
					listener.incomingMessageEvent(incoming);
				}
				if (uiListener != null) {
					uiListener.incomingMessageEvent(incoming);
				}
			}
		}  else {
			LOG.error("Unknown queue item type: " + queueItem.getClass());
		}
	}

	/**
	 * Process an incoming status report.  The status should be set to
	 * @param incomingMessage The incoming status report.
	 */
	private void handleStatusReport(CIncomingMessage incomingMessage) {
		assert(incomingMessage.getType() == MessageType.StatusReport) : "This method can ONLY be called on an incoming status report.";
		// Match the status report with a previously sent message, and update that message's
		// status.  If no message is found to match this to, just ditch the status report.  This
		// means that shredding is of no concern here.
		CStatusReportMessage statusReport = (CStatusReportMessage) incomingMessage;
		// Here, we strip the first four characters off the originator's number.  This is because we
		// cannot be sure if the numbers supplied by the PhoneHandler are localised, or international
		// with or without leading +.
		FrontlineMessage message = messageDao.getMessageForStatusUpdate(statusReport.getOriginator(), incomingMessage.getRefNo());
		if (message != null) {
			LOG.debug("It's a delivery report for message [" + message + "]");
			switch(statusReport.getDeliveryStatus()) {
			case CStatusReportMessage.DeliveryStatus.Delivered:
				message.setStatus(Status.DELIVERED);
				break;
			case CStatusReportMessage.DeliveryStatus.Aborted:
				message.setStatus(Status.FAILED);
				break;
			}
			if (uiListener != null) {
				uiListener.outgoingMessageEvent(message);
			}
		}
	}

	/**
	 * Processes keyword actions for a text message.
	 * @param message
	 */
	/* not private to allow unit testing */
	void handleMessage(final FrontlineMessage message) {
		Keyword keyword;
		
		if (message instanceof FrontlineMultimediaMessage) {
			keyword = keywordDao.getKeyword(FrontlineSMSConstants.MMS_KEYWORD);
		} else {
			keyword = keywordDao.getFromMessageText(message.getTextContent());
		}
		
		if (keyword != null) {
			LOG.debug("The message contains keyword [" + keyword.getKeyword() + "]");
			final Collection<KeywordAction> actions = this.keywordActionDao.getActions(keyword);
			// TODO process pre-message actions (e.g. "shred") TODO this should actually be done BEFORE the message object is persisted

			if(actions.size() > 0) {
				LOG.debug("Executing actions for keyword, if the contact is allowed!");
				Contact contact = contactDao.getFromMsisdn(message.getSenderMsisdn());
				//If we could not find this contact, we execute the action.
				//If we found a contact, he/she needs to be allowed to execute the action.
				if (contact == null || contact.isActive()) {
					final long triggerTime = message.getDate();
					for (KeywordAction action : actions) {
						if (action.isAlive(triggerTime)) {
							try {
								handleIncomingMessageAction_post(action, message);
							} catch(Exception ex) {
								LOG.warn("Exception thrown while executing action.", ex);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Handle relevant incoming message actions AFTER the message has been created with the messageFactory.
	 * @param action The action to executed.
	 * @param incoming The incoming message that triggered this action.
	 */
	private void handleIncomingMessageAction_post(KeywordAction action, FrontlineMessage incoming) {
		LOG.trace("ENTER");
		String incomingSenderMsisdn = incoming.getSenderMsisdn();
		String incomingMessageText = incoming.getTextContent();
		switch (action.getType()) {
			case NO_ACTION:
				// This action is used for testing, and causes nothing to happen
				break;
			case FORWARD:
				// Generate a message, and then forward it to the group attached to this action.
				LOG.debug("It is a forward action!");
				String forwardedMessageText = KeywordAction.KeywordUtils.getForwardText(action, contactDao.getFromMsisdn(incomingSenderMsisdn), incomingSenderMsisdn, incomingMessageText);
				LOG.debug("Message to forward [" + forwardedMessageText + "]");
				for (Contact contact : this.groupMembershipDao.getActiveMembers(action.getGroup())) {
					LOG.debug("Sending to [" + contact.getName() + "]");
					frontline.sendTextMessage(contact.getPhoneNumber(), KeywordAction.KeywordUtils.personaliseMessage(contact, forwardedMessageText));
				}
				break;
			case JOIN: {
				LOG.debug("It is a group join action!");
				
				// If the contact does not exist, we need to persist him so that we can add him to a group.
				// Otherwise, get the contact from the database.
				Contact contact = contactDao.getFromMsisdn(incomingSenderMsisdn);
				try {
					if (contact == null) {
						contact = new Contact("", incomingSenderMsisdn, null, null, null, true);
						contactDao.saveContact(contact);
					}
					Group group = action.getGroup();
					LOG.debug("Adding contact [" + contact.getName() + "], Number [" + contact.getPhoneNumber() + "] to Group [" + group.getName() + "]");
					boolean contactAdded = this.groupMembershipDao.addMember(group, contact);
					if(contactAdded) {
						groupDao.updateGroup(group);
						if(uiListener != null) {
							uiListener.contactAddedToGroup(contact, group);
						}
					}
				} catch(DuplicateKeyException ex) {
					// Due to previous check, this should never be thrown...
					// Not much we can do if it is!
					// FIXME throwing this exception could spit out otherwise-shredded data
					// into the logs!
					throw new RuntimeException(ex);
				}
			}	break;
			case LEAVE: {
				LOG.debug("It is a group leave action!");
				Contact contact = contactDao.getFromMsisdn(incomingSenderMsisdn);
				if (contact != null) {
					Group group = action.getGroup();
					LOG.debug("Removing contact [" + contact.getName() + "] from Group [" + group.getName() + "]");
					if(this.groupMembershipDao.removeMember(group, contact)) {
						this.groupDao.updateGroup(group);
					}
					if (uiListener != null) {
						uiListener.contactRemovedFromGroup(contact, group);
					}
				}
			}	break;
			case REPLY:
				// Generate a message, and then send it back to the sender of the received message.
				LOG.debug("It is an auto-reply action!");
				String reply = KeywordAction.KeywordUtils.getReplyText(action, contactDao.getFromMsisdn(incomingSenderMsisdn), incomingSenderMsisdn, incomingMessageText, null);
				LOG.debug("Sending [" + reply + "] to [" + incomingSenderMsisdn + "]");
				frontline.sendTextMessage(incomingSenderMsisdn, reply);
				// TODO should the message be tied to the action somehow?
				break;
			case EXTERNAL_CMD:
				// Executes a external command
				LOG.debug("It is an external command action!");
				try {
					executeExternalCommand(action, incomingSenderMsisdn, incomingMessageText);
				} catch (IOException e) {
					LOG.debug("Problem executing external command.", e);
				} catch (InterruptedException e) {
					LOG.debug("Problem executing external command.", e);
				} catch (JDOMException e) {
					LOG.debug("Problem executing external command.", e);
				}
				break;
			case EMAIL:
				LOG.debug("It is an e-mail action!");
				Email email = new Email(
						action.getEmailAccount(),
						action.getEmailRecipients(),
						KeywordAction.KeywordUtils.getEmailSubject(action, contactDao.getFromMsisdn(incomingSenderMsisdn), incomingSenderMsisdn, incomingMessageText, null),
						KeywordAction.KeywordUtils.getReplyText(action, contactDao.getFromMsisdn(incomingSenderMsisdn), incomingSenderMsisdn, incomingMessageText, null)
				);
				emailDao.saveEmail(email);
				LOG.debug("Sending [" + email.getEmailContent() + "] from [" + email.getEmailFrom().getAccountName() + "] to [" + email.getEmailRecipients() + "]");
				emailServerHandler.sendEmail(email);
				break;
		}
				
		this.keywordActionDao.incrementCounter(action);
		
		if (uiListener != null) {
			uiListener.keywordActionExecuted(action);
		}
		LOG.debug("Number of hits for this action [" + action + "] is [" + action.getCounter() + "]");
		LOG.trace("EXIT");
	}
	

	/**
	 * Executes a external command (HTTP or Command Line) and treats its response according to what is defined in the action.
	 * @param action
	 * @param incomingSenderMsisdn 
	 * @param incomingMessageText 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws JDOMException 
	 */
	/* not private to allow unit testing */
	void executeExternalCommand(KeywordAction action, String incomingSenderMsisdn, String incomingMessageText) throws IOException, InterruptedException, JDOMException {
		LOG.trace("ENTER");
		String cmd = KeywordAction.KeywordUtils.getExternalCommand(
				action,
				contactDao.getFromMsisdn(incomingSenderMsisdn),
				incomingSenderMsisdn,
				incomingMessageText
		);
		LOG.debug("Command to be executed [" + cmd + "]");

		if (action.getExternalCommandResponseType() != ExternalCommandResponseType.LIST_COMMANDS) {
			//Executes the command and handle the response as plain text, or no response at all.
			String response;
			LOG.debug("Response will be plain text or nothing at all.");
			boolean waitForResponse = action.getExternalCommandResponseType() == ExternalCommandResponseType.PLAIN_TEXT;
			if (action.getExternalCommandType() == KeywordAction.ExternalCommandType.HTTP_REQUEST) {
				LOG.debug("Executing HTTP request...");
				response = FrontlineUtils.makeHttpRequest(cmd, waitForResponse);
			} else {
				LOG.debug("Executing external program...");
				response = FrontlineUtils.executeExternalProgram(cmd, waitForResponse);
			}
			if (waitForResponse) {
				LOG.debug("Response [" + response + "]");
				handleExternalCommandResponse(action, incomingSenderMsisdn, response);
			}
		} else {
			//LIST OF COMMANDS TO EXECUTE
			LOG.debug("Response will be an XML with Frontline Commands.");
			InputStream toRead = null;
			if (action.getExternalCommandType() == KeywordAction.ExternalCommandType.HTTP_REQUEST) {
				LOG.debug("Executing HTTP request...");
				toRead = FrontlineUtils.makeHttpRequest(cmd);
			} else {
				LOG.debug("Executing external program...");
				toRead = FrontlineUtils.executeExternalProgram(cmd);
			}
			LOG.debug("Reading XML from response...");
			XMLReader reader = new XMLReader(toRead);
			for (XMLMessage msg : reader.readMessages()) {
				LOG.debug("Message found!");
				LOG.debug("Data [" + msg.getData() + "]");
				if (msg.getType() == XMLMessage.TYPE_TEXT) {
					//We add everything to the numbers list, to send in the end.
					//Contacts
					for (String contact : msg.getToContacts()) {
						Contact c = contactDao.getContactByName(contact);
						if (c!= null && c.isActive()) {
							msg.addNumber(c.getPhoneNumber());
						}
					}
					//Groups
					for (String group : msg.getToGroups()) {
						Group g = groupDao.getGroupByPath(group);
						if (g != null) {
							for(Contact c : this.groupMembershipDao.getActiveMembers(g)) {
								if (c.isActive()) {
									msg.addNumber(c.getPhoneNumber());
								}
							}
						}
					}
					//All recipients are in the numbers list now.
					for (String number : msg.getToNumbers()) {
						LOG.debug("Sending to [" + number + "]");
						frontline.sendTextMessage(number, msg.getData());
					}
				} else {
					//TODO BINARY MESSAGE
				}
			}
		}
		LOG.trace("EXIT");
	}

	/**
	 * Handles the command response for this action.
	 * @param action
	 * @param incomingSenderMsisdn
	 * @param response
	 */
	private void handleExternalCommandResponse(KeywordAction action, String incomingSenderMsisdn,
			String response) {
		assert(action.getType() == KeywordAction.Type.EXTERNAL_CMD) : "This method should only be called on external command actions.";
		// PLAIN TEXT RESPONSE so we need to verify if the user wants
		// to auto reply forward the response
		LOG.trace("ENTER");
		ExternalCommandResponseActionType responseActionType = action.getCommandResponseActionType();
		if (responseActionType == KeywordAction.ExternalCommandResponseActionType.DO_NOTHING) {
			LOG.debug("Nothing to do with the response!");
			LOG.trace("EXIT");
			return;
		}
		String message = KeywordAction.KeywordUtils.getExternalCommandReplyMessage(action, response);
		LOG.debug("Message to forward [" + message + "]");
		if (responseActionType == KeywordAction.ExternalCommandResponseActionType.REPLY 
				|| responseActionType == KeywordAction.ExternalCommandResponseActionType.REPLY_AND_FORWARD) {
			//Auto reply
			LOG.debug("Sending to [" + incomingSenderMsisdn + "] as an auto-reply.");
			frontline.sendTextMessage(incomingSenderMsisdn, message);
		}
		if (responseActionType == KeywordAction.ExternalCommandResponseActionType.FORWARD 
				|| responseActionType == KeywordAction.ExternalCommandResponseActionType.REPLY_AND_FORWARD) {
			//Forwarding to a group
			Group fwd = action.getGroup();
			LOG.debug("Forwarding to group [" + fwd.getName() + "]");
			for(Contact contact : this.groupMembershipDao.getActiveMembers(fwd)) {
				if (contact.isActive()) {
					if (responseActionType != KeywordAction.ExternalCommandResponseActionType.REPLY_AND_FORWARD 
							|| !contact.getPhoneNumber().equalsIgnoreCase(incomingSenderMsisdn)) {
						//If we have already replied to the sender and he/she is on the group to forward
						//so we don't send the message again.
						LOG.debug("Sending to contact [" + contact.getName() + "]");
					}
					frontline.sendTextMessage(contact.getPhoneNumber(), message);
				}
			}
		}
		LOG.trace("EXIT");
	}
	
	/**
	 * Adds another {@link IncomingMessageListener} to {@link #incomingMessageListeners}.
	 * @param incomingMessageListener new {@link IncomingMessageListener}
	 */
	public void addIncomingMessageListener(IncomingMessageListener incomingMessageListener) {
		this.incomingMessageListeners.add(incomingMessageListener);
	}
	
	/**
	 * Removes a {@link IncomingMessageListener} from {@link #incomingMessageListeners}.
	 * @param incomingMessageListener {@link IncomingMessageListener} to be removed
	 */
	public void removeIncomingMessageListener(IncomingMessageListener incomingMessageListener) {
		this.incomingMessageListeners.remove(incomingMessageListener);
	}
}

/** Empty interface implemented by items which are put in the {@link IncomingMessageProcessor}'s queue. */
interface IncomingMessageProcessorQueueItem {}

/**
 * Queue item which contains details of an incoming message.
 * @author Alex
 */
class IncomingMessageDetails implements IncomingMessageProcessorQueueItem {
	/** the message received */
	private final CIncomingMessage message;
	/** the device the message was received on */
	private final SmsService receiver;
//> CONSTRUCTOR
	/**
	 * @param receiver The device which this message was received on. 
	 * @param message The message
	 */
	public IncomingMessageDetails(SmsService receiver, CIncomingMessage message) {
		this.receiver = receiver;
		this.message = message;
	}
//> ACCESSORS
	/** @return the message received */
	public CIncomingMessage getMessage() {
		return message;
	}
	/** @return the device the message was received on */
	public SmsService getReceiver() {
		return receiver;
	}
}

/**
 * Queue item which contains an MMS
 * @author Morgan Belkadi <morgan@frontlinesms.com>
 */
class IncomingMms implements IncomingMessageProcessorQueueItem {
	/** the message received */
	private final MmsMessage message;
//> CONSTRUCTOR
	/**
	 * @param receiver The device which this message was received on. 
	 * @param message The message
	 */
	public IncomingMms(MmsMessage message) {
		this.message = message;
	}
//> ACCESSORS
	/** @return the message received */
	public MmsMessage getMessage() {
		return message;
	}
}

/**
 * Queuing an instance of this class will kill the {@link IncomingMessageProcessor}.
 * @author Alex
 */
class IncomingMessageProcessorQueueKiller implements IncomingMessageProcessorQueueItem {}

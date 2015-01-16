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
package net.frontlinesms.plugins.httptrigger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.Group;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.data.repository.GroupMembershipDao;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.PluginSettingsController;
import net.frontlinesms.plugins.forms.FormsPluginController;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.httptrigger.httplistener.groovy.GroovyUrlRequestHandler;
import net.frontlinesms.plugins.httptrigger.httplistener.groovy.UrlMapper;
import net.frontlinesms.plugins.httptrigger.httplistener.HttpTriggerServer;
import net.frontlinesms.ui.Event;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * This plugin controls an HTTP listener for triggering SMS from outside FrontlineSMS.
 * @author Alex
 * @author Fabaris Srl: Attila Aknai
 * www.fabaris.it <http://www.fabaris.it/>  <http://www.fabaris.it/>  
 */
@PluginControllerProperties(name="Internet Connection", i18nKey="plugins.httptrigger.name", iconPath="/icons/import.png", hibernateConfigPath = PluginControllerProperties.NO_VALUE, springConfigLocation = PluginControllerProperties.NO_VALUE)
public class HttpTriggerPluginController extends BasePluginController implements HttpTriggerEventListener {
//> STATIC CONSTANTS
	/** Filename and path of the XML for the HTTP Trigger tab. */
	private static final String UI_FILE_TAB = "/ui/plugins/httptrigger/httpTriggerTab.xml";

//> INSTANCE PROPERTIES
	/** The {@link HttpTriggerListener} that is currently running.  This property will be <code>null</code> if no listener is running. */
	private HttpTriggerListener httpListener;
	/** Thinlet tab controller for this plugin */
	private HttpTriggerThinletTabController tabController;
	/** the {@link FrontlineSMS} instance that this plugin is attached to */
	private FrontlineSMS frontlineController;
	private GroovyUrlRequestHandler groovyUrlRequestHandler;
	private final String I18N_LOADED_IGNORE_PATH = "plugins.httptrigger.ignored.path.loaded";
	private final String I18N_LOADED_IGNORE_PATHS = "plugins.httptrigger.ignored.paths.loaded";
	private final String I18N_LISTENER_STOPPING = "plugins.httptrigger.listener.stopping";
	private final String I18N_LOADED_SCRIPT_PATHS = "plugins.httptrigger.script.paths.loaded";
	private final String I18N_LOADED_SCRIPT_PATH = "plugins.httptrigger.script.path.loaded";
	private final String I18N_SENDING_TO = "plugins.httptrigger.sending.to";
	private final String I18N_TEST_CONNECTION = "plugins.httptrigger.test.connection";
	private final String I18N_TEST_CONNECTION_OK = "plugins.httptrigger.test.connection.ok";
	private final String i18N_TEST_CONNECTION_ERROR = "plugins.httptrigger.test.connection.error";
	private final String I18N_FORM_RESPONSE = "plugins.httptrigger.formResponse";
	private final String I18N_FORM_RESPONSE_OK = "plugins.httptrigger.formResponse.ok";
	private final String I18N_FORM_RESPONSE_ERROR = "plugins.httptrigger.formResponse.error";
	private final String I18N_FORM_SYNC = "plugins.httptrigger.sync";
	private final String I18N_FORM_SYNC_ERROR_UNEXPECTED = "plugins.httptrigger.sync.error.unexpected";
	private final String I18N_FORM_SYNC_ERROR_MALFORMED = "plugins.httptrigger.sync.error.malformed";
	private final String I18N_FORM_SYNC_ERROR_RESULT = "plugins.httptrigger.sync.error.result";
	
//> CONSTRUCTORS

//> ACCESSORS
	/** @see net.frontlinesms.plugins.PluginController#getTab(net.frontlinesms.ui.UiGeneratorController) */
	public Object initThinletTab(UiGeneratorController uiController) {
		this.tabController = new HttpTriggerThinletTabController(this, uiController);
		
		Object httpTriggerTab = uiController.loadComponentFromFile(UI_FILE_TAB, tabController);
		tabController.setTabComponent(httpTriggerTab);
		tabController.initFields();
		
		HttpTriggerProperties httpTriggerProperties = HttpTriggerProperties.getInstance();
		String[] scriptPaths = httpTriggerProperties.getScriptFilePaths();
		log(InternationalisationUtils.getI18nString(I18N_LOADED_SCRIPT_PATHS, scriptPaths.length));
		String serverRoot = "http://localhost:" + httpTriggerProperties.getListenPort() + "/";
		for (int i = 0; i < scriptPaths.length; i++) {
			log(InternationalisationUtils.getI18nString(I18N_LOADED_SCRIPT_PATH, String.valueOf(i), serverRoot + scriptPaths[i]));
		}

		String[] ignoreList = httpTriggerProperties.getIgnoreList();
		log(InternationalisationUtils.getI18nString(I18N_LOADED_IGNORE_PATHS, ignoreList.length));
		
		for(int i=0; i<ignoreList.length; i++) {
			log(InternationalisationUtils.getI18nString(I18N_LOADED_IGNORE_PATH, String.valueOf(i), serverRoot + ignoreList[i]));
		}
		
		if(httpTriggerProperties.isAutostart()) {
			// Start the listener here so that all fields are updated properly.
			// Starting here is little different from starting in init() with the
			// current plugin lifecycle - the plugin is only enabled when visible.
			this.startListener();
			tabController.enableFields(true);
		}
		
		return httpTriggerTab;
	}

	/** @see net.frontlinesms.plugins.PluginController#init(net.frontlinesms.FrontlineSMS, org.springframework.context.ApplicationContext) */
	public void init(FrontlineSMS frontlineController, ApplicationContext applicationContext) throws PluginInitialisationException {
		this.frontlineController = frontlineController;

		String[] scriptFilePaths = HttpTriggerProperties.getInstance().getScriptFilePaths();
		UrlMapper urlMapper = UrlMapper.create(scriptFilePaths);

		this.groovyUrlRequestHandler = new GroovyUrlRequestHandler(this, frontlineController, urlMapper);
	}
	
	/** @see net.frontlinesms.plugins.PluginController#deinit() */
	public void deinit() {
		this.stopListener();
	}

	/**
	 * Start the HTTP listener.  If there is another listener already running, it will be stopped. 
	 */
	public void startListener() {
		this.stopListener();
		
		int portNumber = HttpTriggerProperties.getInstance().getListenPort();
		String[] ignoreList = HttpTriggerProperties.getInstance().getIgnoreList();
		
		this.httpListener = new HttpTriggerServer(this, ignoreList, groovyUrlRequestHandler, portNumber);
		this.httpListener.start();
	}

	/** Stop the {@link #httpListener} if it is runnning. */
	public void stopListener() {
		if(this.httpListener != null) {
			this.httpListener.pleaseStop();
			this.log(InternationalisationUtils.getI18nString(I18N_LISTENER_STOPPING, this.httpListener.toString()));
			this.httpListener = null;
		}
	}
	
	public boolean isRunning() {
		return this.httpListener != null;
	}

//> INSTANCE HELPER METHODS
	
//> HTEL METHODS
	/** @see HttpTriggerEventListener#log(String) */
	public void log(String message) {
		if(this.tabController != null) {
			this.tabController.log(message);
		}
                this.log.trace("****************************************************");
		this.log.trace(message);
	}
	
	/** @see net.frontlinesms.plugins.httptrigger.HttpTriggerEventListener#sendSms(java.lang.String, java.lang.String) */
	public void sendSms(String toPhoneNumber, String message) {
		this.log(InternationalisationUtils.getI18nString(I18N_SENDING_TO, toPhoneNumber, message));
		frontlineController.sendTextMessage(toPhoneNumber, message);
	}

//> STATIC FACTORIES

//> STATIC HELPER METHODS

	public PluginSettingsController getSettingsController(UiGeneratorController uiController) {
		return new HttpTriggerSettingsController(this, uiController, getIcon(this.getClass()));
	}

	public String testConnection(String phone, String data) {
		ContactDao contacts =frontlineController.getContactDao();
		Contact contact = contacts.getFromMsisdn(phone);
		String contactStr = contact != null ? contact.getName() : phone;
		this.log(InternationalisationUtils.getI18nString(I18N_TEST_CONNECTION, contactStr, data));
		tabController.getUiController().getHomeTabController().newEvent(new Event(Event.TYPE_HTTP_TEST, InternationalisationUtils.getI18nString(I18N_TEST_CONNECTION, contactStr, data)));
		if(contact!=null){
			tabController.getUiController().getHomeTabController().newEvent(new Event(Event.TYPE_HTTP_TEST_OK, 
					InternationalisationUtils.getI18nString(I18N_TEST_CONNECTION_OK)));
			return "OK";
		}
		tabController.getUiController().getHomeTabController().newEvent(new Event(Event.TYPE_HTTP_TEST_ERROR, 
				InternationalisationUtils.getI18nString(i18N_TEST_CONNECTION_ERROR, "Error:Phone number \""+phone+"\" not in the server's contacts list")));
		return "ERROR:Phone number \""+phone+"\" not in the server's contacts list";
	}

	public String formResponse(String phone, String data) {
		this.log("Form response service is called");
		FormsPluginController formsController = getFormsPluginController();
		if(formsController==null){
			System.err.println("Controller:"+formsController);
			return "Unexpected Error!";
		}
		String ret = "";
		ContactDao contacts =frontlineController.getContactDao();
		Contact contact = contacts.getFromMsisdn(phone);
		String contactStr = contact != null ? contact.getName() : phone;
		String form = getResponseFormName(data);
		this.log(InternationalisationUtils.getI18nString(I18N_FORM_RESPONSE ,contactStr, form));
		if(!"".equals(form)){
			tabController.getUiController().getHomeTabController().newEvent(new Event(Event.TYPE_HTTP_RESPONSE, InternationalisationUtils.getI18nString(I18N_FORM_RESPONSE
					,contactStr, form)));
			ret = formsController.incomingHTTPResponse(phone, data);
		}
	return ret;
	}
	
	public String syncUserForms(String phone, String data) {
		this.log("Sync service is called");
		FormsPluginController formsController = getFormsPluginController();
		if(formsController==null){
			System.err.println("Controller:"+formsController);
			return InternationalisationUtils.getI18nString(I18N_FORM_SYNC_ERROR_UNEXPECTED);
		}
		ContactDao contacts =frontlineController.getContactDao();
		String contactStr = new String();
		Contact contact = contacts.getFromMsisdn(phone);
		if(contact==null){
			contactStr = phone;
		}else{
			contactStr = contact.getName();
		}
		//saving the received ids in an ArayList of Strings
		ArrayList<String> cIds = new ArrayList<String>();
		try{
			ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes("UTF-8"));
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bis);
			NodeList formsReceived = (NodeList) XPathFactory.newInstance().newXPath().compile("/forms/form").evaluate(doc, XPathConstants.NODESET);
			for(int i=0;i<formsReceived.getLength();i++){
				Node node = formsReceived.item(i);
				cIds.add(node.getTextContent());
			}
		}catch (Exception e){
			e.printStackTrace();
			return InternationalisationUtils.getI18nString(I18N_FORM_SYNC_ERROR_MALFORMED);
		}
		//creating a document for the response
		ArrayList<String> formsSynchronized = new ArrayList<String>();
		try{
		Document respDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Node rootNode = respDoc.createElement("forms");
		Set<Group> setOfGroups = new HashSet<Group>();
		FormDao fDao = frontlineController.getFormDao();
		GroupMembershipDao groupMembershipDao = frontlineController.getGroupMembershipDao();
		
		
		List<Group> groups = groupMembershipDao.getGroups(contact);
		Collection<Form> forms = null;
		setOfGroups.addAll(groups);
		if(!groups.isEmpty()){
			//setOfGroups.addAll(groupDao.getAllDescendantNodeFromGroups(groups));
			forms = fDao.getFormsByGroups(groups);
		}
		if(forms != null && !forms.isEmpty()){
			for(Form f : forms){
				String fId = f.getId_flsmsId();
				if(!FormsPluginController.isInList(cIds, fId)){
					if(FormsPluginController.isAssignedToForm(phone,f)){
						if(!f.isFinalised()) continue;
						Node cNode = respDoc.createElement("form");
						cNode.setTextContent(FormsPluginController.removeEmptyGroups(FormsPluginController.insertSoftwareVersionNumber(FormsPluginController.formToXForm(f), f)));
						rootNode.appendChild(cNode);
						formsSynchronized.add(f.getName());
					}
				}
			}
		}
		respDoc.appendChild(rootNode);
		StringWriter sw = new StringWriter();
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.transform(new DOMSource(respDoc), new StreamResult(sw));
		this.log(InternationalisationUtils.getI18nString(I18N_FORM_SYNC	,String.valueOf(formsSynchronized.size()), contactStr, formsSynchronized.toString()));
		tabController.getUiController().getHomeTabController().newEvent(new Event(Event.TYPE_HTTP_TEST, 
				InternationalisationUtils.getI18nString(I18N_FORM_SYNC,String.valueOf(formsSynchronized.size()), contactStr, formsSynchronized.toString())));
		return sw.toString();
		}catch(Exception e){
			e.printStackTrace();
			return InternationalisationUtils.getI18nString(I18N_FORM_SYNC_ERROR_RESULT);
		}
	}
	
	public FormsPluginController getFormsPluginController(){
		Set<PluginController> plugnins = frontlineController.getPluginManager().getPluginControllers();
		System.out.println("plugins Found:"+plugnins.size());
		for(PluginController p : plugnins){
			if (p instanceof FormsPluginController) {
				return (FormsPluginController) p;
			}
		}
		System.out.println("FormsPluginController was NOT found!");
		return null;
	}
	
	public String getResponseFormName(String data){
		String result = null;
		int dataEnd = data.lastIndexOf("</data>");
		String dataXMLStr = data.substring(0, dataEnd)+"</data>";
		StringReader sr = new StringReader(dataXMLStr);
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(dataXMLStr.getBytes("UTF-8"));
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bais);
			
			String id_idMac = XPathFactory.newInstance().newXPath().compile("/data/id").evaluate(doc);
			FormDao formDao = frontlineController.getFormDao();
			Form f =  formDao.getFromId_IdMac(id_idMac);
			if(f != null){
				return f.getName();
			}else{
				return "";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return "[not found]";
	}
}
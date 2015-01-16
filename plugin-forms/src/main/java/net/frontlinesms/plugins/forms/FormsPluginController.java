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
/*
 *GRASP(Geo-referential Real-time Acquisition Statistics Platform) Designer Tool <http://www.fabaris.it>
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
package net.frontlinesms.plugins.forms;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import net.frontlinesms.AppProperties;
import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.domain.Group;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.data.repository.GroupMembershipDao;
import net.frontlinesms.data.repository.UserDao;
import net.frontlinesms.listener.IncomingMessageListener;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.forms.data.FormHandlingException;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.forms.data.domain.ResponseValue;
import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.forms.data.repository.FormResponseCoordsDao;
import net.frontlinesms.plugins.forms.data.repository.FormResponseDao;
import net.frontlinesms.plugins.forms.data.repository.SurveyDao;
import net.frontlinesms.plugins.forms.data.repository.V_FieldPositionIndexDao;
import net.frontlinesms.plugins.forms.exceptions.ContactExistsException;
import net.frontlinesms.plugins.forms.exceptions.ContactFormOwnershipException;
import net.frontlinesms.plugins.forms.request.DataSubmissionRequest;
import net.frontlinesms.plugins.forms.request.FormsRequestDescription;
import net.frontlinesms.plugins.forms.request.NewFormRequest;
import net.frontlinesms.plugins.forms.request.SubmittedFormData;
import net.frontlinesms.plugins.forms.response.FormsResponseDescription;
import net.frontlinesms.plugins.forms.response.NewFormsResponse;
import net.frontlinesms.plugins.forms.response.SubmittedDataResponse;
import net.frontlinesms.plugins.forms.ui.DefaultComponentDescriptor;
import net.frontlinesms.plugins.forms.ui.FormsThinletTabController;
import net.frontlinesms.ui.Event;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;
import net.frontlinesms.ui.i18n.TextResourceKeyOwner;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Controller for the FrontlineForms plugin.
 *
 * @author Alex
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * @author UNOPS:Mureed Al-BArgouthi
 * www.fabaris.it <http://www.fabaris.it/>
 */
@PluginControllerProperties(name = "Forms", i18nKey = "plugins.forms.forms", iconPath = "/icons/form.png", springConfigLocation = "classpath:net/frontlinesms/plugins/forms/frontlineforms-spring-hibernate.xml", hibernateConfigPath = "classpath:net/frontlinesms/plugins/forms/frontlineforms.hibernate.cfg.xml")
@TextResourceKeyOwner
public class FormsPluginController extends BasePluginController implements IncomingMessageListener {

    String imagesPath = AppProperties.getInstance().getWebServerImagesPath();

    // > CONSTANTS
    /**
     * Filename and path of the XML for the FrontlineForms tab.
     */
    private static final String XML_FORMS_TAB = "/ui/plugins/forms/formsTab.xml";

    /**
     * I18n Text key: SMS text: "There is a new form available: xyz"
     */
    private static final String I18N_NEW_FORMS_SMS = "sms.form.available";
    private static final String I18N_NEW_FORMS_S_XForm = "sms.form.xform";
    private static final String I18N_FORM_RESPONSE_OK = "form.response.ok";
    private static final String I18N_FORM_RESPONSE_ERROR = "form.response.error";
    private static final String I18N_FORM_RESPONSE_EXIST = "form.response.exist";
    // > INSTANCE PROPERTIES
    /**
     * the {@link FrontlineSMS} instance that this plugin is attached to
     */
    private FrontlineSMS frontlineController;
    /**
     * the {@link FormsMessageHandler} for processing incoming and outgoing
     * messages
     */
    private FormsMessageHandler formsMessageHandler;
    /**
     * Fabaris_a.zanchi the {@link CustomFormsMessageHandler} for customized
     * processing of incoming and outgoing messages
     */
    private CustomFormsMessageHandler customFormsMessageHandler;
    /**
     * DAO for forms
     */
    private FormDao formDao;
    /**
     * DAO for contacts
     */
    private ContactDao contactDao;
    /**
     * DAO for form responses
     */
    private FormResponseDao formResponseDao;
    /**
     * Fabaris_a.zanchi DAO for Surveys
     */
    private SurveyDao surveyDao;
    /**
     * Fabaris_a.zanchi DAO for GroupMembership
     */
    private GroupMembershipDao groupMembershipDao;
    /**
     * Fabaris_a.aknai DAO for User
     */
    private UserDao userDao;
    private UiGeneratorController uiGenController;
    private V_FieldPositionIndexDao v_FieldPositionIndexDao;
    private FormResponseCoordsDao coordsDao;
	//private ConfigurationDao configurationDao;                     //<---------------- 17/10/2013

    // > CONFIG METHODS
    /**
     * @see PluginController#init(FrontlineSMS, ApplicationContext)
     */
    public void init(FrontlineSMS frontlineController, ApplicationContext applicationContext) throws PluginInitialisationException {
        this.frontlineController = frontlineController;
        this.contactDao = frontlineController.getContactDao();
        this.userDao = this.frontlineController.getUserDao();
        this.frontlineController.addIncomingMessageListener(this);
        /* Fabaris_a.zanchi: instantiates the CustomFormsMessageHandler */
        this.customFormsMessageHandler = new CustomFormsMessageHandler(this);
        this.surveyDao = frontlineController.getSurveyDao();
        this.groupMembershipDao = frontlineController.getGroupMembershipDao();
        try {
            this.formDao = (FormDao) applicationContext.getBean("formDao");
            this.formResponseDao = (FormResponseDao) applicationContext.getBean("formResponseDao");
            this.v_FieldPositionIndexDao = (V_FieldPositionIndexDao) applicationContext.getBean("vFieldDao");
            this.coordsDao = (FormResponseCoordsDao) applicationContext.getBean("formResponseCoordDao");

            //this.configurationDao = (ConfigurationDao)applicationContext.getBean("configurationDao");   //<------------------------ 17/10/2013
            String handlerClassName = FormsProperties.getInstance().getHandlerClassName();
            setHandler(handlerClassName);
        } catch (Throwable t) {
            log.warn("Unable to load form handler class.", t);
            throw new PluginInitialisationException(t);
        }
    }

    public void logInfo(String message) {
        log.trace(message);
    }

    void setHandler(String handlerClassName) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        this.formsMessageHandler = (FormsMessageHandler) Class.forName(handlerClassName).newInstance();
        this.formsMessageHandler.init(this);
    }

    // > ACCESSORS
    /**
     * @return {@link #formsMessageHandler}
     */
    public FormsMessageHandler getHandler() {
        return this.formsMessageHandler;
    }

    /**
     * @return {@link #frontlineController}
     */
    public FrontlineSMS getFrontlineController() {
        return this.frontlineController;
    }

    /**
     * @param frontlineController new value for {@link #frontlineController}
     */
    void setFrontlineController(FrontlineSMS frontlineController) {
        this.frontlineController = frontlineController;
    }

    void setFormsMessageHandler(FormsMessageHandler formsMessageHandler) {
        this.formsMessageHandler = formsMessageHandler;
    }

    /**
     * Set {@link #formResponseDao}
     *
     * @param formResponseDao new value for {@link #formResponseDao}
     */
    @Required
    public void setFormResponseDao(FormResponseDao formResponseDao) {
        this.formResponseDao = formResponseDao;
    }

    /**
     * Set {@link #formDao}
     *
     * @param formsDao new value for {@link #formDao}
     */
    @Required
    public void setFormsDao(FormDao formsDao) {
        this.formDao = formsDao;
    }

    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public SurveyDao getSurveyDao() {
        return this.surveyDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    /**
     * @see net.frontlinesms.plugins.PluginController#deinit()
     */
    public void deinit() {
        if (this.frontlineController != null) {
            this.frontlineController.removeIncomingMessageListener(this);
        }
    }

//	public ConfigurationDao getConfigurationDao(){
//		return this.configurationDao;
//	}
    /**
     * @see BasePluginController#initThinletTab(UiGeneratorController)
     */
    public Object initThinletTab(UiGeneratorController uiController) {
        FormsThinletTabController tabController = new FormsThinletTabController(this, uiController);
        tabController.setContactDao(this.frontlineController.getContactDao());
        tabController.setGroupMembershipDao(this.frontlineController.getGroupMembershipDao());
        tabController.setFormsDao(formDao);
        tabController.setFormResponseDao(formResponseDao);
        tabController.setContactDao(contactDao);

        //tabController.setConfigurationDao(configurationDao);			//<--------------------- 17/10/2013
        uiGenController = uiController;

        Object formsTab = uiController.loadComponentFromFile(XML_FORMS_TAB, tabController);
        tabController.setTabComponent(formsTab);

        tabController.refresh();

        return formsTab;
    }

    // > EVENT HANDLING METHODS
    /**
     * Process a new message coming into the system.
     */
    public void incomingMessageEvent(FrontlineMessage message) {
        try {
            /*
             * Commented by Fabaris_a.zanchi FormsRequestDescription request =
             * this.formsMessageHandler.handleIncomingMessage(message);
             */

            /*
             * Fabaris_a.zanchi call to the CustomFormsMessageHandler to do
             * custom processing on the message
             */
            FormsRequestDescription request = this.customFormsMessageHandler.handleIncomingMessage(message);

            FormsResponseDescription response;
            if (request instanceof DataSubmissionRequest) {
                response = handleDataSubmissionRequest((DataSubmissionRequest) request, message.getSenderMsisdn());
            } else if (request instanceof NewFormRequest) {
                response = handleNewFormRequest((NewFormRequest) request, message.getSenderMsisdn());
            } else {
                throw new IllegalStateException("Unknown form request description type: " + request);
            }

            // If there is a response to send back to the form submitter, then
            // process it
            if (response != null) {
                handleResponse(request.getSmsPort(), message.getSenderMsisdn(), response);
            }
        } catch (Throwable t) {
            log.info("There was a problem handling incoming message as forms message.", t);
        }
    }

    public String incomingHTTPResponse(String phone, String data) {
        // System.out.println("FormsPluginController.incomingHTTPResponse()");
        FrontlineMessage message = FrontlineMessage.createIncomingMessage(System.currentTimeMillis(), phone, "", data);
        // System.out.println("FormsPluginController.incomingHTTPResponse().message:"+message);
        try {
            FormsRequestDescription request = this.customFormsMessageHandler.handleIncomingMessage(message);
            FormsResponseDescription response = null;
            // System.out.println("request:"+request);
            if (request instanceof DataSubmissionRequest) {
                response = handleDataSubmissionRequest((DataSubmissionRequest) request, message.getSenderMsisdn());
            }
            // If there is a response to send back to the form submitter, then
            // process it
            if (response != null) {
                Collection<FrontlineMessage> messages = this.customFormsMessageHandler.handleOutgoingMessage(response);
                if (messages.size() == 0) {
                    return null;
                }
                for (FrontlineMessage m : messages) {
                    String resp = m.getTextContent();
                    String description = InternationalisationUtils.getI18nString(I18N_FORM_RESPONSE_OK);
                    uiGenController.getHomeTabController().newEvent(new Event(Event.TYPE_HTTP_RESPONSE_OK, description));
                    return resp;
                }
            }
        } catch (FormHandlingException e) {
            uiGenController.getHomeTabController().newEvent(
                    new Event(Event.TYPE_HTTP_RESPONSE_ERROR, InternationalisationUtils.getI18nString(I18N_FORM_RESPONSE_ERROR, e.getMessage())));
            return e.getMessage();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // System.err.println("Error parsing incoming message!!");
            e.printStackTrace();
            uiGenController.getHomeTabController().newEvent(
                    new Event(Event.TYPE_HTTP_RESPONSE_ERROR, InternationalisationUtils.getI18nString(I18N_FORM_RESPONSE_ERROR, e.getMessage())));
            return e.getMessage();
        }
        return null;
    }

    /* default access to allow for unit test */
    void handleResponse(Integer smsPort, String senderMsisdn, FormsResponseDescription response) throws FormHandlingException {
        /*
         * Commented by Fabaris_a.zanchi Collection<FrontlineMessage>
         * responseMessages =
         * this.formsMessageHandler.handleOutgoingMessage(response);
         */

        /* Call to the CustomFormsMessageHandler */
        Collection<FrontlineMessage> responseMessages = this.customFormsMessageHandler.handleOutgoingMessage(response);
        log.info("Sending forms response.  Response messages: " + responseMessages.size());
        for (FrontlineMessage responseMessage : responseMessages) {
            // Make sure that the response is sent to the correct recipient!
            responseMessage.setRecipientMsisdn(senderMsisdn);
            if (smsPort != null) {
                responseMessage.setRecipientSmsPort(smsPort);
            }
            this.frontlineController.sendMessage(responseMessage);
        }
        log.trace("Response messages sent.");
    }

    // > PRIVATE HELPER METHODS
    /**
     * Handles a request of type: {@link DataSubmissionRequest}
     *
     * @param request
     * @param senderMsisdn
     * @return a response of type {@link SubmittedDataResponse}
     */
    /* default access to allow for unit test */
    SubmittedDataResponse handleDataSubmissionRequest(DataSubmissionRequest request, String senderMsisdn) {
        /**
         * List of data IDs of the successfully processed responses
         */
        Collection<SubmittedFormData> dataIds = new HashSet<SubmittedFormData>();
        long id = 0;
        for (SubmittedFormData submittedData : request.getSubmittedData()) {

            log.trace("In handle Data Submission Request ............................ ");

            Form form = this.formDao.getFromId_IdMac(submittedData.getFormId_IdMac());
            if (form == null) {
                log.warn("No form found for submitted data with dataId: " + submittedData.getDataId());
                continue;
            }

            List<ResponseValue> responseValues = submittedData.getDataValues();
            if (form.getEditableFieldCount() != responseValues.size()) {
                log.info("Editable field count mismatch: submitted " + responseValues.size() + "/" + form.getEditableFieldCount());
                continue;
            }

            // Commented by Fabaris_a.zanchi
            // this.formResponseDao.saveResponse(new FormResponse(senderMsisdn,
            // form, responseValues));
            /**
             * Modified by mureed Check the images in submitted form
             */
            FormResponse newResponse = new FormResponse(senderMsisdn, form, responseValues, submittedData.getClientVersion());
            // Fabaris_a.zanchi puts in the FormResponse the identificator
            // retrieved from message
            newResponse.setCode_Form(request.getFormResponseName());
            FormResponse responseOnDB = this.formResponseDao.getFormResponse(newResponse.getCode_Form());
            if (responseOnDB != null) {
                this.uiGenController.getHomeTabController().newEvent(new Event(Event.TYPE_HTTP_RESPONSE_ERROR, InternationalisationUtils.getI18nString(I18N_FORM_RESPONSE_EXIST)));
                dataIds.add(submittedData);
                continue;
            }
            //Save the response and return the entity
            //this.formResponseDao.saveResponse(newResponse);
            FormResponse entityStored = this.formResponseDao.saveResponseAndReturnEntity(newResponse);

            long formResponseId = entityStored.getId();

            log.trace("image check ............................ ");
            String imageValue = "";

            String instancePath = imagesPath + System.getProperty("file.separator") + formResponseId;
            for (ResponseValue responseValue : responseValues) {
                if (responseValue.isIsImage()) {
                    imageValue = responseValue.toString();
                    try {
                        File instanceDirectory = new File(instancePath);
                        if (!instanceDirectory.exists()) {
                            if (instanceDirectory.mkdir()) {

                            } else {
                                log.error("Failed to create instance directory ");
                            }
                        }
                        convertByteImageToFile(Base64.decodeBase64(responseValue.getTempValue()), instancePath + System.getProperty("file.separator") + imageValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            log.trace("create image file from here ............................ ");

            List<ResponseValue> resValues = entityStored.getResults();
            for (ResponseValue resVal : resValues) {
                resVal.setFormResponseID(formResponseId);
                if (resVal.getRVRepeatCount() == -1) {
                    List<ResponseValue> repeatableValues = resVal.getRepetablesValues();
                    for (ResponseValue repVal : repeatableValues) {
                        repVal.setFormResponseID(formResponseId);
                    }
                }
            }
            this.formResponseDao.updateResponse(entityStored);

            id = entityStored.getId();

            int positionIndex = this.v_FieldPositionIndexDao.getGeoPositionIndexByFormId(entityStored.getParentForm().getId(), "GEOLOCATION");
            String geoValue = "";
            if (positionIndex != -1) {
                for (ResponseValue responseValue : responseValues) {
                    if (responseValue.getPositionIndex() == positionIndex) {
                        geoValue = responseValue.toString();
                        break;
                    }
                }
                System.out.println("11111111111111111111111111111111111 ");
                this.coordsDao.insertResponseCoords(entityStored.getId(), geoValue);
            }

            this.formDao.finaliseForm(form);
            dataIds.add(submittedData);
        }

        SubmittedDataResponse ret = new SubmittedDataResponse(dataIds);
        ret.setFormIdentificator(request.getFormIdentificator());
        ret.setFormResponseName(request.getFormResponseName());
        ret.setFormResponceId(id);
        return ret;
    }

    /**
     * Handles a request of type {@link NewFormRequest}
     *
     * @param request
     * @param message
     * @return a response of type {@link NewFormsResponse}, or <code>null</code>
     * if no response should be sent.
     */
    /* default access to allow for unit test */
    NewFormsResponse handleNewFormRequest(NewFormRequest request, String senderMsisdn) {
        Contact contact = this.contactDao.getFromMsisdn(senderMsisdn);
        if (contact == null || !contact.isActive()) {
            // This contact is not known, so there cannot be any forms available
            // for him
            return null;
        } else {
            Collection<Form> newForms = this.formDao.getFormsForUser(contact, ((NewFormRequest) request).getCurrentFormIds());
            return new NewFormsResponse(contact, newForms);
        }
    }

    /**
     * Send a form to a collection of contacts.
     *
     * @param form the form to send
     * @param contacts the contacts to send the form to
     */
    // public void sendForm(Form form, Collection<Contact> contacts) {
    /**
     * Aggiunto da FABARIS_maria .
     *
     * @return
     */
    public int sendForm(Form form, Collection<Contact> contacts, List<String> xform) {
        // Send a text SMS to each contact informing them that a new form is
        // available.
        int sentCount = 0;
        String messageContent = InternationalisationUtils.getI18nString(I18N_NEW_FORMS_SMS, form.getName());
        System.out.println("26-aprile=" + messageContent);
        /**
         * Fabaris_maria c.
         */
        String xf = xform.get(0);
        // xf = xf.replace("[[", "");
        // xf = xf.replace("]]", "");
        // xf = xf.replace(", ", "");
        xf = xf.trim();
        // System.out.println("XF:"+xf);
        String strversionflsms = new String();
        try {
            // Fabaris_a.zanchi the only change here is calling removeEmptyGroup
            // on the xform
            strversionflsms = removeEmptyGroups(insertSoftwareVersionNumber(xf, form));
        } catch (Exception e1) {
            e1.printStackTrace();
            log.error(e1.getMessage());
            JOptionPane.showMessageDialog(null,
                    InternationalisationUtils.getI18nString(FormsThinletTabController.I18N_FORM_COMPONENTS_ERROR),
                    "",
                    JOptionPane.ERROR_MESSAGE);
            return sentCount;
        }
        // concatenazione dei due messaggi per 'l invio dei form
        String x_forms = messageContent + "<__>" + strversionflsms;
        System.out.println("----------- complete message --------------");
        System.out.println(x_forms);
        /**
         * maria c.
         */
        for (Contact c : contacts) {
            // TODO if it is possible, we could send forms directly to people
            // here
            // this.frontlineController.sendTextMessage(c.getPhoneNumber(),
            // messageContent);
            /**
             * Fabaris_maria c.
             */
            sentCount++;
            this.frontlineController.sendTextMessage(c.getPhoneNumber(), x_forms);
            /**
             * maria c.
             */
        }
        return sentCount;
    }

    /**
     * @return The {@link FormDao of the plugin controller}
     */
    public FormDao getFormDao() {
        return formDao;
    }

    /**
     * Fabaris_a.zanchi This method extracts the Form Id from the "id" attribute
     * of <data> tag
     *
     * @param xmlContent String who contains the xml
     * @return a String representing the form ID
     * @throws Exception
     */
    public static String getFormIdFromXml(String xmlContent) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        InputSource inStream = new InputSource();
        inStream.setCharacterStream(new StringReader(xmlContent));
        org.w3c.dom.Document xmlDoc = docBuilder.parse(inStream);
        xmlDoc.getDocumentElement().normalize();
        NodeList nodeList = xmlDoc.getElementsByTagName("id");
        Node dataNode = nodeList.item(0);
        Element elem = (Element) dataNode;
        // String id = elem.getAttribute("id");
        String id = elem.getFirstChild().getNodeValue();
        return id;

    }

    /**
     * Fabaris_a.zanchi This method extracts from the xml the value of a
     * FormField
     *
     * @param xmlContent String containing the entire xml
     * @param formField object of type {@link FormField}
     * @return a String with the value of the respective FormField extracted
     * from the xmlConent
     * @throws Exception
     */
    public static String getFormDataXml(String xmlContent, FormField formField) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        InputSource inStream = new InputSource();
        inStream.setCharacterStream(new StringReader(xmlContent));
        org.w3c.dom.Document xmlDoc = docBuilder.parse(inStream);
        xmlDoc.getDocumentElement().normalize();
        String fieldValue = null;
        if (formField.getType() != FormFieldType.REPEATABLES) {
            if (formField.getType() != FormFieldType.CURRENCY_FIELD) {
                //30.10.2013
                //NodeList nodeList = xmlDoc.getElementsByTagName(String.valueOf(formField.getId()).trim() + "_" + formField.getPositionIndex());

                NodeList nodeList = xmlDoc.getElementsByTagName(formField.getName().trim() + "_" + formField.getPositionIndex());
                if (nodeList.getLength() == 0) // check if the field exsists in xml
                {
                    return "";
                }
                Node dataNode = nodeList.item(0);
                Element elem = (Element) dataNode;
                if (elem.getFirstChild() == null) {
                    return "";
                }
                fieldValue = elem.getFirstChild().getNodeValue();
            } else {
                // search for import value
                //30.10.2013
                //NodeList nodeList = xmlDoc.getElementsByTagName(String.valueOf(formField.getId()).trim() + "_" + formField.getPositionIndex());

                NodeList nodeList = xmlDoc.getElementsByTagName(formField.getName().trim() + "_" + formField.getPositionIndex());
                if (nodeList.getLength() == 0) // check if the field exsists in
                // xml
                {
                    return "";
                }
                Node dataNode = nodeList.item(0);
                Element elem = (Element) dataNode;
                if (elem.getFirstChild() == null) {
                    return "";
                }
                fieldValue = elem.getFirstChild().getNodeValue();
                // Search for currency
                //30.10.2013
                //NodeList nodeList2 = xmlDoc.getElementsByTagName(String.valueOf(formField.getId()).trim() + "_" + formField.getPositionIndex() + "_curr");

                NodeList nodeList2 = xmlDoc.getElementsByTagName(formField.getName().trim() + "_" + formField.getPositionIndex() + "_curr");
                Node dataNode2 = nodeList2.item(0);
                Element currencyElem = (Element) dataNode2;
                if (currencyElem.getFirstChild() != null) {
                    fieldValue = fieldValue + currencyElem.getFirstChild().getNodeValue();
                }
            }
        } else {
            // In this case we have to search "deeper"
            //30.10.2013
            //NodeList nodeList = xmlDoc.getElementsByTagName(String.valueOf(formField.getId()).trim() + "_" + formField.getPositionIndex());

            NodeList nodeList = xmlDoc.getElementsByTagName(formField.getName().trim() + "_" + formField.getPositionIndex());
            Node dataNode = nodeList.item(0);
            Element elem = (Element) dataNode;
            //30.10.2013
            //NodeList repNodeList = elem.getElementsByTagName(formField.getId_flsmsId().trim());

            NodeList repNodeList = elem.getElementsByTagName(formField.getName().trim());
            Node repNode = repNodeList.item(0);
            Element repEelem = (Element) repNode;
            if (repEelem.getFirstChild() == null) {
                return "";
            }
            fieldValue = repEelem.getFirstChild().getNodeValue();
        }
        return fieldValue;
    }

    /**
     * Fabaris_a.zanchi Static utility method to extract tag contents from a xml
     *
     * @param xmlContent The xml conent
     * @param tag the specified xml tag we want to fetch contents from
     * @return a List of Strings with the tags' content
     */
    public static List<String> getTagContentFromXml(String xmlContent, String tag) throws Exception {
        List<String> ret = new ArrayList<String>();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        InputSource inStream = new InputSource();
        inStream.setCharacterStream(new StringReader(xmlContent));
        org.w3c.dom.Document xmlDoc = docBuilder.parse(inStream);
        xmlDoc.getDocumentElement().normalize();
        if (!tag.equals("client_version")) {
            NodeList nodeList = xmlDoc.getElementsByTagName(tag);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node dataNode = nodeList.item(i);
                Element elem = (Element) dataNode;
                ret.add(elem.getFirstChild().getNodeValue());
            }
        } else {
            // Search for client version not knowing its index
            NodeList dataNode = xmlDoc.getElementsByTagName("data");
            Element dataElem = (Element) dataNode.item(0);
            NodeList allNodes = dataElem.getChildNodes();
            for (int i = 0; i < allNodes.getLength(); i++) {
                if (allNodes.item(i).getNodeName().contains("client_version")) {
                    ret.add(allNodes.item(i).getTextContent());
                }
            }
        }
        return ret;
    }

    /**
     * Fabaris_a.zanchi This method inserts the designer software version number
     * in the outgoing xform It also creates an empty tag for the client version
     *
     * @param xmlContent the string representation of the xform
     */
    public static String insertSoftwareVersionNumber(String xmlContent, Form form) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        InputSource inStream = new InputSource();
        inStream.setCharacterStream(new StringReader(xmlContent));
        org.w3c.dom.Document xmlDoc = docBuilder.parse(inStream);
        xmlDoc.getDocumentElement().normalize();
        Node versionNode = xmlDoc.getElementsByTagName(DefaultComponentDescriptor.DESINGER_VERSION_TAGNAME + "_" + DefaultComponentDescriptor.DESIGNER_VERSION_INDEX).item(0);
        if (versionNode != null) {
            versionNode.setTextContent(form.getDesignerVersion());
        }
        /*
         * NodeList nodeList = xmlDoc.getElementsByTagName("h:head"); Node
         * headNode = nodeList.item(0); Node modelNode =
         * xmlDoc.getElementsByTagName("model").item(0); Element newElemDesigner
         * = xmlDoc.createElement("designer_version"); Element newElemClient =
         * xmlDoc.createElement("client_version"); Node newNodeDesigner = (Node)
         * newElemDesigner; Node newNodeClient = (Node) newElemClient; String
         * designerVersion = form.getDesignerVersion(); if (designerVersion ==
         * null) designerVersion = "";
         * newNodeDesigner.appendChild(xmlDoc.createTextNode(designerVersion));
         * newNodeClient.appendChild(xmlDoc.createTextNode(""));
         * headNode.insertBefore(newNodeDesigner, modelNode);
         * headNode.insertBefore(newNodeClient, modelNode);
         */
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer trans = tFactory.newTransformer();
        trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        StringWriter sWriter = new StringWriter();
        Result result = new StreamResult(sWriter);
        trans.transform(new DOMSource(xmlDoc), result);
        String result2 = sWriter.getBuffer().toString();
        System.out.println("-------- inserting version ------------");
        System.out.println(result2);
        return result2;
    }

    /**
     * Fabaris_a.zanchi Method to extract values form repeatable components
     *
     * @param textContent the xml message
     * @param ff the formfield of the container object
     * @param formField the formfield of repeatable object
     * @param index the number of repetition currently ectracting
     * @return the value of the component in xml
     * @throws Exception
     *//*
     public static String getFormRepeatableDataXml(String xmlContent, FormField containerFf, FormField childFormField, int index) throws Exception {
     DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
     DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
     InputSource inStream = new InputSource();
     inStream.setCharacterStream(new StringReader(xmlContent));
     org.w3c.dom.Document xmlDoc = docBuilder.parse(inStream);
     xmlDoc.getDocumentElement().normalize();
     NodeList nodeList = xmlDoc.getElementsByTagName(containerFf.getName().trim() + "_" + containerFf.getPositionIndex());
     Node dataNode = nodeList.item(0);
     Element elem = (Element) dataNode;
     NodeList childNodeList = elem.getElementsByTagName(childFormField.getName().trim() + "_" + index);
     Node childNode = childNodeList.item(0);
     Element childEelem = (Element) childNode;
     if (childEelem.getFirstChild() == null)
     return null;
     return childEelem.getFirstChild().getNodeValue();
     }*/

    /**
     * Fabaris_a.zanchi Method to remove empty groups for outgoing messages so
     * that mobile tool doesn't crash
     *
     * @param content an xml document (just xml without any additional content)
     * @return xml document without empty groups with attribute
     * appearance="field-list"
     * @throws Exception
     */
    public static String removeEmptyGroups(String content) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        InputSource inStream = new InputSource();
        inStream.setCharacterStream(new StringReader(content));
        org.w3c.dom.Document xmlDoc = docBuilder.parse(inStream);
        xmlDoc.getDocumentElement().normalize();
        NodeList groupList = xmlDoc.getElementsByTagName("group");
        int i = 0;
        while (i < groupList.getLength()) {
            Element group = (Element) groupList.item(i);
            if (group.getAttribute("appearance").equals("field-list")) {
                if (group.getChildNodes().getLength() == 0) {
                    groupList.item(i).getParentNode().removeChild(groupList.item(i));
                    i--;
                }
                if (group.getTextContent().equals(", ")) {
                    groupList.item(i).getParentNode().removeChild(groupList.item(i));
                    i--;
                }
            }
            i++;
        }
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer trans = tFactory.newTransformer();
        trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        StringWriter sWriter = new StringWriter();
        Result result = new StreamResult(sWriter);
        trans.transform(new DOMSource(xmlDoc), result);
        String result2 = sWriter.getBuffer().toString();
        System.out.println("---------- remove empty groups ---------");
        System.out.println(result2);
        return result2;
    }

    /**
     * Fabaris_a.zanchi Counts the actual repetitions of a repeatable section
     *
     * @param xmlContent xml message
     * @param repContainerName the name of the repeatables container
     * @return count of repetitions
     * @throws Exception
     */
    public static int countRepetitionOfRepeatable(String xmlContent, String repContainerName) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        InputSource inStream = new InputSource();
        inStream.setCharacterStream(new StringReader(xmlContent));
        org.w3c.dom.Document xmlDoc = docBuilder.parse(inStream);
        xmlDoc.getDocumentElement().normalize();
        NodeList repetitionList = xmlDoc.getElementsByTagName(repContainerName);
        return repetitionList.getLength();
    }

    /**
     * Fabaris_a.zanchi Gets values of items in repeatable container for a given
     * repetition
     *
     * @param xmlContent xml message
     * @param repContainerName name of the repeatables container
     * @param repeatables List of FormField contained in the repetition
     * @param repetitionNumber the repetition instance (it starts from 0 not
     * from 1)
     * @return List of values for the given repetition
     * @throws Exception
     */
    public static List<String> getValuesForRepetition(String xmlContent, String repContainerName, List<FormField> repeatables, int repetitionNumber) throws Exception {
        List<String> ret = new ArrayList<String>();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        InputSource inStream = new InputSource();
        inStream.setCharacterStream(new StringReader(xmlContent));
        org.w3c.dom.Document xmlDoc = docBuilder.parse(inStream);
        xmlDoc.getDocumentElement().normalize();
        NodeList repetitionList = xmlDoc.getElementsByTagName(repContainerName);
        Node givenRepetition = null;
        try {
            givenRepetition = repetitionList.item(repetitionNumber);
        } catch (Exception e) {
            for (FormField rep : repeatables) {
                ret.add("");
            }
            return ret;
        }
        Element repElement = (Element) givenRepetition;
        for (int i = 0; i < repeatables.size(); i++) {
            if (repeatables.get(i).getType() == FormFieldType.CURRENCY_FIELD) {
                String nodename = repeatables.get(i).getName() + "_curr";
                try {
                    String currency = repElement.getElementsByTagName(nodename).item(0).getTextContent();
                    ret.add(repElement.getElementsByTagName(repeatables.get(i).getName()).item(0).getTextContent() + currency);
                } catch (Exception e) {
                    ret.add("");
                }

            } else {
                try {
                    String value = repElement.getElementsByTagName(repeatables.get(i).getName()).item(0).getTextContent();
                    if (repeatables.get(i).getType() == FormFieldType.CHECK_BOX && value.equals("")) {
                        ret.add("false");
                    } else {
                        ret.add(repElement.getElementsByTagName(repeatables.get(i).getName()).item(0).getTextContent());
                    }
                } catch (Exception e) {
                    ret.add("");
                }
            }
        }
        return ret;
    }

    /**
     * Fabaris_a.zanchi Gets values of items in repeatable container in a
     * certain survey value section
     *
     * @param xmlContent xml message
     * @param repContainerName name of the repeatables container
     * @param surveySection name of the current survey section
     * @param repeatables List of FormField contained in the repetition
     * @return List of values for the given repetition (identified by surver
     * section name)
     * @throws Exception
     */
    public static List<String> getValuesForRepetitionWithSurvey(String xmlContent, String repContainerName, String surveySection, List<FormField> repeatables) throws Exception {
        List<String> ret = new ArrayList<String>();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        InputSource inStream = new InputSource();
        inStream.setCharacterStream(new StringReader(xmlContent));
        org.w3c.dom.Document xmlDoc = docBuilder.parse(inStream);
        xmlDoc.getDocumentElement().normalize();
        NodeList repetitionNodeList = xmlDoc.getElementsByTagName(repContainerName);
        if (repetitionNodeList.getLength() == 0) { // check if the repetition
            // exissts
            for (FormField ff : repeatables) {
                ret.add("");
            }
            return ret;
        }
        Element repetitionNode = (Element) xmlDoc.getElementsByTagName(repContainerName).item(0);
        Element surveyNode = (Element) repetitionNode.getElementsByTagName(surveySection).item(0);
        for (FormField ff : repeatables) {
            if (ff.getType() == FormFieldType.CURRENCY_FIELD) {
                String nodename = ff.getName() + "_curr";
                try {
                    String currency = surveyNode.getElementsByTagName(nodename).item(0).getTextContent();
                    ret.add(surveyNode.getElementsByTagName(ff.getName()).item(0).getTextContent() + currency);
                } catch (Exception e) {
                    ret.add("");
                }
            } else {
                try {
                    String value = surveyNode.getElementsByTagName(ff.getName()).item(0).getTextContent();
                    if (ff.getType() == FormFieldType.CHECK_BOX && value.equals("")) {
                        ret.add("false");
                    } else {
                        ret.add(surveyNode.getElementsByTagName(ff.getName()).item(0).getTextContent());
                    }
                } catch (Exception e) {
                    ret.add("");
                }
            }
        }
        return ret;

    }

    public static boolean isInList(List<String> list, String obj) {
        for (String item : list) {
            if (item.equals(obj)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAssignedToForm(String phone, Form f) {
        Contact c = FormsThinletTabController.getCurrentInstance().getPluginController().contactDao.getFromMsisdn(phone);
        if (c == null) {
            return false;
        }
        Group g = f.getPermittedGroup();
        if (g == null) {
            return false;
        }
        List<Contact> contacts = FormsThinletTabController.getCurrentInstance().getPluginController().groupMembershipDao.getActiveMembers(g);
        for (Contact listContact : contacts) {
            if (listContact.equals(c)) {
                return true;
            }
        }
        return false;
    }

    public static String formToXForm(Form f) {
        String xform = new String();
        for (FormField ff : f.getFields()) {
            if (ff.getX_form() != null) {
                xform += ff.getX_form();
            }
        }

        return xform;
    }

    /**
     * Fabaris_a.zanchi method that checks if a contact and a form are
     * associated
     *
     * @param formId identificator of the form. Currently the id_idfsms of the
     * form
     * @param telephone the phone number of the sender
     * @throws ContactExistsException throw if the contact doesn't exist in the
     * database
     * @throws ContactFormOwnershipException throw if contact and form are not
     * associated
     */
    public void checkContactFormOwnership(String formId, String telephone) throws ContactExistsException, ContactFormOwnershipException {
        Contact c = contactDao.getFromMsisdn(telephone);
        if (c == null) {
            throw new ContactExistsException("The contact with phone number: " + telephone + " does not exists on local database");
        }
        Form incoming = formDao.getFromId_IdMac(formId);
        Group permittedGroup = incoming.getPermittedGroup();
        List<Contact> contacts = groupMembershipDao.getActiveMembers(permittedGroup);
        if (!contacts.contains(c)) {
            throw new ContactFormOwnershipException("The contact with phone number: " + telephone + " is not associated with the incoming form with id_idMac: " + formId);
        }

    }

    private void convertByteImageToFile(byte[] imageByte, String path) throws Exception {
        System.out.println("path ...... convertByteImageToFile ......... "+path);
        
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        Iterator<?> readers = ImageIO.getImageReadersByFormatName("jpg");

        ImageReader reader = (ImageReader) readers.next();
        Object source = bis;
        ImageInputStream iis = ImageIO.createImageInputStream(source);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();

        Image image = reader.read(0, param);
        //got an image file

        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        //bufferedImage is the RenderedImage to be written

        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, null, null);

        File imageFile = new File(path);
        ImageIO.write(bufferedImage, "jpg", imageFile);
        System.out.println(" end write image ");
    }
}

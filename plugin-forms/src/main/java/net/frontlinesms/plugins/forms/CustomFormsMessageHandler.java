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

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.forms.data.FormHandlingException;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.ResponseValue;
import net.frontlinesms.plugins.forms.data.domain.SurveyElement;
import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.forms.exceptions.ContactExistsException;
import net.frontlinesms.plugins.forms.exceptions.ContactFormOwnershipException;
import net.frontlinesms.plugins.forms.request.DataSubmissionRequest;
import net.frontlinesms.plugins.forms.request.FormsRequestDescription;
import net.frontlinesms.plugins.forms.request.SubmittedFormData;
import net.frontlinesms.plugins.forms.request.SyncFormsRequest;
import net.frontlinesms.plugins.forms.response.FormsResponseDescription;
import net.frontlinesms.plugins.forms.response.SubmittedDataResponse;

/**
 * This class implements the interface FormsMessageHandler. It is designed to
 * process non-base64 form's contents.
 *
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>
 */
public class CustomFormsMessageHandler implements FormsMessageHandler {

    // Separator for form identificator at the end of message content (outside
    // xml)
    private final static String IDENTIFICATOR_SEPARATOR = "?formidentificator?";
    private final static String FORMNAME_IDENTIFICATOR = "?formname?";
    private final static String FORMHOUR_IDENTIFICATOR = "?formhour?";
    FormsPluginController formsController;

    // Form identificator
    // private String formIdentificator = null;
    public FormsRequestDescription handleIncomingMessage(
            FrontlineMessage message) throws FormHandlingException {
        System.out.println("The message length is " + (message.getTextContent().getBytes().length) + " bytes");
        System.out.println("handleIncomingMessage");
        String formIdentificator = null;
        String formName = null;
        // First extract the form identificator
        if (IDENTIFICATOR_SEPARATOR != null) {
            int indexCutBegin = message.getTextContent().indexOf(IDENTIFICATOR_SEPARATOR);
            int indexCutBeginformname = message.getTextContent().indexOf(FORMNAME_IDENTIFICATOR);
            // Fabaris a.aknai
            // check if identifier is found.
            // if not try to return FormsSyncRequest
            if (indexCutBegin < 0) {
                try {
                    return handleSyncMessage(message);
                } catch (Exception e) {
                    throw new FormHandlingException(e.getMessage());
                }
            }
            int indexCutEnd = message.getTextContent().indexOf(FORMNAME_IDENTIFICATOR);
            int indexCutEnd1 = message.getTextContent().indexOf(FORMHOUR_IDENTIFICATOR);
            // Cuts everything after ?formname? included
            String temp = message.getTextContent().substring(0, indexCutEnd);
            String tempformname = message.getTextContent().substring(0, indexCutEnd1);
            formIdentificator = temp.substring(indexCutBegin + IDENTIFICATOR_SEPARATOR.length());
            formName = tempformname.substring(indexCutBeginformname + FORMNAME_IDENTIFICATOR.length());
            // Cut identificator stuff from textcontent
            System.out.println("indexCutEnd: " + indexCutEnd + " temp: " + temp + " formIdentificator: " + formIdentificator + " formName: " + formName);
            message.setTextMessageContent(message.getTextContent().substring(0, indexCutBegin));
        }

        Set<SubmittedFormData> submittedFormSet = new HashSet<SubmittedFormData>();
        List<ResponseValue> dataValues = new ArrayList<ResponseValue>();
        // Long formId = null;
        String formId = null;
        try {
            formId = formsController.getFormIdFromXml(message.getTextContent());
        } catch (NumberFormatException e) {
            throw new FormHandlingException(e.getMessage());
        } catch (Exception e) {
            throw new FormHandlingException(e.getMessage());
        }

        // check contact - form associations
        try {
            formsController.checkContactFormOwnership(formId, message.getSenderMsisdn());
        } catch (ContactExistsException e1) {
            throw new FormHandlingException(e1.getMessage());
        } catch (ContactFormOwnershipException e1) {
            throw new FormHandlingException(e1.getMessage());
        }

        Form returnedForm = formsController.getFormDao().getFromId_IdMac(formId);

        // Start to parse the entire returned values
        for (FormField ff : returnedForm.getFields()) {
            if (ff.getType() == FormFieldType.SEPARATOR) {
                // Separator are not taken in consideration as they are not
                // Response
                // dataValues.add(new ResponseValue(""));
            } else if ((ff.getType() == FormFieldType.TRUNCATED_TEXT) || (ff.getType() == FormFieldType.WRAPPED_TEXT)) {
                // TruncatedText are not taken in consideration as they are not
                // a Response
                // dataValues.add(new ResponseValue(""));
            } else if (ff.getType() == FormFieldType.REPEATABLES_BASIC) {
				// manage repeatables without survey

                // Response Value for repeatables container. Writes the number
                // of repetitions (could be useful)
                ResponseValue containerValue = null;
                int numberOfReps;
                try {
                    // 30.10.2013
                    // numberOfReps =
                    // FormsPluginController.countRepetitionOfRepeatable(message.getTextContent(),
                    // ff.getId() + "_" + ff.getPositionIndex());

                    numberOfReps = FormsPluginController.countRepetitionOfRepeatable(message.getTextContent(), ff.getName() + "_" + ff.getPositionIndex());
                } catch (Exception e) {
                    throw new FormHandlingException(e.getMessage());
                }

                containerValue = new ResponseValue(String.valueOf(numberOfReps));  //Numero di compilazioni del Roaster
                containerValue.setFormFieldId((int) ff.getId());
                containerValue.setRVRepeatCount(-1);
                containerValue.setPositionIndex(ff.getPositionIndex());

                if (numberOfReps != 0) {

                    /*
                     * // count number of elements with a value int j = 0; for
                     * (FormField rep : ff.getRepetables()) { if
                     * (rep.getType().hasValue()) j++; } containerValue = new
                     * ResponseValue(String.valueOf(numberOfReps * j));
                     */
                    // populate repeatable items values
                    int i = 0;
                    List<FormField> repeatableSection = ff.getRepetables();
                    while (i < numberOfReps) {
                        List<String> repValues = null;
                        try {
                            // 31.10.2013
                            // repValues =
                            // FormsPluginController.getValuesForRepetition(message.getTextContent(),
                            // ff.getId() + "_" + ff.getPositionIndex(),
                            // repeatableSection, i);

                            repValues = FormsPluginController.getValuesForRepetition(message.getTextContent(), ff.getName() + "_" + ff.getPositionIndex(), repeatableSection, i);
                        } catch (Exception e) {
                            throw new FormHandlingException(e.getMessage());
                        }
                        int n = 0;
                        for (String val : repValues) {
                            ResponseValue responseValue = new ResponseValue(val);
                            responseValue.setPositionIndex(ff.getPositionIndex());
                            int formFieldID = (int) repeatableSection.get(n).getId();
                            responseValue.setFormFieldId(formFieldID);

                            /*if (formId != null) {
                             if (formId.contains("_")) {
                             String[] formIdArray = formId.split("_");
                             try {
                             responseValue.setFormResponseID(Integer.parseInt(formIdArray[0]));
                             } catch (Exception e) {
                             throw new NumberFormatException(e.getMessage());
                             }

                             }
                             }*/
                            responseValue.setRVRepeatCount(i + 1);
                            containerValue.addRepetableValue(responseValue);
                            n++;
                        }
                        i++;
                    }
                } else {
                    //containerValue = new ResponseValue("");
                }
                dataValues.add(containerValue);

            } else if (ff.getType() == FormFieldType.REPEATABLES) {
                // manage repeatables with survey  ....AKA TABLES

                /*
                 * // count number of elements with a value */
                int j = 0;
                for (FormField rep : ff.getRepetables()) {
                    if (rep.getType().hasValue()) {
                        j++;
                    }
                }
                List<FormField> repeatableSection = ff.getRepetables();
				  //ResponseValue containerValueTmp = new  ResponseValue(String.valueOf(ff.getSurvey().getValues().size() * j));

                // ResponseValue containerValue = new
                // ResponseValue(String.valueOf(ff.getSurvey().getValues().size()));
                ResponseValue containerValue = new ResponseValue(ff);
                containerValue.setFormFieldId((int) ff.getId());
                containerValue.setRVRepeatCount(-1);
                containerValue.setPositionIndex(ff.getPositionIndex());

                //Survey survey = ff.getSurvey();
                int i = 0;
                for (SurveyElement surveySection : ff.getSurvey().getValues()) {
                    List<String> repValues = null;
                    try {
                        repValues = FormsPluginController.getValuesForRepetitionWithSurvey(message.getTextContent(), ff.getName() + "_" + ff.getPositionIndex(), "item" + surveySection.getPositionIndex(), ff.getRepetables());

                        // 30.10.2013
                        // repValues =
                        // FormsPluginController.getValuesForRepetitionWithSurvey(message.getTextContent(),
                        // ff.getId() + "_" + ff.getPositionIndex(),
                        // "item"+surveySection.getPositionIndex(),
                        // ff.getRepetables());
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new FormHandlingException(e.getMessage());
                    }
                    int n = 0;
                    for (String val : repValues) {
                        ResponseValue responseValue = new ResponseValue(val);
                        responseValue.setPositionIndex(ff.getPositionIndex());

                        int formFieldID = (int) repeatableSection.get(n).getId();
                        responseValue.setFormFieldId(formFieldID);

                        /*if (formId != null) {
                         if (formId.contains("_")) {
                         String[] formIdArray = formId.split("_");
                         try {
                         responseValue.setFormResponseID(Integer.parseInt(formIdArray[0]));
                         } catch (Exception e) {
                         throw new NumberFormatException(e.getMessage());
                         }

                         }

                         }*/
                        responseValue.setRVRepeatCount(i + 1);
                        containerValue.addRepetableValue(responseValue);
                        n++;
                    }
                    i++;
                }

                dataValues.add(containerValue);

            } else {
                String value = null;
                String tempValue=null;
                boolean isImage=false;
                try {
                    value = FormsPluginController.getFormDataXml(message.getTextContent(), ff);

                    if (ff.getType() == FormFieldType.IMAGE) {
                        isImage=true;
                        FileOutputStream out;
                        String imageName=ff.getName()+".jpg";
                        try {
                            tempValue=value;
                            //value=imageName; Removed by Saad Mansour
                            System.out.println("tempValue "+tempValue);
                            System.out.println("value "+value);
                            
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            formsController.logInfo("Failed to create image ............................ ");
                        }
                        formsController.logInfo("In image data");
                    }else if (ff.getType() == FormFieldType.SIGNATURE) {
                         formsController.logInfo("Parce SIGNATURE.................In custom Form handler ");
                    }else if (ff.getType() == FormFieldType.GEOLOCATION) {
                        /**
                         * Value received :   Latitude,Longitude ,Altitude ,Accuracy ex: 31.88778 35.215851 0.0 3.0
                         */
                        System.out.println("GEOLOCATION  ... value "+value);
                    }
                    // dataValues.add(new
                    // ResponseValue(FormsPluginController.getFormDataXml(message.getTextContent(),
                    // ff)));
                } catch (Exception e) {
                    throw new FormHandlingException(e.getMessage());
                }

                /*
                 * This forces the "false" value instead of empty string for not
                 * selected checkbox. Needed for management tool.
                 */
                if (ff.getType() == FormFieldType.CHECK_BOX && value.equals("")) {
                    value = "false";
                }
                
                // added by a.aknai@fabaris
                if (value != null && value.equals("")) {
                    value = null;
                }

                ResponseValue responseValue = new ResponseValue(value);
                responseValue.setPositionIndex(ff.getPositionIndex());
                responseValue.setFormFieldId((int) ff.getId());
                responseValue.setTempValue(tempValue);
                responseValue.setIsImage(isImage);
                
                if (ff.getType() == FormFieldType.NUMERIC_TEXT_FIELD) {
                    try {
                        float nvalue = Float.parseFloat(value);
                        responseValue.setNvalue(nvalue);
                    }catch(NumberFormatException e)
                    {}
                }
                
//                if (ff.getType() == FormFieldType.DATE_FIELD) {
//                    try {
//                        2015-11-06 21:55:27.810
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-/yyyy HH:mm");
//                        float dvalue = Date.(value);
//                        responseValue.setDvalue(dvalue);
//                    }catch(NumberFormatException e)
//                    {}
//                }
                
                /*if (formId != null) {
                 if (formId.contains("_")) {
                 String[] formIdArray = formId.split("_");
                 try {
                 responseValue.setFormResponseID(Integer.parseInt(formIdArray[0]));
                 } catch (Exception e) {
                 throw new NumberFormatException(e.getMessage());
                 }

                 }

                 }*/
                dataValues.add(responseValue);
            }
        }
        String clientVersion = null;
        try {
            clientVersion = FormsPluginController.getTagContentFromXml(message.getTextContent(), "client_version").get(0);
        } catch (Exception e) {
            throw new FormHandlingException(e.getMessage());
        }
        // SubmittedFormData submittedForm = new
        // SubmittedFormData(formId.intValue(), formId.intValue(), dataValues,
        // clientVersion);
        SubmittedFormData submittedForm = new SubmittedFormData(formId, formId, dataValues, clientVersion);
        submittedFormSet.add(submittedForm);
        DataSubmissionRequest ret = new DataSubmissionRequest(submittedFormSet);
        // ret.setFormIdentificator(formIdentificator);
        ret.setFormIdentificator(formIdentificator);
        ret.setFormResponseName(formName);
        ret.setSmsPort(0);
        return ret;
    }

    private SyncFormsRequest handleSyncMessage(FrontlineMessage message)
            throws Exception {
        System.out.println("handleSyncMessage");
        SyncFormsRequest sfr = new SyncFormsRequest(message.getTextContent());
        FormDao fDao = this.formsController.getFormDao();
        Collection<Form> forms = fDao.getAllForms();
        for (Form f : forms) {
            String fId = f.getId_flsmsId();
            if (!FormsPluginController.isInList(sfr.getReceivedFormsIDs(), fId)) {
                if (FormsPluginController.isAssignedToForm(message.getSenderMsisdn(), f)) {
                    sfr.returnMessages.add(FrontlineMessage.createOutgoingMessage(System.currentTimeMillis(), "", message.getSenderMsisdn(), FormsPluginController.insertSoftwareVersionNumber(FormsPluginController.formToXForm(f), f)));
                }
            }
        }
        return sfr;
    }

    public Collection<FrontlineMessage> handleOutgoingMessage(
            FormsResponseDescription response) throws FormHandlingException {
        List<FrontlineMessage> messages = new ArrayList<FrontlineMessage>();
        SubmittedDataResponse dataReponse = (SubmittedDataResponse) response;
        Collection<SubmittedFormData> formsCollection = dataReponse.getSubmittedData();
        String formId = "";
        Iterator<SubmittedFormData> iterator = formsCollection.iterator();
        while (iterator.hasNext()) {
            formId = formId + String.valueOf(iterator.next().getFormId()) + " ";
        }
        long id = -1;
        if (response instanceof SubmittedDataResponse) {
            id = ((SubmittedDataResponse) response).getFormResponceId();
        }
        String formResponseTag = id == -1 ? "" : "</name><formResponseID>" + id + "</formResponseID>";
        String ident = (dataReponse.getFormIdentificator() != null) ? dataReponse.getFormIdentificator() : formId;
        // a.zanchi this is the return confirmation message.
        String returnMessage = "<response><data>" + ident.toString() + "</data><stato>finalized</stato><name>" + dataReponse.getFormResponseName() + formResponseTag + "</response>";
        FrontlineMessage message = FrontlineMessage.createOutgoingMessage(new Date().getTime(), null, null, returnMessage);
        messages.add(message);
        return messages;
    }

    public void init(FormsPluginController formsPluginController) {
        // TODO Auto-generated method stub
    }

    public CustomFormsMessageHandler(FormsPluginController controller) {
        this.formsController = controller;
    }
    
   
    

}

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
package net.frontlinesms.plugins.forms.ui;

import java.awt.Frame;
import java.lang.reflect.Array;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.Survey;
import net.frontlinesms.plugins.forms.data.domain.SurveyElement;
import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.forms.data.repository.SurveyDao;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.plugins.forms.ui.components.VisualForm;

/**
 * Controller class for survey window
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 *   www.fabaris.it <http://www.fabaris.it/>  
 */
public class SurveyEditorController {

	/* Reference to SurveyDao */
	private SurveyDao surveyDao;
	/* Reference to FormDao */
	private FormDao formDao;

	/* Reference to current selected component */
 	private PreviewComponent currentComponent;

	/* Reference to ownerForm */
	private Form ownerForm;

	// reference to frame owner
	private Frame owner;

	// Reference to current form in edit state
	private VisualForm current;

        //Current selected survey. Uses to select the list after closing the popup.
        private Survey selectedSurvey;
        
        //A reference to the SurveysEditor object. Uses to access some variables like: JList surveyList.
        private SurveysEditor surveyEditor;
        
	/**
	 * Constructor accepting an owner frame and the current opened visualform
	 * 
	 * @param current
	 * @param owner
	 */
	public SurveyEditorController(VisualForm current, Frame owner) {
		this.surveyDao = FormsThinletTabController.getCurrentInstance().getPluginController().getSurveyDao();
		this.formDao = FormsThinletTabController.getCurrentInstance().getPluginController().getFormDao();
		this.owner = owner;
		this.current = current;
		if (current.getFormId() != null) {
			this.ownerForm = FormsThinletTabController.getCurrentInstance().getPluginController().getFormDao().getFromId_IdMac(current.getFormId());
		}
                this.selectedSurvey = null;
	}

	/**
	 * Constructor accepting the currently selected PreviewComponent
	 * 
	 * @param current
	 * @param owner
	 * @param currentComp
	 */
	public SurveyEditorController(VisualForm current, Frame owner, PreviewComponent currentComp) {
		this(current, owner);
		this.currentComponent = currentComp;

	}

	public void addElementInSurvey(Survey survey, String element) {
		for (Survey s : current.getTemporarySurveys()) {
			if (s.equals(survey)) {
				s.addValue(element);
			}
		}
		survey.addValue(element);
		this.surveyDao.updateSurvey(survey);
	}
	
	public void removeElementFromSurvey(Survey survey, String element) {
		survey.removeValue(element);
		this.surveyDao.updateSurvey(survey);
	}

	public Survey addNewSurveyWithName(String surveyName) {
		String formId = current.getFormId();
		// System.out.println(formId);
		Survey newSurvey = new Survey();
		newSurvey.setName(surveyName);
		if (formId != null) { // this is called if we are editing a form
			Form ownerForm = this.formDao.getFromId_IdMac(formId);
			newSurvey.setOwner(ownerForm);
		} else { // if the form is new
			current.addTemporarySurvey(newSurvey);

		}
		surveyDao.saveSurvey(newSurvey);
                return newSurvey;
	}

	public void removeSurvey(Survey surveyToRemove) {
		this.current.removeTemporarySurvey(surveyToRemove);
		surveyDao.deleteSurvey(surveyToRemove);
	}

	public void updateSurvey(Survey survey) {
		this.surveyDao.updateSurvey(survey);
	}

	/**
	 * Method to rename survey in database and in memory
	 * 
	 * @param survey
	 * @param newName
	 */
	public void renameSurvey(Survey survey, String newName) {
		for (Survey s : current.getTemporarySurveys()) {
			if (s.equals(survey)) {
				s.setName(newName);
			}
		}

		survey.setName(newName);
		this.updateSurvey(survey);
                //setSelectedSurveryInSurveyList(survey);
	}

	public List<Survey> getAllSurveys() {
		return (List<Survey>) surveyDao.getAllSurvey();
	}

	public List<Survey> getAllSurveysForCurrentForm() {
		if (ownerForm != null)
			return surveyDao.getSurveyOwnedByForm(ownerForm);
		else
			return current.getTemporarySurveys();
	}

	public VisualForm getCurrent() {
		return current;
	}

	public void setCurrent(VisualForm current) {
		this.current = current;
	}

	public void showWindow(Survey selectedSurveyItem) {
		SurveysEditor frame = new SurveysEditor(this, owner);
		this.surveyEditor = frame;
                setSelectedSurveryInSurveyList(selectedSurveyItem);
                frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		frame.toFront();
	}

	/**
	 * Check if survey is associated with any form
	 * 
	 * @param survey
	 * @return list of forms using the survey
	 */
	public List<Form> checkIfListUsed(Survey survey) {
		// Check in all existing forms
		ArrayList<Form> ret = new ArrayList<Form>();
		for (Form f : FormsThinletTabController.getCurrentInstance().getPluginController().getFormDao().getAllForms()) {
			for (FormField ff : f.getFields()) {
				if (ff.getType() == FormFieldType.DROP_DOWN_LIST || ff.getType() == FormFieldType.RADIO_BUTTON || ff.getType() == FormFieldType.REPEATABLES) {
					if (ff.getSurvey() != null) {
						if (ff.getSurvey().equals(survey)) {
							ret.add(f);
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Check if survey is associated with any preview component in the current
	 * editing form except the currently selected one.
	 * 
	 * @param survey
	 * @return list of PreviewComponnet in current form associatied with survey
	 */
	public HashMap<PreviewComponent, Integer> checkIfListUsedInCurrent(Survey survey) {
		HashMap<PreviewComponent, Integer> ret = new HashMap<PreviewComponent, Integer>();
		int i = 1;
		for (PreviewComponent pc : current.getComponents()) {
			if (pc.getType() == FormFieldType.DROP_DOWN_LIST || pc.getType() == FormFieldType.RADIO_BUTTON || pc.getType() == FormFieldType.REPEATABLES) {
				if (pc.getSurvey() != null && survey != null) {
					if (pc.getSurvey().getId() == survey.getId() && (pc != currentComponent)) {
						ret.put(pc, i);
					}
				}
			}
			i++;
		}
		return ret;
	}

	/**
	 * Fabaris_a.zanchi Makes a copy of a survey, changes its name appending *,
	 * and saves it on db
	 * 
	 * @param sur
	 */
	public Survey copySurvey(Survey sur) {
		Survey cloned = sur.clone();
		cloned.setName(sur.getName() + "*");
		String formId = current.getFormId();
		if (formId != null) { // this is called if we are editing a form
			Form ownerForm = this.formDao.getFromId_IdMac(formId);
			cloned.setOwner(ownerForm);
		} else {
			current.addTemporarySurvey(cloned);
		}
		surveyDao.saveSurvey(cloned);
                return cloned;
	}

        private void setSelectedSurveryInSurveyList(Survey selectedSurvey)
        {
            if (surveyEditor != null) {
                surveyEditor.getSurveyJList().setSelectedValue(selectedSurvey, true);
            }
        }
        
	public Survey copySurveyWithName(Survey sur, String name) throws Exception {
		Survey cloned = sur.clone();
		if (current.getFormId() != null) {
			Form currentForm = formDao.getFromId_IdMac(current.getFormId());
			List<Survey> surveyList = FormsThinletTabController.getCurrentInstance().getPluginController().getSurveyDao().getSurveyOwnedByForm(currentForm);
			for (Survey s : surveyList) {
				if (s.getName().equals(name))
					throw new Exception(SurveysEditor.ERROR_NO_DUPLICATES);
			}
			cloned.setOwner(currentForm);
		} else {
			for (Survey s : current.getTemporarySurveys()) {
				if (s.getName().equals(name))
					throw new Exception(SurveysEditor.ERROR_NO_DUPLICATES);
			}
			current.addTemporarySurvey(cloned);
		}
		//Survey cloned = sur.clone();
		cloned.setName(name);
		surveyDao.saveSurvey(cloned);
                return cloned;
	}

	public void test() {
		Form saved = formDao.getFromId_IdMac("41_AAABPGcIRB0");
		List<Survey> sur = FormsThinletTabController.getCurrentInstance().getPluginController().getSurveyDao().getSurveyOwnedByForm(saved);
		System.out.println(sur.size());
	}
	
	public PreviewComponent getCurrentComponent(){
		return this.currentComponent;
	}
	
	public Form getOwnerForm() {
		return this.ownerForm;
	}
        
//        public void setCurrentSelectedSurvey(Survey survey)
//        {
//            selectedSurvey = survey;
//        }
//        
//        public Survey getCurrentSelectedSurvey()
//        {
//            return selectedSurvey;
//        }
        
        public SurveysEditor getSurveyEditor()
        {
            return surveyEditor;
        }
}

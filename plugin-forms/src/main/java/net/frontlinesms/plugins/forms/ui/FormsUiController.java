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
package net.frontlinesms.plugins.forms.ui;

import java.awt.Color;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import net.frontlinesms.plugins.forms.data.domain.BindingContainer;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.helper.PanelHelper;
import net.frontlinesms.plugins.forms.ui.components.FComponent;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.plugins.forms.ui.components.Separator;
import net.frontlinesms.plugins.forms.ui.components.TextArea;
import net.frontlinesms.plugins.forms.ui.components.TextField;
import net.frontlinesms.plugins.forms.ui.components.TruncatedText;
import net.frontlinesms.plugins.forms.ui.components.WrappedText;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent.BindType;
import net.frontlinesms.plugins.forms.ui.components.VisualForm;
import net.frontlinesms.resources.ResourceUtils;
import static net.frontlinesms.resources.UserHomeFilePropertySet.LOG;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * This class is responsible for doing all actions triggered by UI classes. This
 * class implements the Singleton design pattern to ensure only one instance of
 * this class during the execution.
 * 
 * @author Carlos Eduardo Genz <li>kadu(at)masabi(dot)com
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>  
 */
public class FormsUiController {
	private static FormsUiController instance;
	private FormsEditorDialog mainFrame;
	PreviewComponent newLabel;
	
	public FormsEditorDialog getMainFrame() {
		return mainFrame;
	}
	public void setMainFrame(FormsEditorDialog mainFrame) {
		this.mainFrame = mainFrame;
	}

	private VisualForm current;
	private DrawingPanel pnDrawing;
	PreviewPanelRepeatables pnRepeatablePreview;

	//*added by Fabaris_raji
	List<PreviewComponent> componentslst = new ArrayList<PreviewComponent>();
	boolean check=false;				

	/**
	 * Show properties of the selected component.
	 */
	public void showProperties() {
		mainFrame.showProperties();
	}
	
	public void editLabelOnPropertyField(){
		//mainFrame.getPropertyTable().setFocusable(true);
		mainFrame.getPropertyTable().changeSelection(1, 1, false, false);
		System.out.println(mainFrame.getPropertyTable().editCellAt(1, 1));
	}
	
	/**
	 * Hide the formula editor panel.
	 * Fabaris a.aknai
	 */
	public void hideFormulaEditor() {
		mainFrame.hideFormulaEditor();
	}

	/**Fabaris_A.zanchi
	 *    
	 *    
	 * Method called before the show properties method
	 */
	public void deSelectAllBeforeShowProperties() {

		mainFrame.deselectEverything();
	}

	/**
	 * Method invoked when there is a change on the properties table. We just
	 * update the preview if there was a modification on the value.
	 * 
	 * @param property
	 *            Property changed.
	 * @param value
	 *            The new value.
	 */
	// Fabaris_a.zanchi Added con	trol not to change the label property
	public void propertiesChanged(String property, String value) {
		//this.mainFrame.getConstraintTable().repaint();
		if(mainFrame.getSelectedComponent() != null){
			FComponent comp = mainFrame.getSelectedComponent().getComponent();
			if(comp != null && (comp instanceof TextArea || comp instanceof TextField)){
				mainFrame.enableIsCalculatedControl(false);
			}
			if (property != null && property.equals(InternationalisationUtils.
					getI18nString(FComponent.PROPERTY_LABEL))) {
				if (comp.getLabel() == null || !comp.getLabel().equals(value)) {
					comp.setLabel(value);
					/*
					 * a.zanchi: modifies the property in the FormField too. If the
					 * component is new and the FormField is null does nothing
					 */
					if (mainFrame.getSelectedComponent().getFormField() != null) {
						mainFrame.getSelectedComponent().getFormField().setLabel(value);
					}
					mainFrame.refreshPreview();
				}
			}
			// Fabaris_raji Fabaris_m.cilione for name field control
			if (property != null && property.equals(InternationalisationUtils.
					getI18nString(FComponent.PROPERTY_NAME))) {
	
	//			FComponent comp = mainFrame.getSelectedComponent().getComponent();			
	
				if(value.equals("")||comp.getName().equals("")) {
					/*String str =(String) JOptionPane.showInputDialog(
					null,
					InternationalisationUtils.getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FIELD_NAME_BLANK))	;				
					 */
					//Fabaris_raji to test name dialog				
					//FormsEditorDialog.createAndShowGUI(comp);		
	
					String str=comp.getName();
					if(str!=null){					
						comp.setName(str);	
					}
	
					else
	
						JOptionPane.showMessageDialog(null,InternationalisationUtils.
								getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FIELD_NAME_INVALID).replace("\\n", System.getProperty("line.separator")));	
	
					}
				
				if( !(comp instanceof Separator) && !( comp instanceof TruncatedText) && !(comp instanceof WrappedText)){
					if(!value.equals("")||!comp.getName().equals("")||!comp.getName().equals(value) ||(comp.getName() == null)){
							if(!value.equals("")){
								comp.setName(value);
								String name=value;
							}
							else{
			
								String name=comp.getName(); 
							}
							String name=comp.getName(); 	
			
							Pattern p=Pattern.compile("^[a-zA-Z]{1,}");				
							Matcher m = p.matcher(name);	     
							boolean validBegin = m.find();	
			
							p=Pattern.compile("[a-zA-Z0-9]{1}$");
							m=p.matcher(name);
							boolean validEnd = m.find();
			
							p=Pattern.compile("^[a-zA-Z0-9_]*$");
							m=p.matcher(name);
							boolean validContent = m.find();
			
			
						if((validBegin ==false)||(validContent==false)||(validEnd==false)||(name.equals(null))||(name.length()==0)||(name.equals(null))||(name.length()>15)){		
								//if(!name.equals("")){
								JOptionPane.showMessageDialog(null,InternationalisationUtils.
										getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FIELD_NAME_INVALID).replace("\\n", System.getProperty("line.separator")));								
								comp.setName("");
								FormsEditorDialog.createAndShowGUI(comp, false);//Fabaris_raji alert for name 
								mainFrame.refreshPreview();	
								mainFrame.showProperties();							
								//}	
							}	
			
							boolean check=mainFrame.check(comp, name);	//Added to check for duplicate names in the list  of components						
			
							if(check==true){
								if(!name.equals("")){
									JOptionPane.showMessageDialog(null,InternationalisationUtils.
											getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FIELD_NAME_EXISTS));	
									comp.setName("");
									FormsEditorDialog.createAndShowGUI(comp, false);//Fabaris_raji alert for name 
									mainFrame.refreshPreview();
									mainFrame.showProperties();
								}	
							}	
									
							if(mainFrame.checkDuplicateRepeatables(comp,name)){//Added to check for duplicate names when properties changed
							
								if(!name.equals("")){
									JOptionPane.showMessageDialog(null,InternationalisationUtils.
											getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FIELD_NAME_REP_EXISTS));	
									comp.setName("");
									FormsEditorDialog.createAndShowGUI(comp,false);//Fabaris_raji alert for name 
									mainFrame.refreshPreview();
									mainFrame.showProperties();
								}	
								
							}
							if((validBegin==true)&&(validContent==true)&&(validEnd==true)&&(name.length()>0)&&(name.length()<=15)){	
			
								if(mainFrame.getSelectedComponent().getFormField() != null){									
									mainFrame.getSelectedComponent().getFormField().setName(value);									
								}
								mainFrame.refreshPreview();	
								//mainFrame.showProperties();							
							}
			
						}				
	
				}
			}//name field end	
	
	
			// Fabaris_raji for Required field
			if (property != null && property.equals(InternationalisationUtils.
					getI18nString(FComponent.PROPERTY_REQUIRED))) {
	//			FComponent comp = mainFrame.getSelectedComponent().getComponent();
	
				Boolean req = Boolean.parseBoolean(value);
				if (!comp.getRequired().equals(req)) {
					comp.setRequired(req);
					if (mainFrame.getSelectedComponent().getFormField() != null) {
						mainFrame.getSelectedComponent().getFormField().setRequired(req);
					}
					mainFrame.refreshPreview();
				}
	
			}
		}
	}

	/**
	 * Fabaris_a.zanchi changes one of the bindings type of the selected
	 * component
	 * 
	 * @param pComponent
	 *            The PreviewComponent whose bind type changes
	 * @param oldTypeCont
	 * @param newTypeCont
	 */
	public void bindChanged(PreviewComponent pComponent, BindingContainer oldTypeCont, BindingContainer 
			newTypeCont) {
		mainFrame.bindChanged(pComponent, oldTypeCont, newTypeCont);
	}
	
	/**Fabaris_a.zanchi
	 * Changes the component of an existing binding
	 * 
	 * @param newComp
	 * @param bCont
	 * @param oldComp
	 */
	public void bindChangedComponent(PreviewComponent newComp, BindingContainer bCont, PreviewComponent oldComp) {
		mainFrame.bindChangedComopnent(newComp, bCont, oldComp);
	}

	/**
	 * Fabaris_a.zanchi hightlight the Component of the selected Binding
	 * 
	 * @param selectedPComponent
	 */
	public void highlightBinding(PreviewComponent selectedPComponent, Color color) {
		mainFrame.highlightComponent(selectedPComponent, null);
	}
	
	/**Fabaris_A.zanchi called when a new constraint is inserted in table so that it 
	 * is reflected in selected component
	 * 
	 * @param cont
	 */
	public void addConstraint(ConstraintContainer cont) {
		mainFrame.addConstraint(cont);
	}
	
	/**Fabaris_a.zanchi called when a constraint is updated in the table
	 * 
	 * @param cont
	 * @param index
	 */
	public void updateConstraint(ConstraintContainer cont, int index) {
		mainFrame.updateConstraint(cont, index);
	}
	
	public void removeConstraint(int index) {
		mainFrame.removeConstraint(index);
	}

	public VisualForm showFormsEditor(Frame owner, VisualForm form) {
		mainFrame = new FormsEditorDialog(owner);
		DefaultComponentDescriptor defCompDesc = getDefaultComponentsFromFile();
		mainFrame.setForm(form);
		mainFrame.addDefaultComponents(defCompDesc);		
		mainFrame.setVisible(true);			
		return mainFrame.getVisualForm();		
	}
	//Added by Fabaris_raji and A.aknai
	
	public VisualForm showNewFormsEditor(Frame owner, VisualForm form) {
		mainFrame = new FormsEditorDialog(owner);
		DefaultComponentDescriptor defCompDesc = getDefaultComponentsFromFile();
		mainFrame.setForm(form);
		mainFrame.addDefaultComponents(defCompDesc);	
		//added by Fabaris_raji			
		String formName = askForName(mainFrame, form);
		if(formName != null){
			return mainFrame.getVisualForm();	
		}else{
			return null;
		}	
	}
	public String askForName(FormsEditorDialog editor, VisualForm form){
		FormNamePanel panel = mainFrame.createAndShowGUIS(mainFrame, form);
		if(panel.state == FormNamePanel.STATE_CANCEL){
			return null;
		}else{
			if(isFormNameValid(panel.text)){
				return panel.text;
			}else{
				return askForName(editor, form);
			}
		}
	}
	
	public boolean isFormNameValid(String name){
		if (name.equals("")) {
			JOptionPane
			.showMessageDialog(
					null,
					InternationalisationUtils
					.getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FORM_NAME_BLANK));
			return false;
		}

		/*added to compare form name with the list of forms -Fabaris_raji 
		 * 		
		 */
		for (Form f : FormsThinletTabController.getCurrentInstance().formsDao.getAllForms()) {			
			String formname=f.getName();			
			if (formname.equalsIgnoreCase(name)) {
				JOptionPane.showMessageDialog(null, "Form name already exists", "Error", JOptionPane.WARNING_MESSAGE );
				return false;
			}
		}
		return true;
	}
	/**Fabaris_A.zanchi called when a repeatable container is selected in the preview panel
	 * 
	 */
	public void showRepeatableContainerDetails() {
		mainFrame.showRepeatableContainerDetails();
	}

	public static FormsUiController getInstance() {
		if (instance == null)
			instance = new FormsUiController();
		return instance;
	}

	/**Fabaris_a.zanchi
	 * This method fetches the Preview Components from file!
	 * 
	 * @return
	 */
	private DefaultComponentDescriptor getDefaultComponentsFromFile() {
		DefaultComponentDescriptor defCompDesc = new DefaultComponentDescriptor();
		FComponent[] ret = null;

		Properties prop = new Properties();
		//String filePath = System.getProperty("user.home") + "/FrontlineSMS/properties/default_components";
                //String filePath = getClass().getResource().; "./src/main/resources/resources/properties/default_components";
//		FileInputStream fis = null;
//		try {
//			 fis = new FileInputStream(new File(filePath));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
                String filePath = "/resources/properties/default_components";
                InputStream fis = ResourceUtils.class.getResourceAsStream(filePath);
                if(fis == null) {
                    LOG.fatal("default_components file could not be found!");
                }
                
		try {
			prop.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
                
		Set<String> list = prop.stringPropertyNames();

		//Set as umodifiable every component except the last component (a separator)
		DefaultComponentDescriptor.UNMODIFIABLE_COMPONENT_INDEX = list.size() - 1;

		ret = new FComponent[list.size()];

		int index = 0;
		for (String s : list) {
			String[] info = prop.getProperty(s).split(",");
			FormFieldType[] types = FormFieldType.values();
			for (FormFieldType fft : types) {
				if (fft.toString().equals(info[1].trim())) {
					Class<? extends FComponent> clazz = FComponent.getComponentClass(fft);
					FComponent fComp = null;
					try {
						fComp = clazz.newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					fComp.setName(s.toLowerCase());
					if (s.contains("date") || 
                                                s.contains("enumerator") ||
                                                s.contains("GPS")) {
						fComp.setReadOnly(false);
					}
					else {
						fComp.setReadOnly(true);
					}
					if (info.length == 3)
						fComp.setLabel(info[2].trim());
					else
						fComp.setLabel("");
					ret[Integer.parseInt(info[0]) - 1] = fComp;
				}
			}
			index++;
		}
		defCompDesc.setDefaultComponents(ret);
		return defCompDesc;
	}
	
	/**Fabaris_A.zanchi returns the selected previewComponent from the form dialog
	 * 
	 * @return
	 */
	public PreviewComponent getSelectedComponent() {
		return mainFrame.getSelectedComponent();
	}
	
	public void resetPropertyTable(){
		this.mainFrame.resetPropertyTable();
	}
	
	public void resetConstraintTable(){
		this.mainFrame.resetConstraintTable();
	}
	
	public void resetBindingTable(){
		this.mainFrame.resetBindingTable();
	}
	
	public void enableVisibilityPanel(boolean enable){
		this.mainFrame.enableVisibilityPanel(enable);
	}
	
	public void enableValidityPanel(boolean enable){
		this.mainFrame.enableValidityPanel(enable);
	}
	
	public void enablePropertyPanel(boolean enable){
		this.mainFrame.enablePropertyPanel(enable);
	}
	
	public void enableConstraintTable(boolean enabled){
		mainFrame.enableConstraintTable(enabled);
	}
	
	public void enableBindingTable(boolean enabled){
		mainFrame.enableBindingTable(enabled);
	}
	
	public void enablePropertiesTable(boolean enabled){
		mainFrame.enablePropertiesTable(enabled);
	}
	
	public void clickOnHeaderFieldInPreviewPanel(){
		setRequiredValue(true);
		enablePropertyPanel(false);
		enableValidityPanel(false);
		enableVisibilityPanel(false);
		resetPropertyTable();
		resetBindingTable();
		resetConstraintTable();
		enableBindingTable(false);
		enablePropertiesTable(false);
		enableConstraintTable(false);
	}
	
	
	
	public void setRequiredValue(boolean required){
		mainFrame.setRequiredValue(required);
	}
}

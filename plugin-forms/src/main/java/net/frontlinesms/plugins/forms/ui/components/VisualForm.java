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
package net.frontlinesms.plugins.forms.ui.components;

import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.plugins.forms.FormFormula;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldAndBinding;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.Survey;
import net.frontlinesms.plugins.forms.exceptions.FormFormulaException;
/**
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 *  www.fabaris.it <http://www.fabaris.it/>  
 */
@SuppressWarnings("serial")   //class which visualizes all components--Fabaris_raji
public class VisualForm extends FComponent {
	private static final Logger LOG = FrontlineUtils.getLogger(VisualForm.class);
	
	private List<PreviewComponent> components = new ArrayList<PreviewComponent>();

	private String name;
	public String owner;
	
	//Fabaris_a.zanchi
	private String bindingsPolicy;
	
	//Fabaris_a.zanchi univoqe id of the form entity this visualForm is a representation of
	private String formId;
	
	/*Fabaris_a.zanchi 
	 * A temporary list of surveys used if the form is new and not existing on db
	 * */
	private List<Survey> temporarySurveys = new ArrayList<Survey>();
	
	public VisualForm() {
		this.bindingsPolicy = "All";
	}
	
	public void addComponent(PreviewComponent component) {
		components.add(component);
	}
	
	public void removeComponent(PreviewComponent component) {
		components.remove(component);
	}

	public List<PreviewComponent> getComponents() {
		return components;
	}

	@Override
	public String getDescription() {
		return "Form";
	}

	@Override
	public Container getDrawingComponent() {
		return null;
	}

	@Override
	public String getIcon() {
		return "form.png";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

		//Fabaris_a.zanchi
		public String getBindingsPolicy() {
			return bindingsPolicy;
		}

		//Fabaris_a.zanchi
		/**Method to set the AND-OR policy for bindings
		 * Please use the strings "All" or "Any";
		 * 
		 * @param bindingsPolicy
		 */
		public void setBindingsPolicy(String bindingsPolicy) {
			this.bindingsPolicy = bindingsPolicy;
		}
	
	/* Edit by Fabaris_a.zanchi to recreate the binding relations between PreviewCompoenents
	 * fetching them from FormFields
	 * 2nd edit by Fabaris_A.zanchi to recreate the relations for repeatable sections
	 * 
	 */
	public static VisualForm getVisualForm(Form f) {
		VisualForm ret = new VisualForm();
		ret.setName(f.getName());
		ret.setFormId(f.getId_flsmsId());
		ret.setBindingsPolicy(f.getBindingsPolicy());
		//this Map is needed to track the corrispondency between PreviewComponents and FormFields
		HashMap<FormField, PreviewComponent> fFieldTOpComp = new HashMap<FormField, PreviewComponent>();
		try {
			for (FormField field : f.getFields()) {
//				if(!field.getName().equals("des_version") && !field.getName().equals("client_version")){
					//System.out.println(field.getBindingCouples().size());
					PreviewComponent comp = new PreviewComponent();
					FComponent component;
					Class<? extends FComponent> clazz = FComponent.getComponentClass(field.getType());
					component = clazz.newInstance();
					component.setLabel(field.getLabel());
					component.setName(field.getName());			//Fabaris_a.zanchi
					component.setRequired(field.getRequired()); //Fabaris_Raji
					component.setReadOnly(field.isReadOnly()); //Fabaris_a.zanchi
					comp.setComponent(component);
					comp.setFormField(field);
					comp.setConstraints(field.getConstraints());
					fFieldTOpComp.put(field, comp);
					ret.addComponent(comp);
					comp.setBindingsPolicy(field.getBindingsPolicy());
					comp.setConstraintPolicy(field.getConstraintPolicy());
					
					
					if (field.getType() == FormFieldType.DROP_DOWN_LIST) {
						comp.setSurvey(field.getSurvey());
					}
					if (field.getType() == FormFieldType.RADIO_BUTTON) {
						comp.setSurvey(field.getSurvey());
					}
					if (field.getType() == FormFieldType.REPEATABLES || field.getType() == FormFieldType.REPEATABLES_BASIC) {
						comp.setSurvey(field.getSurvey());
						for (FormField ff : field.getRepetables()) {
							PreviewComponent c = new PreviewComponent();
							FComponent fcomponent;
							Class<? extends FComponent> fClazz = FComponent.getComponentClass(ff.getType());
							fcomponent = fClazz.newInstance();
							fcomponent.setLabel(ff.getLabel());
							fcomponent.setName(ff.getName());
							fcomponent.setRequired(ff.getRequired());
							fcomponent.setReadOnly(ff.isReadOnly());
							c.setComponent(fcomponent);
							c.setFormField(ff);
							
							c.setBindingsPolicy(ff.getBindingsPolicy());
							c.setConstraintPolicy(ff.getConstraintPolicy());
							c.setConstraints(ff.getConstraints());
							comp.addRepeatable(c);
							fFieldTOpComp.put(ff, c);
							
							
							c.setBindingsPolicy(ff.getBindingsPolicy());
							c.setConstraintPolicy(ff.getConstraintPolicy());
	
							if (ff.getType() == FormFieldType.DROP_DOWN_LIST || ff.getType() == FormFieldType.RADIO_BUTTON) {
								c.setSurvey(ff.getSurvey());
							}
						}
						
						
						
						//bindings
						for (FormField ff : field.getRepetables()) {
							PreviewComponent currentPC = fFieldTOpComp.get(ff);
							List<FormFieldAndBinding> bindingCouples = ff.getBindingCouples();
							for (FormFieldAndBinding couple : bindingCouples) {
								currentPC.addBinding(fFieldTOpComp.get(couple.getfField()), couple.getbContainer());
							}
						}
						if (field.getNumberOfRep() != 0) {
							comp.setBasicContainerFixed(true);
							comp.setNumberOfReps(field.getNumberOfRep());
						}
						
					}
				}
//			}
			//bindings
			for (FormField field : f.getFields()) {
				PreviewComponent currentPC = fFieldTOpComp.get(field);
				List<FormFieldAndBinding> bindingCouples = field.getBindingCouples();
				for (FormFieldAndBinding couple : bindingCouples) {
					currentPC.addBinding(fFieldTOpComp.get(couple.getfField()), couple.getbContainer());
				}
			}
			//Fabaris_a.aknai write formula form formField to previewComponent
			for(PreviewComponent p : ret.getComponents()){
				if(p.getFormField().getCalculated()!=null)p.setCalculated(p.getFormField().getCalculated().booleanValue());
				if(p.getFormField().getFormula() != null)p.setFormula(FormFormula.parseFormula(p.getFormField().getFormula(), p,  ret));
				for(PreviewComponent repPC : p.getRepeatables()){
					if(repPC.getFormField().getCalculated()!=null)repPC.setCalculated(repPC.getFormField().getCalculated().booleanValue());
					if(repPC.getFormField().getFormula() != null)repPC.setFormula(FormFormula.parseFormula(repPC.getFormField().getFormula(), repPC,  ret));
				}
			}
		} catch (InstantiationException e) {
			LOG.debug("", e);
		} catch (IllegalAccessException e) {
			LOG.debug("", e);
		} catch (FormFormulaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public void addTemporarySurvey(Survey sur) {
		this.temporarySurveys.add(sur);
	}
	
	public void removeTemporarySurvey(Survey sur) {
		this.temporarySurveys.remove(sur);
	}

	public List<Survey> getTemporarySurveys() {
		return temporarySurveys;
	}
}
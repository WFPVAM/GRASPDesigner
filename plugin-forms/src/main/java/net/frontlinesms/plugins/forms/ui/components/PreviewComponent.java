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

import java.awt.Color;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.swing.JPanel;

import net.frontlinesms.plugins.forms.FormFormula;
import net.frontlinesms.plugins.forms.data.domain.BindingContainer;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer.ConstraintNumber;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.Survey;
import net.frontlinesms.plugins.forms.data.domain.SurveyElement;
import net.frontlinesms.plugins.forms.ui.PropertiesTable;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.jdom.output.XMLOutputter;

/**
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph,Mureed Al-Barghouthi
 *  www.fabaris.it <http://www.fabaris.it/>  
 */
public class PreviewComponent {
	private boolean selected;
	private FComponent component;
	private Container drawComponent;
	private static final JPanel defaultPanel = new JPanel();
	public static final Color DEFAULT_BACKGROUND = defaultPanel.getBackground();
	public static final Color ACTIVE_BACKGROUND = new Color(255, 255, 255);
	public static final Color HIGHLIGHT_BACKGROUND = new Color(170, 200, 250);
	public static final Color SELECTION_RECT_COLOR = new Color(0x316ac5);

	// Fabaris_a.zanchi number of repetitions of repeatable components
	// public static final int numberOfRepForRepeatables = 5;

	private FormField formField;

	// policy for constraints and bindings
	private String constraintPolicy;
	private String bindingsPolicy;

	// Fabaris_a.zanchi mappa che contiene i vincoli e i previewcomponent
	// relativi a
	// questi vincoli
	private Map<PreviewComponent, ArrayList<BindingContainer>> componentToBindType = new HashMap<PreviewComponent, ArrayList<BindingContainer>>();

	// Fabaris_a.zanchi constraints list
	private List<ConstraintContainer> constraints = new ArrayList<ConstraintContainer>();

	// Fabaris_a.zanchi list of repetable components depending on this component
	private List<PreviewComponent> repeatableComponents = new ArrayList<PreviewComponent>();

	// Fabarus_a.zanchi the eventual survey reference
	private Survey survey;

	// Fabaris_a.zanchi
	private boolean isRepetableContainer = false;
	private PreviewComponent parentComponent;
	private boolean isBasicContainerFixed;
	private int numberOfReps;
	// Fabaris_a.zanchi
	// private boolean isReadOnly;

	// Fabaris_a_zanchi flag to determine if the component is deletable in form
	// designer
	private boolean isDefaultComponent;

	// Fabaris_a.zanchi String containing the complete path of element. Usefule
	// to calculate path for bindings.
	private String refString;

	// Fabaris_a.zanchi map surveyElement to a specific ref path
	private HashMap<String, String> surveyelemToRefString = new HashMap<String, String>();
	private HashMap<String, String> surveyelemToBindingBegin = new HashMap<String, String>();
	//Another map for "dobule" elements like currency field
	private HashMap<String, String> surveyelemToBindingBegin2 = new HashMap<String, String>();
	private boolean hasManyRefs;

	/*
	 * Fabaris_a.zanchi this String represents the starting part of the binding
	 * string of this component. For Example, if the component was a textField,
	 * it would contain the binding on the data type (string). When the
	 * method(s) getBindString*() are called they just return this string in
	 * addition with the dynamic bindings
	 */
	private String xFormBindingBegin;

	/* Useful for "double components" like currency */
	private String xFormBindingBegin2;

	/*
	 * Fabaris_a.zanchi array of strings containing the beginning of the
	 * binding. Use only for repeatable components
	 */
	private String[] xFormBindingBeginForRep;

	private boolean isTail;

	/*
	 * Fabaris_a.aknai flag to indicate if the field represented by this preview
	 * component need to be calculated using a formula.
	 */
	private boolean isCalculated = false;
	/*
	 * Fabaris_a.aknai flag to indicate if the field represented by this preview
	 * component is in the active ones.
	 */
	private boolean isActive = false;
	/*
	 * Fabaris_a.aknai the formula associated with the field represented by this
	 * preview component.
	 */
	private FormFormula formula;

	public PreviewComponent() {
		this.bindingsPolicy = FormField.AND_POLICY;
		this.constraintPolicy = FormField.AND_POLICY;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public FComponent getComponent() {
		return component;
	}

	public void setComponent(FComponent component) {
		this.component = component;
		this.drawComponent = component.getDrawingComponent();
	}

	public Container getDrawComponent() {
		return drawComponent;
	}

	public void updateDrawComponent() {
		this.drawComponent = component.getDrawingComponent();
	}

	public FormField getFormField() {
		return formField;
	}

	public void setFormField(FormField formField) {
		this.formField = formField;
	}

	// a.zanchi
	public Map<PreviewComponent, ArrayList<BindingContainer>> getComponentToBindType() {
		return componentToBindType;
	}

	// a.zanchi
	public void addBinding(PreviewComponent component, BindingContainer bindType) {
		if (this.componentToBindType.containsKey(component))
			this.componentToBindType.get(component).add(bindType);
		else {
			this.componentToBindType.put(component, new ArrayList<BindingContainer>());
			this.componentToBindType.get(component).add(bindType);
		}
	}

	// a.zanchi
	public void removeBinding(PreviewComponent component, BindingContainer bCont) {
		this.componentToBindType.get(component).remove(bCont);
		if (this.componentToBindType.get(component).size() == 0)
			this.componentToBindType.remove(component);
	}

	// a.zanchi
	public void removeAllBindings(PreviewComponent component) {
		this.componentToBindType.remove(component);
		if (this.formField != null)
			this.formField.removeAllBindings(component.getFormField());
	}

	public void addConstraint(ConstraintContainer cont) {
		this.constraints.add(cont);
	}

	public void removeCostraint(int index) {
		this.constraints.remove(index);
	}

	public void updateConstraint(ConstraintContainer cont, int index) {
		ConstraintContainer toUpdate = this.constraints.get(index);
		toUpdate.setcNumber(cont.getcNumber());
		toUpdate.setValue(cont.getValue());
	}

	
	/**Fabaris_A.zanchi
	 * 
	 * Method to update an exsisting Binding on the PreviewComponent. Checks for
	 * the bind and update it mantaining the order in the array (important to
	 * assure choerency in the table)
	 * 
	 * @param component
	 * @param oldType
	 * @param newType
	 */
	public void updateBinding(PreviewComponent component, BindingContainer oldType, BindingContainer newType) {
		ArrayList<BindingContainer> bindings = this.componentToBindType.get(component);
		// int i = 0;
		for (BindingContainer bt : bindings) {
			if (bt.getbType().equals(oldType.getbType()) && bt.getValue().equals(oldType.getValue())) {
				int index = bindings.indexOf(bt);
				// bindings.remove(i);
				bindings.set(index, newType);
				break;
			}
			// i++;
		}
	}
	
	/**Fabaris_A.zanchi changes the component of an existing binding
	 * 
	 * @param newComp the new component involved in the binding
	 * @param bCont the binding container itself
	 * @param oldComp The old component previously involved in the binding.
	 */
	public void updateBindingComponent(PreviewComponent newComp, BindingContainer bCont, PreviewComponent oldComp) {
		ArrayList<BindingContainer> bindings = this.componentToBindType.get(oldComp);
		ArrayList<BindingContainer> temp = new ArrayList<BindingContainer>();
		for (BindingContainer bt : bindings) {
			temp.add(bt);
		}
		for (BindingContainer bt : temp) {
			if (bt.getbType().equals(bCont.getbType()) && bt.getValue().equals(bCont.getValue())) {
				this.removeBinding(oldComp, bCont);
			}
		}
		this.addBinding(newComp, bCont);
	}

/** 
 * @author Fabaris Srl: Andrea Zanchi      
 *           www.fabaris.it <http://www.fabaris.it/>  
 *
 */
	
	@Entity
	public enum BindType {
		NULL, NOT_NULL, NOT_ACTIVE, GREATER_THAN, LESS_THAN, EQUALS, NOT_EQUALS, IS_CHECKED, LESS_EQUALS_THAN, GREATER_EQUALS_THAN /* IS_CALCULATED_FROM */;

		@Override
		public String toString() {
			if (this.name().equals("GREATER_THAN"))
				return InternationalisationUtils.getI18nString("plugin.forms.bindingstable.greater");
			else if (this.name().equals("LESS_THAN"))
				return InternationalisationUtils.getI18nString("plugin.forms.bindingstable.less");
			else if (this.name().equals("EQUALS"))
				return InternationalisationUtils.getI18nString("plugin.forms.bindingstable.equals");
			else if (this.name().equals("NOT_EQUALS"))
				return InternationalisationUtils.getI18nString("plugin.forms.bindingstable.notequals");
			else if (this.name().equals("NULL"))
				return InternationalisationUtils.getI18nString("plugin.forms.bindingstable.null");
			else if (this.name().equals("NOT_NULL"))
				return InternationalisationUtils.getI18nString("plugin.forms.bindingstable.notnull");
			else if (this.name().equals("IS_CHECKED"))
				return InternationalisationUtils.getI18nString("plugin.forms.bindingstable.ischecked");
			else if (this.name().equals("NOT_ACTIVE"))
				return InternationalisationUtils.getI18nString("plugin.forms.bindingstable.inactive");
			else if (this.name().equals("IS_CALCULATED_FROM"))
				return InternationalisationUtils.getI18nString("plugin.forms.bindingstable.calculated");
			else if (this.name().equals("LESS_EQUALS_THAN"))
				return InternationalisationUtils.getI18nString("plugin.forms.bindingstable.lessequals");
			else if (this.name().equals("GREATER_EQUALS_THAN"))
				return InternationalisationUtils.getI18nString("plugin.forms.bindingstable.greaterequals");
			return super.toString();
		}

	}

	/**
	 * Fabaris_a.zanchi Creates an "empty" FormField when the component is new and
	 * doesnt have a representation on the DB. 
	 * 
	 * @param index
	 *            a String representing the model id of the form containing the
	 *            field
	 * @param componentPosition
	 *            an int representing the position of the component in the Form
	 * @param isTail
	 *            boolean true if the component is the last one in the Form
	 */ //NL TODO: Punto di ingresso per le tables
	public void generateEmptyFormField(String index, int componentPosition, boolean isTail) {
		index = "\"" + index + "\"";
		String x_Form = new String();
		FormFieldType fieldType = FComponent.getFieldType(this.getComponent().getClass());
		String requiredString = "";
		String readOnlyString = "";
		/*
		 * Fabaris_a.aknai implementing calculated in the xform saved on the db
		 */
		String calculated = new String();
		String calculatedAttribute = new String(); // Fabaris_a.zanchi
													// calculated formula to put
													// in attribute
		if (this.isCalculated && this.formula != null) {
			calculated = this.formula.toXFormString(this);
			calculatedAttribute = this.formula.toFormulaString(); // Fabaris_a.zanchi
		}
		if (!calculated.equals("")) {
			calculated = " calculate=\"" + calculated + "\" ";

		}

		if (this.component.getRequired()) {
			requiredString = " required=\"true()\"";
		}
		if (this.component.isReadOnly() || !calculated.equals("")) {
			readOnlyString = " readonly=\"true()\"";
			if (this.component.getRequired()) {
				requiredString = " required=\"true()\"";
			}else{
				requiredString = " required=\"false()\"";
			}
		}

		XMLOutputter outputter = new XMLOutputter();

		// Fabaris_a.zanchi escaping of calculated attribute
		if (!calculated.equals("")) {
			calculatedAttribute = outputter.escapeAttributeEntities(calculatedAttribute);
			calculatedAttribute = " calc=\"" + calculatedAttribute + "\" "; // Fabaris_a.zanchi
		}

		String escapedLabelForContent = outputter.escapeElementEntities(this.component.getLabel());
        if (escapedLabelForContent.equals("")) escapedLabelForContent= " ";
		if (fieldType == FormFieldType.CHECK_BOX) {

			x_Form = "<select ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><item><label>" + escapedLabelForContent
					+ "</label><value>true</value></item></select> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"select\" " +  readOnlyString
					+ calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}
		if (fieldType == FormFieldType.TEXT_FIELD) {
			x_Form = "<input ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input>";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"string\" " + requiredString + readOnlyString
					+ calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}

		if (fieldType == FormFieldType.DATE_FIELD) {
			x_Form = "<input ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"date\" " + requiredString + readOnlyString
					+ calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}
		if (fieldType == FormFieldType.EMAIL_FIELD) {
			x_Form = "<input ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"string\" " + requiredString + readOnlyString
					+ calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}
		if (fieldType == FormFieldType.NUMERIC_TEXT_FIELD) {
			x_Form = "<input ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"decimal\" " + requiredString + readOnlyString
					+ calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}

		if (fieldType == FormFieldType.CURRENCY_FIELD) {
			String values = "<item><label>Select</label><value>0</value></item><item><label>AFN</label><value>AFN</value></item><item><label>AMD</label><value>AMD</value></item><item><label>AOA</label><value>AOA</value></item>" +
					"<item><label>AZN</label><value>AZN</value></item><item><label>BDT</label><value>BDT</value></item><item><label>BIF</label><value>BIF</value></item><item><label>BOB</label><value>BOB</value></item>" +
					"<item><label>BTN</label><value>BTN</value></item><item><label>CDF</label><value>CDF</value></item><item><label>COP</label><value>COP</value></item><item><label>CRC</label><value>CRC</value></item>" +
					"<item><label>CUP</label><value>CUP</value></item><item><label>CVE</label><value>CVE</value></item><item><label>DJF</label><value>DJF</value></item><item><label>DOP</label><value>DOP</value></item>" +
					"<item><label>DZD</label><value>DZD</value></item><item><label>ECS</label><value>ECS</value></item><item><label>EGP</label><value>EGP</value></item><item><label>ERN</label><value>ERN</value></item>" +
					"<item><label>ETB</label><value>ETB</value></item><item><label>EURO</label><value>E</value></item><item><label>GEL</label><value>GEL</value></item><item><label>GHS</label><value>GHS</value></item><item><label>GMD</label><value>GMD</value></item>" +
					"<item><label>GNF</label><value>GNF</value></item><item><label>GWP</label><value>GWP</value></item><item><label>HNL</label><value>HNL</value></item><item><label>HTG</label><value>HTG</value></item>" +
					"<item><label>IDR</label><value>IDR</value></item><item><label>INR</label><value>INR</value></item><item><label>IQD</label><value>IQD</value></item><item><label>IRR</label><value>IRR</value></item>" +
					"<item><label>JOD</label><value>JOD</value></item><item><label>KES</label><value>KES</value></item><item><label>KGS</label><value>KGS</value></item><item><label>KHR</label><value>KHR</value></item>" +
					"<item><label>KPW</label><value>KPW</value></item><item><label>LAK</label><value>LAK</value></item><item><label>LBP</label><value>LBP</value></item><item><label>LKR</label><value>LKR</value></item><item><label>LRD</label><value>LRD</value></item>" +
					"<item><label>LSL</label><value>LSL</value></item><item><label>MGF</label><value>MGF</value></item><item><label>MMK</label><value>MMK</value></item><item><label>MRO</label><value>MRO</value></item>" +
					"<item><label>MWK</label><value>MWK</value></item><item><label>MZN</label><value>MZN</value></item><item><label>NAD</label><value>NAD</value></item><item><label>NGN</label><value>NGN</value></item>" +
					"<item><label>NIO</label><value>NIO</value></item><item><label>NIS</label><value>NIS</value></item><item><label>NPR</label><value>NPR</value></item><item><label>PAB</label><value>PAB</value></item>" +
					"<item><label>PEN</label><value>PEN</value></item><item><label>PHP</label><value>PHP</value></item><item><label>PKR</label><value>PKR</value></item><item><label>QTQ</label><value>QTQ</value></item>" +
					"<item><label>RWF</label><value>RWF</value></item><item><label>SBD</label><value>SBD</value></item><item><label>SDG</label><value>SDG</value></item><item><label>SLL</label><value>SLL</value></item>" +
					"<item><label>SOS</label><value>SOS</value></item><item><label>SSP</label><value>SSP</value></item><item><label>STD</label><value>STD</value></item><item><label>SVC</label><value>SVC</value></item>" +
					"<item><label>SYP</label><value>SYP</value></item><item><label>SZL</label><value>SZL</value></item><item><label>TGS</label><value>TGS</value></item><item><label>TZS</label><value>TZS</value></item>" +
					"<item><label>UGX</label><value>UGX</value></item><item><label>USD</label><value>$</value></item><item><label>WST</label><value>WST</value></item><item><label>XAF</label><value>XAF</value></item>" +
					"<item><label>XOF</label><value>XOF</value></item><item><label>YER</label><value>YER</value></item><item><label>ZAR</label><value>ZAR</value></item><item><label>ZMW</label><value>ZMW</value></item><item><label>ZWD</label><value>ZWD</value></item>";

			x_Form = "<input type=\"currency\" ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + " ><label>" + escapedLabelForContent
					+ "</label></input> " + "<select1 type=\"currency\" appearance=\"minimal\" ref=\"" + this.component.getName() + "_" + componentPosition + "_curr"
					+ "\"><label>" + "Currency (select one)" + "</label>" + values + "</select1>";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"decimal\" " + requiredString + readOnlyString
					+ calculated;
			this.xFormBindingBegin2 = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "_curr" + "\" type=\"select1\" " + requiredString ;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}

		if (fieldType == FormFieldType.RADIO_BUTTON) {
			String values = "";
			if(survey != null){
				values = survey.containsAdefaultValue() == null ? "<item><label>Select</label><value>0</value></item>" : "<item><label>Select</label><value>"+survey.containsAdefaultValue().getValue()+"</value></item>"; //empty value for first element of dropdown
				if ((this.survey.getValues() != null)) {
					if (this.survey.getValues().size() != 0) {
						List<SurveyElement> elements = this.survey.getValues();
						for (SurveyElement s : elements) {
							String escapedItem = outputter.escapeElementEntities(s.getValue());
							values = values + "<item><label>" + escapedItem + "</label><value>" + escapedItem + "</value></item>";
						}
						String reflistName = outputter.escapeAttributeEntities(this.survey.getName());
						x_Form = "<select1 appearance=\"full\" reflist=\""+ reflistName +"\" ref=\"" + this.component.getName() + "_" + componentPosition + "\"><label>" + escapedLabelForContent + "</label>"
								+ values + "</select1>";
						this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"select1\" " + " required=\"true()\""
								+ calculated;
					}
				}
			} else {
				x_Form = "<input ref=\"" + this.component.getName() + "_" + componentPosition + calculatedAttribute + "\"><label>" + escapedLabelForContent + "</label></input> ";
				this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"string\" readonly=\"true()\" " + requiredString
						+ calculated;
			}

			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"select1\" " + " required=\"true()\""
					+ readOnlyString + calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}

		if (fieldType == FormFieldType.PHONE_NUMBER_FIELD) {
			x_Form = "<input ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition
					+ "\" type=\"string\" jr:preload=\"property\" jr:preloadParams=\"phonenumber\" " + requiredString + readOnlyString + calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}
		if (fieldType == FormFieldType.TEXT_AREA) {
			x_Form = "<input type=\"tarea\" ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent
					+ "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"string\" " + requiredString
					+ " readonly=\"false()\" " + calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}

		if (fieldType == FormFieldType.TRUNCATED_TEXT) {

			String escapedLabelForAttribute = outputter.escapeAttributeEntities(this.component.getLabel());
			requiredString="required=\"false()\"";
			// System.out.println(escapedLabel);
			x_Form = "<input type=\"label\" ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><label>" + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"string\" readonly=\"true()\" " + requiredString
					+ calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}
		if (fieldType == FormFieldType.WRAPPED_TEXT) {

			String escapedLabelForAttribute = outputter.escapeAttributeEntities(this.component.getLabel());
			requiredString="required=\"false()\"";
			x_Form = "<input type=\"multi-label\" ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><label>" + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"string\" readonly=\"true()\" " + requiredString
					+ calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}
		//**************************************************** BARCODE
		
		if (fieldType == FormFieldType.BARCODE) {
			x_Form = "<input ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"barcode\" " + requiredString + readOnlyString
					+ calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}
		
		//****************************************************
		
		//**************************************************** IMAGE	
		if (fieldType == FormFieldType.IMAGE) {
		x_Form = "<input ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
		  this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"binary\" " + requiredString + readOnlyString
				+ calculated;
		  this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}
		//****************************************************
                
                
                //*********************Added by mureed******************************* SIGNATURE	
		if (fieldType == FormFieldType.SIGNATURE) {
		x_Form = "<input ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
		  this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"signature\" " + requiredString + readOnlyString
				+ calculated;
		  this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}
		//****************************************************
		
		if (fieldType == FormFieldType.GEOLOCATION) {
			x_Form = "<input ref=\"" + this.component.getName() + "_" + componentPosition + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"geopoint\" " + requiredString + readOnlyString
					+ calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}
		if (fieldType == FormFieldType.SEPARATOR) {
			x_Form = "</group><group appearance=\"field-list\">";
		}

		if (fieldType == FormFieldType.REPEATABLES) {
			if (survey != null) {
				String reflistName = outputter.escapeAttributeEntities(this.survey.getName());
				String inputs = "</group>";
				inputs = inputs + "<group value=\"repeatable-survey\" reflist=\""+reflistName+"\" ref=\"" + this.component.getName() + "_" + componentPosition + "\">";
				inputs = inputs + "<label>" + escapedLabelForContent + "</label>";
				// String bindings = new String();
				int indexS = 0;
				for (SurveyElement value : survey.getValues()) {
					String escapedValue = outputter.escapeElementEntities(value.getValue());
					inputs = inputs + "<group appearance=\"field-list\">";
					inputs = inputs + "<label>" + escapedValue + "</label>";
					for (PreviewComponent repComponents : repeatableComponents) {
						// a.zanchi: concats the name of component with the
						// survey name to get a complete ref
						repComponents.generateEmptyFormFieldWithKey(this.component.getName(), componentPosition, ("item"+indexS), value.getValue());
						// surveyelemToRefString.put(value,
						// repComponents.refString);

						inputs = inputs + repComponents.getFormField().getX_form();
						// bindings = bindings +
						// repComponents.xFormBindingBegin;
					}
					// closing for appearance group
					inputs = inputs + "</group>";
					indexS++;
				}
				inputs = inputs + "</group>";
				inputs = inputs + "<group appearance=\"field-list\">";
				x_Form = inputs;
				// this.xFormBindingBegin = bindings;
				this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\"" + calculated;
				this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
			}

		}
		if (fieldType == FormFieldType.REPEATABLES_BASIC) {
			String inputs = new String();
			String bindings = new String();
			for (PreviewComponent insideRepComponents : this.repeatableComponents) {
				insideRepComponents.generateEmptyFormFieldWithKey(this.component.getName(), componentPosition, "", "");
				inputs = inputs + insideRepComponents.getFormField().getX_form();
				// bindings = bindings + insideRepComponents.xFormBindingBegin;

			}
			if (this.isBasicContainerFixed) {
				bindings = "<bind nodeset=\"/data/count_" + this.component.getName() + "_" + componentPosition + "\" type=\"int\"/>" + bindings;
				x_Form = "</group><group ref=\"" + this.component.getName() + "_" + componentPosition + "\" >" + "<label>" + escapedLabelForContent + "</label>"
						+ "<repeat nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" jr:count=\"/data/count_" + this.component.getName() + "_"
						+ componentPosition + "\"" + " jr:noAddRemove=\"true()\"><group appearance=\"field-list\"" + ">" + inputs + "</group></repeat>"
						+ "</group><group appearance=\"field-list\">";
			} else {

				x_Form = "</group><group ref=\"" + this.component.getName() + "_" + componentPosition + "\" >" + "<label>" + escapedLabelForContent + "</label>"
						+ "<repeat nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\"><group appearance=\"field-list\">" + inputs + "</group></repeat>"
						+ "</group><group appearance=\"field-list\">";
			}
			this.xFormBindingBegin = bindings + "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\"" + calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}

		if (fieldType == FormFieldType.DROP_DOWN_LIST) {
			String values = "";
			if(survey != null){
				values =  survey.containsAdefaultValue() == null ? "<item><label>Select</label><value>0</value></item>" : "<item><label>Select</label><value>" + survey.containsAdefaultValue().getValue()+"</value></item>";
				if ((this.survey.getValues() != null)) {
					if (this.survey.getValues().size() != 0) {
						List<SurveyElement> elements = this.survey.getValues();
						for (SurveyElement s : elements) {
							String escapedItem = outputter.escapeElementEntities(s.getValue());
							values = values + "<item><label>" + escapedItem + "</label><value>" + escapedItem + "</value></item>";
						}
						String reflistName = outputter.escapeAttributeEntities(this.survey.getName());
						
						x_Form = "<select1 appearance=\"minimal\" reflist=\""+reflistName+"\" ref=\"" + this.component.getName() + "_" + componentPosition + "\"><label>" + escapedLabelForContent + "</label>"
								+ values + "</select1>";
						this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"select1\" " + requiredString + calculated;
					}
				}
			} else {
				x_Form = "<input ref=\"" + this.component.getName() + "_" + componentPosition + "\"><label>" + escapedLabelForContent + "</label></input> ";
				this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"string\" readonly=\"true()\" " + requiredString
						+ calculated;
			}

			this.xFormBindingBegin = "<bind nodeset=\"/data/" + this.component.getName() + "_" + componentPosition + "\" type=\"select1\" " + requiredString + calculated;
			this.refString = "/data/" + this.component.getName() + "_" + componentPosition;
		}

		if (false) {
			String head = /*
						 * "<?xml version=\"1.0\"?><h:html xmlns=\"http://www.w3.org/2002/xforms\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:jr=\"http://openrosa.org/javarosa\"><h:head><h:title>Basic</h:title><model>"
						 * + "<instance> <data id="+ index +
						 * "><StringData/><date>2010-06-15</date><phonenumber/><int/><regex>test@test.com</regex> <branch>n</branch><output/></data></instance> "
						 * +
						 * "<bind nodeset=\"/data/StringData\" type=\"string\" /><bind nodeset=\"/data/phonenumber\" type=\"string\" jr:preload=\"property\" jr:preloadParams=\"phonenumber\"  constraint=\"regex(.,'^(\\+?[0-9]+)$')\" jr:constraintMsg=\"this isn't a valid phonenumber \"/>"
						 * +
						 * "<bind nodeset=\"/data/output\" type=\"string\" readonly=\"true()\"relevant=\"selected(/data/branch, 'n')\"/> <bind nodeset=\"/data/date\" type=\"date\" /><bind nodeset=\"/data/int\" type=\"int\"/><bind nodeset=\"/data/regex\" type=\"string\" required=\"true()\" constraint=\"regex(., '[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}')\" jr:constraintMsg=\"this isn't a valid email address\" relevant=\"selected(/data/branch, 'n')\"/>"
						 * + "</model></h:head><h:body>"
						 */"<?xml version=\"1.0\"?><h:html xmlns=\"http://www.w3.org/2002/xforms\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:jr=\"http://openrosa.org/javarosa\">"
					+ "                      <h:head><h:title>Basic</h:title><model><instance> <data id="
					+ index
					+ "><StringData/><date>2010-06-15</date><phonenumber/><int/><regex>test@test.com</regex> <branch>n</branch><decimal>18.31</decimal><language/><output/>"
					+ "</data></instance> <bind nodeset=\"/data/StringData\" type=\"string\" />"
					+ "<bind nodeset=\"/data/phonenumber\" type=\"string\" />"
					+ "<bind nodeset=\"/data/date\" type=\"date\" />"
					+ "<bind nodeset=\"/data/int\" type=\"int\"/>"
					+ "<bind nodeset=\"/data/regex\" type=\"string\" required=\"true()\" constraint=\"regex(., '[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}')\" jr:constraintMsg=\"this isn't a valid email address\" relevant=\"selected(/data/branch, 'n')\"/>   "
					+ "<bind nodeset=\"/data/decimal\" type=\"decimal\" constraint=\". &gt; 0.01 and . &lt; \" jr:constraintMsg=\"number must be between 10.51 and 18.39\" />"
					+ "<bind nodeset=\"/data/output\" type=\"string\" readonly=\"true()\" relevant=\"selected(/data/branch, 'n')\"/></model></h:head><h:body>";

			x_Form = head + x_Form;
		}

		if (isTail) {
			String tail = "</group></h:body></h:html>";
			x_Form = x_Form + tail;
		}
		if (this.formField == null) {
			this.formField = new FormField(fieldType, this.component.getLabel(), x_Form, this.component.getName(), this.component.getRequired());

		} else {
			this.formField.setX_form(x_Form);

		}
		this.formField.setSurvey(this.survey);
		this.formField.setNumberOfRep(numberOfReps);
		this.formField.setReadOnly(this.component.isReadOnly());
		this.formField.setBindingsPolicy(this.bindingsPolicy);
		this.formField.setConstraintPolicy(this.constraintPolicy);
		this.formField.setRequired(this.component.getRequired());

		/*
		 * Fabaris.a_zanchi checks if the component is a repeatable container
		 * with fixed number of repetitions. If true sets to 0 the value of reps
		 * in formfield, as it means the formfield is an infinite repeatable
		 */
		if (!this.isBasicContainerFixed)
			this.formField.setNumberOfRep(0);

		this.isTail = isTail;
	}


	/**
	 * Fabaris_a.zanchi Creates an "empty" FormField when the component is new
	 * and doesnt have a representation on the DB. This method is for formfields
	 * in a repeatable container
	 * 
	 * @param keyName
	 *            : the name of the repeatable container
	 * @param keyIndex
	 *            : the index of the repeatable container
	 * @param surveyString
	 *            : the name of the survey to generate ref and binding. If the
	 *            repeatable sections is NOT tied to a survey it must be NULL or
	 *            ""
	 * 
	 */
	public void generateEmptyFormFieldWithKey(String keyName, int keyIndex, String surveyString, String surveyName) {

//		String surveyName = surveyString;

		/*
		 * Fabaris_a.aknai implementing calculated in the xform saved on the db
		 */
		String calculated = new String();
		String calculatedAttribute = new String(); // Fabaris_a.zanchi
													// calculated formula to put
													// in attribute
		if (this.isCalculated && this.formula != null) {
			calculated = this.formula.toXFormString(this);
			calculatedAttribute = this.formula.toFormulaString(); // Fabaris_a.zanchi
		}
		if (!calculated.equals("")) {
			calculated = " calculate=\"" + calculated + "\" ";
		}

		String readOnlyString = "";
		if (this.component.isReadOnly() || !calculated.equals("")) {
			readOnlyString = " readonly=\"true()\" ";
		}

		FormFieldType fieldType = FComponent.getFieldType(this.getComponent().getClass());
		String x_Form = new String();

		if (surveyString == null || surveyString.equals("")) {
			surveyString = "";
		} else {
			surveyString = surveyString + "/";
		}

		// a.zanchi this refPath has a value different from "" if the component
		// is inside a repeatable section tied with a survey
		String refPath = new String();
		if (!surveyString.equals("")) {
			refPath = /* keyName + "_" + keyIndex + "/" + */surveyString; // +
		}

		XMLOutputter outputter = new XMLOutputter();
		String escapedLabelForContent = outputter.escapeElementEntities(this.component.getLabel());
		if (escapedLabelForContent.equals("")) escapedLabelForContent=" ";

		// Fabaris_a.zanchi escaping of calculated attribute
		if (!calculated.equals("")) {
			calculatedAttribute = outputter.escapeAttributeEntities(calculatedAttribute);
			calculatedAttribute = " calc=\"" + calculatedAttribute + "\" "; // Fabaris_a.zanchi
		}
		String required = " required=\"true()\"";
		if (this.component.getRequired()) {
			required = " required=\"true()\"";
		}else{
			required = " required=\"false()\"";
		}
		if (this.component.isReadOnly() || !calculated.equals("")) {
			readOnlyString = " readonly=\"true()\"";
			if (this.component.getRequired()) {
				required = " required=\"true()\"";
			}else{
				required = " required=\"false()\"";
			}
		}
		
		if (fieldType == FormFieldType.CHECK_BOX) {
			x_Form = x_Form + "<select ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><item><label>" + escapedLabelForContent
					+ "</label><value>true</value></item></select> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"select\" " + readOnlyString
					+ calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();
			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}

		if (fieldType == FormFieldType.CURRENCY_FIELD) {
			String values = "<item><label>Select</label><value>0</value></item><item><label>AFN</label><value>AFN</value></item><item><label>AMD</label><value>AMD</value></item><item><label>AOA</label><value>AOA</value></item>" +
					"<item><label>AZN</label><value>AZN</value></item><item><label>BDT</label><value>BDT</value></item><item><label>BIF</label><value>BIF</value></item><item><label>BOB</label><value>BOB</value></item>" +
					"<item><label>BTN</label><value>BTN</value></item><item><label>CDF</label><value>CDF</value></item><item><label>COP</label><value>COP</value></item><item><label>CRC</label><value>CRC</value></item>" +
					"<item><label>CUP</label><value>CUP</value></item><item><label>CVE</label><value>CVE</value></item><item><label>DJF</label><value>DJF</value></item><item><label>DOP</label><value>DOP</value></item>" +
					"<item><label>DZD</label><value>DZD</value></item><item><label>ECS</label><value>ECS</value></item><item><label>EGP</label><value>EGP</value></item><item><label>ERN</label><value>ERN</value></item>" +
					"<item><label>ETB</label><value>ETB</value></item><item><label>EURO</label><value>E</value></item><item><label>GEL</label><value>GEL</value></item><item><label>GHS</label><value>GHS</value></item><item><label>GMD</label><value>GMD</value></item>" +
					"<item><label>GNF</label><value>GNF</value></item><item><label>GWP</label><value>GWP</value></item><item><label>HNL</label><value>HNL</value></item><item><label>HTG</label><value>HTG</value></item>" +
					"<item><label>IDR</label><value>IDR</value></item><item><label>INR</label><value>INR</value></item><item><label>IQD</label><value>IQD</value></item><item><label>IRR</label><value>IRR</value></item>" +
					"<item><label>JOD</label><value>JOD</value></item><item><label>KES</label><value>KES</value></item><item><label>KGS</label><value>KGS</value></item><item><label>KHR</label><value>KHR</value></item>" +
					"<item><label>KPW</label><value>KPW</value></item><item><label>LAK</label><value>LAK</value></item><item><label>LBP</label><value>LBP</value></item><item><label>LKR</label><value>LKR</value></item><item><label>LRD</label><value>LRD</value></item>" +
					"<item><label>LSL</label><value>LSL</value></item><item><label>MGF</label><value>MGF</value></item><item><label>MMK</label><value>MMK</value></item><item><label>MRO</label><value>MRO</value></item>" +
					"<item><label>MWK</label><value>MWK</value></item><item><label>MZN</label><value>MZN</value></item><item><label>NAD</label><value>NAD</value></item><item><label>NGN</label><value>NGN</value></item>" +
					"<item><label>NIO</label><value>NIO</value></item><item><label>NIS</label><value>NIS</value></item><item><label>NPR</label><value>NPR</value></item><item><label>PAB</label><value>PAB</value></item>" +
					"<item><label>PEN</label><value>PEN</value></item><item><label>PHP</label><value>PHP</value></item><item><label>PKR</label><value>PKR</value></item><item><label>QTQ</label><value>QTQ</value></item>" +
					"<item><label>RWF</label><value>RWF</value></item><item><label>SBD</label><value>SBD</value></item><item><label>SDG</label><value>SDG</value></item><item><label>SLL</label><value>SLL</value></item>" +
					"<item><label>SOS</label><value>SOS</value></item><item><label>SSP</label><value>SSP</value></item><item><label>STD</label><value>STD</value></item><item><label>SVC</label><value>SVC</value></item>" +
					"<item><label>SYP</label><value>SYP</value></item><item><label>SZL</label><value>SZL</value></item><item><label>TGS</label><value>TGS</value></item><item><label>TZS</label><value>TZS</value></item>" +
					"<item><label>UGX</label><value>UGX</value></item><item><label>USD</label><value>$</value></item><item><label>WST</label><value>WST</value></item><item><label>XAF</label><value>XAF</value></item>" +
					"<item><label>XOF</label><value>XOF</value></item><item><label>YER</label><value>YER</value></item><item><label>ZAR</label><value>ZAR</value></item><item><label>ZMW</label><value>ZMW</value></item><item><label>ZWD</label><value>ZWD</value></item>";

			x_Form = "<input type=\"currency\" ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + " ><label>" + escapedLabelForContent
					+ "</label></input> " + "<select1 type=\"currency\" appearance=\"minimal\" ref=\"" + refPath + this.getComponent().getName() + "_curr" + "\"><label>"
					+ "Currency (select one)" + "</label>" + values + "</select1>";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"decimal\" " + required
					+ calculated + readOnlyString;
			this.xFormBindingBegin2 = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "_curr" + "\" type=\"select1\" "
					+ required ;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();

			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
				this.surveyelemToBindingBegin2.put(surveyName, this.xFormBindingBegin2);
			}
		}

		if (fieldType == FormFieldType.DROP_DOWN_LIST) {
			String values = "";
			if(survey != null){
				values = survey.containsAdefaultValue() == null ? "<item><label>Select</label><value>0</value></item>" : "<item><label>Select</label><value>"+survey.containsAdefaultValue().getValue()+"</value></item>";
				if ((this.survey.getValues() != null)) {
					if (this.survey.getValues().size() != 0) {
						List<SurveyElement> elements = this.survey.getValues();
						for (SurveyElement s : elements) {
							String escapedValue = outputter.escapeElementEntities(s.getValue());
							values = values + "<item><label>" + escapedValue + "</label><value>" + escapedValue + "</value></item>";
						}
						String reflistName = outputter.escapeAttributeEntities(this.survey.getName());
						x_Form = "<select1 appearance=\"minimal\" reflist=\""+reflistName+"\" ref=\"" + refPath + this.getComponent().getName() + "\"><label>" + escapedLabelForContent + "</label>" + values
								+ "</select1>"; 
						this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"select1\" "
								+ readOnlyString + calculated;
					}
				} 
			}else {
				x_Form = "<input ref=\"" + refPath + this.getComponent().getName() + "\"><label>" + escapedLabelForContent + "</label></input> ";
				this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName()
						+ "\" type=\"string\" readonly=\"true()\" " + readOnlyString + calculated;
			}

			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"select1\"" + required
					+ readOnlyString + calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();

			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}

		if (fieldType == FormFieldType.RADIO_BUTTON) {
			String values = "";
			if(this.survey != null){
				values = survey.containsAdefaultValue() == null ? "<item><label>Select</label><value>0</value></item>" : "<item><label>Select</label><value>"+survey.containsAdefaultValue().getValue()+"</value></item>";
				if ((this.survey.getValues() != null)) {
					if (this.survey.getValues().size() != 0) {
						List<SurveyElement> elements = this.survey.getValues();
						for (SurveyElement s : elements) {
							String escapedValue = outputter.escapeElementEntities(s.getValue());
							values = values + "<item><label>" + escapedValue + "</label><value>" + escapedValue + "</value></item>";
						}
						String reflistName = outputter.escapeAttributeEntities(this.survey.getName());
						x_Form = "<select1 appearance=\"full\" reflist=\""+reflistName+"\" ref=\"" + refPath + this.getComponent().getName() + "\"><label>" + escapedLabelForContent + "</label>" + values
								+ "</select1>";
						this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"select1\" "
								+ readOnlyString + calculated;
					}
				}
			} else {
				x_Form = "<input ref=\"" + refPath + this.getComponent().getName() + "\"><label>" + escapedLabelForContent + "</label></input> ";
				this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName()
						+ "\" type=\"string\" readonly=\"true()\" " + readOnlyString + calculated;
			}

			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"select1\"" + required
					+ readOnlyString + calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();

			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}

		if (fieldType == FormFieldType.TEXT_FIELD) {
			x_Form = x_Form + "<input ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"string\"" + required
					+ readOnlyString + calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();
			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}
		/*
		 * if (fieldType == FormFieldType.CURRENCY_FIELD) {
		 * 
		 * }
		 */
		if (fieldType == FormFieldType.DATE_FIELD) {
			x_Form = x_Form + "<input ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"date\"" + required
					+ readOnlyString + calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();
			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}
		if (fieldType == FormFieldType.EMAIL_FIELD) {
			x_Form = x_Form + "<input ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"string\" " + required
					+ readOnlyString + calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();
			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}
		
		if (fieldType == FormFieldType.NUMERIC_TEXT_FIELD) {
//			System.out.println("----numeric field parsing----");
			try{
				x_Form = x_Form + "<input ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
				this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"decimal\"" + required
						+ readOnlyString + calculated;
				this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();
//				System.out.println("RefString:"+this.refString);
//				System.out.println("XFORM:"+"<input ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ");
//				System.out.println("bindingBegin:"+xFormBindingBegin);
				if (!surveyString.equals("")) {
					this.hasManyRefs = true;
					this.surveyelemToRefString.put(surveyName, this.refString);
					this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
//			System.out.println("----numeric field parsed end----");
		}

		if (fieldType == FormFieldType.PHONE_NUMBER_FIELD) {
			x_Form = x_Form + "<input ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName()
					+ "\" type=\"string\" jr:preload=\"property\" jr:preloadParams=\"phonenumber\" " + required + readOnlyString + calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();

			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}
		if (fieldType == FormFieldType.TEXT_AREA) {
			x_Form = x_Form + "<input type=\"tarea\" ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent
					+ "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"string\"" + required
					+ " readonly=\"false()\" " + calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();
			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}	
		}

		if (fieldType == FormFieldType.TRUNCATED_TEXT) {
			String escapedLabel = outputter.escapeAttributeEntities(this.component.getLabel());
			required="required=\"true()\"";
			// labelvalue=\""+escapedLabel+"\"
			x_Form = x_Form + "<input type=\"label\" ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"string\" readonly=\"true()\" "
					+ required + calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();

			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}

		if (fieldType == FormFieldType.GEOLOCATION) {
			x_Form = "<input ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"geopoint\" " + readOnlyString
					+ calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();
			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}
		//*********************************
		if (fieldType == FormFieldType.BARCODE) {
			x_Form = "<input ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"barcode\" " + readOnlyString
					+ calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();
			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}
		//*********************************
		if (fieldType == FormFieldType.IMAGE) {
			x_Form = "<input ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"binary\" " + readOnlyString
					+ calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();
			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}
		//*********************************
                
                // Added by Mureed
                //*********************************
		if (fieldType == FormFieldType.SIGNATURE) {
			x_Form = "<input ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + escapedLabelForContent + "</label></input> ";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"signature\" " + readOnlyString
					+ calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();
			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}
		//*********************************
		
		if (fieldType == FormFieldType.WRAPPED_TEXT) {
			String escapedLabel = outputter.escapeAttributeEntities(this.component.getLabel());
			required=" required=\"true()\" ";
			x_Form = x_Form + "<input type=\"multi-label\" ref=\"" + refPath + this.getComponent().getName() + "\"" + calculatedAttribute + "><label>" + "</label></input>";
			this.xFormBindingBegin = "<bind nodeset=\"/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName() + "\" type=\"string\" readonly=\"true()\""
					+ required + calculated;
			this.refString = "/data/" + keyName + "_" + keyIndex + "/" + surveyString + this.component.getName();
			if (!surveyString.equals("")) {
				this.hasManyRefs = true;
				this.surveyelemToRefString.put(surveyName, this.refString);
				this.surveyelemToBindingBegin.put(surveyName, this.xFormBindingBegin);
			}
		}
		if (fieldType == FormFieldType.SEPARATOR) {

		}

		if (this.formField == null) {
			this.formField = new FormField(fieldType, this.component.getLabel(), x_Form, this.component.getName(), this.component.getRequired());

		} else {
			this.formField.setX_form(x_Form);
		}
		this.formField.setSurvey(this.survey);
		this.formField.setReadOnly(this.component.isReadOnly());
		this.formField.setBindingsPolicy(this.bindingsPolicy);
		this.formField.setConstraintPolicy(this.constraintPolicy);
		this.formField.setRequired(this.component.getRequired());

	}

	/**
	 * This method get FormFields from binded previewComponents and assigns them
	 * to this component's formfield
	 * 
	 * Fabaris_a.zanchi Correctly pouplates the FormField of this component
	 */
	public void populateFormField() {

		// bindings
		HashMap<FormField, ArrayList<BindingContainer>> pass = new HashMap<FormField, ArrayList<BindingContainer>>();

		for (PreviewComponent c : this.componentToBindType.keySet()) {
			pass.put(c.getFormField(), this.componentToBindType.get(c));
			for (BindingContainer b : this.componentToBindType.get(c))
				this.formField.addBinding(c.getFormField(), b);
		}

		this.formField.deleteUnusedBindings(pass);

		// Constraints
		for (ConstraintContainer cont : this.constraints) {
			this.formField.addConstraint(cont);
		}
		this.formField.deleteUnusedConstraints(constraints);

		if (this.getType() == FormFieldType.REPEATABLES || this.getType() == FormFieldType.REPEATABLES_BASIC) {
			for (PreviewComponent rep : this.getRepeatables()) {
				rep.populateFormField();
			}
		}

	}

	/**
	 * private accessor collector for two methods
	 * 
	 * @param surveyElement
	 * @param andOr
	 *            Indicates the binding policy "and" "or"
	 * @return
	 */
	private String getBindStringPolicy(String surveyElement, String andOr) {
		boolean hasNoBindings = false;
		boolean hasActiveBindings = false;

		String beginRelevant = null;
		String beginRelevant2 = null;

		// check for active bindings
		for (PreviewComponent p : componentToBindType.keySet()) {
			for (BindingContainer bc : componentToBindType.get(p)) {
				if (bc.getbType() != BindType.NOT_ACTIVE)
					hasActiveBindings = true;
			}

		}

		// this is the case where the elemente has no binding and is not a
		// repeatable container: it returns immediately
		if (componentToBindType.size() == 0 && !(this.getType() == FormFieldType.REPEATABLES || this.getType() == FormFieldType.REPEATABLES_BASIC) && !hasActiveBindings) {
			if (!this.hasManyRefs) {
				if (this.getType() != FormFieldType.CURRENCY_FIELD)
					return this.xFormBindingBegin + this.getConstraintStringAndOrPolicy() + "/>";
				else
					return this.xFormBindingBegin + this.getConstraintStringAndOrPolicy() + "/>" + this.xFormBindingBegin2 + "/>";
			} else {
				if (this.getType() != FormFieldType.CURRENCY_FIELD)
					return this.surveyelemToBindingBegin.get(surveyElement) + this.getConstraintStringAndOrPolicy() + "/>";
				else
					return this.surveyelemToBindingBegin.get(surveyElement) + this.getConstraintStringAndOrPolicy() + "/>" + this.surveyelemToBindingBegin2.get(surveyElement)
							+ "/>";
			}
		}
		// this is the case where the element has no binding but it is a
		// repeatalbe container so the process goes on.
		if (componentToBindType.size() == 0 && (this.getType() == FormFieldType.REPEATABLES || this.getType() == FormFieldType.REPEATABLES_BASIC)) {
			beginRelevant = "";
			hasNoBindings = true;
		} else {
			beginRelevant = " relevant=\"";
		}
		String firstBinding = "";
		
		//check if component is in repeatable container
		boolean isRepeatable = false;
		if (this.parentComponent != null)
			isRepeatable = true;
		
		for (PreviewComponent pc : this.componentToBindType.keySet()) {
			
			String mutableRef = pc.refString;
			if (isRepeatable && parentComponent.getRepeatables().contains(pc))
				mutableRef = "./../" + pc.getNameWithIndexFromRef();

			for (BindingContainer bCont : this.componentToBindType.get(pc)) {
				
				String bContValue = new String();
				if (bCont.getValue() != null && !bCont.getValue().equals("")) {
					try {
						Double.parseDouble(bCont.getValue());
						bContValue = bCont.getValue();
					} catch (Exception e) {
						bContValue = "'"+bCont.getValue()+"'";
					}
				}
				if (bCont.getbType().equals(BindType.NOT_NULL)) {
					if (!pc.hasManyRefs)
						beginRelevant = beginRelevant + " " + firstBinding + " " +  mutableRef + " != ''";
					else {
						beginRelevant = beginRelevant + " " + firstBinding + " " + pc.surveyelemToRefString.get(surveyElement) + " != ''";
					}
				}
				if (bCont.getbType().equals(BindType.EQUALS)) {
					if (!pc.hasManyRefs)

						beginRelevant = beginRelevant + " " + firstBinding + " " + mutableRef + " = " + bContValue;
					else {
						beginRelevant = beginRelevant + " " + firstBinding + " " + pc.surveyelemToRefString.get(surveyElement) + " = " + bContValue;
					}
				}
				if (bCont.getbType().equals(BindType.NOT_EQUALS)) {
					if (!pc.hasManyRefs)
						beginRelevant = beginRelevant + " " + firstBinding + " " + mutableRef + " != " + bContValue;
					else {
						beginRelevant = beginRelevant + " " + firstBinding + " " + pc.surveyelemToRefString.get(surveyElement) + " != " + bContValue;
					}
				}
				if (bCont.getbType().equals(BindType.GREATER_THAN)) {
					if (!pc.hasManyRefs)
						beginRelevant = beginRelevant + " " + firstBinding + " " + mutableRef + " &gt; " + bContValue;
					else {
						beginRelevant = beginRelevant + " " + firstBinding + " " + pc.surveyelemToRefString.get(surveyElement) + " &gt; " + bContValue;
					}
				}
				if (bCont.getbType().equals(BindType.GREATER_EQUALS_THAN)) {
					if (!pc.hasManyRefs)
						beginRelevant = beginRelevant + " " + firstBinding + " " + mutableRef + " &gt;= " + bContValue;
					else {
						beginRelevant = beginRelevant + " " + firstBinding + " " + pc.surveyelemToRefString.get(surveyElement) + " &gt;= "+ bContValue;
					}
				}

				if (bCont.getbType().equals(BindType.LESS_THAN)) {
					if (!pc.hasManyRefs)
						beginRelevant = beginRelevant + " " + firstBinding + " " + mutableRef + " &lt; " + bContValue;
					else {
						beginRelevant = beginRelevant + " " + firstBinding + " " + pc.surveyelemToRefString.get(surveyElement) + " &lt; " + bContValue;
					}
				}
				if (bCont.getbType().equals(BindType.LESS_EQUALS_THAN)) {
					if (!pc.hasManyRefs)
						beginRelevant = beginRelevant + " " + firstBinding + " " + mutableRef + " &lt;= " + bContValue;
					else {
						beginRelevant = beginRelevant + " " + firstBinding + " " + pc.surveyelemToRefString.get(surveyElement) + " &lt;= " + bContValue;
					}
				}
				if (bCont.getbType().equals(BindType.NULL)) {
					if (!pc.hasManyRefs)
						beginRelevant = beginRelevant + " " + firstBinding + " " +  mutableRef + " = ''";
					else {
						beginRelevant = beginRelevant + " " + firstBinding + " " + pc.surveyelemToRefString.get(surveyElement) + " = ''";
					}
				}
				if (bCont.getbType().equals(BindType.IS_CHECKED)) {
					if (!pc.hasManyRefs) {
						//beginRelevant = beginRelevant + " " + firstBinding + " " + "selected(" +  mutableRef + " , " + "'yes')";
						beginRelevant = beginRelevant + " " + firstBinding + " " + mutableRef + " = 'true'";
					}
					else {
						//beginRelevant = beginRelevant + " " + firstBinding + " " + "selected(" + pc.surveyelemToRefString.get(surveyElement) + " , " + "'yes')";
						beginRelevant = beginRelevant + " " + firstBinding + " " + pc.surveyelemToRefString.get(surveyElement) + " = 'true'";
					}
				}

				firstBinding = andOr;
			}

		}

		// Sets the relevant string for currency dropbox
		if (hasNoBindings)
			beginRelevant2 = beginRelevant + "/>";
		else
			beginRelevant2 = beginRelevant + "\" />";

		// Move this part to the end
		if (this.getType() == FormFieldType.REPEATABLES || this.getType() == FormFieldType.REPEATABLES_BASIC) {
			if (hasNoBindings) {
				beginRelevant = beginRelevant + this.getConstraintStringAndOrPolicy() + "/>";
			} else {
				beginRelevant = beginRelevant + "\"" + this.getConstraintStringAndOrPolicy() + "/>";
			}
			if (this.getType() == FormFieldType.REPEATABLES) {
				for (SurveyElement s : this.getSurvey().getValues()) {
					for (PreviewComponent pc : this.getRepeatables()) {
						beginRelevant = beginRelevant + pc.getBindStringPolicy(s.getValue(), andOr);

					}
				}
			} else {
				for (PreviewComponent pc : this.getRepeatables()) {
					beginRelevant = beginRelevant + pc.getBindStringPolicy(null, andOr);

				}
			}
		} else {
			beginRelevant = beginRelevant + "\"" + this.getConstraintStringAndOrPolicy() + "/>";
		}

		// checks if component has many refs, if true i will return the right
		// binding string
		if (!this.hasManyRefs) {
			if (this.getType() != FormFieldType.CURRENCY_FIELD)
				return this.xFormBindingBegin + beginRelevant;
			else
				return this.xFormBindingBegin + beginRelevant + this.xFormBindingBegin2 + beginRelevant2;
		} else {
			if (this.getType() != FormFieldType.CURRENCY_FIELD)
				return this.surveyelemToBindingBegin.get(surveyElement) + beginRelevant;
			else
				return this.surveyelemToBindingBegin.get(surveyElement) + beginRelevant + this.surveyelemToBindingBegin2.get(surveyElement) + beginRelevant2;
		}
	}

	/**
	 * Fabaris_a.zanchi Methods returning a String with all the bindings
	 * informations of this Field Component When multiple bindings are active on
	 * this component, they are bounded following an && policy
	 * 
	 * @params surveyElement A string with the current survey repetition, null
	 *         if component is not in a repeatable container with survey
	 * @return
	 */
	private String getBindStringAndPolicy(String surveyElement) {
		return getBindStringPolicy(surveyElement, "and");
	}

	/**
	 * Fabaris_a.zanchi Methods returning a String with all the bindings
	 * informations of this Field Component When multiple bindings are active on
	 * this component, they are bounded following an || policy
	 * 
	 * @params surveyElement A string with the current survey repetition, null
	 *         if component is not in a repeatable container with survey
	 * @return
	 */
	private String getBindStringOrPolicy(String surveyElement) {
		return getBindStringPolicy(surveyElement, "or");

	}

	
	/**Fabaris_a.zanchi Entry point method to retrieve the bind string of this component
	 * 
	 * @return
	 */
	public String getBindStringAndOrPolicy() {
		this.deleteInvalidBindings(); //deletes invalid bindings to ensure the xform will be valid
		
		if (this.bindingsPolicy.equals(FormField.AND_POLICY))
			return getBindStringAndPolicy(null);
		if (this.bindingsPolicy.equals(FormField.OR_POLICY))
			return getBindStringOrPolicy(null);
		return getBindStringAndPolicy(null);
	}

	private String getConstrainStringPolicy(String andOr) {
		String constraint = null;
		boolean isConstraintOpen = false;
		boolean hasConstraintOnType = false;
		if (this.getType() == FormFieldType.EMAIL_FIELD) {
			constraint = " constraint=\"regex(., '[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}')\" jr:constraintMsg=\"this isn't a valid email address";
			hasConstraintOnType = true;
			isConstraintOpen = true;
		} else if (this.getType() == FormFieldType.PHONE_NUMBER_FIELD) {
			constraint = "  constraint=\"regex(.,'^(\\+?[0-9]+)$')\" jr:constraintMsg=\"this isn't a valid phonenumber ";
			hasConstraintOnType = true;
			isConstraintOpen = true;
		} else if (this.constraints.size() != 0) {
			constraint = " constraint=\"";
			isConstraintOpen = true;
		} else
			constraint = "";
		String firstConstraint = "";
		if (hasConstraintOnType)
			firstConstraint = " " + andOr + " ";
		for (ConstraintContainer cont : this.constraints) {
			String contValue = new String();
			if (cont.getValue() != null && !cont.getValue().equals("")) {
				try {
					Double.parseDouble(cont.getValue());
					contValue = cont.getValue();
				} catch (Exception e) {
					contValue = "'"+cont.getValue()+"'";
				}
			}
			
			if (cont.getcNumber() == ConstraintNumber.EQUALS) {
				constraint = constraint + firstConstraint + ". = " + contValue;
			}
			if (cont.getcNumber() == ConstraintNumber.NOT_EQUALS) {
				constraint = constraint + firstConstraint + ". != "  + contValue;
			}
			if (cont.getcNumber() == ConstraintNumber.GREATER_THAN) {
				constraint = constraint + firstConstraint + ". &gt; "  + contValue;
			}
			if (cont.getcNumber() == ConstraintNumber.GREATER_EQUALS_THAN) {
				constraint = constraint + firstConstraint + ". &gt;= "  + contValue;
			}
			if (cont.getcNumber() == ConstraintNumber.LESS_THAN) {
				constraint = constraint + firstConstraint + ". &lt; "  + contValue;
			}
			if (cont.getcNumber() == ConstraintNumber.LESS_EQUALS_THAN) {
				constraint = constraint + firstConstraint + ". &lt;= "  + contValue;
			}
			
			if(cont.getcNumber() == ConstraintNumber.MAXIMUM_NUMBER_OF_CHARACTER){
				constraint = constraint + firstConstraint + "string-length(.) &lt; " + contValue;
			}
			
			if(cont.getcNumber() == ConstraintNumber.MINIMUM_NUMBER_OF_CHARACTER){
				constraint = constraint + firstConstraint + "string-length(.) &gt; " + contValue;
				System.out.println(constraint);
			}
			
			firstConstraint = " " + andOr + " ";
		}

		if (isConstraintOpen) {
			constraint = constraint + "\"";
		}
		return constraint;
	}

	private String getConstraintStringAndPolicy() {
		return getConstrainStringPolicy("and");
	}

	private String getConstraintStringOrPolicy() {
		return getConstrainStringPolicy("or");
	}

	public String getConstraintStringAndOrPolicy() {
		this.deleteInvalidConstraints(); //checks and deletes invalid constraints
		
		if (this.getConstraintPolicy().equals(FormField.AND_POLICY))
			return getConstraintStringAndPolicy();
		if (this.getConstraintPolicy().equals(FormField.OR_POLICY))
			return this.getConstraintStringOrPolicy();
		return getConstraintStringAndPolicy();
	}

	public boolean isRepetableContainer() {
		return this.isRepetableContainer;
	}

	public void setRepetableContainer(boolean bool) {
		this.isRepetableContainer = bool;
	}

	/**
	 * Fabaris_a.zanchi Note: You can use this method to retrieve the
	 * formfieldType when a FormFild has not been assigned yet to this preview
	 * component.
	 * 
	 * @return returns the FormFieldType
	 */
	public FormFieldType getType() {
		return FComponent.getFieldType(this.getComponent().getClass());
	}

	public void addRepeatable(PreviewComponent pc) {
		this.repeatableComponents.add(pc);
		pc.setParentComponent(this);
	}

	public void removeRepeatable(PreviewComponent pc) {
		this.repeatableComponents.remove(pc);
		/*
		 * if (this.formField != null) {
		 * this.formField.removeRepeatable(pc.getFormField()); }
		 */
	}

	public void addRepeatable(int index, PreviewComponent pc) {
		this.repeatableComponents.add(index, pc);
	}

	public List<PreviewComponent> getRepeatables() {
		return this.repeatableComponents;
	}

	/**
	 * Method to retrieve the form fields of the repeatable components in this
	 * container
	 * 
	 * @return
	 */
	public List<FormField> getRepeteablesFormFields() {
		List<FormField> repFields = new ArrayList<FormField>();
		for (PreviewComponent pc : this.repeatableComponents) {
			repFields.add(pc.getFormField());
		}
		return repFields;
	}

	/**
	 * Fabaris_a.zanchi
	 * 
	 * Method to obtain the whole instance string for repeatable components
	 * 
	 * @return
	 */
	public String getInstanceStringForRepeatables() {
		String instance = new String();
		for (PreviewComponent pc : this.repeatableComponents) {
			if (pc.getType() == FormFieldType.WRAPPED_TEXT || pc.getType() == FormFieldType.TRUNCATED_TEXT) {
				instance = instance + "<" + pc.getFormField().getName() + ">" + pc.getFormField().getLabel() + "</" + pc.getFormField().getName() + ">";
			} else if (pc.getType() == FormFieldType.CURRENCY_FIELD) {
				instance = instance + "<" + pc.getFormField().getName() + "/>" + "<" + pc.getFormField().getName() + "_curr" + "/>";
			} else {
				instance = instance + "<" + pc.getFormField().getName() + "/>";
			}
		}
		return instance;
	}

	/**
	 * Fabaris_a.zanchi
	 * 
	 * Method to obtain the whiole instance string for repeatable components
	 * tied with a survey
	 * 
	 * @return
	 */
	public String getInstanceStringForRepetablesWithSurvey() {
		if (survey != null) {
			String instance = new String();
			int index = 0;
			for (SurveyElement value : this.survey.getValues()) {
				instance = instance + "<item" + index + ">";
				for (PreviewComponent pc : this.repeatableComponents) {
					if (pc.getType() == FormFieldType.WRAPPED_TEXT || pc.getType() == FormFieldType.TRUNCATED_TEXT) {
						instance = instance + "<" + pc.getFormField().getName() + ">" + pc.getFormField().getLabel() + "</" + pc.getFormField().getName() + ">";
					} else if (pc.getType() == FormFieldType.CURRENCY_FIELD) {
						instance = instance + "<" + pc.getFormField().getName() + "/>" + "<" + pc.getFormField().getName() + "_curr" + "/>";
					} else {
						instance = instance + "<" + pc.getFormField().getName() + "/>";
					}
				}
				instance = instance + "</item" + index + ">";
				index++;
			}
			return instance;
		} else {
			return "";
		}
	}

	/**
	 * Fabaris_a.zanchi
	 * 
	 * checks wether this component has a multiple relevant binding on the same
	 * component
	 */
	public boolean hasMultipleRelevantOnSameComponent() {
		boolean ret = false;
		for (PreviewComponent otherComp : componentToBindType.keySet()) {
			if (componentToBindType.get(otherComp).size() > 1) {
				ret = true;
				break;
			}

		}
		return ret;
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public boolean isDefaultComponent() {
		return isDefaultComponent;
	}

	public void setDefaultComponent(boolean isDefaultComponent) {
		this.isDefaultComponent = isDefaultComponent;
               
                
	}

	public int getNumberOfReps() {
		return numberOfReps;
	}

	public void setNumberOfReps(int numberOfReps) {
		this.numberOfReps = numberOfReps;
	}

	public boolean isBasicContainerFixed() {
		return isBasicContainerFixed;
	}

	public void setBasicContainerFixed(boolean isBasicContainerFixed) {
		this.isBasicContainerFixed = isBasicContainerFixed;
	}

	public List<ConstraintContainer> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<ConstraintContainer> constraints) {
		this.constraints = constraints;
	}

	public boolean isCalculated() {
		return isCalculated;
	}

	public void setCalculated(boolean isCalculated) {
		this.isCalculated = isCalculated;
	}

	public FormFormula getFormula() {
		return formula;
	}

	public void setFormula(FormFormula formula) {
		this.formula = formula;
	}

	public String getConstraintPolicy() {
		return constraintPolicy;
	}

	public void setConstraintPolicy(String constraintPolicy) {
		if (constraintPolicy == null)
			this.constraintPolicy = FormField.AND_POLICY;
		else
			this.constraintPolicy = constraintPolicy;
	}

	/**
	 * Fabaris_A.zanchi
	 * 
	 * @return the policy of the relevant bindings on this components under the
	 *         form of a String "All" or "Any"
	 */
	public String getBindingsPolicy() {
		return bindingsPolicy;
	}

	public void setBindingsPolicy(String bindingsPolicy) {
		if (bindingsPolicy == null)
			this.bindingsPolicy = FormField.AND_POLICY;
		else
			this.bindingsPolicy = bindingsPolicy;
	}

	/**Returns wheter the provided component is in the bindings list
	 * 
	 * @param pc PreviewComponent to check 
	 * @return 
	 */
	public boolean hasComponentInBindings(PreviewComponent pc) {
		if (componentToBindType.keySet().contains(pc))
			return true;
		else
			return false;
	}

	public PreviewComponent getParentComponent() {
		return parentComponent;
	}

	public void setParentComponent(PreviewComponent parentComponent) {
		if (this.parentComponent == null)
				this.parentComponent = parentComponent;
	}
	
	/**Gets the last part of the ref string, the one composed by "name_position"
	 * 
	 * @return the name_index, null if the ref string is null
	 */
	public String getNameWithIndexFromRef() {
		
		if (this.refString != null) {
			return this.refString.substring(this.refString.lastIndexOf("/")+1);
		}
		else 
			return null;
	}
	
	/**Fabaris_A.zanchi
	 * Method deleting the invalid bindings before creating the xform.
	 */
	private void deleteInvalidBindings() {
		HashMap<PreviewComponent, ArrayList<BindingContainer>> temp = new HashMap<PreviewComponent, ArrayList<BindingContainer>>();
		for (PreviewComponent pc :componentToBindType.keySet()) {
			for (BindingContainer b : componentToBindType.get(pc)) {
				if (temp.get(pc) != null) {
					temp.get(pc).add(b);
				}
				else {
					temp.put(pc, new ArrayList<BindingContainer>());
					temp.get(pc).add(b);
				}
			}
		}
		
		for (PreviewComponent pc : temp.keySet()) {
			for (BindingContainer cont : temp.get(pc)) {
				if (cont.getbType() == BindType.NOT_ACTIVE)
					this.removeBinding(pc, cont);
				else {
					BindType t = cont.getbType();
					if (t == BindType.GREATER_THAN || t==BindType.GREATER_EQUALS_THAN || t==BindType.LESS_THAN || t== BindType.LESS_EQUALS_THAN) {
						if (cont.getValue().equals("")) {
							this.removeBinding(pc,  cont);
						}
					}
				}
			}
		}
	}
	
	/**Fabaris_A.zanchi
	 * Method deleting the invalid constraints before creating the xform.
	 */
	private void deleteInvalidConstraints() {
		boolean isNumeric = false;
		if (this.getType() == FormFieldType.NUMERIC_TEXT_FIELD || this.getType() == FormFieldType.CURRENCY_FIELD)
			isNumeric = true;
		List<ConstraintContainer> temp = new ArrayList<ConstraintContainer>();
		for (ConstraintContainer c : this.constraints) {
			temp.add(c);
		}
		for (ConstraintContainer c : temp) {
			ConstraintNumber type = c.getcNumber();
			if (type == ConstraintNumber.EQUALS || type == ConstraintNumber.LESS_EQUALS_THAN || type == ConstraintNumber.GREATER_THAN || type == ConstraintNumber.GREATER_EQUALS_THAN) {
				if (c.getValue().equals("") && isNumeric) {
					this.constraints.remove(c);
				}
			}
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	@Override
	public String toString() {
		return this.component.getName();
	}
	
}

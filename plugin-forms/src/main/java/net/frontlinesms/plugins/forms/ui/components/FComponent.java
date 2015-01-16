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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.ui.BarcodeField;
import net.frontlinesms.ui.i18n.InternationalisationUtils;
/**
@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph,Mureed Al-Barghouthi 
 www.fabaris.it <http://www.fabaris.it/>  
*/
@SuppressWarnings("serial")
public abstract class FComponent implements Serializable, Cloneable {
	
//> CONSTANTS
	
	/** Property name for property: Label */
	public static final String PROPERTY_LABEL = "plugins.forms.label";
	/** Property name for property: Type */
	public static final String PROPERTY_TYPE = "plugins.forms.type";
	/** Property name for property: Required */
	public static final String PROPERTY_REQUIRED="plugins.forms.required";//Fabaris_Raji	
	
	/** Value to display for {@link #label} when it is <code>null</code> */
	public static final String PROPERTY_DISPLAY_VALUE_NO_LABEL = "plugins.forms.no.label";
	
	private int renderHeight = 30;
	
	//Fabaris_A.zanchi modified to fill the preview component width
	protected static final int renderWidth = 445;
	//Fabaris_raji
	public static final String PROPERTY_NAME ="plugins.forms.name";
	private static final String PROPERTY_DISPLAY_VALUE_NO_NAME = "plugins.forms.Invalid";
	private static final String PROPERTY_DISPLAY_VALUE_ERROR ="plugins.forms.Error";	

	private String label = "";
	private String name="";	//Fabaris_raji
	private boolean required = true;
	
	//-------------------------------------------------- 30.10.2013
	public static final String FORM_FIELD_ID = "plugins.forms.id";
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name=FORM_FIELD_ID,unique=true,nullable=false,updatable=false)
	private long id;
	//--------------------------------------------------
	
	
	//Fabaris_a.zanchi
	private boolean isReadOnly;
	private boolean defaultValue = false;
        
//> ABSTRACT ACCESSORS
	/** @return the path of the icon to be displayed with this component. */
	public abstract String getIcon();
	public abstract String getDescription();
	public abstract Container getDrawingComponent();
	
	/**Fabaris_A.zanchi method to darken component while disabled
	 * Is not abstract but it must be overriden by NiceComponent.java
	 */
	public void darken() {}
	
	/**Fabaris_A.zanchi method do lighten up components while enabled
	 * It is not abstract but it must be overridden by NiceComponent.java
	 */
	public void lighten() {}
	
//> ACCESSORS
	/** @param renderHeight new value for {@link #renderHeight} */
	public void setRenderHeight(int renderHeight) {
		this.renderHeight = renderHeight;
	}
	
	/** @return {@link #renderHeight} */
	public int getHeight() {
		return renderHeight;
	}
	
	/** @return {@link #label} */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Get the label, or a placeholder if the label is null.  This method should be used for rendering
	 * components so that there is always something displayed for empty labels.
	 * @return {@link #label} value, or a placeholder if it is <code>null</code>
	 */
	public String getDisplayLabel() {
		if(this.label != null && this.label.length() > 0) {
			return this.label;
		} else {
			return InternationalisationUtils.getI18nString(PROPERTY_DISPLAY_VALUE_NO_LABEL);
		}
	}
	
	/** @param label new value for {@link #label} */
	public void setLabel(String label) {
		if(label == null) label = "";
		this.label = label;
	}
	
	//-------------------------------------------------  30.10.2013
	/** @param label new value for {@link #id} */
	public long getId()
	{
		return id;
	}
	//------------------------------------------------
	
	/*Fabaris_Raji*/
	  public String getName() {
		return name;
	}
	/*Fabaris_Raji*/
	 public void setName(String name) {
		if(name == null) name = "";
	 	this.name = name;
	}		
		
	 public Boolean getRequired() {       
			// TODO Auto-generated method stub
			return this.required;
		}
		public void setRequired(Boolean required) {
			this.required=required;
		}
	 public String getDisplayName() {
			if(this.name != null && this.name.length() > 0) {
				return this.name;
			} else {
				return InternationalisationUtils.getI18nString(PROPERTY_DISPLAY_VALUE_NO_NAME);
			}
		}	
	
	   
//> INSTANCE METHODS
	 //-------------------------------------------------------------  30.10.2013
	 
//	 /** @see Object#clone() */
//		public FComponent clone() {		
//			try {
//				FComponent clone = (FComponent) super.clone();
//				clone.setLabel(this.getLabel());
//				clone.setId(this.getId());//Fabaris_raji
//				clone.setRequired(this.required);//Fabaris_raji
//				return clone;
//			} catch (CloneNotSupportedException e) {
//				e.printStackTrace();
//				return null;
//			}
//		}
		

	/** @see Object#clone() */
	public FComponent clone() {		
		try {
			FComponent clone = (FComponent) super.clone();
			clone.setLabel(this.getLabel());
			clone.setName(this.name);//Fabaris_raji
			clone.setRequired(this.required);//Fabaris_raji
			return clone;
		} catch (CloneNotSupportedException e) {
		e.printStackTrace();
			return null;
		}
	}
	
	//---------------------------------------------------------------
	
	
	public boolean isReadOnly() {
		return isReadOnly;
	}
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
	
	//> STATIC HELPER METHODS
	public static FormFieldType getFieldType(Class<? extends FComponent> componentClass) {
		if(componentClass == CheckBox.class) return FormFieldType.CHECK_BOX;
		if(componentClass == CurrencyField.class) return FormFieldType.CURRENCY_FIELD;
		if(componentClass == DateField.class) return FormFieldType.DATE_FIELD;
		if(componentClass == EmailField.class) return FormFieldType.EMAIL_FIELD;
		if(componentClass == NumericTextField.class) return FormFieldType.NUMERIC_TEXT_FIELD;
	//	if(componentClass == PasswordField.class) return FormFieldType.PASSWORD_FIELD;
		if(componentClass == PhoneNumberField.class) return FormFieldType.PHONE_NUMBER_FIELD;
		if(componentClass == TextArea.class) return FormFieldType.TEXT_AREA;
		if(componentClass == TextField.class) return FormFieldType.TEXT_FIELD;
		//Fabaris_Raji
		if(componentClass == DropDownList.class) return FormFieldType.DROP_DOWN_LIST;	
		if(componentClass == RadioButton.class) return FormFieldType.RADIO_BUTTON;	
		//Fabaris_a.zanchi
		if(componentClass == Repeatables.class) return FormFieldType.REPEATABLES;
		if(componentClass == RepeatablesBasic.class) return FormFieldType.REPEATABLES_BASIC;
		
		//if(componentClass == Singleline.class) return FormFieldType.SINGLELINE;					//25/10/2013
		//if(componentClass == Multiline.class) return FormFieldType.MULTILINE;
		if(componentClass == Separator.class) return FormFieldType.SEPARATOR;		
		//Fabaris_Raji 
	//	if(componentClass == TimeField.class) return FormFieldType.TIME_FIELD;
		if(componentClass == TruncatedText.class) return FormFieldType.TRUNCATED_TEXT;
		if(componentClass == WrappedText.class) return FormFieldType.WRAPPED_TEXT;
		//Fabaris_a.zanchi
		if(componentClass == GeoLocationField.class ) return FormFieldType.GEOLOCATION;
		
		//**********************************************
		if(componentClass == BarcodeField.class ) return FormFieldType.BARCODE;
		//**********************************************
		if(componentClass == ImageField.class ) return FormFieldType.IMAGE;
                // Mureed Al-Barghouthi 
		if(componentClass == SignatureField.class ) return FormFieldType.SIGNATURE;
		
		throw new IllegalStateException("Unknown component type: " + componentClass);
	}
	
	public static Class<? extends FComponent> getComponentClass(FormFieldType fieldType) {
		if(fieldType == FormFieldType.CHECK_BOX) 			return CheckBox.class;
		if(fieldType == FormFieldType.CURRENCY_FIELD) 		return CurrencyField.class;
		if(fieldType == FormFieldType.DATE_FIELD) 			return DateField.class;
		if(fieldType == FormFieldType.EMAIL_FIELD) 			return EmailField.class;                             
		if(fieldType == FormFieldType.NUMERIC_TEXT_FIELD) 	return NumericTextField.class;
		//if(fieldType == FormFieldType.PASSWORD_FIELD) 		return PasswordField.class;
		if(fieldType == FormFieldType.PHONE_NUMBER_FIELD) 	return PhoneNumberField.class;
		if(fieldType == FormFieldType.TEXT_AREA) 			return TextArea.class;
		if(fieldType == FormFieldType.TEXT_FIELD) 			return TextField.class;
		//Fabaris_Raji
		if(fieldType == FormFieldType.DROP_DOWN_LIST)		return DropDownList.class;		
		if(fieldType == FormFieldType.RADIO_BUTTON)			return RadioButton.class;	
		if (fieldType == FormFieldType.REPEATABLES)			return Repeatables.class;
		if(fieldType == FormFieldType.REPEATABLES_BASIC) 	return RepeatablesBasic.class;
		if (fieldType == FormFieldType.GEOLOCATION)			return GeoLocationField.class;
		//*********************************************
		if (fieldType == FormFieldType.BARCODE)			return BarcodeField.class;
		//**********************************************
		if (fieldType == FormFieldType.IMAGE)			return ImageField.class;
                 // Mureed Al-Barghouthi 
		if (fieldType == FormFieldType.SIGNATURE)		return SignatureField.class;
		//*********************************************
		
		//if(fieldType == FormFieldType.MULTILINE)			return Multiline.class;				//25/10/2013
		//if(fieldType == FormFieldType.SINGLELINE)			return Singleline.class;		
		if(fieldType == FormFieldType.SEPARATOR)			return Separator.class;		
		//Fabaris_Raji		
		//if(fieldType == FormFieldType.TIME_FIELD) 			return TimeField.class;
		if(fieldType == FormFieldType.TRUNCATED_TEXT) 		return TruncatedText.class;
		if(fieldType == FormFieldType.WRAPPED_TEXT) 		return WrappedText.class;
		throw new IllegalStateException("No handling for form field type: " + fieldType);
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public boolean isDefaultValue() {
		return defaultValue;
	}
}

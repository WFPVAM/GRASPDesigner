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
/**
 * 
 */
package net.frontlinesms.plugins.forms.data.domain;

import net.frontlinesms.plugins.forms.ui.FormsThinletTabController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

	
	

/**
 * The different types of form field that are available.
 * @author Alex
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph,Mureed F. Al-Bargouthi  
 * www.fabaris.it <http://www.fabaris.it/>  
 */
public enum FormFieldType {
	TRUNCATED_TEXT(false),		
	WRAPPED_TEXT(false), 
	TEXT_AREA(true),
	TEXT_FIELD(true),
	NUMERIC_TEXT_FIELD(true),
	DROP_DOWN_LIST(true),//Fabaris_Raji 
	CHECK_BOX(true),
	RADIO_BUTTON(true),//Fabaris_Raji
	CURRENCY_FIELD(true),
	DATE_FIELD(true),
	REPEATABLES(true),//Fabaris_a.zanchi
	REPEATABLES_BASIC(true), //Fabaris_a.zanchi
	//PASSWORD_FIELD(true),
	GEOLOCATION(true), //Fabaris_a.zanchi
	BARCODE(true),
	IMAGE(true),
	EMAIL_FIELD(true),
	PHONE_NUMBER_FIELD(true),
	SIGNATURE(true),   // Mureed F. Al-Bargouthi
	SEPARATOR(false);                                    //Fabaris_Raji
	//MULTILINE(false),
	//SINGLELINE(false);
		
	//TIME_FIELD(true),
	
	
	
	/** Indicates whether fields of this type can have a value set. */
	private final boolean hasValue;
	
	
	
	/**Fabaris_a.zanchi method to achieve a nice readable name for components type (overriding toString() causes issues with PalettePanel.java)
	 *(must be localized in i18 files)
	 * 
	 */
	public String getNiceName() {
		String nicename;
		switch (this) {
		case CHECK_BOX:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.CHECK_BOX_STRING);
			break;
		case CURRENCY_FIELD:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.CURRENCY_FIELD_STRING);
			break;
		case DATE_FIELD:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.DATE_FIELD_STRING);
			break;
		case DROP_DOWN_LIST:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.DROP_DOWN_LIST_STRING);
			break;
		case EMAIL_FIELD:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.EMAIL_FIELD_STRING);
			break;
		case NUMERIC_TEXT_FIELD:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.NUMERIC_TEXT_FIELD_STRING);
			break;
		case PHONE_NUMBER_FIELD:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.PHONE_NUMBER_FIELD_STRING);
			break;
		case RADIO_BUTTON:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.RADIO_BUTTON_STRING);
			break;
			
		case SEPARATOR:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.SEPARATOR_STRING);
			break;
			
			/*
		case MULTILINE:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.MULTILINE_STRING);
			break;
		case SINGLELINE:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.SINGLELINE_STRING);
			break;
			*/
			
		case REPEATABLES :
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.REPEATABLES_STRING);
			break;
		case REPEATABLES_BASIC:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.REPEATABLES_BASIC_STRING);
			break;
		case TEXT_AREA:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.TEXT_AREA_STRING);
			break;
		case TEXT_FIELD:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.TEXT_FIELD_STRING);
			break;
		case TRUNCATED_TEXT:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.TRUNCATED_TEXT_STRING);
			break;
		case WRAPPED_TEXT:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.WRAPPED_TEXT_STRING);
			break;
		case GEOLOCATION:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.GEOLOCATION_STRING);
			break;
			//*********************************************
		case BARCODE:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.BARCODE_STRING);
			break;
		case IMAGE:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.IMAGE_STRING);
			break;
                case SIGNATURE:
			nicename = InternationalisationUtils.getI18nString(FormsThinletTabController.SIGNATURE_STRING);
			break;    
			
		default:
			nicename = super.toString();
			break;
		}
		return nicename;
	}

	/**
	 * Creates a new {@link FormFieldType}.
	 * @param hasValue value for {@link #hasValue}
	 */
	private FormFieldType(boolean hasValue) {
		this.hasValue = hasValue;
	}
	
	/** @return {@link #hasValue} */
	public boolean hasValue() {
		return this.hasValue;
	}
}

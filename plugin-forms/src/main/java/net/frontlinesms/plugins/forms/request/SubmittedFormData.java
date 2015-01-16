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
package net.frontlinesms.plugins.forms.request;

import java.util.List;

import net.frontlinesms.plugins.forms.data.domain.ResponseValue;

/**
 * Submitted data for a single form.
 * @author Alex
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 */
public class SubmittedFormData {
	/** ID of the form that this data was submitted for. */
	private final int formId;
	/** ID of this data set */
	private final int dataId;
	
	/*Fabaris_a.zanchi id duplicate for alphanumeric version */
	private final String formId_IdMac;
	private final String dataId_IdMac;
	
	/** List of data values that were submitted. */
	private final List<ResponseValue> dataValues;
	/**Fabaris_a.zanchi String of the client version who filled the form */
	private  String clientVersion;
	
	/**
	 * Create a new instance of this class.
	 * @param formId
	 * @param dataId 
	 * @param dataValues
	 */
	public SubmittedFormData(int formId, int dataId, List<ResponseValue> dataValues) {
		this.formId = formId;
		this.dataId = dataId;
		this.dataValues = dataValues;
		formId_IdMac = null;
		dataId_IdMac = null;
	}
	
	/** Fabaris_A.zanchi
	 * Create a new instance of this class.
	 * @param formId
	 * @param dataId 
	 * @param dataValues
	 */
	public SubmittedFormData(String formId_mac, String dataId_mac, List<ResponseValue> dataValues) {
		this.formId_IdMac = formId_mac;
		this.dataId_IdMac = dataId_mac;
		this.dataValues = dataValues;
		formId = -1;
		dataId = -1;
	}
	
	/**Fabaris_a.zanchi
	 * Creates a new instance of this class with client version
	 * @param formId
	 * @param dataId
	 * @param dataValues
	 * @param clientVersion
	 */
	public SubmittedFormData(int formId, int dataId, List<ResponseValue> dataValues, String clientVersion) {
		this(formId, dataId, dataValues);
		if (clientVersion != null)
			this.clientVersion = clientVersion;
		else
			this.clientVersion = "";
	}
	
	/**Fabaris_a.zanchi
	 * Creates a new instance of this class with client version (this version accepts alphanumeric IDs)
	 * @param formId
	 * @param dataId
	 * @param dataValues
	 * @param clientVersion
	 */
	public SubmittedFormData(String formId_mac, String dataId_mac, List<ResponseValue> dataValues, String clientVersion) {
		this(formId_mac, dataId_mac, dataValues);
		if (clientVersion != null)
			this.clientVersion = clientVersion;
		else
			this.clientVersion = "";
	}

	/** @return {@link #formId} */
	public int getFormId() {
		return formId;
	}
	
	/** @return {@link #formId} */
	public int getFormMobileId() {
		return formId;
	}

	/** @return {@link #dataValues} */
	public List<ResponseValue> getDataValues() {
		return dataValues;
	}

	/** @return {@link #dataId} */
	public int getDataId() {
		return this.dataId;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getFormId_IdMac() {
		return formId_IdMac;
	}

	public String getDataId_IdMac() {
		return dataId_IdMac;
	}
	
	
	
	
}

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
package net.frontlinesms.plugins.forms.data.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.CollectionOfElements;

import net.frontlinesms.data.domain.Roles;

/**
 * A response to a {@link Form}.
 * @author Alex
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 */
@SuppressWarnings("serial")
@Entity
public class FormResponse implements Serializable {
	
//> COLUMN NAMES
	/** Column name for field {@link #parentForm} */
	public static final String FIELD_FORM = "parentForm";	
	public static final String FIELD_CODEFORM = "Code_Form";
	

//> INSTANCE PROPERTIES
	/** Unique id for this entity.  This is for hibernate usage. */
	@SuppressWarnings("unused")
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long id;
	/** The SMS message that this response was received in. */
	private String senderMsisdn;
	/** The form that this object is a response to. */
	@ManyToOne(fetch=FetchType.LAZY)
	private Form parentForm;	
	/** Fabaris_a.zanchi version of the client who filled the form */
	private String clientVersion;	
	/** The data content of the form response. */
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)	
	private List<ResponseValue> results = new ArrayList<ResponseValue>();
	/** The flag id_flsmsId attached to this field. */	
	private String id_flsmsId;
	private boolean pushed=false;	
	/**aggiunto Fabaris_maria cilione.*/
	private String Code_Form;
	private boolean fromDataEntry = false;
        private int ResponseStatusID = 1;

//> CONSTRUCTORS
	/** Empty constructor for hibernate */
	FormResponse() {}
	
	/**
	 * Create a new form response.
	 * @param message
	 * @param parentForm
	 * @param results
	 */
	public FormResponse(String senderMsisdn, Form parentForm, List<ResponseValue> results) {
		this.senderMsisdn = senderMsisdn;
		this.setParentForm(parentForm);
		this.results = results;
                this.setResponseStatusID(1);
	}
        
        public FormResponse(String senderMsisdn, Form parentForm) {
		this.senderMsisdn = senderMsisdn;
		this.setParentForm(parentForm);
                this.setResponseStatusID(1);
	}
        
	/**Fabaris_a.zanchi
	 * Creates a new form response with client version
	 * @param senderMsisdn
	 * @param parentForm
	 * @param results
	 * @param clientVersion 
	 */
	public FormResponse(String senderMsisdn, Form parentForm, List<ResponseValue> results, String clientVersion) {
		this(senderMsisdn, parentForm, results);
		this.clientVersion = clientVersion;
	}
	
//> ACCESSOR METHODS
	/** @return {@link #results} */
	public List<ResponseValue> getResults() {
		return this.results;
	}
	
	/** @return the MSISDN of the form submitter */
	public String getSubmitter() {
		return this.senderMsisdn;
	}

	/**Fabaris_a.zanchi */
	public String getClientVersion() {
		return clientVersion;
	}
	/**Fabaris_a.zanchi */
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
	/**
	 * checks if pushed or not.
	 * @param True if pushed
	 */
	/**Fabaris_raji */
	public boolean isPushed() {
		return pushed;
	}
	/**
	 * Sets pushed as true or false
	 * @param pushed
	 */
	public void setPushed(boolean pushed) {
		
		this.pushed = pushed;
	}
	

	public String getId_flsmsId() {
		return id_flsmsId;
	}

	public void setId_flsmsId(String id_flsmsId) {
		this.id_flsmsId = id_flsmsId;
	}
	/**Fabaris_raji */
	public Form getParentForm() {
		return parentForm;
	}

	public void setParentForm(Form parentForm) {
		this.parentForm = parentForm;
	}

	public String getCode_Form() {
		return Code_Form;
	}

	public void setCode_Form(String code_Form) {
		Code_Form = code_Form;
	}

	public boolean isFromDataEntry() {
		return fromDataEntry;
	}

	public void setFromDataEntry(boolean fromDataEntry) {
		this.fromDataEntry = fromDataEntry;
	}
	
	public long getId(){
		return this.id;
	}
        
        public void setResponseStatusID(int responseStatusID) {
		this.ResponseStatusID  = responseStatusID;
	}
	
	public int getResponseStatusID() {
		return ResponseStatusID;
	}
}

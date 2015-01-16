package net.frontlinesms.plugins.forms.data.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Configuration implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String exportExcelLink;
	
	private String googleKey;
	
	//private int autoSave;            <------------------------------------ 17/10/2013
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long id;
	
	/*
	public int getAutoSave() {
		return autoSave;
	}
	*/
	
	public String getExportExcelLink() {
		return exportExcelLink;
	}
	
	public String getGoogleKey() {
		return googleKey;
	}
	
	/*
	public void setAutoSave(int autoSave) {
		this.autoSave = autoSave;
	}
	*/
	
	public void setExportExcelLink(String exportExcelLink) {
		this.exportExcelLink = exportExcelLink;
	}
	
	public void setGoogleKey(String googleKey) {
		this.googleKey = googleKey;
	}
}

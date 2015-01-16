package net.frontlinesms.data.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Wraps a {@link String} value for {@link SmsInternetServiceSettings} in a persistable {@link Entity}.
 * @author Alex
 */
@Entity
public class SmsInternetServiceSettingValue {
//> STATIC CONSTANTS

//> INSTANCE PROPERTIES
	/** Unique id for this entity.  This is for hibernate usage. */
	@SuppressWarnings("unused")
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long id;
	/** the value of this String */
	private String value;

//> CONSTRUCTORS
	/** Empty default constructor for Hibernate. */
	@SuppressWarnings("unused")
	private SmsInternetServiceSettingValue() {}
	
	/**
	 * Creates a new {@link SmsInternetServiceSettingValue}.
	 * @param value The value for {@link #value}.
	 */
	public SmsInternetServiceSettingValue(String value) {
		this.value = value;
	}

//> ACCESSORS
	/** @return {@link #value} */
	public String getValue() {
		return this.value;
	}

//> INSTANCE HELPER METHODS

	// TODO implement .equals() and .hashcode().  May need to watch out as 2 different instances may have the same value.  For this reason, might be safer not to implement for now.
	
//> STATIC FACTORIES

//> STATIC HELPER METHODS
}

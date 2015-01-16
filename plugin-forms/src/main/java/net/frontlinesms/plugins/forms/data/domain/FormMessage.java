package net.frontlinesms.plugins.forms.data.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class FormMessage implements Serializable{
	private String message;
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}

package net.frontlinesms.plugins.forms.data.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
public class FormResponseCoords {
	@SuppressWarnings("unused")
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long FRCoordID;

	
	/**Fabaris_a.zanchi: repetable form fields values*/
	@OneToMany(targetEntity=ResponseValue.class,cascade=javax.persistence.CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<FormResponse> formResponses = new ArrayList<FormResponse>(); 
	/** The flag pushed attached to this field. */
	
	/** Empty constructor for hibernate */
	public FormResponseCoords() {}
	
	public void addRepetableValue(FormResponse value) {
		this.formResponses.add(value);
	}
	
	public List<FormResponse> getRepetablesValues() {
		return this.formResponses;
	}
}
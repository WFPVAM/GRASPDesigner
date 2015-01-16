package net.frontlinesms.plugins.forms.data.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class V_FieldPositionIndex {
	@SuppressWarnings("unused")
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long id;

	private String frXCoordText;
	/** Empty constructor for hibernate */
	
	private int positionIndex;
	
	private long form_id; 
	
	private String type;
	
	public V_FieldPositionIndex() {}
	

	public long getId() {
		return id;
	}

	public String getFrXCoordText() {
		return frXCoordText;
	}

	public void setFrXCoordText(String frXCoordText) {
		this.frXCoordText = frXCoordText;
	}

	public int getPositionIndex() {
		return positionIndex;
	}

	public void setPositionIndex(int positionIndex) {
		this.positionIndex = positionIndex;
	}
	
	
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public long getForm_id() {
		return form_id;
	}
}
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

import java.util.ArrayList;
import java.util.Date;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>
 */
/**
 * Class wrapping {@link String} as an {@link Entity} to
 */
@Entity
public class ResponseValue {

//> INSTANCE PROPERTIES
    /**
     * Unique id for this entity. This is for hibernate usage.
     */
    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, updatable = false)
    private long id;
    /**
     * the value of this String
     */
    private String value;

    /**
     * Fabaris_a.zanchi: repetable form fields values
     */
    @OneToMany(targetEntity = ResponseValue.class, cascade = javax.persistence.CascadeType.ALL)
    @Column(unique = true, nullable = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ResponseValue> repetableResponseValues = new ArrayList<ResponseValue>();
    /**
     * The flag pushed attached to this field.
     */
    //added by Fabaris_raji
    private boolean pushed = false;
    //added by Fabaris_raji 
    private String id_flsmsId;
    private int positionIndex;

    /*@ManyToOne(targetEntity = FormResponse.class)*/
    private long FormResponseID;
    private int RVRepeatCount;
    private Integer formFieldId;

    @Transient
    private String tempValue;
    @Transient
    private boolean isImage;
    private float nvalue;
    private Date dvalue;

    public String getId() {
        return String.valueOf(id);
    }

    public int getPositionIndex() {
        return positionIndex;
    }

    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

    //> CONSTRUCTORS
    /**
     * Empty constructor for hibernate
     */
    public ResponseValue() {
    }

    /**
     * Create a new {@link ResponseValue}
     *
     * @param value value of this object
     */
    public ResponseValue(String value) {
        this.value = value;
    }

    public ResponseValue(FormField field) {
        this.value = String.valueOf(field.getSurvey().getValues().size());
        this.positionIndex = field.getPositionIndex();
    }

    /**
     * checks if pushed or not.
     *
     * @param True if pushed
     */
    //added by Fabaris_raji 
    public boolean isPushed() {
        return pushed;
    }

    /**
     * Sets pushed as true or false
     *
     * @param pushed
     */
    //added by Fabaris_raji 
    public void setPushed(boolean pushed) {
        this.pushed = pushed;
    }

//> INSTANCE METHODS
    /**
     * @return the value of this response
     */
    @Override
    public String toString() {
        return this.value;
    }

    public void addRepetableValue(ResponseValue value) {
        this.repetableResponseValues.add(value);

    }

    public List<ResponseValue> getRepetablesValues() {
        return this.repetableResponseValues;
    }

    //added by Fabaris_raji 

    public String getId_flsmsId() {
        return id_flsmsId;
    }

    //added by Fabaris_raji 

    public void setId_flsmsId(String id_flsmsId) {
        this.id_flsmsId = id_flsmsId;
    }

    public Integer getFormFieldId() {
        return formFieldId;
    }

    public void setFormFieldId(Integer formFieldId) {
        this.formFieldId = formFieldId;
    }

    public long getFormResponseID() {
        return FormResponseID;
    }

    public void setFormResponseID(long formResponseID) {
        this.FormResponseID = formResponseID;
    }

    public int getRVRepeatCount() {
        return RVRepeatCount;
    }
    
    public void setRVRepeatCount(int rVRepeatCount) {
        this.RVRepeatCount = rVRepeatCount;
    }

    public String getTempValue() {
        return tempValue;
    }

    public void setTempValue(String tempValue) {
        this.tempValue = tempValue;
    }

    public boolean isIsImage() {
        return isImage;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }
    
    public float getNvalue() {
        return nvalue;
    }
    
    public void setNvalue(float nvalue) {
        this.nvalue = nvalue;
    }
    
    public Date getDvalue() {
        return dvalue;
    }
    
    public void setDvalue(Date dvalue) {
        this.dvalue = dvalue;
    }
}

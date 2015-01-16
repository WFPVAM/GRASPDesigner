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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import net.frontlinesms.plugins.forms.ui.components.PreviewComponent.BindType;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
/**Class modeling the domain object who represents Constraint table.
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph   
 *   www.fabaris.it <http://www.fabaris.it/>    
 *
 */
public class ConstraintContainer {

	@SuppressWarnings("unused")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false, updatable = false)
	private long id;

	// @Column(name=FIELD_BINDINGTYPE)
	@Enumerated(EnumType.STRING)
	private ConstraintNumber cNumber;
	// @Column(name=FIELD_VALUE)
	private String value;
	// @Column(name=FIELD_MINRANGE)
	private int minRange;
	// @Column(name=FIELD_MAXRANGE)
	private int maxRange;
	/** The flag pushed attached to this field. */
	private Boolean pushed = false;// Fabaris_Raji

	public ConstraintContainer() {
	}

	public ConstraintContainer clone() {
		ConstraintContainer clone = new ConstraintContainer();
		if (this.cNumber != null) {
			clone.setcNumber(cNumber);
			clone.setValue(this.value);
			clone.setMinRange(this.minRange);
			clone.setMaxRange(this.maxRange);
		}
		return clone;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof ConstraintContainer) {
			ConstraintContainer compared = (ConstraintContainer) arg0;
			if (compared.cNumber != this.cNumber)
				return false;
			if (compared.value != null) {
				if (this.value == null)
					return false;
				else {
					if (!compared.value.equals(this.value))
						return false;
				}
			}
			if (!(compared.minRange == this.minRange && compared.maxRange == this.maxRange))
				return false;
			return true;
		} else
			return false;
	}

	public ConstraintNumber getcNumber() {
		return cNumber;
	}
	
	public void setcNumber(ConstraintNumber cNumber) {
		this.cNumber = cNumber;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getMinRange() {
		return minRange;
	}

	public void setMinRange(int minRange) {
		this.minRange = minRange;
	}

	public int getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange;
	}

	public Boolean getPushed() {
		return pushed;
	}

	public void setPushed(Boolean pushed) {
		this.pushed = pushed;
	}

	public enum t {

	}

	public enum ConstraintDomain {
		NUMERIC, TEXT;
	}

	@Entity
	public enum ConstraintNumber {
		MAXIMUM_NUMBER_OF_CHARACTER("plugin.forms.bindingstable.maximumNumberOfCharacter",ConstraintDomain.TEXT), 
		MINIMUM_NUMBER_OF_CHARACTER("plugin.forms.bindingstable.minimumNumberOfCharacter",ConstraintDomain.TEXT), 
		GREATER_THAN("plugin.forms.bindingstable.greater"), 
		LESS_THAN("plugin.forms.bindingstable.less"), 
		EQUALS("plugin.forms.bindingstable.equals"), 
		NOT_EQUALS("plugin.forms.bindingstable.notequals"), 
		LESS_EQUALS_THAN("plugin.forms.bindingstable.lessequals"),
		GREATER_EQUALS_THAN("plugin.forms.bindingstable.greaterequals");

		private ConstraintNumber(String utfIdx) {
			this.utfIndex = utfIdx;
			this.kindOf = ConstraintDomain.NUMERIC;
		}

		private ConstraintNumber(String utfIdx, ConstraintDomain kindOf) {
			this.utfIndex = utfIdx;
			this.kindOf = kindOf;
		}

		@Override
		public String toString() {
			return InternationalisationUtils.getI18nString(this.utfIndex);
		}

		public ConstraintDomain getKindOf() {
			return kindOf;
		}
		
		public static ConstraintNumber[] getTheValues(ConstraintDomain text){
			List<ConstraintNumber> dummy = new ArrayList<ConstraintNumber>();
			for(ConstraintNumber el: ConstraintNumber.values()){
				if (el.getKindOf().equals(text)){
					dummy.add(el);
				}
			}
			return dummy.toArray( new ConstraintNumber[]{});
		}

		String utfIndex;
		ConstraintDomain kindOf;
	}

}

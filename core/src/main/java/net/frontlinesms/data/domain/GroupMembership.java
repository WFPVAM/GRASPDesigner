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
package net.frontlinesms.data.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Alex
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph         
 *  www.fabaris.it <http://www.fabaris.it/>  
 */
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"contact_contact_id", "group_path"}))
public class GroupMembership {
	@SuppressWarnings("unused")
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", nullable=false, unique=true, updatable=false)
	private long id;
	@ManyToOne(optional=false)
	private Group group;
	@ManyToOne(optional=false)
	private Contact contact;
	/** The flag pushed attached to this field. */
	private Boolean pushed;//Fabaris_Raji

//> CONSTRUCTORS
	/** Empty constructor for Hibernate */
	public GroupMembership() {}
	
	public GroupMembership(Group group, Contact contact) {
		this.group = group;
		this.contact = contact;
	}
	/**
	 * checks if pushed or not.
	 * @param True if pushed
	 */
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
//> GENERATED CODE
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupMembership other = (GroupMembership) obj;
		if (contact == null) {
			if (other.contact != null)
				return false;
		} else if (!contact.equals(other.contact))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		return true;
	}
}

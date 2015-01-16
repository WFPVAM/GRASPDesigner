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

import javax.persistence.*;

import org.hibernate.annotations.Type;

import net.frontlinesms.data.EntityField;
/**
 * Object representing credentials of a  user in data structure.

 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph      
 * www.fabaris.it <http://www.fabaris.it/>  
 */
@Entity
public class User_Credential {
	
//> COLUMN NAME CONSTANTS
	/** Column name for {@link #name} */
	private static final String FIELD_NAME ="name";
	/** Column name for {@link #surname} */
	private static final String FIELD_SURNAME ="surname";
	/** Column name for {@link #username} */
	private static final String FIELD_USERNAME ="username";
	/** Column name for {@link #password} */
	private static final String FIELD_PASSWORD ="password";
	/** Column name for {@link #role} */
	private static final String FIELD_ROLE ="role";
	/** Column name for {@link #supervisor} */
	private static final String FIELD_SUPERVISOR ="supervisor";
	/** Column name for {@link #frontlinesms_id} */
	private static final String FIELD_FRONTLINESMS_ID ="frontlinesms_id";
	/** Column name for {@link #Email} */
	private static final String FIELD_EMAIL ="email";
	/** Column name for {@link #phone_number} */
	private static final String FIELD_PHONENUMBER ="phone_number";
	/** Column name for {@link #pushed} */
	private static final String FIELD_PUSHED ="pushed";
	
//> ENTITY FIELDS
	/** Details of the fields that this class has. */
	public enum Field implements EntityField<User_Credential> {
		/** field mapping for {@link User_Credential#name} */
		NAME(FIELD_NAME),
		/** field mapping for {@link User_Credential#surname} */
		SURNAME(FIELD_SURNAME),
		/** field mapping for {@link User_Credential#username} */
		USERNAME(FIELD_USERNAME),
		/** field mapping for {@link User_Credential#password} */				
		PASSWORD(FIELD_PASSWORD),
		/** field mapping for {@link User_Credential#role} */
		ROLE(FIELD_ROLE),
		/** field mapping for {@link User_Credential#supervisor} */
		SUPERVISOR (FIELD_SUPERVISOR),
		/** field mapping for {@link User_Credential#frontlinesms_id} */
		FRONTLINESMS_ID (FIELD_FRONTLINESMS_ID),
		/** field mapping for {@link User_Credential#email} */
		EMAIL (FIELD_EMAIL),
		/** field mapping for {@link User_Credential#phone_number} */
		PHONENUMBER (FIELD_PHONENUMBER),
		/** field mapping for {@link User_Credential#pushed} */
		PUSHED(FIELD_PUSHED);
		
		private final String fieldName;
		/**
		 * Creates a new {@link Field}
		 * @param fieldName name of the field
		 */
		Field(String fieldName) { this.fieldName = fieldName; }
		/** @see EntityField#getFieldName() */
		public String getFieldName() { return this.fieldName; }
	}
//> INSTANCE PROPERTIES
	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private int user_id;
	@Column(name=FIELD_NAME,nullable=false)
	private String name;
	@Column(name=FIELD_SURNAME,nullable=false)
	private String surname;
	@Column(name=FIELD_USERNAME,nullable=false)
	private String username;
	@Column(name=FIELD_PASSWORD,nullable=false)
	private String password;	
	@ManyToOne(targetEntity=Roles.class)
	private Roles roles;
	@Column(name=FIELD_SUPERVISOR,nullable=true)
	private String supervisor;
	@Column(name=FIELD_FRONTLINESMS_ID,nullable=true)
	private String frontlinesms_id;	
	@Column(name=FIELD_EMAIL,nullable=false)
	private String email;
	@Column(name=FIELD_PHONENUMBER,nullable=false)
	private String phone_number;
	@Column(name=FIELD_PUSHED,nullable=true)
	//@org.hibernate.annotations.Type(type="true_false")	
	private Boolean pushed;
//> CONSTRUCTORS
	/** Empty constructor for hibernate 
	 * @return */
	public User_Credential() {}
	
	/**
	 * Creates a User_Credentials with the specified attributes.
	 * @param name The name of the new User
	 * @param surname The surname of the User
	 * @param username The username assigned                   
	 * @param password The password 
	 * @param role The role of the User                         
	 * @param supervisor The name of his supervisor             
	 * @param frontlinesms_id The id of the frontlinesms installed
	 * @param roles 
	 * @return 
	 */
	public User_Credential(String name, String surname, String username, String password, 
			String supervisor,String frontlinesms_id, Roles roles,String email,String phone_number,boolean pushed) {
		this.name =name;
		this.surname =surname;
		this.username =username;
		this.password =password;		
		this.roles=roles;
		this.supervisor = supervisor;
		this.frontlinesms_id=frontlinesms_id;
		this.email=email;
		this.phone_number=phone_number;
		this.pushed=pushed;
	}
	//> ACCESSOR METHODS
	
	
	/** @return the database ID of this user */
	public int getUserId() {
		return this.user_id;
	}	

	/** @return  user name */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets  user name.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/** @return the surname of the user */
	public String getSurname() {
		return surname;
	}
	/**
	 * Sets this surname.
	 * @param surname
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}
	/** @return the username of the user */
	public String getUsername() {
		return username;
	}
	/**
	 * Sets this username
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/** @return the password of the  user */
	public String getPassword() {
		return password;
	}
	/**
	 * Sets this password.
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/** @return the role ID of this user */
	public Roles getRole() {
		return roles;
	}
	/**
	 * Sets the role id.
	 * @param roles
	 */
	public void setRole(Roles roles) {
		this.roles = roles;
	}
	/** @return the supervisor name  of this user */
	public String getSupervisor() {
		return supervisor;
	}
	/**
	 * Sets this supervisor.
	 * @param supervisor
	 */
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	/** @return the frontlinesms ID of this user */
	public String getFrontlinesms_id() {
		return frontlinesms_id;
	}
	/**
	 * Sets this frontlinesms_id
	 * @param frontlinesms_id
	 */
	public void setFrontlinesms_id(String frontlinesms_id) {
		this.frontlinesms_id=frontlinesms_id;
	}
	/** @return the email of this user */
	public String getEmail() {
		return email;
	}
	/**
	 * Sets this email
	 * @param email
	 */
	public void setEmail(String email) {
			this.email = email;
	}
	/** @return the phonenumber of this user */
	public String getPhone_number() {
		return phone_number;
	}
	
	/**
	 * Sets this phone_number.
	 * @param phone_number
	 */
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
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
	/** @see Object#toString() */
	@Override
	public String toString() {
		return this.getClass().getName() + "[" +
				"name=" + this.name + ";" +
				"surname=" + this.surname + ";" +
				"username=" + this.username + ";" +
				"password=" + this.password+ ";" +
				"role=" + this.roles+ ";" + 
				"supervisor=" + this.supervisor+
				"frontlinesms_id=" + this.frontlinesms_id+	
				"email=" + this.email+	
				"phone_number=" + this.phone_number+	
				"pushed=" + this.pushed+	
				"]";
	}
	/** Generates a hashcode for the {@link User_Credential} using the {@link #user_id} field. */
	
	//@Override
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	/** Checks that the two {@link User_Credentials}have the same {@link #user_id} */
	/** @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User_Credential other = (User_Credential) obj;
		if (username== null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (password!= other.password)
			return false;
		
		return true;
	}
	public String getSortingField(Field sortBy) {
		switch (sortBy) {
		case NAME:
			return getName();
		case SURNAME:			
		default:
			throw new IllegalStateException("Trying to sort a user by something different than the name or surname");
		}
		
	}

}

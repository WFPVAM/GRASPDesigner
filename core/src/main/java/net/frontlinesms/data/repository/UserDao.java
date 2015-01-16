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
package net.frontlinesms.data.repository;
import java.util.List;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.Order;
import net.frontlinesms.data.domain.User_Credential;
import net.frontlinesms.data.domain.User_Credential.Field;

/**
 * Interface to represent credentials of a  User
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph      
 *www.fabaris.it <http://www.fabaris.it/>  
 */
public interface UserDao {


	/** @return all countacts in the system */
	public List<User_Credential> getAllUsers();
	
	/**
	 * Returns all users from a particular start index, with a maximum number of returned contacts set.
	 * @param startIndex index of the first contact to fetch
	 * @param limit max number of contacts to fetch
	 * @return a subset of all the contacts
	 */
	public List<User_Credential> getAllUsers(int startIndex, int limit);
	
	/**
	 * Returns all users from a particular start index, with a maximum number of returned contacts set.
	 * @param startIndex index of the first contact to fetch
	 * @param limit max number of contacts to fetch
	 * @param order the order to sort by
	 * @return a subset of all the contacts, ordered by the order asked
	 */
	public List<User_Credential> getAllUsersSorted(int startIndex, int limit, Field sortBy, Order order);
	

	public User_Credential getByUserName(String name);

	/**
	 * Saves a user to the system
	 * @param user the User_Credential to save
	 * @throws DuplicateKeyException if the contact's phone number is already in use by another contact 
	 */
	public void saveUser_Credential(User_Credential user) throws DuplicateKeyException;
	
	/**
	 * Updates a contact's details in the data source
	 * @param contact the contact whose details should be updated
	 * @throws DuplicateKeyException if the contact's phone number is already in use by another contact
	 */
	public void updateUser_Credential(User_Credential user) throws DuplicateKeyException;
	
	/**
	 * @param contactNameFilter A contact's name, or any part of it 
	 * @param start The first contact to return
	 * @param limit the maximum number of contacts to return
	 * @return all contacts whose names match the filter
	 */
	public List<User_Credential> getUsersFilteredByName(String userNameFilter, int start, int limit);
	
	/** 
	 * @param contactNameFilter A contact's name, or any part of it
	 * @return count of all contacts whose names match the filter
	 */
	public int getUsersFilteredByNameCount(String contactNameFilter);
	/** 
	 * @param username A user name
	 * @return user with corresponding username and password
	 */
	
	public List<User_Credential> getByUsernameAndPassword(String username, String password);
	/** Added by Fabaris_Raji
	 * @param username As user name
	 * @param password As password
	 * @return boolean  
	 */
	public boolean getByUsernameAndPassword_check(String username,
			String password) ;
	/** Added by Fabaris_Raji
	 * @param username As user name
	 * @param password As password
	 * @return boolean	   
	 */
	public String getByUsernameAndPassword_Id(String username,String password) ;
	/** Added by Fabaris_Raji
	 * @param username As user name
	 * @param password As password
	 *  @param id As frontlinesms_id
	 *  @return String
	 */
	public void saveFrontlineSMS_Id(String name, String pword, String id);
	

	/** Added by Fabaris_Andrei
	 *  @return String 
	 *  Supervisor's email or null if no one
	 */
	public String getSupervisorEmail();

	/** Added by Fabaris_Andrei
	 *  @return String 
	 *  admin's frontlinesms_id or null if no one
	 */
	public String getAdminFrontlineSMS_ID();
}

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
package net.frontlinesms.data.repository.hibernate;


import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.Order;
import net.frontlinesms.data.domain.User_Credential.Field;
import net.frontlinesms.data.domain.User_Credential;
import net.frontlinesms.data.repository.UserDao;

/**
 * Hibernate implementation of {@link UserDao}.
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph     
 *  www.fabaris.it <http://www.fabaris.it/>        
 * 
 */
public class HibernateUserDao extends BaseHibernateDao<User_Credential> implements UserDao {
	/** Create a new instance of this DAO. */
		
	public HibernateUserDao() {
		super(User_Credential.class);
	}
	/** @return all users in the system */
	public List<User_Credential> getAllUsers(){
		return null;
		}
	/** @return all users in the system */
	public List<User_Credential> getAllUsers(int startIndex, int limit){
		return null;
		}
	/** @return all users sorted in the system */
	public List<User_Credential> getAllUsersSorted(int startIndex, int limit, Field sortBy, Order order) {
		return null;
	}
	
	/** @return all user by name in the system */
	public User_Credential getByUserName(String name){
		return null;
		}
	/** @return save users in the system */
	public void saveUser_Credential(User_Credential user) throws DuplicateKeyException{}
	
	/** @return update users in the system */
	public void updateUser_Credential(User_Credential user) throws DuplicateKeyException{}
	
	/** @return save users filtered by name  in the system */
	public List<User_Credential> getUsersFilteredByName(String userNameFilter, int start, int limit){
		return null;
		}
	/** @return save users filtered by name count in the system */
	public int getUsersFilteredByNameCount(String contactNameFilter){
		return 0;
		}
	/** @return save users by name and password in the system */
	public List<User_Credential> getByUsernameAndPassword(String username, String password){
		return null;
		}	
	/**Added by Fabaris_Raji to get frontlinesms_Id for given username and password*/
	public String getByUsernameAndPassword_Id(String username,
			String password){
		String id = null;
		List<?> users = getHibernateTemplate().findByNamedParam(					
				"select u.frontlinesms_id from User_Credential as u where u.username=:username and u.password=:password", 
				new String[]{ "username", "password" },new Object[]{ username, password });			
		if (users.size() == 1){							
			id=(String) users.get(0);						
			}			

		return id;			
		}
		/**Added by Fabaris_Raji to insert frontlinesms_Id if not present for the first time*/
	public void saveFrontlineSMS_Id(String username,
				String password,String id){		
			int i=getHibernateTemplate().
					bulkUpdate("update User_Credential as u set u.frontlinesms_id='"+id+"'"+
							" where u.username=? and u.password=?",new Object[]{ username, password });		
			}	

	/**Added by Fabaris_Raji to control username and password*/
	public boolean getByUsernameAndPassword_check(String username,String password) {			
            List<?> users = getHibernateTemplate().findByNamedParam(					
				"select u from User_Credential as u where u.username=:username and u.password=:password",
				new String[]{ "username", "password" },new Object[]{ username, password });				
		if (users.size() == 1){							
			return true;						
			}
		return false;
	}
        
	/**Added by Fabaris_Andrei to get supervisor email
	 * @return
	 * supervisor's email or null if no one found*/
	public String getSupervisorEmail(){
		List resultList = (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				SQLQuery sq = session.createSQLQuery("SELECT TOP 1 User_Credential.email FROM User_Credential inner join Roles on Roles.id = User_Credential.roles_id WHERE Roles.id = 4");
				if(sq.list().size()==1){
					return sq.list();
				}else{
					return null;
				}
			}
		});
		if (resultList != null) //Fabaris_a.zanchi checks if list of results is not null to prevent null pointer exception
			return (String) resultList.get(0);
		else 
			return null;
	}

	/**Added by Fabaris_Andrei to get admin's frontlinesms_id
	 * @return
	 * admin's frontlinesms_id or null if no admin user found */
	public String getAdminFrontlineSMS_ID(){
		List resultList = (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				SQLQuery sq = session.createSQLQuery("SELECT TOP 1 User_Credential.frontlinesms_id FROM User_Credential WHERE User_Credential.username = 'admin'");
				if(sq.list().size()==1){
					return sq.list();
				}else{
					return null;
				}
			}
		});
		return (String) resultList.get(0);
	}
	
}

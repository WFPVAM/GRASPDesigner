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
import java.sql.SQLException;

import java.sql.Time;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.Order;
import net.frontlinesms.data.domain.DataTransfer;
import net.frontlinesms.data.domain.User_Credential;
import net.frontlinesms.data.domain.User_Credential.Field;

/**
 * Class to represent data transfer.
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph      
 * www.fabaris.it <http://www.fabaris.it/>  
 
 */
public interface DataTransferDao {
	
	public List<DataTransfer> getAllUsers();
	
	
	public void saveDataTransfer(DataTransfer user) throws DuplicateKeyException;
	 
	
	public void updateDataTransfer(DataTransfer user) throws DuplicateKeyException;

	
	public void saveDataTransfer(List<DataTransfer> datatransfer);


	public DataTransfer getConfig();
	
}

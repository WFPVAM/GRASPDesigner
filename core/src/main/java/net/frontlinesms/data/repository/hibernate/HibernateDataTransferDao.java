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

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;

import javassist.bytecode.Descriptor.Iterator;

import com.mysql.jdbc.Field;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.Order;
import net.frontlinesms.data.domain.DataTransfer;
import net.frontlinesms.data.repository.DataTransferDao;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.ui.handler.core.DatabaseConnectionFailedDialog;
/**
 * Hibernate implementation of {@link DataTransferDao}.
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph        
 * www.fabaris.it <http://www.fabaris.it/>  
 * 
 */
public class HibernateDataTransferDao extends BaseHibernateDao<DataTransfer> implements DataTransferDao{
	/** Create a new instance of this DAO. */
		
	public HibernateDataTransferDao() {
		super(DataTransfer.class);
	}
	
	/**Added by Fabaris_Raji to select all records of DatabaseTransfer
	 * @return */
	
	public  ArrayList<DataTransfer> getAllUsers(){				
			List<DataTransfer> users = getHibernateTemplate().find("from DataTransfer");	
			ArrayList<DataTransfer> l = new ArrayList<DataTransfer>(); 	
			for(DataTransfer dt: users){			
				l.add(new DataTransfer(dt.getServername(),dt.getServerport(),dt.getDbname(),
						dt.getUsername(),dt.getPassword(),dt.isSharable(),dt.isScheduled(),
						dt.isManual(),dt.getScheduledtime()));		
			}	
			
			return l;
	}
	/**Added by Fabaris_Raji to select a record of DatabaseTransfer
	 * @return DataTransfer*/
	public  DataTransfer getConfig(){
		DataTransfer dt=new DataTransfer();
		List<DataTransfer> users = getHibernateTemplate().find("from DataTransfer");
		for(DataTransfer df: users){			
		dt=new DataTransfer(df.getServername(),df.getServerport(),df.getDbname(),
					df.getUsername(),df.getPassword(),df.isSharable(),df.isScheduled(),
					df.isManual(),df.getScheduledtime());		
		}				
		
		return dt;
	}
	/**Added by Fabaris_Raji to update a record of DatabaseTransfer
	 * @return int*/
	public  void saveDataTransfer(DataTransfer dt)
			throws DataAccessException {
				
		getHibernateTemplate().saveOrUpdate(dt);
	
	}				

	public List<DataTransfer> getAllUsersSorted(int startIndex, int limit,
			net.frontlinesms.data.domain.DataTransfer.Field sortBy,
			Order order) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void updatedataTransfer(DataTransfer user)
			throws DuplicateKeyException {
		// TODO Auto-generated method stub
		
	}
	
	public List<DataTransfer> getAllUsers(int startIndex, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<DataTransfer> getPushed(int startIndex, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateDataTransfer(DataTransfer user)
			throws DuplicateKeyException {
		// TODO Auto-generated method stub
		
	}	
	
	public void saveDataTransfer(List<DataTransfer> datatransfer) {
		// TODO Auto-generated method stub
		
	}	

}


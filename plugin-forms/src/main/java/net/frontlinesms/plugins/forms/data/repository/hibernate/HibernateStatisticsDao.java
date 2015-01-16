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
package net.frontlinesms.plugins.forms.data.repository.hibernate;
import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.forms.data.domain.StatisticsTable;
import net.frontlinesms.plugins.forms.data.repository.StatisticsDao;
/**
 * 
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/>  
 */
public class HibernateStatisticsDao extends BaseHibernateDao<StatisticsTable> implements StatisticsDao, EventObserver {

	protected HibernateStatisticsDao() {
		super(StatisticsTable.class);
		// TODO Auto-generated constructor stub
	}
	
	public void updateStatistics(StatisticsTable statistics) {
		super.updateWithoutDuplicateHandling(statistics);
		
	}

	public void notify(FrontlineEventNotification notification) {
		// TODO Auto-generated method stub
		
	}

	public List<StatisticsTable> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}


	public int getUsersFilteredByNameCount(String contactNameFilter) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void saveStatistics(StatisticsTable statistics)
			throws DuplicateKeyException {
		// TODO Auto-generated method stub
		
	}

	public void setEventBus(EventBus eventBus) {
		eventBus.registerObserver(this);
		super.setEventBus(eventBus);
	}

	public List<StatisticsTable> getAllStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	}

	



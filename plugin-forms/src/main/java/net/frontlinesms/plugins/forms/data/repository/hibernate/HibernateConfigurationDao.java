package net.frontlinesms.plugins.forms.data.repository.hibernate;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.forms.data.domain.Configuration;
import net.frontlinesms.plugins.forms.data.repository.ConfigurationDao;

import org.hibernate.Query;
import org.hibernate.Session;

public class HibernateConfigurationDao extends BaseHibernateDao<Configuration> implements ConfigurationDao, EventObserver{

	protected HibernateConfigurationDao() {
		super(Configuration.class);
	}

	public void notify(FrontlineEventNotification notification) {
	}
	
	/*
	public Configuration getConfiguration() {
		String sql = "from Configuration c";
		Session session = getHibernateTemplate().getSessionFactory().openSession();
		Query query = session.createQuery(sql);
		List<Configuration> result = query.list();
		session.close();
		return result.isEmpty() ? null : result.get(0);
	}
	*/

	             																			
	public Configuration getConfiguration() {
		String sql = "select autoSave from Configuration";
		Session session = getHibernateTemplate().getSessionFactory().openSession();
		Query query = session.createQuery(sql);
		ArrayList<Configuration> result = (ArrayList<Configuration>) query.list();
		session.close();
		return result.isEmpty() ? null : result.get(0);
	}
	

}

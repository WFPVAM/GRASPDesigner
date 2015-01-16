package net.frontlinesms.plugins.forms.data.repository.hibernate;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessResourceFailureException;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.forms.data.domain.FormResponseCoords;
import net.frontlinesms.plugins.forms.data.repository.FormResponseCoordsDao;

public class HibernateFormResponseCoordsDao extends BaseHibernateDao<FormResponseCoords> implements FormResponseCoordsDao,EventObserver {
	
	protected HibernateFormResponseCoordsDao() {
		super(FormResponseCoords.class);
	}

	public void insertResponseCoords(long formResponseID, String frCoordText) {
		try {
			PreparedStatement ps = getSession().connection().prepareStatement("{call InsertResponseCoords(?,?)}");
			ps.setLong(1, formResponseID);
			ps.setString(2, frCoordText);
			ps.execute();
		} catch (DataAccessResourceFailureException e) {
			e.printStackTrace();
		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//ps.execute();
	}

	public void notify(FrontlineEventNotification notification) {
	}

}

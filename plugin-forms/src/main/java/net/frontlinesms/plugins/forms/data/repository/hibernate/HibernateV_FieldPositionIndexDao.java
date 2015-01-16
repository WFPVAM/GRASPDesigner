package net.frontlinesms.plugins.forms.data.repository.hibernate;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.forms.data.domain.V_FieldPositionIndex;
import net.frontlinesms.plugins.forms.data.repository.V_FieldPositionIndexDao;

public class HibernateV_FieldPositionIndexDao extends BaseHibernateDao<V_FieldPositionIndex> implements V_FieldPositionIndexDao, EventObserver{
	
	protected HibernateV_FieldPositionIndexDao() {
		super(V_FieldPositionIndex.class);
	}
	
	public void notify(FrontlineEventNotification notification) {
	}
	
	public int getGeoPositionIndexByFormId(long formId,String type) {
		ArrayList<String> parametersNames = new ArrayList<String>();
		ArrayList<Object> parametersValues = new ArrayList<Object>();
		parametersNames.add("formId");
		parametersValues.add(formId);
		parametersNames.add("type");
		parametersValues.add(type);
		String queryString = "SELECT positionIndex FROM V_FieldPositionIndex AS posIndex WHERE posIndex.form_id = :formId AND posIndex.type =:type";
		List<Integer> result = super.getHibernateTemplate().findByNamedParam(queryString,
				parametersNames.toArray(new String[parametersNames.size()]), parametersValues.toArray());
		if(result.isEmpty())
			return -1;
		else
			return result.get(0);
	}
        
        public List<Integer> getPositionIndexByFormIdAndType(long formId,String type) {
		ArrayList<String> parametersNames = new ArrayList<String>();
		ArrayList<Object> parametersValues = new ArrayList<Object>();
		parametersNames.add("formId");
		parametersValues.add(formId);
		parametersNames.add("type");
		parametersValues.add(type);
		String queryString = "SELECT positionIndex FROM V_FieldPositionIndex AS posIndex WHERE posIndex.form_id = :formId AND posIndex.type =:type";
		List<Integer> result = super.getHibernateTemplate().findByNamedParam(queryString,
				parametersNames.toArray(new String[parametersNames.size()]), parametersValues.toArray());
		return result;
	}
}
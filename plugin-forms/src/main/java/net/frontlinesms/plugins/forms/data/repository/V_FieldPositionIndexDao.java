package net.frontlinesms.plugins.forms.data.repository;

import java.util.List;


public interface V_FieldPositionIndexDao {
	
	public int getGeoPositionIndexByFormId(long formId,String type);
        public List<Integer> getPositionIndexByFormIdAndType(long formId,String type);
	
}

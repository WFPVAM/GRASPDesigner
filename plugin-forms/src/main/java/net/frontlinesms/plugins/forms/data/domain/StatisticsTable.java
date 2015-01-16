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
package net.frontlinesms.plugins.forms.data.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**A class modeling domain for saving statistics
 * 
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 */

@Entity
@Table(name="StatisticsTable")
public class StatisticsTable  {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=true,updatable=false)
	private long id;
	@Column( name = "title", nullable = true)
	private String title;	
	@Column(name="query", columnDefinition = "nvarchar(MAX)" ,nullable = true)
	private String query;
	@Column( name = "form_id", nullable = true)
	private long form_id;
	@Column( name = "chartType", nullable = true)
	private long chartType;
	
	//default Constructor
	public StatisticsTable() {
		
	}
	
	public StatisticsTable(String title,String query,long form_id,long chartType){
		this.title=title;
		this.query=query;
		this.form_id=form_id;
		this.chartType=chartType;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public long getForm_id() {
		return form_id;
	}

	public void setForm_id(long form_id) {
		this.form_id = form_id;
	}

	public long getChartType() {
		return chartType;
	}

	public void setChartType(long chartType) {
		this.chartType = chartType;
	}	
	
	@Override	
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((title == null) ? 0 : title.hashCode());
			return result;
		}
	//> GENERATED CODE
		/** @see Object#toString() */
	@Override
		public String toString() {
			return this.getClass().getName() + "[" +
					"title=" + this.title + ";" +
					"query=" + this.query + ";" +									
					"]";
		}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof StatisticsTable) {
			StatisticsTable comparing = (StatisticsTable) arg0;
			if (comparing.getTitle().equals(this.getTitle())) {
				return true;
			}
			else{
				return false;
			}
			
		}
		else {
			return false;
		}
}
}

/*
 * FrontlineSMS <http://www.frontlinesms.com>
 * Copyright 2007, 2008 kiwanja
 * 
 * This file is part of FrontlineSMS.
 * 
 * FrontlineSMS is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 * 
 * FrontlineSMS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FrontlineSMS. If not, see <http://www.gnu.org/licenses/>.
 */
package net.frontlinesms.data;

import org.hibernate.criterion.Criterion;

/**
 * 
 * @author Alex
 *
 */
public enum Order {
	/** Order from least to greatest. */
	ASCENDING("ASC"),
	/** Order from greatest to least. */
	DESCENDING("DESC");
	
	private final String hqlEquivalent;
	
	private Order(String hqlEquivalent) {
		this.hqlEquivalent = hqlEquivalent;
	}
	
	/**
	 * Creates a criteria to sort a particular property by.
	 * @param propertyName The name of the property we would like to sort by.
	 * @return Hibernate {@link Criterion} for sorting the supplied property 
	 */
	public org.hibernate.criterion.Order getHibernateOrder(String propertyName) {
		if(this==ASCENDING) return org.hibernate.criterion.Order.asc(propertyName);
		if(this==DESCENDING) return org.hibernate.criterion.Order.desc(propertyName);
		throw new IllegalStateException("Unknown order: " + this);
	}
	
	public String toHqlString() {
		return this.hqlEquivalent;
	}
}
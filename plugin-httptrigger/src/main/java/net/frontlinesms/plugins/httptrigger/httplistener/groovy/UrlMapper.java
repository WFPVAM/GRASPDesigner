/**
 * GRASP(Geo-referential Real-time Acquisition Statistics Platform) Designer Tool <http://www.fabaris.it>
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
package net.frontlinesms.plugins.httptrigger.httplistener.groovy;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Class that maps URLs to specified paths given at instantiation time
 * 
 * @author Goncalo Silva
 */
public class UrlMapper {
// > PROPERTIES
	private final String[] filePaths;

// > CONSTRUCTORS	
	private UrlMapper(String[] filePaths){
		this.filePaths = filePaths;
	}

// > ISTANCE METHODS
	/**
	 * Maps a String URL according to the pre-specified files
	 * 
	 * @param The URL that needs to be mapped
	 * @return A String with the mapped file path 
	 */
	public String mapToPath(String url) {
		if (url == null){
			url = "";
		}
		
		for (String s : filePaths) {
			if(url.equals(s)){
				return s;
			}
			
			if (url.startsWith(s + "/")) {
				return s;
			}
		}
		
		return "";
	}

// > FACTORY METHODS
	/**
	 * Factory method that will instantiate UrlMapper with the specified URL paths
	 * 
	 * @param The possible URL paths that will be mapped with this UrlMapper - this array may be modified
	 * @return A new Instance of UrlMapper
	 */
	public static UrlMapper create(String... filePaths) {
		Arrays.sort(filePaths, new Comparator<String>(){
			public int compare(String o1, String o2) {
				return o2.length() - o1.length();
			}
		});
		
		return new UrlMapper(filePaths);
	}
}

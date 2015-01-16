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

import java.io.File;

import net.frontlinesms.resources.ResourceUtils;

/**
 * Class that decides which script to use according to a specific URL
 * 
 * @author Gonçalo Silva
 * @author Alex Anderson <alex@frontlinesms.com>
 */
class ScriptFinder {
	private static final File scriptDirectory = new File(ResourceUtils.getConfigDirectoryPath(), "httptrigger.scripts");;
	
//> INSTANCE METHODS
	/**
	 * Determines which script to call with the passed URI
	 * 
	 * @param The URI path for the required script
	 * @return A {@link File} instance with the script's path
	 * @throws {@link IllegalArgumentException} if the path starts with a '/'
	 */
	public File mapToFile(String path) {

		if(path == null || path.length() == 0){
			path = "index.groovy";
		} else {
			if(path.charAt(0) == '/'){
				throw new IllegalArgumentException();
			}
	
			while(path.endsWith("/")){
				path = path.substring(0, path.length() - 1);
			}
			
			if (!path.endsWith(".groovy")) {
				path += ".groovy";
			}
		}
		
		return new File(getScriptDirectory(), path);
	}

	/** @return the root directory that scripts are stored in */
	public static File getScriptDirectory() {
		return scriptDirectory;
	}
}

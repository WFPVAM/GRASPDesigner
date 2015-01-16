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
package net.frontlinesms.plugins.httptrigger.httplistener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Alex
 *
 */
public interface SimpleUrlRequestHandler {
	/**
	 * Check if this handler should handle the supplied URI.
	 * @param requestUri The request URI, without leading / character
	 * @return <code>true</code> if this should process the supplied URI, <code>false</code> otherwise
	 */
	public boolean shouldHandle(String requestUri);
	
	/**
	 * Process the supplied URI.
	 * @param requestUri
	 * @param response 
	 * @param request 
	 * @return {@link ResponseType#SUCCESS} if the request was processed successfully,
	 * 	{@link ResponseType#FAILURE} if there was a problem,
	 * 	or {@link ResponseType#HANDLED} if the response was handled internally 
	 */
	public ResponseType handle(String requestUri, HttpServletRequest request, HttpServletResponse response);
}

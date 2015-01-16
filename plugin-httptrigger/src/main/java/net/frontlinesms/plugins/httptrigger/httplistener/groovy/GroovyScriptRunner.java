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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.frontlinesms.ErrorUtils;
import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.plugins.httptrigger.httplistener.ResponseType;

/**
 * Class which will handle the script calls to the Groovy shell
 * 
 * @author Gonçalo Silva
 *
 */
class GroovyScriptRunner {
	
//> INSTANCE PROPERTIES
	private final Logger log = FrontlineUtils.getLogger(this.getClass());
	private final File groovyScript;
	private final String[] bindingNames;
	private final Object[] boundObjects;
	
//> CONSTRUCTORS
	/**
	 * @param The Groovy script file
	 * @param A controller for {@link FrontlineSMS} 
	 */
	public GroovyScriptRunner(File mapToFile, String[] bindingNames, Object[] boundObjects) {
		assert(bindingNames.length == boundObjects.length);
		this.groovyScript = mapToFile;
		this.bindingNames = bindingNames;
		this.boundObjects = boundObjects;
	}

//> ISTANCE METHODS	
	public ResponseType run() {
		Binding binding = getBinding();
		GroovyShell shell = new GroovyShell(binding);
		try {
			Object returnedObject = shell.evaluate(groovyScript);
			
			log.trace("returnedObject: " + 
					(returnedObject == null ? null : returnedObject.toString()));
		} catch (Throwable t) {
			handleThrowable(t);
		}

		return ResponseType.HANDLED;
	}
	
	private void handleThrowable(Throwable t) {
		log.trace("Groovy Script " + groovyScript.getName() + " failed to run", t);
		HttpServletResponse response = (HttpServletResponse) getBinding().getVariable("response");
		response.setStatus(500);
		try {
			response.getWriter().write("Error processing script '" + this.groovyScript.getAbsolutePath() + "':\r\n" + ErrorUtils.getStackTraceAsString(t));
		} catch (IOException ex) {
			log.error("Could not output script error due to exception.", ex);
		}
	}

	private Binding getBinding() {
		Binding binding = new Binding();
		for (int i = 0; i < bindingNames.length; i++) {
			binding.setVariable(bindingNames[i], boundObjects[i]);
		}
		return binding;
	}
	
	private String getThreadName() {
		return GroovyScriptRunner.class.getSimpleName() + "::'" + groovyScript.getAbsolutePath() + "'";
	}
}

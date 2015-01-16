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
package net.frontlinesms.plugins.forms.ui;

import net.frontlinesms.plugins.forms.ui.components.FComponent;

/**
 * 
 * Class modeling informations container for the default components 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph    
 * www.fabaris.it <http://www.fabaris.it/>  
 *
 */
public class DefaultComponentDescriptor {
	
	//If you change this you MUST change the position of designer_version in file default_components!!!
	public static int DESIGNER_VERSION_INDEX = 2;
	public static int UNMODIFIABLE_COMPONENT_INDEX;
	//the designer version tag name must match with name in default_components
	public static String DESINGER_VERSION_TAGNAME = "des_version";
	FComponent[] defaultComponents;
	
	
	public FComponent[] getDefaultComponents() {
		return defaultComponents;
	}
	public void setDefaultComponents(FComponent[] defaultComponents) {
		this.defaultComponents = defaultComponents;
	}

}

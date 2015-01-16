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
package net.frontlinesms.ui.handler.importexport;

import net.frontlinesms.ui.UiGeneratorController;

/**
 * @author aga
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/> 
 * 
 */
 
public class ImportDialogHandlerFactory {
	public static ImportDialogHandler createHandler(UiGeneratorController ui, String type) {
		if(type.equals("contacts")) {
			return new ContactImportDialogHandler(ui);
		} else if(type.equals("messages")) {
			return new MessageImportDialogHandler(ui);
		} else if(type.equals("forms")){ 	//fabaris_a.zanchi
			return new FormImportDialogHandler(ui);
		} else if(type.equals("forms-prototype")) { //fabaris_a.zanchi
			return new FormPrototypeImportDialogHandler(ui);
		} else if(type.equals("sql")){				//fabaris_raji
			return new SqlImportDialogHandler(ui);
		} else throw new IllegalStateException();
	}
}

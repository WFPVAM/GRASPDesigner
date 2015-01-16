/*
 * GRASP(Geo-referential Real-time Acquisition Statistics Platform) Designer Tool <http://www.fabaris.it>
 *  Copyright © 2012 ,Fabaris s.r.l
 *  This file is part of GRASP Designer Tool.  
 *   GRASP Designer Tool is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or (at
 *   your option) any later version.  
 *   GRASP Designer Tool is distributed in the hope that it will be useful, but
 *   WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser  General Public License for more details.  
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with GRASP Designer Tool. 
 *   If not, see <http://www.gnu.org/licenses/>
 */
package net.frontlinesms.plugins.forms.xform;
import java.io.File;
import java.io.FileOutputStream;

import net.frontlinesms.plugins.forms.FormsPluginController;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;

/**Exports a Form to a file 
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>  
 */
public class XformFormExporter {

	public static void exportXformToFile(File file, Form selectedForm) throws Exception {
		FileOutputStream outStream = new FileOutputStream(file);
		String fileContent = new String();
		for (FormField ff : selectedForm.getFields()) {
			if (ff.getType() == FormFieldType.DROP_DOWN_LIST){
				System.out.println("-------in export----------- "+ff);
			}
			fileContent = fileContent + ff.getX_form();
		}
		fileContent = FormsPluginController.removeEmptyGroups(fileContent);
		outStream.write(fileContent.getBytes("UTF-8"));
		outStream.flush();
		outStream.close();
	}
}

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
package net.frontlinesms.plugins.forms.csv;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.csv.CsvExporter;
import net.frontlinesms.csv.CsvUtils;
import net.frontlinesms.csv.Utf8FileWriter;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.forms.data.domain.ResponseValue;
import net.frontlinesms.plugins.forms.data.repository.FormResponseDao;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class CsvFormExporter extends CsvExporter {

	/**
	 * Exports results of the supplied form to the supplied file.
	 * 
	 * FIXME fix CSV exports in this method - should be properly escaped - use {@link CsvUtils}
	 * 
	 * @param exportFile File to be written
	 * @param toExport Form to get the results from.
	 * @param aggregate TRUE to aggregate values
	 * @param contactDao Factory to look for the contact
	 * @throws IOException
	 */
	public static void exportForm(File exportFile, Form toExport, ContactDao contactDao, FormResponseDao formResponseDao) throws IOException {
		LOG.trace("ENTER");
		LOG.debug("Filename [" + exportFile.getAbsolutePath() + "]");
		
		Utf8FileWriter out = null;
		try {
			out = new Utf8FileWriter(exportFile);
			
			CsvUtils.writeLine(out, CsvFormExporter.getColumnsNameAsStringArray(toExport));
			for (FormResponse formResponse : formResponseDao.getFormResponses(toExport)) {
				String submitterPhoneNumber = formResponse.getSubmitter();
				Contact submitterContact = contactDao.getFromMsisdn(submitterPhoneNumber);
				if(submitterContact == null) submitterContact = new Contact("", submitterPhoneNumber, "", "", "", true);
				CsvUtils.writeLine(out, CsvFormExporter.getResultsAsStringArray(formResponse, submitterContact));
			}
		}  finally {
			if (out != null) out.close();
			LOG.trace("EXIT"); 
		}
	}
	
	/**
	 * @param toExport The form to be exported
	 * @return An array of Strings representing the text to display as columns
	 */
	private static String[] getColumnsNameAsStringArray (Form toExport) {
		LinkedList<String> columnsName = new LinkedList<String>(); 
		columnsName.add(InternationalisationUtils.getI18nString(FrontlineSMSConstants.COMMON_CONTACT_NAME));
		columnsName.add(InternationalisationUtils.getI18nString(FrontlineSMSConstants.COMMON_PHONE_NUMBER)); 
		
		for (FormField field : toExport.getFields()) {
			if (field.getType().hasValue()) {
				columnsName.add(field.getLabel());
			}
		}
		
		return columnsName.toArray(new String[0]);
	}
	
	/**
	 * @param formResponse A form response
	 * @param contact The contact having submitted this form response
	 * @return An array of Strings representing the text to display as the form values for this contact
	 */
	private static String[] getResultsAsStringArray(FormResponse formResponse, Contact contact) {
		LinkedList<String> resultsValue = new LinkedList<String>();
		resultsValue.add(contact.getName());
		resultsValue.add(contact.getPhoneNumber());
		
		for(ResponseValue r : formResponse.getResults()) {
			resultsValue.add(r.toString());
		}
		return resultsValue.toArray(new String[0]);
	}
}

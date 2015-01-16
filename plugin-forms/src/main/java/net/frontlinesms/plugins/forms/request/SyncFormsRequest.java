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
package net.frontlinesms.plugins.forms.request;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.frontlinesms.data.domain.FrontlineMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Synchronization request by sms.
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 *
 */
public class SyncFormsRequest extends FormsRequestDescription {

	private List<String> receivedFormsIDs = new ArrayList<String>(); 
	public List<FrontlineMessage> returnMessages = new ArrayList<FrontlineMessage>();
	/**
	 * Parse the message as xml and get all ids in /forms/form in a List<String>
	 */
	public SyncFormsRequest(String message) {
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(message.getBytes("UTF8"));
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bais);
			NodeList list = (NodeList) XPathFactory.newInstance().newXPath().compile("/forms/form").evaluate(doc, XPathConstants.NODESET);
			for(int i=0;i<list.getLength();i++){
				Node item = list.item(i);
				receivedFormsIDs.add(item.getTextContent());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * @return the receivedFormsIDs
	 */
	public List<String> getReceivedFormsIDs() {
		return receivedFormsIDs;
	}
	
	public boolean isInList(String item){
		for(String id : receivedFormsIDs){
			if(id.equals(item)) return true;
		}
		return false;
	}

}

/**
 * GRASP(Geo-referential Real-time Acquisition Statistics Platform) Designer
 * Tool <http://www.fabaris.it>
 * Copyright © 2012 ,Fabaris s.r.l This file is part of GRASP Designer Tool.
 * GRASP Designer Tool is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. GRASP Designer Tool is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with GRASP Designer Tool.
 * If not, see <http://www.gnu.org/licenses/>
 */
package net.frontlinesms.plugins.httptrigger.httplistener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.frontlinesms.plugins.httptrigger.HttpTriggerEventListener;

import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

import sun.misc.BASE64Decoder;

/**
 * This class is used to handle request from mobile devices via internet
 * connection.<br/>
 *
 * @author Fabaris Srl: Attila Aknai www.fabaris.it <http://www.fabaris.it/>
 * <http://www.fabaris.it/>
 */
public class FormRequestsHandler extends AbstractHandler {

    HttpTriggerEventListener listener;

    public FormRequestsHandler(HttpTriggerEventListener listener) {
        this.listener = listener;
    }

    public void handle(String target, HttpServletRequest request,
            HttpServletResponse response, int dispatch) throws IOException,
            ServletException {
        if (target.equals("/test")) {
            handleTestRequest(target, request, response, dispatch);
        }
        if (target.equals("/response")) {
            handleResponseRequest(target, request, response, dispatch);
        }
        if (target.equals("/sync")) {
            handleSyncRequest(target, request, response, dispatch);
        }
    }

    public void handleTestRequest(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        Request base_request = (request instanceof Request) ? (Request) request : HttpConnection.getCurrentConnection().getRequest();
        String phone = (String) request.getParameter("phoneNumber");
        String data = (String) request.getParameter("data");
        String result = new String();
        if (phone == null || phone.equals("")) {
            result = "ERROR:Client phone number not received.";
        }
        if (result.equals("")) {
            result = listener.testConnection(phone, data);
        }
        base_request.setHandled(true);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");
        response.getWriter().println(result);
    }

    public void handleResponseRequest(String target,
            HttpServletRequest request, HttpServletResponse response,
            int dispatch) throws IOException, ServletException {
        Request base_request = (request instanceof Request) ? (Request) request
                : HttpConnection.getCurrentConnection().getRequest();
        String phone = (String) request.getParameter("phoneNumber");
        String data = request.getParameter("data");
        System.out.println("XMLString" + data);
        data = decodeString(data);
        if (phone == null || phone.equals("")) {
            return;
        }
        String ret = listener.formResponse(phone, data);
        if (ret == null || "".equals(ret)) {
            ret = "";
            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        base_request.setHandled(true);
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(ret);
    }

    public void handleSyncRequest(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        Request base_request = (request instanceof Request) ? (Request) request : HttpConnection.getCurrentConnection().getRequest();
        String phone = (String) request.getParameter("phoneNumber");
        String data = (String) request.getParameter("data");
        data = data.replace("\n", "").replace("\r", "");
        data = data.replace("  ", "");
        data = data == null ? new String() : data;
        if (phone == null || phone.equals("")) {
            return;
        }
        String ret = listener.syncUserForms(phone, data);
        if (ret == null) {
            ret = "";
        }
        base_request.setHandled(true);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");//response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(ret);
    }

    public static String decodeString(String input) {
        if (input != null) {
            try {
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] decodedString = decoder.decodeBuffer(input);
                ByteArrayInputStream inStream = new ByteArrayInputStream(decodedString);
                GZIPInputStream zipInStream = new GZIPInputStream(inStream);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                int i = 0;
                byte[] buffer = new byte[1024];
                while ((i = zipInStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, i);
                }
                zipInStream.close();
                inStream.close();
                return outStream.toString("UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }
}

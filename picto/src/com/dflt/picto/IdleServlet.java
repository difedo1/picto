package com.dflt.picto;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;


@WebServlet(urlPatterns = {"/Idle"})
public class IdleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public IdleServlet() {
        super();
    }

    
    @Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println("IdleServlet.init()");
    	super.init(config);
		
		
	}
    
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	System.out.println("IdleServlet.doGet().");
    	HttpSession session = request.getSession();
		String roomName     = (String) session.getAttribute("roomName");
		String userName     = (String) session.getAttribute("userName");
		String strRoomName  = "ROOM_" + roomName;
		
		ServletContext application = getServletConfig().getServletContext();
		JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);
		
		if(roomObject != null) {
			JSONArray userArr = roomObject.getJSONArray("USERS");
			
			for(int j = 0; j < userArr.length(); j++) {
				JSONObject user = userArr.getJSONObject(j);
				String name = user.getString("USERNAME");
				
				if(name.equalsIgnoreCase(userName)) {
					boolean blnAdmin = user.getBoolean("ADMIN");
					System.out.println("IdleServlet.init() User " + userName + " setting as Admin " + blnAdmin);
					session.setAttribute("admin", blnAdmin);
					break;
				}
			}
		}
    	
    	response.sendRedirect("idleUser.jsp");
    }
}
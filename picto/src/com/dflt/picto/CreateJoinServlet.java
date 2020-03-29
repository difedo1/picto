package com.dflt.picto;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;


@WebServlet("/CreateJoin")
public class CreateJoinServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CreateJoinServlet() {
        super();
    }

    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("CreateJoinServlet.doPost()");
		
		String userName  = request.getParameter("userName");
		String roomName  = request.getParameter("roomName");
		
		if(userName == null || userName.trim().equalsIgnoreCase("")) {
			HttpSession session = request.getSession();
			session.setAttribute("message",  "Please enter Player name." );
			session.setAttribute("showMsg",  true);
			
			response.sendRedirect("index.jsp");
			return;
		}
		
		if(roomName == null || roomName.trim().equalsIgnoreCase("")) {
			HttpSession session = request.getSession();
			session.setAttribute("message",  "Please enter Room name." );
			session.setAttribute("showMsg",  true);
			
			response.sendRedirect("index.jsp");
			return;
		}
		
		String createRoomBtn = request.getParameter("createBtn");
		String joinRoomBtn   = request.getParameter("joinBtn");
		roomName             = roomName.toUpperCase();
		
		System.out.println("CreateJoinServlet.doPost() UserName: " + userName + " - RoomName: " + roomName);
		System.out.println("CreateJoinServlet.doPost() Create: " + createRoomBtn + " - Join: " + joinRoomBtn);
		
		if(createRoomBtn != null) {
			String strRoomName  = "ROOM_" + roomName;
			ServletContext application = getServletConfig().getServletContext();
			JSONObject newRoom = (JSONObject) application.getAttribute(strRoomName);
				
			if(newRoom == null) {
				JSONObject user    = new JSONObject();
				user.put("USERNAME", userName);
				user.put("ADMIN",    true);
				user.put("POINTS",   0);
				user.put("WORDS",    new JSONArray());
				user.put("READY",    false);
				
				JSONArray usersArr = new JSONArray();
				usersArr.put(user);
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String strFecha = sdf.format(Calendar.getInstance().getTime());
				
				newRoom = new JSONObject();
				newRoom.put("CREATEDDATE",   strFecha);
				newRoom.put("ROOMNAME",      strRoomName);
				newRoom.put("START",         false);
				newRoom.put("FINISHED",      false);
				newRoom.put("MISSEDWORDS",   0);
				newRoom.put("USERS",         usersArr);
				newRoom.put("PLAYERS",    	 new JSONArray());
				newRoom.put("WORDS",    	 new JSONArray());
				newRoom.put("CURRENTPLAYER", "NONE");
				newRoom.put("CURRENTWORD",   "NONE");
					
				application.setAttribute(strRoomName, newRoom);
				
				HttpSession session = request.getSession();
				session.setAttribute("roomName", roomName);
				session.setAttribute("userName", userName);
				session.setAttribute("admin", 	 true);
				session.setAttribute("message",  "");
				session.setAttribute("showMsg",  false);

				//response.sendRedirect("idleUser.jsp");
				response.sendRedirect("Idle");
				return;
			}
			else {
				HttpSession session = request.getSession();
				session.setAttribute("roomName", roomName);
				session.setAttribute("userName", userName);
				session.setAttribute("message",  "Room already exists.");
				session.setAttribute("showMsg",  true);
				
				response.sendRedirect("index.jsp");
				return;
			}
		}
		
		else if(joinRoomBtn != null) {
			String strRoomName  = "ROOM_" + roomName;
			ServletContext application = getServletConfig().getServletContext();
			JSONObject newRoom =  (JSONObject) application.getAttribute(strRoomName);
			
			if(newRoom == null) {
				HttpSession session = request.getSession();
				session.setAttribute("roomName", roomName);
				session.setAttribute("userName", userName);
				session.setAttribute("message",  "Room  "+roomName+" doesn't exists." );
				session.setAttribute("showMsg",  true);
				
				response.sendRedirect("index.jsp");
				return;
			}
			else {
				boolean blnStart = newRoom.getBoolean("START");
				
				if(blnStart) {
					HttpSession session = request.getSession();
					session.setAttribute("roomName", roomName);
					session.setAttribute("userName", userName);
					session.setAttribute("message",  "Room "+roomName+" already started.");
					session.setAttribute("showMsg",  true);
					
					response.sendRedirect("index.jsp");
					return;
				}
				
				JSONArray userArr = newRoom.getJSONArray("USERS");
				
				for(int j = 0; j < userArr.length(); j++) {
					JSONObject user = userArr.getJSONObject(j);
					String name = user.getString("USERNAME");
					
					if(name.equalsIgnoreCase(userName)) {
						HttpSession session = request.getSession();
						session.setAttribute("roomName", roomName);
						session.setAttribute("userName", userName);
						session.setAttribute("message",  "Already exists Player "+userName+" for Room "+roomName+"." );
						session.setAttribute("showMsg",  true);
						
						response.sendRedirect("index.jsp");
						return;
					}
				}
				
				JSONObject user = new JSONObject();
				user.put("USERNAME", userName);
				user.put("ADMIN",    false);
				user.put("POINTS",   0);
				user.put("WORDS",    new JSONArray());
				user.put("READY",    false);
				userArr.put(user);
				
				newRoom.put("USERS", userArr);		
				application.setAttribute(strRoomName, newRoom);
				
				HttpSession session = request.getSession();
				session.setAttribute("roomName", roomName);
				session.setAttribute("userName", userName);
				session.setAttribute("admin", 	 false);
				session.setAttribute("message",  "" );
				session.setAttribute("showMsg",  false);
				
				//response.sendRedirect("idleUser.jsp");
				response.sendRedirect("Idle");
				return;
			}
		}
	}
}
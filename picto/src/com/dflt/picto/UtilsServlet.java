package com.dflt.picto;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;


@WebServlet("/PictoUtils/*")
public class UtilsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public UtilsServlet() {
        super();
    }


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String option = request.getParameter("option");
		//System.out.println("UtilsServlet.doPost() " + option);
		String roomName    = request.getParameter("roomName");
		String strRoomName = "ROOM_" + roomName;
		
		if(option != null && option.equals("listUsers")) { 	
			ServletContext application = getServletConfig().getServletContext();
			JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);
			
			if(roomObject != null) {
				JSONArray usersArr = roomObject.getJSONArray("USERS");
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(usersArr.toString());
			}
			else {
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(new JSONArray().toString());
			}
			
		}
		
		else if(option != null && option.equals("isStarted")) {
			ServletContext application = getServletConfig().getServletContext();
			JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);
			boolean blnStarted         = false;
			
			if(roomObject != null) {
				blnStarted = roomObject.getBoolean("START");
			}
			
			JSONObject respuesta = new JSONObject();
			respuesta.put("STARTED", blnStarted);
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(respuesta.toString());
		}
		
		else if(option != null && option.equals("sendWordsBtn")) {			
 			ServletContext application = getServletConfig().getServletContext();
			JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);
			
			if(roomObject != null) {
				JSONArray userArr = roomObject.getJSONArray("USERS");
				String userName   = request.getParameter("userName");
				
				for(int j = 0; j < userArr.length(); j++) {
					JSONObject user = userArr.getJSONObject(j);
					String auxUserName = user.getString("USERNAME");
					
					if(auxUserName.equalsIgnoreCase(userName)){
						String strWord1 = request.getParameter("word1");
						String strWord2 = request.getParameter("word2");
						
						JSONArray wordArr = new JSONArray();
						wordArr.put(strWord1.trim());
						wordArr.put(strWord2.trim());
						user.put("WORDS", wordArr);
						user.put("READY", true);							
						
						userArr.put(j, user);
						roomObject.put("USERS", userArr);
						application.setAttribute(strRoomName, roomObject);
						
						HttpSession session = request.getSession();
						session.setAttribute("word1", strWord1);
						session.setAttribute("word2", strWord2);
						break;
					}
				}
			}
		}

		else if(option != null && option.equals("startBtn")) { 
			ServletContext application = getServletConfig().getServletContext();
			JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);

			roomObject          = polulateGame(roomObject);
					
			JSONArray playerArr = roomObject.getJSONArray("PLAYERS");
			JSONArray wordArr   = roomObject.getJSONArray("WORDS");
					
			String strUserName = (String) playerArr.remove(0);
			String strWord     = (String) wordArr.remove(0);
						
			roomObject.put("PLAYERS", 		playerArr);
			roomObject.put("WORDS", 		wordArr);
			roomObject.put("CURRENTPLAYER", strUserName);
			roomObject.put("CURRENTWORD",   strWord);
			roomObject.put("START", 		true);
			roomObject.put("START", 		true);
			roomObject.put("MISSEDWORDS",   0);
			
			application.setAttribute(strRoomName, roomObject);
					
			//System.out.println("UtilsServlet.doPost() Started Room: " + roomObject.toString());
			response.sendRedirect("play.jsp");	
		}
		
		else if(option != null && option.equals("currentPlayer")) {
			ServletContext application = getServletConfig().getServletContext();
			JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);
			JSONObject respuesta = new JSONObject();
			
			if(roomObject !=null) {
				String strUserName  = roomObject.getString("CURRENTPLAYER");
				String strWord      = roomObject.getString("CURRENTWORD");
	
				respuesta.put("USERNAME", strUserName);
				respuesta.put("WORD",     strWord);
			}
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(respuesta.toString());
			
		}
		
		else if(option != null && option.equals("nextPlayer")) {
			ServletContext application = getServletConfig().getServletContext();
			JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);

			JSONArray playerArr = roomObject.getJSONArray("PLAYERS");
			JSONArray wordArr   = roomObject.getJSONArray("WORDS");
					
			String strUserName="";
			String strWord    ="";
					
			if(playerArr.length()>0) {
				strUserName  = (String) playerArr.remove(0);
				strWord      = (String) wordArr.remove(0);
						
				roomObject.put("PLAYERS", playerArr);
				roomObject.put("WORDS", wordArr);
				roomObject.put("CURRENTPLAYER", strUserName);
				roomObject.put("CURRENTWORD",   strWord);
			}
			else {
				roomObject.put("FINISHED",      true);
				roomObject.put("CURRENTPLAYER", "");
				roomObject.put("CURRENTWORD",   "");
			}
			
			application.setAttribute(strRoomName, roomObject);
					
			JSONObject respuesta = new JSONObject();
			respuesta.put("USERNAME", strUserName);
			respuesta.put("WORD",     strWord);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(respuesta.toString());
		}

		else if(option != null && option.equals("missBtn")) {
			ServletContext application = getServletConfig().getServletContext();
			JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);
			
			int intMissed = roomObject.getInt("MISSEDWORDS");
			intMissed++;
					
			roomObject.put("MISSEDWORDS", intMissed);
			application.setAttribute(strRoomName, roomObject);
					
			JSONObject respuesta = new JSONObject();
			respuesta.put("MISSED", intMissed);
					
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(respuesta.toString());
		}
		
		else if(option != null && option.equals("hitBtn")) {
			ServletContext application = getServletConfig().getServletContext();
			JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);
			JSONArray userArr = roomObject.getJSONArray("USERS");
			String userName   = request.getParameter("userName");
			int userPoints    = 0;		
			for(int j = 0; j < userArr.length(); j++) {
				JSONObject user = userArr.getJSONObject(j);
				String auxUserName = user.getString("USERNAME");
				
				if(auxUserName.equalsIgnoreCase(userName)){
					userPoints = user.getInt("POINTS");
					userPoints++;
							
					user.put("POINTS", userPoints);				
					userArr.put(j, user);
					roomObject.put("USERS", userArr);
					application.setAttribute(strRoomName, roomObject);
					
					break;
				}
			}
			
			JSONObject respuesta = new JSONObject();
			respuesta.put("POINTS", userPoints);
					
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(respuesta.toString());
		}
		
		else if(option != null && option.equals("isFinished")) {
			ServletContext application = getServletConfig().getServletContext();
			JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);
			boolean blnFinished        = true;
			
			if(roomObject != null) {
				blnFinished        = roomObject.getBoolean("FINISHED");
			}
			
			JSONObject respuesta = new JSONObject();
			respuesta.put("FINISHED", blnFinished);
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(respuesta.toString());
		}
		
		else if(option != null && option.equals("recapRoom")) {
			ServletContext application = getServletConfig().getServletContext();
			JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);
			
			if(roomObject != null) {
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(roomObject.toString());
			}
			else {
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(new JSONObject().toString());
			}
				
			

		}
		
		else if(option != null && option.equals("homeBtn")) {
			ServletContext application = getServletConfig().getServletContext();
			JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);
			JSONArray userArr = roomObject.getJSONArray("USERS");
			String userName = request.getParameter("userName");
				
			for(int j = 0; j < userArr.length(); j++) {
				JSONObject user = userArr.getJSONObject(j);
				String auxUserName = user.getString("USERNAME");
						
				if(auxUserName.equalsIgnoreCase(userName)){				
					user.put("POINTS", 0);
					user.put("READY", false);
					userArr.put(j, user);
							
					roomObject.put("START",         false);
					roomObject.put("FINISHED",      false);
					roomObject.put("CURRENTPLAYER", "NONE");
					roomObject.put("CURRENTWORD",   "NONE");
					roomObject.put("USERS", 		userArr);
							
					application.setAttribute(strRoomName, roomObject);
					break;
				}
			}
		}
		else if(option != null && option.equals("exitBtn")) {
			ServletContext application = getServletConfig().getServletContext();
			JSONObject roomObject      = (JSONObject) application.getAttribute(strRoomName);
			
			if(roomObject != null) {
				JSONArray userArr   = roomObject.getJSONArray("USERS");
				JSONArray playerArr = roomObject.getJSONArray("PLAYERS");
				String userName     = request.getParameter("userName");
				
				System.out.println("UtilsServlet.doPost() " + option + " Username: "+userName);
				for(int j = 0; j < userArr.length(); j++) {
					JSONObject user = userArr.getJSONObject(j);
					String auxUserName = user.getString("USERNAME");
					
					if(auxUserName.equalsIgnoreCase(userName)){
						System.out.println("UtilsServlet.doPost() encontrado");
						boolean blnAdmin = user.getBoolean("ADMIN");
						userArr.remove(j);
						
						if(userArr.isEmpty()){
							application.removeAttribute(strRoomName);
							break;
						}
						
						for(int k = 0; k < playerArr.length(); k++) {
							String player = playerArr.getString(k);
							if(player.equalsIgnoreCase(userName)) {
								playerArr.remove(k);
								k=0;
							}
						}

						if(blnAdmin) {
							if(!userArr.isEmpty()) {
								JSONObject user2 = userArr.getJSONObject(0);
								user2.put("ADMIN", true);
								userArr.put(0, user2);
							}
						}
								
						roomObject.put("USERS", userArr);
						roomObject.put("PLAYERS", playerArr);
								
						application.setAttribute(strRoomName, roomObject);
						System.out.println("EXIT: " + roomObject);
						break;
					}
				}
			}
			
			
		}
	}
	
	
	
	private JSONObject polulateGame(JSONObject roomObject) {
		JSONArray userArr   = roomObject.getJSONArray("USERS");
		JSONArray playerArr = new JSONArray();
		JSONArray wordArr   = new JSONArray();
		
		LinkedList<String> names = new LinkedList<String>();
		LinkedList<String> words = new LinkedList<String>();
		
		for(int i=0; i<userArr.length(); i++) {
			JSONObject user = userArr.getJSONObject(i);
			names.add(user.getString("USERNAME"));
			names.add(user.getString("USERNAME"));
			
			JSONArray wordAux = user.getJSONArray("WORDS");
			words.add(wordAux.getString(0));
			words.add(wordAux.getString(1));
		}
		
		while(names.size()>0) {
			 int index = new Random().nextInt(names.size());
			 playerArr.put(names.remove(index));
		}
		
		while(words.size()>0) {
			int index = new Random().nextInt(words.size());
			wordArr.put(words.remove(index));
		}
		
		roomObject.put("PLAYERS", playerArr);
		roomObject.put("WORDS", wordArr);
		return roomObject;
	}	
}	
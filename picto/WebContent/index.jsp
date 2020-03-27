<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<html>
	<head>
		<title>Picto</title>
		<link rel="icon" type="image/png" href="img/icon.png" />
	</head>
	<body>
		<h1>Picto</h1>
		<!-- Feedback for Errors -->
		<div id="msgOthers" style="color: red">
       		<c:if test="${showMsg}">
       			<label>${message}</label>
       		</c:if>
       	</div>
        <br style="clear: both" />
		
		<form id="indexForm" method="post" action="CreateJoin" onsubmit="return validateForm()" >
	        <!-- Create/Join Room -->
        	<table>
        	<tr>
        		<td><label>Player name:</label></td>
        		<td><input id="userName" name="userName" type="text" maxlength="10" value="${userName}"></td>
        	</tr>        	
        	<tr>
        		<td><label>Room:</label></td>
        		<td><input id="roomName" name="roomName" type="text" maxlength="10" style="text-transform:uppercase" value="${roomName}"></td>
        	</tr>
        	</table>
        	<br style="clear: both" /> <br style="clear: both" />
			
			<div>
        		<input id="createBtn" name="createBtn" type="submit" value="Create">
            	<input id="joinBtn"   name="joinBtn"   type="submit" value="Join">
        	</div>
  		</form>
	</body>
	<script src="js/jquery-3.4.1.min.js"></script>
	<script>
	
	function validateForm() {
		$("#msgOthers").hide();
		
		var name = $("#userName").val();
        var room = $("#roomName").val();
        var blnShow = false;
  
        if (name == null || name.trim() == "" ) {
        	$("#msgOthers").text("Enter valid Player name.")
        	blnShow = true;
        }
        else if( name.indexOf(" ") > 0){
        	$("#msgOthers").text("Not allow blankspace in Player name.")
        	blnShow = true;
        }
        else if (room == null || room.trim() == "" ) {
        	$("#msgOthers").text("Enter valid Room name.")
        	blnShow = true;
        }
        else if( room.indexOf(" ") > 0){
        	$("#msgOthers").text("Not allow blankspace in Room name.")
        	blnShow = true;
        }
        
        if(blnShow) {
        	$("#msgOthers").show();
            $('html,body').animate({
               scrollTop: $("#indexForm").offset().top - 180 
            });
            return false;
        }
	}

	</script>
</html>
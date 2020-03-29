<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<html>
	<head>
		<title>Picto - ${userName}</title>
		<link rel="icon" type="image/png" href="img/icon.png" />
	</head>
	<body>
		<h1>Picto</h1>
		
		<img id="homeBtn" src="img/home.png" alt="Home" align="right" width="32" height="32" style="cursor: pointer;">
		<img id="exitBtn" src="img/exit.png" alt="Exit" align="right" width="32" height="32" style="cursor: pointer;">
		
		<h2 id="roomsName" style="color: blue">Room: ${roomName}</h2>
        
        <div style="color: blue">
       		<label style="font: bold">Players:</label>
       		<ul id="usersName" style="width: max-content;"> 
       			<li>${userName}</li>
       		</ul>
       	</div>
		
		<!-- Feedback for Errors -->
		<div id="msgOthers" style="color: red; display: none">
       		<label>No Message.</label>
       	</div>
        <br style="clear: both" /> <br style="clear: both" />
		
		<form id="recapForm">
	        <input id="userName"  name="userName"  type="hidden" value="${userName}">
        	<input id="roomName"  name="roomName"  type="hidden" value="${roomName}">
        	<input id="roomAdmin" name="roomAdmin" type="hidden" value="${admin}">
  		</form>
	</body>
	
	<script src="js/jquery-3.4.1.min.js"></script>
	<script>
	
	$("#homeBtn").click(function(){ 
		$.ajax({
			type: 'POST',
			url : 'PictoUtils',
			data : { roomName : $('#roomName').val(), userName : $('#userName').val(), option : "homeBtn" },
			success : function(roomData) {
				//$(location).attr('href', 'idleUser.jsp');
				$(location).attr('href', 'Idle');
			},
			error : function (xhr, ajaxOptions, thrownError) {
		    	$("#msgOthers").text("recap.recapRoom() " + thrownError + ": " + xhr.responseText);
				$("#msgOthers").show();
			},
			complete : function(xhr, status) {
		    }
		});
		
	});
	
	$("#exitBtn").click(function(){ 
		$.ajax({
			type: 'POST',
			url : 'PictoUtils',
			data : { roomName : $('#roomName').val(), userName : $('#userName').val(), option : "exitBtn" },
			success : function(roomData) {
				$(location).attr('href', 'index.jsp');
			},
			error : function (xhr, ajaxOptions, thrownError) {
		    	$("#msgOthers").text("recap.recapRoom() " + thrownError + ": " + xhr.responseText);
				$("#msgOthers").show();
			},
			complete : function(xhr, status) {
		    }
		});
		
	});
	
	$(document).ready(function(){
		recapRoom();
	});
	
	function recapRoom(){
		$.ajax({
			type: 'POST',
			url : 'PictoUtils',
			dataType : 'json',
			data : { roomName : $('#roomName').val(), userName : $('#userName').val(), option : "recapRoom" },
			success : function(roomData) {
				if($.isEmptyObject(roomData)){
					$(location).attr('href', 'index.jsp');
				}
				
				$("#roomsName").text("Room: " + roomData.ROOMNAME + " Missed Words: " + roomData.MISSEDWORDS);
						
				var data = roomData.USERS;
				$("#usersName").empty();
				$.each(data, function(index, item) {
					var varWords = item.WORDS;
					var auxWords = "Words: ";
					$.each(varWords, function(idx, word) {
						auxWords = auxWords + word+" ";
					});
					
				    $("#usersName").append("<li>"+item.USERNAME+" &#8594; "+item.POINTS+" Points. "+auxWords+"</li>");
				});
			},
			error : function (xhr, ajaxOptions, thrownError) {
		    	$("#msgOthers").text("recap.recapRoom() " + thrownError + ": " + xhr.responseText);
				$("#msgOthers").show();
			},
			complete : function(xhr, status) {
		    }
		});
	}
	
	</script>
</html>
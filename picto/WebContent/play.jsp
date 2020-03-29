<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<html>
	<head>
		<title>Picto - ${userName}</title>
		<link rel="icon" type="image/png" href="img/icon.png" />
	</head>
	<body>
		<h1>Picto</h1>
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
		
		<form id="playForm">
	        <input id="userName"  name="userName"  type="hidden" value="${userName}">
        	<input id="roomName"  name="roomName"  type="hidden" value="${roomName}">
        	<input id="roomAdmin" name="roomAdmin" type="hidden" value="${admin}">
	        <div >
        		<label id="userPlaying"></label>
        	</div>
	        <div id="userWord" style="display: none">
        		<label>Your Word is:</label>
        		<label id="word"> </label>
        	</div>
  		</form>
  		<div id="divButtons">
  			<%-- <button type="button" id="nextPlayerBtn" name="nextPlayerBtn" value="Next Player" disabled>Next Player</button> --%>
  			<button type="button" id="missWordBtn"   name="missWordBtn"   value="Missed Word">Missed Word</button>
  		</div>
	</body>
	
	<script src="js/jquery-3.4.1.min.js"></script>
	<script>
	
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
	
	var blnPause = false;
	
	function fetchUsersName(){
		$.ajax({
			type: 'POST',
			url : 'PictoUtils',
			dataType : 'json',
			data : { roomName : $('#roomName').val(), option : "listUsers" },
			success : function(data) {
				$("#usersName").empty();
				
				$.each(data, function(index, item) {
				    $("#usersName").append("<li id=" + item.USERNAME + ">" + item.USERNAME +"</li>");
				});
			},
			error : function (xhr, ajaxOptions, thrownError) {
		    	$("#msgOthers").text("play.fetchUsersName() " + thrownError + ": " + xhr.responseText);
				$("#msgOthers").show();
			},
			complete : function(xhr, status) {
		    }
		});
	}
	
	function nextPlayer( typeCall ){
		if(blnPause)
			return;
		
		$.ajax({
			type: 'POST',
			url : 'PictoUtils',
			dataType : 'json',
			data : { roomName : $('#roomName').val(), option : typeCall },
			success : function(data) {
				if(data.USERNAME == ""){
					return;
				}
				else if(data.USERNAME == "NONE"){
					nextPlayer( typeCall );
					return;
				}
				
				$("#usersName").children().css( "background-color", "white" );
				$("#userPlaying").text("Playing: " + data.USERNAME);
				
				var userID = "#" + data.USERNAME;
				$(userID).css("background-color","yellow");

				if($("#userName").val() == data.USERNAME){
					$("#word").text(data.WORD);
					$("#userWord").show();
					//$("#nextPlayerBtn").show();
					//$("#nextPlayerBtn").prop("disabled",true);
					
					$("#missWordBtn").show();
					$('#usersName li').on("click", clickListado);
					blnPause= true;
				}
				else{
					$("#word").text(":)");
					$("#userWord").hide();
					//$("#nextPlayerBtn").hide();
					$("#missWordBtn").hide();
					
					$("#usersName li").off("click");
					blnPause = false;
				}
			},
			error : function (xhr, ajaxOptions, thrownError) {
		    	$("#msgOthers").text("Llamada: " + typeCall + "play.nextPlayer() " + thrownError + ": " + xhr.responseText);
				$("#msgOthers").show();
			},
			complete : function(xhr, status) {
		    }
		});
	}
	
	function isFinished(){
		if(blnPause)
			return;
		
		$.ajax({
			type: 'POST',
			url : 'PictoUtils',
			dataType : 'json',
			data : { roomName : $('#roomName').val(), userName : $('#userName').val(), option : "isFinished" },
			success : function(data) {
				if(data.FINISHED){
					$(location).attr('href', 'recap.jsp');
				}
			},
			error : function (xhr, ajaxOptions, thrownError) {
		    	$("#msgOthers").text("play.isFinished() " + thrownError + ": " + xhr.responseText);
				$("#msgOthers").show();
			},
			complete : function(xhr, status) {
		    }
		});
	}
	
	function clickListado(){
		if($(this).attr('id') == $("#userName").val()){
			alert("Can't assing points to yourself.");
			return;	
		}
		
		var confirmar = confirm("Points for Player: " + $(this).attr('id') +"?");
		if(!confirmar)
			return;
		
		$.ajax({
			type: 'POST',
			url : 'PictoUtils',
			dataType : 'json',
			data : { roomName : $('#roomName').val(), userName : $(this).attr('id'), option : "hitBtn" },
			success : function(data) {
				$("#missWordBtn").hide();
				//$("#nextPlayerBtn").prop("disabled",false);
				$("#usersName li").off("click");
				
				blnPause = false;
				nextPlayer("nextPlayer");
			},
			error : function (xhr, ajaxOptions, thrownError) {
		    	$("#msgOthers").text("play.usersName li() " + thrownError + ": " + xhr.responseText);
				$("#msgOthers").show();
			},
			complete : function(xhr, status) {
		    }
		});
	}
	
	$(document).ready(function(){
		fetchUsersName();

		//$("#nextPlayerBtn").click(function() {
		//	blnPause = false;
		//	nextPlayer("nextPlayer");
		//});
		
		$("#missWordBtn").click(function() {
			var confirmar = confirm("Missed Word???");
			if(!confirmar)
				return;
			
			$.ajax({
				type: 'POST',
				url : 'PictoUtils',
				dataType : 'json',
				data : { roomName : $('#roomName').val(), option : "missBtn" },
				success : function(data) {
					//$("#nextPlayerBtn").prop("disabled",false);
					$("#missWordBtn").hide();
					$("#usersName li").off("click");
					
					blnPause = false;
					nextPlayer("nextPlayer");
				},
				error : function (xhr, ajaxOptions, thrownError) {
			    	$("#msgOthers").text("play.missWordBtn() " + thrownError + ": " + xhr.responseText);
					$("#msgOthers").show();
				},
				complete : function(xhr, status) {
			    }
			});
		});

		nextPlayer("currentPlayer");
		setInterval(nextPlayer, 3000, "currentPlayer");
		setInterval(isFinished, 3000);
	});

	</script>
</html>
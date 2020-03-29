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
		
		<form id="userForm"  onsubmit="return validateForm()">
	        <div>
        		<label>Words:</label>
        		<input id="word1"     name="word1"     type="text"   value="${word1}" maxlength="20"> - 
        		<input id="word2"     name="word2"     type="text"   value="${word2}" maxlength="20">
        		<input id="userName"  name="userName"  type="hidden" value="${userName}">
        		<input id="roomName"  name="roomName"  type="hidden" value="${roomName}">
        		<input id="roomAdmin" name="roomAdmin" type="hidden" value="${admin}">
        	</div>   	
        	<br style="clear: both" /> <br style="clear: both" />
	        	
			<div>
            	<input id="sendBtn" name="sendBtn" type="submit" value="Send Words">
        	</div>
  		</form>
  		<c:if test="${admin}">
	  		<form id="adminForm">
	  			<div>
					<button type="button" id="startBtn" name="startBtn" value="Start Game" disabled>Start Game</button>
	        	</div>
	  		</form>
  		</c:if>
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
	
	function validateForm() {
		$("#msgOthers").hide();
		
		var word1 = $("#word1").val();
        var word2 = $("#word2").val();
        var blnShow = false;
  
        if (word1 == null || word1.trim() == "" ) {
        	$("#msgOthers").text("Enter valid Word 1.")
        	blnShow = true;
        }
        else if( word1.trim().indexOf(" ") > 0){
        	$("#msgOthers").text("Not allow blankspace in Word 1.")
        	blnShow = true;
        }
        else if (word2 == null || word2.trim() == "" ) {
        	$("#msgOthers").text("Enter valid Word 2")
        	blnShow = true;
        }
        else if( word2.trim().indexOf(" ") > 0){
        	$("#msgOthers").text("Not allow blankspace in Word 2")
        	blnShow = true;
        }
        else if(word1 == word2){
        	$("#msgOthers").text("Words must be differents.")
        	blnShow = true;
        }
        
        if(blnShow) {
        	$("#msgOthers").show();
            $('html,body').animate({
               scrollTop: $("#userForm").offset().top - 180 
            });
            return false;
        }
         
        $.ajax({
			type: 'POST',
			url : 'PictoUtils',
			data : { roomName : $('#roomName').val(), userName : $('#userName').val(),
					 word1 : $('#word1').val(), word2 : $('#word2').val(), option : "sendWordsBtn"},
			success : function(data) {
				$("#word1").prop("disabled",true);
		        $("#word2").prop("disabled",true);
				$("#sendBtn").prop("disabled",true);
		        $("#msgOthers").text("Waiting for others users...")
		    	$("#msgOthers").show();
		        
		        roomStarted();
		        setInterval(roomStarted, 3000);
			},
			error : function (xhr, ajaxOptions, thrownError) {
		    	$("#msgOthers").text('idleUser.validateForm() &#8594; ' + thrownError + ": " + xhr.responseText);
				$("#msgOthers").show();
			},
			complete : function(xhr, status) {
		    }
		});

        return false
	}

	function fetchUsersName(){
		$.ajax({
			type: 'POST',
			url : 'PictoUtils',
			dataType : 'json',
			data : { roomName : $('#roomName').val(), option : "listUsers" },
			success : function(data) {
				if (data.length == 0){
					$(location).attr('href', 'index.jsp');
					return;
				}
				
				$("#usersName").empty();
				var allReady = true;
				$.each(data, function(index, item) {
					var strReady ="";
					
					if(item.READY)
						strReady = " &#8594; READY";
					else
						allReady = false;	
						
				    $("#usersName").append('<li>' + item.USERNAME + strReady +'</li>');
				});
				
				var isAdmin = $("#roomAdmin").val();
				if(isAdmin){
					if(allReady){
						$("#startBtn").prop("disabled",false);
					}
					else{
						$("#startBtn").prop("disabled", true);
					}	
				}
			},
			error : function (xhr, ajaxOptions, thrownError) {
		    	$("#msgOthers").text("idleUser.fetchUsersName()" + thrownError + ": " + xhr.responseText);
				$("#msgOthers").show();
			},
			complete : function(xhr, status) {
		    }
		});
	}
	
	function roomStarted(){
		$.ajax({
			type: 'POST',
			url : 'PictoUtils',
			dataType : 'json',
			data : { roomName : $('#roomName').val(), option : "isStarted" },
			success : function(data) {
				if(data.STARTED){
					$(location).attr('href', 'play.jsp');
				}
			},
			error : function (xhr, ajaxOptions, thrownError) {
		    	$("#msgOthers").text("idleUser.roomStarted() " + thrownError + ": " + xhr.responseText);
				$("#msgOthers").show();
			},
			complete : function(xhr, status) {
		    }
		});
	}

	$(document).ready(function(){
		fetchUsersName();
		setInterval(fetchUsersName, 3000);
		
		var isAdmin = ($("#roomAdmin").val() == 'true');
		if(isAdmin){
			$("#startBtn").click(function(){
				$("#startBtn").prop("disabled",true);
			
				$.ajax({
					type: 'POST',
					url : 'PictoUtils',
					data : { roomName : $('#roomName').val(), userName : $('#userName').val(), option : "startBtn" },
					success : function(data) {
						roomStarted();
					},
					error : function (xhr, ajaxOptions, thrownError) {
			    		$("#msgOthers").text("idleAdmin.startBtn" + thrownError + ": " + xhr.responseText);
						$("#msgOthers").show();
					},
				});
			});	
		}
	});
	
	</script>
</html>
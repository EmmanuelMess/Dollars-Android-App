<?php
	$ver = "2";
	$server = ""; //replaced it with database server name
	$username = "";  //replaced it with database username
	$password = "";  //replaced it with database password
	$dbname = "";//replaced it with database
	$user_table = "users"; //(INT id, STRING nick, avatar, INT birthDay, INT birthMonth, INT birthYear, STRING description, STRING gender)
	$private_chats_index_table = "privatesI";//(STRING nick, STRING privates)
	$global_chat_table = "chat";//(INT id, INT time, STRING nick, BOOL isImage, STRING msg) 
	$private_chat_table = "privates";

	// Create connection
	$conn = mysqli_connect($server, $username, $password, $dbname);
	// Check connection
	if (mysqli_connect_errno()) {
		echo "Failed to connect to MySQL: ".mysqli_connect_error();
	}
?>
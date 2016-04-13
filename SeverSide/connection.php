<?php
	$server = ""; //replaced it with database server name
	$username = "";  //replaced it with database username
	$password = "";  //replaced it with database password
	$dbname = "";//replaced it with database
	$user_table = "users";
	$global_chat_table = "chat";

	// Create connection
	$conn = mysqli_connect($server, $username, $password, $dbname);
	// Check connection
	if (!$conn) {
		die("Connection failed: ".mysqli_connect_error());
	}
?>
<?php
	$ver = "5";
	$server = "localhost:8080";//replaced it with database server name
	$username = "";//replaced it with database username
	$password = "";//replaced it with database password
	$dbname = "F:\usuarios\alumno\Documentos\GitHub\Dollars-Android-App\DB (APROXIMATION!).sqlite";//replaced it with database
	$user_table = "users"; //CREATE TABLE "users" ("id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , "nick" VARCHAR NOT NULL , "avatar" BLOB NOT NULL , "birth" DATE NOT NULL , "description" VARCHAR NOT NULL , "gender" INTEGER NOT NULL , "isTracked" BOOL NOT NULL  DEFAULT 1)
	$private_chats_table = "privates";//CREATE TABLE "privates" ("id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , "id_sender" INTEGER NOT NULL , "id_receiver" INTEGER NOT NULL , "time" DATETIME NOT NULL , "isImage" BOOL NOT NULL  DEFAULT 0, "msg" BLOB NOT NULL )
	$global_chat_table = "chat";//CREATE TABLE "chat" ("id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , "time" DATETIME NOT NULL , "nick" VARCHAR NOT NULL , "isImage" BOOL NOT NULL  DEFAULT 0, "msg" BLOB NOT NULL )

	// Create connection
	$conn = mysqli_connect($server, $username, $password, $dbname);
	// Check connection
	if (mysqli_connect_errno()) {
		echo "Failed to connect to MySQL: ".mysqli_connect_error();
	}
?>

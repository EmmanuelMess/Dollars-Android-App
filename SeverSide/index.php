<!doctype html>

<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Web Chat Alpha</title>
	<meta name="author" content="EmmanuelMess">
	<link rel="stylesheet" href="css/styles.css?v=1.0">

	<!--[if lt IE 9]>
	<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
</head>

<body>
	<a href="http://roadrunner-forums.com/boards/App/chat.php?amount=10&web=1">Click here for last 10 messages</a><br />
	<a href="http://roadrunner-forums.com/boards/App/send_msg.php?&chat=global&nick=test&is_text=1&msg=test&web=1">Click here to send test message</a><br />
	
	<?php
		include_once("connection.php");
		echo "Version: $ver";
	?>	
</body>
</html>

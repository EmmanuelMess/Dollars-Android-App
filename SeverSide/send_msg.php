<?PHP	
	include_once("connection.php");
	if(isset($_POST['chat']) && isset($_POST['nick_id']) && isset($_POST['is_image']) && isset($_POST['msg'])) {
		$time = time();
		$chat = $_POST['chat'];
		$nickId = $_POST['nick_id'];
		$isImage = $_POST['is_image'];
		$msg = $_POST['msg'];
		
		if($chat === "global") {
			$query = "INSERT INTO $global_chat_table (time, nick, isImage, msg) VALUES ('$time', '$nickId', '$isImage', '$msg')";
		} elseif(isset($_POST['receiver'])) {
			$receiverId = $_POST['receiver'];
			$query = "INSERT INTO $private_chat_table (time, id_sender, id_receiver, isImage, msg) VALUES ('$time', '$nickId', '$receiverId', '$isImage', '$msg')";
		}

		$result = mysqli_query($conn, $query);		
	}

	echo(($result?"success":die(mysqli_error($conn).". Query: '$query'.")));

	include_once("return.php");
?>
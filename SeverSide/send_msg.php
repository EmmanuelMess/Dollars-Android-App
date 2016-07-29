<?PHP	
	include_once("connection.php");
	if(isset($_POST['chat']) && isset($_POST['nick'])) {
		$time = time();
		$chat = $_POST['chat'];
		$nickId = $_POST['nick'];

		if(isset($_POST['is_text']) && $_POST['is_text']) {
			$isImage = 0;
			$msg = $_POST['msg'];
		} else {
			$isImage = 1;
			$msg = $_POST['image'];
		}
		
		if(isset($_POST['msg'])){
			if($chat === "global") {
				$query = "INSERT INTO $global_chat_table (time, nick, isImage, msg) VALUES ($time, $nickId, $isImage, '$msg')";
			} elseif(isset($_POST['receiver'])) {
				$receiverId = $_POST['receiver'];
				$query = "INSERT INTO $private_chat_table (time, id_sender, id_receiver, isImage, msg) VALUES ($time, $nickId, $receiverId, $isImage, '$msg')";
			}
		}

		$result = mysqli_query($conn, $query);		
	}

	echo(($result?"success":die(mysqli_error($conn).". Query: '$query'.")));

	include_once("return.php"); 
?>
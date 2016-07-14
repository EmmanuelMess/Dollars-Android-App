<?PHP	
	include_once("connection.php");
	if(isset($_POST['chat']) && isset($_POST['nick'])) {
		$time = time();
		$chat = $_POST['chat'];
		$nick = $_POST['nick'];

		if(isset($_POST['is_text']) && $_POST['is_text']) {
			if(isset($_POST['msg'])){
				$msg = $_POST['msg'];

				if($chat === "global") {
					$query = "INSERT INTO $global_chat_table (time, nick, isImage, msg) VALUES ($time, '$nick', 0, '$msg')";
				} elseif(isset($_POST['receiver'])) {
					$query = "INSERT INTO $private_chat_table (time, id_sender, id_receiver, isImage, msg) VALUES ($time, '$nick', 0, '$msg')";
				}
			}
		} elseif(isset($_POST['image'])) {
			$msg = $_POST['image'];

			if($chat == "global")
				$query = "INSERT INTO $global_chat_table (time, nick, isImage, msg) VALUES($time, '$nick', 1, $image)";
			elseif(isset($_POST['receiver'])) {
				$query = "INSERT INTO $private_chat_table (time, id_sender, id_receiver, isImage, msg) VALUES ($time, '$nick', 1, '$image')";
			}
		}

		$result = mysqli_query($conn, $query);
		
		if(!$result) die(mysqli_error($conn).". Query: '$query'.");
		
		echo(($result?"succes":"failure"));
	}
	include_once("return.php"); 
?>
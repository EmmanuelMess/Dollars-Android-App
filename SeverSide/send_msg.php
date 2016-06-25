<?PHP	
	include_once("connection.php"); 
	if(isset($_POST['time']) && isset($_POST['chat']) && isset($_POST['nick'])) {
			$time = $_POST['time'];
			$chat = $_POST['chat'];
			$nick = $_POST['nick'];
		if((isset($_POST['is_text'])) > 0) {
			if(isset($_POST['msg'])){
				$msg = $_POST['msg'];

				if($chat === "global")
					$query = "INSERT INTO $global_chat_table (time, nick, isImage, msg) VALUES ($time, '$nick', FALSE, '$msg');";
				elseif(isset($_POST['destination'])) {
					//TODO
				}
			}
		} elseif(isset($_POST['image'])) {
			$msg = $_POST['image'];

			if($chat === "global")
				$query = "INSERT INTO $global_chat_table (time, nick, isImage, msg) VALUES($time, '$nick', TRUE, $image);";
			else {
				//todo
			}
		}

		$result = mysqli_query($conn, $query);
		
		if(!$result) die(mysqli_error($conn).". Connection: '$conn'. Query: '$query'.");
		echo("$result:".($result > 0?"succes":"failure"));
	}
	include_once("return.php"); 
?>
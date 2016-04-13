<?PHP
    include_once("connection.php");

	//TODO private chat

	if(isset($_POST['amount'])) {
		$query = "SELECT * FROM ".$global_chat_table.";";//TODO use amount
		$result = mysqli_query($conn, $query);

		while($row = mysqli_fetch_assoc($result)){
				$data[] = $row;
		}
		
		echo json_encode($data);//TODO encode the number of messages before the first one
	}
?>
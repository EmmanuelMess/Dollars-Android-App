<?PHP
    include_once("connection.php");

	if(isset($_POST['nick'])) {
		if(isset($_POST['location'])) {
			//TODO
		}

		$nick = $_POST['nick'];
		
		$query = "SELECT ".$nick." FROM ".$user_table.";";

		$result = mysqli_query($conn, $query);

		while($row = mysqli_fetch_assoc($result)){
				$data[] = $row;
		}
		echo json_encode($data);
	}
?>


<?PHP
    include_once("connection.php");

	//TODO private chat
	$last_id = "SELECT max(id) FROM".$global_chat_table;

	if(isset($_POST['lastId'])) {
		$query = $last_id;
	} elseif(isset($_POST['amount'])) {
		$amount = $_POST['amount'];		
		$query = "SELECT * FROM ".$global_chat_table." WERE id>($last_id)-$amount;";
	}
	
	$result = mysqli_query($conn, $query);

	while($row = mysqli_fetch_assoc($result)){
			$data[] = $row;
	}

	echo json_encode($data);
	exit;
?>
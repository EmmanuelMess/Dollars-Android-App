<?PHP
    include_once("connection.php");

	//TODO private chat
	if(isset($_POST['startId']))
		$startId = $_POST['startId'];
	else
		$startId = 1;

	if(isset($_POST['amount'])) 
		$amount = $_POST['amount'];	
	else $amount = 100;

	$query = "((SELECT * FROM $global_chat_table LIMIT $startId) ORDER BY id DESC LIMIT $amount) ORDER BY id ASC";

	$result = mysqli_query($conn, $query);

	if(!$result) die(mysqli_error($conn).". \nQuery: '$query'.");

	while($row = mysqli_fetch_assoc($result)){
		$data[] = $row;
	}

	echo json_encode($data);
	include_once("return.php"); 
?>
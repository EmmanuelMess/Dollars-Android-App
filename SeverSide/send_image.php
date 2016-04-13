<?PHP
include_once("connection.php"); 
if(isset($_POST['time']) && isset($_POST['chat']) && isset($_POST['nick']) && isset($_POST['image'])){
	$time = $_POST['time'];
	$chat = $_POST['chat'];
	$nick = $_POST['nick'];
    $msg = $_POST['image'];
	
	if($chat === "global")
    	$query = "INSERT INTO $global_chat_table VALUES($time, '$nick', $image, TRUE)";
	else //todo
		;
    
    $result = mysqli_query($conn, $query);
    if($result > 0){
        echo "success";
        exit;
    } else {
        echo "failed";
        exit;
    }
}
?>

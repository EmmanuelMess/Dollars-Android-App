<?PHP
include_once("connection.php"); 
if(isset($_POST['nick'])){
	$nick = $_POST['nick'];
    //TODO
    
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
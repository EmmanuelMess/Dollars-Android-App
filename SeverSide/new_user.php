<?PHP
	include_once("connection.php");

	if(isset($_POST['image']) && isset($_POST['nick']) && isset($_POST['birth_day']) 
	   && isset($_POST['birth_month']) && isset($_POST['birth_year'])
	   && isset($_POST['gender'])) {
		$image = $_POST['image'];
		$nick = $_POST['nick'];
		$birth_day = $_POST['birth_day'];
		$birth_month = $_POST['birth_month'];
		$birth_year = $_POST['birth_year'];
		$gender = $_POST['gender'];

		$query = "INSET INTO $users_table VALUES ($image, '$nick', $birth_day, $birth_month, $birth_year, '$gender'";

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

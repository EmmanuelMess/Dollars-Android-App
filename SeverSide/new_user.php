<?PHP
	include_once("connection.php");

	if(isset($_POST['nick']) && isset($_POST['avatar']) && isset($_POST['birth_year']) && isset($_POST['birth_month']) && isset($_POST['birth_day']) && isset($_POST['description']) && isset($_POST['gender']) && isset($_POST['isTracked'])) {
		
		$nick = $_POST['nick'];
		$image = $_POST['avatar'];
		$birth = $_POST['birth_year']."-".$_POST['birth_month']."-".$_POST['birth_day'];
		$desc = $_POST['description'];
		$gender = $_POST['gender'];
		if(isset($_POST['isTracked']))
			$tracked = $_POST['isTracked'];
		else $tracked = 0;
		
		$query = "INSET INTO $users_table (nick, avatar, birth, description, gender) VALUES ('$nick', $image, $birth, '$desc', $gender)";

		$result = mysqli_query($conn, $query);
	}
	echo(($result?"succes":"failure"));
?>

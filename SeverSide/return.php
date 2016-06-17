<?PHP
    mysqli_free_result($result);
	mysqli_close($conn);

	if(isset($_POST['web'])) {
		if($_POST['web'] > 0) {
			echo "<br /><a href=\"./index.html\">Click to return</a>";
		}
	}
?>
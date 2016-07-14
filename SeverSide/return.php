<?PHP
	if($result != null)
		mysqli_free_result($result);
	
	mysqli_close($conn);

	if(isset($_POST['web']) && $_POST['web'] != 0) {
		echo "Version ".$ver;
		echo "<br /><a href=\"./index.html\">Click to return</a>";
	}
?>
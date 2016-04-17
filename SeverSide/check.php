<?PHP
	$g_link = false;
	if(is_resource($g_link)  &&  get_resource_type($g_link)=='mysql link'){
	   echo 'MYSQL';
	}else{
		if(is_object($g_link)  && get_class($g_link)=='mysqli'){
			echo 'MYSQLI';
		}
	}
?>

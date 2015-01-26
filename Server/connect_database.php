<?php
	
	/* initializing database variables. */
	$server = 'localhost';
	$database = 'InfoX';
	$username = 'agamagarwal';
	$password = 'abcd1234';

	/* connecting to the databse. */
	$connection = mysqli_connect($server, $username, $password, $database);

?>
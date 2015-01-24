<?php
	
	/* initializing database variables. */
	$server = 'localhost';
	$database = 'InfoX';
	$username = 'root';
	$password = '123';

	/* connecting to the databse. */
	$connection = mysqli_connect($server, $username, $password, $database);

?>
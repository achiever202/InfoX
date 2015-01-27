<?php
	
	/* connecting to the databse. */
	require_once("connect_database.php");
   require_once("common/functions.php");
   require_once("common/constants.php");

	/* checking for successful connection. */
   	if(!$connection)
   		die("Connection failed: " . mysqli_connect_error());

   	/* for post requests. */
   	if(isset($_POST['phone']) && isset($_POST['password']))
   	{
   		$phone = mysqli_real_escape_string($connection, $_POST['phone']);
   		$password = mysqli_real_escape_string($connection, $_POST['password']);

   		/* getting the record from the databse. */
      	$sql_query = "SELECT * FROM `users` WHERE `phone`='$phone' LIMIT 1";
      	$result = mysqli_query($connection, $sql_query);

      	/* error in connection. */
      	if(!$result)
      		die("Error logging in: " . mysqli_error($connection));

      	/* if no record found. */
      	if(mysqli_num_rows($result)==0)
      		die("Error logging in: Phone number not registered.");

      	/* getting the associated array. */
      	$row = $result->fetch_assoc();

      	/* checking for correct password. */
      	if(strcmp(encode_password($password), $row['password'])==0)
      		echo "Success!";
      	else
      		die("Error logging in: Wrong password.");
   	} else {
         echo INVALID_REQUEST;
      }

   	mysqli_close($connection);

?>
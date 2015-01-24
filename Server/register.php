<?php

   /* connecting to the databse. */
   include("connect_database.php");

   /* checking for successful connection. */
   if(!$connection)
      die("Connection failed: " . mysqli_connect_error());

   /* TO-D0 change it to post request. */
   /* if post request. */
   if($_GET)
   {
      /* getting the details of the user from the POST request. */
      $phone = $_GET['Phone'];
      $name = $_GET['Name'];
      $GCM_ID = $_GET['GCM_ID'];
      $password = $_GET['Password'];

      /* check if the phone is already registered. */
      $sql_query = "SELECT * FROM `users` WHERE `phone`='$phone' LIMIT 1";
      $result = mysqli_query($connection, $sql_query);

      if(!$result)
         die("Error registering user: " . mysqli_error($connection));

      if(mysqli_num_rows($result)>0)
         die("Error registring user: Phone number already registered.");

      /* registering the new user. */
      $sql_query = "INSERT INTO users (phone, GCM_ID, name, password)
                     VALUES ('$phone', '$GCM_ID', '$name', '$password')";

      if(mysqli_query($connection, $sql_query))
         echo "Success!";
      else
         die("Error registering user: " . mysqli_error($connection));

   } 

   mysqli_close($connection);
?>
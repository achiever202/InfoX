<?php

   /* connecting to the databse. */
   require_once("connect_database.php");
   require_once("common/functions.php");
   require_once("common/constants.php");

   if(isset($_POST['phone']) && isset($_POST['name']) && isset($_POST['password']))
   {
      /* getting the details of the user from the POST request. */
      $phone = mysqli_real_escape_string($connection, $_POST['phone']);
      $name = mysqli_real_escape_string($connection, $_POST['name']);
      $password = mysqli_real_escape_string($connection, $_POST['password']);

      $hashed_password = encode_password($password);

      /* check if the phone is already registered. */
      $sql_query = "SELECT * FROM `users` WHERE `phone`='$phone' LIMIT 1";
      $result = mysqli_query($connection, $sql_query);

      if(!$result)
         die("Error registering user: " . mysqli_error($connection));

      if(mysqli_num_rows($result)>0)
         die("Error registring user: Phone number already registered.");

      /* registering the new user. */
      $sql_query = "INSERT INTO users (phone, name, password)
                     VALUES ('$phone', '$name', '$hashed_password')";

      if(mysqli_query($connection, $sql_query))
         echo "Success!";
      else
         die("Error registering user: " . mysqli_error($connection));

   } else {
      echo INVALID_REQUEST;
   }
   mysqli_close($connection);
?>
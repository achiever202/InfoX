<?php
   
   /* initializing database variables. */
   $server = 'localhost';
   $database = 'android_tracker';
   $username = 'root';
   $password = '123';
   /* connecting to the databse. */
   $connection = mysqli_connect($server, $username, $password, $database);
   /* checking for successful connection. */
   if(!$connection)
      die("Connection failed: " . mysqli_connect_error());
   /* for post requests. */
   if($_POST)
   {
      /* getting the list of contacts. */
      $contact_string = $_POST['Contacts'];
      $contacts = explode("$", $contact_string, 10000);
      /* checking if the conntacts are registered. */
      $registered_contacts = "";
      foreach ($contacts as $contact)
      {
         $sql_query = "SELECT * FROM `users` WHERE `phone` = '$contact' LIMIT 1";
         $result = mysqli_query($connection, $sql_query);
         /* error in connection. */
         if(!$result)
            die("Error building contact list: " . mysqli_error($connection));
         if(mysqli_num_rows($result)==1)
            $registered_contacts = $registered_contacts . $contact . "$";
      }
      echo $registered_contacts;
   }
   mysqli_close($connection);
?>

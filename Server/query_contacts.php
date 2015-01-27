<?php
   
   /* connecting to the databse. */
   include("connect_database.php");
   
   /* checking for successful connection. */
   if(!$connection)
      die("Connection failed: " . mysqli_connect_error());
   
   /* for post requests. */
   if($_POST)
   {
      /* getting the list of contacts. */
      $contact_json_string = $_POST['data'];

      $json = json_decode($contact_json_string, true);

      $registered_contacts = "";
      foreach ($json as $item)
      {
         foreach($item->data->number as $contact)
         {
            $sql_query = "SELECT * FROM `users` WHERE `phone` = '$contact' LIMIT 1";
            $result = mysqli_query($connection, $sql_query);
         
            /* error in connection. */
            if(!$result)
               die("Error building contact list: " . mysqli_error($connection));
            if(mysqli_num_rows($result)==1)
               $registered_contacts = $registered_contacts . $contact . "$";
         }
      }
      echo $registered_contacts;
   }
   mysqli_close($connection);
?>

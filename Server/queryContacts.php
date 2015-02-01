<?php
   
   /* connecting to the databse. */
   include("connect_database.php");
   
   /* checking for successful connection. */
   if(!$connection)
      die("Connection failed: " . mysqli_connect_error());
   
   /* for post requests. */
   if(isset($_POST['data']))
   {
      /* getting the list of contacts. */
      $contact_json_string = $_POST['data'];

      $json = json_decode($contact_json_string, true);

      $data = $json["data"];
      $registered_contacts = array();
      foreach($data as $contact)
      {
         $sql_query = "SELECT * FROM `users` WHERE `phone` = '".$contact["number"]."'";
         $result = mysqli_query($connection, $sql_query);
      
         /* error in connection. */
         if(!$result)
            die("Error building contact list: " . mysqli_error($connection));
         if(mysqli_num_rows($result)==1)
            array_push($registered_contacts, $contact);
      }
      echo json_encode(array("data"=>$registered_contacts));
   }
   mysqli_close($connection);
?>

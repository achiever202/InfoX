<?php
require_once("common/constants.php");
require_once("connect_database.php");

if(!isset($_POST['data']))
	die(INVALID_REQUEST);


$json_data=json_decode($_POST['data'], true);

$phone=mysqli_real_escape_string($json_data["phNo"]);
$list=$json_data['content_id_list'];


$query="SELECT user_id FROM users WHERE phone='$phone'";
$result=mysqli_query($connection, $query);

if(!$result)
	die(INVALID_REQUEST);

$user_id=mysqli_fetch_assoc($result)["user_id"];

$query="INSERT IGNORE INTO downloads (user_id, content_id) VALUES ";

for($i=0;$i<sizeof($list)-1;$i++)
	$query.="({$user_id}, ".$list[$i]."), ";
$query.="({$user_id}, ".$list[$i].");";
$result=mysqli_query($connection, $query);

if(!$result)
	die(INVALID_REQUEST);

echo json_encode(array("status" => OK));s
?>
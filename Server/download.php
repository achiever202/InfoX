<?php
require_once("common/constants.php");
require_once("connect_database.php");

if(!isset($_POST['data']))
	die(INVALID_REQUEST);

$json_data=json_decode($_POST['data'], true);

$phone=mysqli_real_escape_string($connection, $json_data["phNo"]);

$query="SELECT user_id FROM users WHERE phone='$phone'";
$result=mysqli_query($connection, $query);

if(!$result)
	die(INVALID_REQUEST);

$user_id=mysqli_fetch_assoc($result)["user_id"];

//get user preferences
$query="SELECT EXISTS(SELECT 1 FROM preferences WHERE user_id='$user_id')";
$result=mysqli_query($connection, $query);
$has_preferences=(mysqli_fetch_row($result)[0])?true:false;

//TODO: Modify this to get apt content
$query="SELECT * FROM contents AS c"
	.($has_preferences?" INNER JOIN preferences AS p ON (c.category_id=p.category_id AND p.user_id='$user_id')":"")
	." WHERE NOT EXISTS (SELECT 1 FROM downloads AS d WHERE d.user_id='$user_id' AND d.content_id=c.content_id)
	ORDER BY rand() LIMIT 5;";

$result=mysqli_query($connection, $query);

if($result) {
	$data=array();

	while(($row=mysqli_fetch_assoc($result))) {
		$new=array(
			"category"=>$row["category_id"],
			"tileType"=>$TILES[$row["category_id"]],
			"content"=>$row["content"],
			"content_id"=>$row["content_id"],
			"langId"=>$row["lang_id"],
			"file_name"=>$row["file_name"],
			"file_path"=>$row["file_path"],
			"time_added"=>$row["time_added"],
			"time_expiry"=>$row["time_expiry"],
			"downloadRequired"=>$row["content"]==NULL?1:0
		);
		array_push($data, $new);
	}

	$contents=array(
		"status"=>OK,
		"data"=>$data
	);

	echo json_encode($contents);
}
?>
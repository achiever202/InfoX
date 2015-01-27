<?php
require_once("common/constants.php");
require_once("connect_database.php");

//TODO: Modify this to get apt content
$query="SELECT * FROM contents ORDER BY rand();";

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
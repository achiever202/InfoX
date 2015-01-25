<?php
require_once("common/constants.php");

//dummy data
$edu=array(
	"tileType" => TILE_EDUCATION,
	"category" => EDU,
	"content" => "Ye duniya, ye duniya peetal di. Ye duniya peetal di. Baby doll mai sone di.",
	"downloadRequired" => 0
	);

$weat=array(
	"tileType" => TILE_WEATHER,
	"category" => PARTLY_SUNNY,
	"content" => "24;Aaj",
	"downloadRequired" => 0
	);

$reply=array(
	"status" => OK,
	"data" => array($edu, $weat)
	);

echo json_encode($reply);
?>
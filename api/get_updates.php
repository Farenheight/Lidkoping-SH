<?php

function getUpdates(){
	echoTestOrder();
}

function echoTestOrder(){
	$a = array(
	   "id" => 1,
	   "timeCreated" => 1380616350928,
	   "lastTimeUpdate" => 1380616350931,
	   "cemetary" => "KyrkogÃ¥rd",
	   "orderDate" => 1380616350928,
	   "orderNumber" => "130001",
	   "idName" => "O.S",
	   "cemetaryBoard" => "KyrkogÃ¥rdsnÃ¤mnd",
	   "cemetaryBlock" => "Kvarter",
	   "cemetaryNumber" => "Nummer",
	   "customer" => array(
	      "title" => "Mr",
	      "name" => "Namn Efternamn",
	      "address" => "Adress gata 5",
	      "postAddress" => "123 45 Stad",
	      "eMail" => "email@test.se",
	      "id" => 500
	   ),
	   "products" => array(
	      	array(
	         "stoneModel" => "NB 49",
	         "sideBackWork" => "RÃ¥hugget",
	         "textStyle" => "Helvetica nedhuggen i guld",
	         "ornament" => "Blomma nedhuggen i guld",
	         "id" => 400,
	         "materialColor" => "Hallandia",
	         "description" => "Beskrivning",
	         "frontWork" => "Polerad",
	         "tasks" => array(
	            array(
	               "status" => "DONE",
	               "station" => array(
	                  "id" => 1,
	                  "name" => "Sagning"
	               )
	            ),
	            array(
	               "status" => "NOT_DONE",
	               "station" => array(
	                  "id" => 3,
	                  "name" => "Rahuggning"
	               )
	            ),
	            array(
	               "status" => "NOT_DONE",
	               "station" => array(
	                  "id" => 4,
	                  "name" => "Gravering"
	               )
	            ),
	            array(
	               "status" => "NOT_DONE",
	               "station" => array(
	                  "id" => 5,
	                  "name" => "Malning"
	               )
	            )
	         )
	      ),
	      array(
	         "id" => 401,
	         "materialColor" => "Hallandia",
	         "description" => "Sockel under mark",
	         "frontWork" => "Polerad",
	         "tasks" => array(
	            array(
	               "status" => "DONE",
	               "station" => array(
	                  "id" => 1,
	                  "name" => "Sagning"
	               )
	            ),
	            array(
	               "status" => "NOT_DONE",
	               "station" => array(
	                  "id" => 2,
	                  "name" => "Slipning"
	               )
	            )
	         )
	      )
	   )
	);

	echo json_encode($a, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
}

?>
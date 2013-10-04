<?php

function postOrder() {
	// Convert incoming data to array
	if(!isset($_POST['data'])|| empty($_POST['data'])){
		die("No data provided");
	}
	$jsonData = json_decode($_POST['data'], true);
	if(is_null($jsonData)){
		die("Faulty json format");
	}
	
	// Process data and decide what should be accepted
	
	/*
	 * Search for the id in db, if it exists, update it.
	 * If it doesnt't exist, insert it.
	 */
	
	$jsonData['id'];
	$jsonData['timeCreated'];
	$jsonData['lastTimeUpdate'];
	$jsonData['cemetery'];
	$jsonData['orderDate'];
	$jsonData['orderNumber'];
	$jsonData['idName'];
	$jsonData['cemeteryBoard'];
	$jsonData['cemeteryBlock'];
	$jsonData['cemeteryNumber'];
	$jsonData['customer']['title'];
	$jsonData['customer']['name'];
	$jsonData['customer']['address'];
	$jsonData['customer']['postAddress'];
	$jsonData['customer']['eMail'];
	$jsonData['customer']['id'];
	
	for($i=0; $i<count($jsonData['products']); $i++){ // Go through all products
		$product = $jsonData['products'][$i];
		
		// Stone
		$product['stoneModel'];
		$product['sideBackWork'];
		$product['textStyle'];
		$product['ornament'];
		
		// Product
		$product['id'];
		$product['materialColor'];
		$product['description'];
		$product['frontWork'];
		
		for($j=0; $j<count($product['tasks']); $j++){ // Go through all products tasks
			$task = $product['tasks'][$j];
			
			$task['status'];
			$task['station']['id'];
			$task['station']['name'];
		}
	}
	
	// Update or insert data into the database
	
	$query = "INSERT INTO `order` (`order_id`, `order_number`) 
	VALUES (?, ?) ON DUPLICATE KEY UPDATE 
	`order_id`=values(`order_id`), `order_number`=values(`order_number`);";
}
?>
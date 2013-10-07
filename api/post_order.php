<?php

function updateOrder(){
	$su = true;
	
	if(!isset($_POST['data']) || empty($_POST['data'])){
		errorGeneric("No data provided");
	}
	$jsonData = json_decode($_POST['data'], true);
	if(is_null($jsonData)){
		errorGeneric("Invalid JSON data provided");
	}
	
	sqlUpdateOrder($jsonData, $su);
	sqlUpdateCustomer($jsonData['customer']);
	
	$countProducts = count($jsonData['products']);
	for($i=0; $i<$countProducts; $i++){ // Go through all products
		$product = $jsonData['products'][$i];
		sqlUpdateProduct($product);
		
		$countTasks = count($product['tasks']);
		for($j=0; $j<$countTasks; $j++){ // Go through all products tasks
			$task = $product['tasks'][$j];
			sqlUpdateTask($task, $product['id']);
		}
	}
	$GLOBALS['con']->commit();
	//TODO Response. Where are we notified if the sql returns error?
}

function sqlUpdateTask($task, $productId){
	$sql = "UPDATE `task` SET `status`=? WHERE `station_id`=? AND `product_id`=?"; //TODO No indexes?
	$stmt = $GLOBALS['con']->prepare($sql);
	@$stmt->bind_param("iii", $task['status'], $task['station']['id'], $task['station']['name']);
	$stmt->execute();
	
	$sql2 = "INSERT INTO `station` (`station_id`, `name`) VALUES (?, ?)";
	$stmt2 = $GLOBALS['con']->prepare($sql2);
	@$stmt2->bind_param("is", $task['station']['id'], $task['station']['name']);
	$stmt2->execute();
}

function sqlUpdateProduct($product){
	$sql = "UPDATE `product` SET `description`=?, `frontwork`=?, `material_color`=?, `product_type_id`=?
		WHERE `product_id`=?";
	$stmt = $GLOBALS['con']->prepare($sql);
	@$stmt->bind_param("sssii", $product['description'], $product['frontWork'],
		$product['materialColor'], $product['type']['id'], $product['id']);
	$stmt->execute();

	if($product['type']['name'] === "Sten" || $product['type']['name'] === "Gravsten"){
		$sql2 = "UPDATE `stone` SET `stone_model`=?, `side_back_work`=?, `textstyle`=?, `ornament`=?
			WHERE `stone_product_id`=?";
		$stmt2 = $GLOBALS['con']->prepare($sql2);
		@$stmt2->bind_param("ssssi", $product['stoneModel'], $product['sideBackWork'],
			$product['textStyle'], $product['ornament'], $product['id']);
	}
}

function sqlUpdateCustomer($array){
	// Update customer
	$sql = "UPDATE `customer` SET
		`title`=?,
		`name`=?,
		`address`=?,
		`postal_address`=?,
		`email`=?
		WHERE `customer_id`=?";
	$stmt = $GLOBALS['con']->prepare($sql);
	@$stmt->bind_param("sssssi", $jsonData['title'], $jsonData['name'],
		$jsonData['address'], $jsonData['postAddress'], $jsonData['eMail'],
		$jsonData['id']);
	$stmt->execute();
}

function sqlUpdateOrder($array, $su){
		// Update order
	$sql = "UPDATE `order` SET
		`cemetery_board`=?,
		`cemetery`=?,
		`cemetery_block`=?,
		`cemetery_number`=?,
		`time_last_update`=?";
	
	if($su){
		$sql .= ", `cancelled`=?, `archived`=?";
	}
	$sql .= " WHERE `order_id`=? AND `time_last_update` < ?";
	$stmt = $GLOBALS['con']->prepare($sql);
	if($su){
		@$stmt->bind_param("ssssiiiii", $array['cemeteryBoard'], $array['cemetery'],
			$array['cemeteryBlock'], $array['cemeteryNumber'], $array['lastTimeUpdate'],
			$array['cancelled'], $array['archived'], $array['id'], $array['lastTimeUpdate']);
	}else{
		@$stmt->bind_param("ssssiii", $array['cemeteryBoard'], $array['cemetery'],
			$array['cemeteryBlock'], $array['cemeteryNumber'], $array['lastTimeUpdate'],
			$array['id'], $array['lastTimeUpdate']);
	}
	$stmt->execute();
}


function insertOrder() {
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
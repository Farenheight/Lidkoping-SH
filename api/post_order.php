<?php
function insertOrder() {
	$jsonData = getValidInput();
	
	prepareSql();
	$orderNrGen = new OrderNumberGenerator();
	$orderId = sqlInsertOrder($jsonData, $orderNrGen);

	$orderNrGen->saveChanges();
	$GLOBALS['con'] -> commit();	//TODO Response. Where are we notified if the sql returns error?

	output(true);
}

function updateOrder() {
	$su = true;

	$jsonData = getValidInput();

	prepareSql();
	sqlUpdateOrder($jsonData, $su);

	$GLOBALS['con'] -> commit();	//TODO Response. Where are we notified if the sql returns error?
	output(true);
}

/**
 * Validates and returns the POST input if it is valid.
 * Otherwise, kills the process and returns the error.
 * @return The input data as an object, if it is valid.
 */
function getValidInput() {
	if (!isset($_POST['data']) || empty($_POST['data'])) {
		errorGeneric("No data provided. Check that header ContentType is 'application/x-www-form-urlencoded'.");
	}
	$array = json_decode($_POST['data'], true);
	if (is_null($array)) {
		errorGeneric("Invalid JSON data provided");
	}
	
	// Verify that JSON data contains an order.
	requiredField("cemetery", $array);
	requiredField("cemeteryBoard", $array);
	requiredField("orderDate", $array);
	requiredField("name", $array["customer"], "customer");
	requiredField("address", $array["customer"], "customer");
	requiredField("postAddress", $array["customer"], "customer");
	// Check products
	if (array_key_exists("products", $array)) {
		foreach ($array["products"] as $index => $product) {
			requiredField("type", $product, "product at index $index");
			requiredField("id", $product["type"], "product[type] at index $index");
			requiredField("frontWork", $product, "product at index $index");
			requiredField("materialColor", $product, "product at index $index");
			// Check stone details
			if (array_key_exists("stoneModel", $product)) {
				requiredField("stoneModel", $product, "product (stone) at index $index");
				requiredField("sideBackWork", $product, "product (stone) at index $index");
				requiredField("textStyle", $product, "product (stone) at index $index");
				requiredField("ornament", $product, "product (stone) at index $index");
			}
			// Check tasks and stations
			if (array_key_exists("tasks", $product)) {
				foreach ($product["tasks"] as $tIndex => $task) {
					requiredField("station", $task, "product at index $index, task at index $tIndex");
					requiredField("name", $task["station"], "product at index $index, 
						task at index $tIndex, station name");
				}
			}
		}
	}
	
	return $array;
}

/**
 * Validates a field that should be required. Generates
 * an error if the field is missing or empty.
 */
function requiredField($fieldName, $array, $moreInfo = "") {
	if (!array_key_exists($fieldName, $array)) {
		errorGeneric("Missing required field: $fieldName ($moreInfo)");
	}
	if (empty($array[$fieldName])) {
		errorGeneric("Empty required field: $fieldName ($moreInfo)");
	}
}


// SQL COMMON METHODS

function prepareSql() {
	// Store all stations
	$sql = "SELECT * FROM `station`";
	$stmt = $GLOBALS['con'] -> prepare($sql);
	$stmt -> execute();
	$res = $stmt -> get_result();
	while ($row = $res -> fetch_assoc()) {
		$GLOBALS['stations'][$row['name']] = $row['station_id'];
	}
}

// SQL UPDATE METHODS

function sqlUpdateCustomer($jsonData) {
	$sql = "UPDATE `customer` SET
		`title`=?,
		`name`=?,
		`address`=?,
		`postal_address`=?,
		`email`=?
		WHERE `customer_id`=?";
	$stmt = $GLOBALS['con'] -> prepare($sql);
	$stmt -> bind_param("sssssi", $jsonData['title'], $jsonData['name'], $jsonData['address'],
		$jsonData['postAddress'], $jsonData['eMail'], $jsonData['id']);
	$stmt -> execute();
}

function sqlUpdateImage($image) {
	$sql = "UPDATE `image` SET `file_name`=? WHERE `image_id`=?";
	$stmt = $GLOBALS['con'] -> prepare($sql);
	$stmt -> bind_param("si", $image['imagePath'], $image['id']);
	$stmt -> execute();
}

function sqlUpdateOrder($array, $su) {
	// Update the order
	$sql = "UPDATE `order` SET
		`cemetery_board`=?,
		`cemetery`=?,
		`cemetery_block`=?,
		`cemetery_number`=?,
		`time_last_update`=?";

	if ($su) {
		$sql .= ", `cancelled`=?, `archived`=?";
	}
	$sql .= " WHERE `order_id`=? AND `time_last_update` < ?";
	$stmt = $GLOBALS['con'] -> prepare($sql);
	$currentTime = getMilliseconds();
	if ($su) {
		$stmt -> bind_param("ssssiiiii", $array['cemeteryBoard'], $array['cemetery'], $array['cemeteryBlock'],
			$array['cemeteryNumber'], $currentTime, $array['cancelled'], $array['archived'],
			$array['id'], $array['lastTimeUpdate']);
	} else {
		$stmt -> bind_param("ssssiii", $array['cemeteryBoard'], $array['cemetery'], $array['cemeteryBlock'],
			$array['cemeteryNumber'], $currentTime, $array['id'], $array['lastTimeUpdate']);
	}
	$stmt -> execute();
	
	if ($stmt -> affected_rows) {
		// Update the customer
		sqlUpdateCustomer($array['customer']);
		
		// Update images for this order
		if (array_key_exists('images', $array)) {
			foreach ($array['images'] as $image) {
				sqlUpdateImage($image);
			}
		}
	
		// Update products
		if (array_key_exists('products', $array)) {
			$countProducts = count($array['products']);
			for ($i = 0; $i < $countProducts; $i++) {// Go through all products
				sqlUpdateProduct($array['products'][$i]);
			}
		}
	} else {
		die(output(false, "Order was not updated. Order has been changed after sent 'lastTimeUpdate'."));
	}
}

function sqlUpdateProduct($product) {
	$sql = "UPDATE `product` SET `description`=?, `frontwork`=?, `material_color`=?, `product_type_id`=?
		WHERE `product_id`=?";
	$stmt = $GLOBALS['con'] -> prepare($sql);
	$stmt -> bind_param("sssii", $product['description'], $product['frontWork'], $product['materialColor'],
		$product['type']['id'], $product['id']);
	$stmt -> execute();

	if (array_key_exists('stoneModel', $product)) {
		$sql2 = "UPDATE `stone` SET `stone_model`=?, `side_back_work`=?, `textstyle`=?, `ornament`=?
			WHERE `stone_product_id`=?";
		$stmt2 = $GLOBALS['con'] -> prepare($sql2);
		$stmt2 -> bind_param("ssssi", $product['stoneModel'], $product['sideBackWork'], $product['textStyle'],
			$product['ornament'], $product['id']);
		$stmt2 -> execute();
	}

	if (array_key_exists('tasks', $product)) {
		$countTasks = count($product['tasks']);
		for ($j = 0; $j < $countTasks; $j++) {// Go through all products tasks
			$task = $product['tasks'][$j];
			sqlProcessTask($task, $product['id'], $j);
		}
	}
}

// SQL INSERT METHODS

/**
 * Inserts customer and returns the id of the inserted row.
 * @return int
 */
function sqlInsertCustomer($customer) {
	$sql = "INSERT INTO `customer` (`address`, `name`, `email`, `title`,
		`postal_address`) VALUES (?, ?, ?, ?, ?);";

	$stmt = $GLOBALS['con'] -> prepare($sql);
	$stmt -> bind_param("sssss", $customer['address'], $customer['name'], $customer['eMail'], $customer['title'],
		$customer['postAddress']);
	$stmt -> execute();
	return $stmt -> insert_id;
}

function sqlInsertImage($image, $orderId) {
	$sql = "INSERT INTO `image` (`order_id`, `file_name`) VALUES (?, ?)";
	$stmt = $GLOBALS['con'] -> prepare($sql);
	$stmt -> bind_param("is", $orderId, $image['imagePath']);
	$stmt -> execute();
}

function sqlInsertOrder($order, $orderNrGen) {
	$customerId = sqlInsertCustomer($order['customer']);

	$orderNr = $orderNrGen -> newOrderNumber($order['orderDate']);
	$idName = "CustomID";
	// TODO: Generate id name
	$time = getMilliseconds();

	$sql = "INSERT INTO `order` (`order_number`, `id_name`, `order_date`, `cemetery_board`,
		`cemetery`, `cemetery_block`, `cemetery_number`, `customer_id`, `time_created`,
		`time_last_update`, `cancelled`, `archived`)
		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	$zero = 0;

	$stmt = $GLOBALS['con'] -> prepare($sql);
	$stmt -> bind_param("ssissssiiiii", $orderNr, $idName, $order['orderDate'], $order['cemeteryBoard'],
		$order['cemetery'], $order['cemeteryBlock'], $order['cemeteryNumber'], $customerId, $time, $time,
		$zero, $zero);

	$stmt -> execute();
	$orderId = $stmt -> insert_id;
	
	// Add images for this order
	if (array_key_exists('images', $order)) {
		foreach ($order['images'] as $image) {
			sqlInsertImage($image, $orderId);
		}
	}

	// Add all products for this order
	if (array_key_exists('products', $order)) {
		$countProducts = count($order['products']);
		for ($i = 0; $i < $countProducts; $i++) {// Go through all products
			sqlInsertProduct($order['products'][$i], $orderId);
		}
	}
}

function sqlInsertProduct($product, $orderId) {
	// Add product type if not exist
	if (!$product['type']['id']) {
		$sql = "INSERT INTO `product_type` (`name`) VALUES (?)";
		$stmt = $GLOBALS['con'] -> prepare($sql);
		$stmt -> bind_param("s", $product['type']['name']);
		$stmt -> execute();
		$product['type']['id'] = $stmt -> insert_id;
	}

	// Add product
	$sql = "INSERT INTO `product` (`order_id`, `description`, `frontwork`, `material_color`,
		`product_type_id`) VALUES (?, ?, ?, ?, ?)";
	$stmt = $GLOBALS['con'] -> prepare($sql);
	$stmt -> bind_param("isssi", $orderId, $product['description'], $product['frontWork'], 
		$product['materialColor'], $product['type']['id']);
	$stmt -> execute();
	
	$productId = $stmt -> insert_id;
	
	// Add stone details
	if (array_key_exists('stoneModel', $product)) {
		$sqlStone = "INSERT INTO `stone` (`stone_product_id`, `stone_model`, `side_back_work`, `textstyle`,
			`ornament`) VALUES (?, ?, ?, ?, ?)";
		$stmtStone = $GLOBALS['con'] -> prepare($sqlStone);
		$stmtStone -> bind_param("issss", $productId, $product['stoneModel'], $product['sideBackWork'], 
			$product['textStyle'], $product['ornament']);
		$stmtStone -> execute();
	}

	// Add tasks for this product
	if (array_key_exists('tasks', $product)) {
		$countTasks = count($product['tasks']);
		for ($j = 0; $j < $countTasks; $j++) {// Go through all products tasks
			sqlProcessTask($product['tasks'][$j], $productId, $j);
		}
	}
}

/**
 * @return the id of the inserted station.
 */
function sqlInsertStation($station) {
	$sql = "INSERT INTO `station` (`name`) VALUES (?)";
	$stmt = $GLOBALS['con'] -> prepare($sql);
	$stmt -> bind_param("s", $station['name']);
	$stmt -> execute();
	return $stmt -> insert_id;
}

/**
 * @return the id of the station
 */
function getStationId($station) {
	if (array_key_exists('id', $station)) {
		return $station['id'];
	} else if (array_key_exists($station['name'], $GLOBALS['stations'])) {
		return $GLOBALS['stations'][$station['name']];
	} else {
		// Insert station into database. It does not exist
		return sqlInsertStation($station);
	}
}

/**
 * Updates a task or insert if not exists.
 */
function sqlProcessTask($task, $productId, $sortOrder) {
	$stationId = getStationId($task['station']);
	
	if (!array_key_exists("status", $task)) {
		$task['status'] = 0;
	}

	$sql = "INSERT INTO `task` (`station_id`, `product_id`, `status`, `sort_order`) VALUES (?, ?, ?, ?)
		ON DUPLICATE KEY UPDATE `status` = VALUES(`status`), `sort_order` = VALUES(`sort_order`)";
	$stmt = $GLOBALS['con'] -> prepare($sql);
	$stmt -> bind_param("iiii", $stationId, $productId, $task['status'], $sortOrder);
	$stmt -> execute();
}

function getMilliseconds() {
	return time() * 1000;
}
?>
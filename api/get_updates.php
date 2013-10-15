<?php

function getUpdates(){
	$data = "";
	if(isset($_POST['data'])){
		$data = $_POST['data'];
	}
	
	$select = "SELECT *, o.order_id AS order_table_id, c.name AS customer_name,
		p.product_id AS p_product_id, pt.name AS type_name
		FROM `order` o
		JOIN `customer` c ON c.customer_id = o.customer_id
		LEFT JOIN `image` i ON i.order_id = o.order_id
		LEFT JOIN `product` p ON p.order_id = o.order_id
		LEFT JOIN `product_type` pt ON pt.product_type_id = p.product_type_id
		LEFT JOIN `stone` s ON s.stone_product_id = p.product_id
		LEFT JOIN `task` t ON t.product_id = p.product_id
		LEFT JOIN `station` st ON st.station_id = t.station_id
		HAVING `archived`='0'";
	
	if(!empty($_POST['data'])){
		$data_array = json_decode($data, true);
		
		checkInputFormat($data_array);
		
		$size = sizeof($data_array);
		if($size > 0){
			$select .= " AND NOT(";
			for($i = 0; $i < $size; $i++){
				if($i > 0){
					$select .= " OR ";
				}
				$select .= "((`time_last_update`=". $GLOBALS['con']->escape($data_array[$i][1]) .") 
					AND (`order_table_id`=". $GLOBALS['con']->escape($data_array[$i][0]) ."))";
			}
			$select .= ")";
		}
		
	}
	
	$select .= " ORDER BY order_table_id, p.product_id, t.sort_order";
	
	$stmt = $GLOBALS['con']->prepare($select);
	$stmt->execute();
	
	doDie(output(true, produceOrders($stmt->get_result())));
}

function checkInputFormat($jsonarray) {
	if (is_null($jsonarray)) {
			errorGeneric("JSON data was not valid, when trying to decode request data", 20);
	}
	
	$size = sizeof($jsonarray);
	for ($i = 0; $i < $size; $i++) {
		if (sizeof($jsonarray[$i]) != 2) {
			errorGeneric("Array element at index $i does not contain two values.", 21);
		}
		if (empty($jsonarray[$i][0]) || empty($jsonarray[$i][1])) {
			errorGeneric("Missing or invalid argument(s) for order at index $i.", 22);
		}
		if (!is_numeric($jsonarray[$i][0]) || !is_numeric($jsonarray[$i][1])) {
			errorGeneric("Not numeric argument(s) for order at index $i.", 23);
		}
	}
}

function produceOrders($res){
	$allOrders = array();
	while($row = $res->fetch_assoc()){
		// Add order if not already added, otherwise get latest order as current
		$currentOrder = !empty($allOrders) ? $allOrders[count($allOrders)-1] : null;
		if(is_null($currentOrder) || $currentOrder['id'] != $row['order_id']){
			$currentOrder = array(
			   "id" => $row['order_table_id'],
			   "timeCreated" => $row['time_created'],
			   "lastTimeUpdate" => $row['time_last_update'],
			   "cemetery" => $row['cemetery'],
			   "orderDate" => $row['order_date'],
			   "orderNumber" => $row['order_number'],
			   "idName" => $row['id_name'],
			   "cemeteryBoard" => $row['cemetery_board'],
			   "cemeteryBlock" => $row['cemetery_block'],
			   "cemeteryNumber" => $row['cemetery_number'],
			   "customer" => array(
			      "title" => $row['title'],
			      "name" => $row['customer_name'],
			      "address" => $row['address'],
			      "postAddress" => $row['postal_address'],
			      "eMail" => $row['email'],
			      "id" => $row['customer_id']
			   )
			);
			array_push($allOrders, $currentOrder);
		}
		
		// Add image if not added
		@$images = $currentOrder['images'] ?: array();
		if(!is_null($row['image_id']) && (empty($images) || 
				$images[count($images)-1]['id'] != $row['image_id'])){
			// Check if image is added
			$image = array(
				"id" => $row['image_id'],
				"imagePath" => $row['file_name']
			);
			if(!in_array($image, $images)){
				array_push($images, $image);
				$currentOrder['images'] = $images;
			}
		}
		
		// Add product if not added
		@$products = $currentOrder['products'] ?: array();
		if(!is_null($row['p_product_id']) && (empty($products) ||
				$products[count($products)-1]['id'] != $row['p_product_id'])){
			$product = array(
				"id" => $row['p_product_id'],
		        "materialColor" => $row['material_color'],
		        "description" => $row['description'],
		        "frontWork" => $row['frontwork'],
		        "type" => array(
		        	"id" => $row['product_type_id'],
		        	"name" => $row['type_name']
				)
			);
			// Add stone details if exists
			if (!is_null($row['stone_product_id'])) {
				$product = array_merge($product, array(
					"stoneModel" => $row['stone_model'],
			        "sideBackWork" => $row['side_back_work'],
			        "textStyle" => $row['textstyle'],
			        "ornament" => $row['ornament']
				));
			}
			array_push($products, $product);
			$currentOrder['products'] = $products;
		}
		
		// Add task to current product
		@$products = $currentOrder['products'];
		if(!empty($products)){
			$currentProduct = $products[count($products)-1];
			@$tasks = $currentProduct['tasks'] ?: array();
			if(!is_null($row['status'])){
				$task = array(
					"status" => $row['status'],
					"station" => array(
						"id" => $row['station_id'],
						"name" => $row['name']
					)
				);
				if (!in_array($task, $tasks)) {
					array_push($tasks, $task);
					$currentProduct['tasks'] = $tasks;
					$currentOrder['products'][count($products)-1] = $currentProduct;
				}
			}
		}
		
		// Update order data in array
		$allOrders[count($allOrders)-1] = $currentOrder;
	}
	
	return $allOrders;
}

?>
<?php

function getUpdates(){
	$data = $_POST['getUpdates'];
	
	$select = "SELECT * FROM `order` o
		JOIN `customer` c ON c.customer_id = o.customer_id
		LEFT JOIN `image` i ON i.order_id = o.order_id
		LEFT JOIN `product` p ON p.order_id = o.order_id
		LEFT JOIN `product_type` pt ON pt.product_type_id = p.product_type_id
		LEFT JOIN `stone` s ON s.stone_product_id = p.product_id
		LEFT JOIN `task` t ON t.product_id = p.product_id
		LEFT JOIN  `station` st ON st.station_id = t.station_id
		WHERE `archived`='0'";
	
	if(isset($_POST['data'])){
		$data_array = json_decode($data, true);
		if($data_array === null){
			errorGeneric("JSON data was not valid, when trying to decode request data");
		}else if(count($data_array) > 0){
			$select .= " AND IS NOT(";
			for($i = 0; $i < count($data_array); $i++){
				if($i > 0){
					$select .= " OR ";
				}
				$select .= "((`time_last_update`=". $GLOBALS['con']->escape($data_array[0][1]) .") 
				AND (`o.order_id`=". $GLOBALS['con']->escape($data_array[0][0]) ."))";
			}
			$select .= ")";
		}
		
	}
	
	$select .= " ORDER BY o.order_id, p.product_id, t.sort_order";
	
	$stmt = $GLOBALS['con']->prepare($select);
	$stmt->execute();
	echo json_encode(produceOrders($stmt->get_result()));
}


function produceOrders($res){
	$allOrders = array();
	while($row = $res->fetch_assoc()){
		if (!empty($allOrders)){
			$currentOrder = $allOrders[count($allOrders)-1];
		}
		// Add order if not already added
		if(empty($allOrders) || $currentOrder['id'] != $row['order_id']){
			$currentOrder = array(
			   "id" => $row['order_id'],
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
			      "name" => $row['name'],
			      "address" => $row['address'],
			      "postAddress" => $row['postal_address'],
			      "eMail" => $row['email'],
			      "id" => $row['customer_id']
			   )
			);
			array_push($allOrders, $currentOrder);
		}
		
		// Add image if not added
		if(!array_key_exists("images", $currentOrder)){
			$currentOrder['images'] = array();
		}
		$images = $currentOrder['images'];
		
		if(empty($images) || $images[count($images)-1]['id'] != $row['image_id']){
			array_push($currentOrder, array(
				"id" => $row['image_id'],
				"imagePath" => $row['file_name']
			));
		}
		
		// Add product if not added
		if(!array_key_exists("products", $currentOrder)){
			$currentOrder['products'] = array();
		}
		$products = $currentOrder['products'];
		if(empty($products) || $products[count($products)-1]['id'] != $row['product_id']){
			$product = array(
				"id" => $row['product_id'],
		        "materialColor" => $row['material_color'],
		        "description" => $row['description'],
		        "frontWork" => $row['frontwork'],
		        "type" => array(
		        	"id" => $row['product_type_id'],
		        	"name" => $row['name']
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
			array_push($currentOrder['products'], $product);
		}
		
		// Add task to current product
		$products = $currentOrder['products'];
		if(!empty($products)){
			$currentProduct = $products[count($products)-1];
			if(!array_key_exists("tasks", $currentProduct)){
				$currentProduct['tasks'] = array();
			}
			if(!is_null($row['status'])){
				array_push($currentProduct['tasks'], array(
					"status" => $row['status'],
					"station" => array(
						"id" => $row['station_id'],
						"name" => $row['name']
					)
				));
			}
			
		}
		
		
	}
	
	return $allOrders;
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
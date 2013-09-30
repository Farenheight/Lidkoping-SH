<?php
header('Content-type: text/json');

// Authorization
if(!isset($_GET['apiKey'])){
	$a = array(
		'error' => array(
			'code' => 401,
			'subcode' => 1,
			'description' => "Access Denied, no API key specified"
		)
	);
	die(json_encode($a));
}else if($_GET['apiKey'] != "123456789qwertyuiop"){
	$a = array(
		'error' => array(
			'code' => 401,
			'subcode' => 2,
			'description' => "Access Denied, API key is not valid"
		)
	);
	die(json_encode($a));
}

// Use or give specified information
if(isset($_GET['activeItems'])){
	echoActiveItems();
}else if(isset($_GET['allItems'])){

}else if(isset($_POST['itemUpdate'])){

}

function echoActiveItems(){
	$arrayName = array(
		'currentTimestamp' => time()*1000,
		'order' => array(
			'created' => mktime(0, 0, 0, 7, 1, 2013)*1000,
			'lastChanged' => mktime(0, 0, 0, 7, 1, 2013)*1000,
			'cemetary' => "Tun kkg",
			"orderDate" => mktime(0, 0, 0, 6, 26, 2013)*1000,
			"orderNumber" => "090572",
			"customer" => array(
				"title" => "",
				"name" => "Arne Viebke",
				"address" => "Fröjdegårdsvägen 19",
				"postAddress" => "531 56 Lidköping"
			),
			"cemetaryBoard" => "Örlösa",
			"assembly" => "GRO",
			"assemblyDescription" => "",
			"comment" => "",
			"status" => "Påbörjad"
		),
		"stone" => array(
			"created" => mktime(0, 0, 0, 7, 1, 2013)*1000,
			"lastChanged" => mktime(0, 0, 0, 7, 1, 2013)*1000,
			"model" => "NB 46",
			"materialAndColor" => "Hallandia-granit",
			"frontWork" => "Framsida & sockelns ovansida polerad ,matt svart",
			"sideBackWork" => "Polerade",
			"textStyleProcessing" => "Sx358-nedhuggen i guld",
			"ornamentProcessing" => "Kors & sol i guld\nFåglar & blommor vita",
			"description" => "Gravvård 80x65 cm\nPolerade blomlister 90x40 cm",
			"steps" => array(
				array(
					"name" => "Huggning",
					"status" => true
				),
				array(
					"name" => "Polering",
					"status" => false
				)
			)
		)
	);
	echo json_encode($arrayName, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
}

?>
<?php
define("LOGIN", 1);
define("APIKEY", 2);

function checkAuthenticated(){
	if(isset($_SERVER['HTTP_LIDKOPINGSH_APIKEY'])){
		// Trying to make an api request

	}
}

function validateApikey($apikey, $deviceID){
	$pepper = "unique";
	$hash = md5($pepper . $apikey);
	
	$sql = "SELECT `apikey_id` FROM `apikey` WHERE `hashed_apikey`=? AND `device_id`=?";
	$stmt = $GLOBALS['con']->prepare($sql);
	$stmt->bind_param("ss", $hash, $deviceID);
	$stmt->execute();
	$res = $stmt->get_result();
	$valid = $res->num_rows === 1;
	
	if($valid){
		$row = $res->fetch_assoc();
	}
	logAccess($valid, APIKEY, (isset($row['apikey_id']) ? $row['apikey_id'] : "0"));
	return $valid;
}

function logAccess($success, $authorizationType, $user){	
	$sql = "INSERT INTO `logging` (`ip`, `timestamp`, `success`, `authorization_type`, `authorization_id`)
		VALUES(?, ?, ?, ?, ?)";
	$stmt = $GLOBALS['con']->prepare($sql);
	$stmt->bind_param("siiii", $ip, $timestamp, $success, $authorizationType, $id);
	$ip = $_SERVER['REMOTE_ADDR'];
	$timestamp = time()*1000;	
	
	$stmt->execute();
}
?>

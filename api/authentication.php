<?php
define("LOGIN", 1);
define("APIKEY", 2);

function checkAuthenticated(){
	if(isset($_SERVER['HTTP_LIDKOPINGSH_APIKEY']) && isset($_SERVER['HTTP_LIDKOPINGSH_DEVICEID'])){
		if(!validateApikey($_SERVER['HTTP_LIDKOPINGSH_APIKEY'], $_SERVER['HTTP_LIDKOPINGSH_DEVICEID'])){
			errorGeneric("Apikey is invalid or outdated, try again. Please honor the exponential back off.", 41);
		}
	}else{
		errorGeneric("No apikey specified, try again.", 42);
	}
}

function validateApikey($apikey, $deviceID){
	$notExpired = false;
	$returning = false;
	
	$hash = md5($GLOBALS['apikeyPepper'] . $apikey);
	
	$sql = "SELECT `apikey_id`, `expires` FROM `apikey` WHERE `hashed_apikey`=? AND `device_id`=?";
	$stmt = $GLOBALS['con']->prepare($sql);
	$stmt->bind_param("ss", $hash, $deviceID);
	$stmt->execute();
	$res = $stmt->get_result();
	$valid = $res->num_rows === 1;
	
	if($valid){
		$row = $res->fetch_assoc();
		$notExpired = $row['expires'] < time();
	}
	$returning = $valid && $notExpired;
	logAccess($returning, APIKEY, (isset($row['apikey_id']) ? $row['apikey_id'] : "0"));
	return $returning;
}

function logAccess($success, $authorizationType, $user){		
	$sql = "INSERT INTO `logging` (`ip`, `timestamp`, `success`, `authorization_type`, `authorization_id`)
		VALUES(?, ?, ?, ?, ?)";
	$stmt = $GLOBALS['con']->prepare($sql);
	$stmt->bind_param("siiii", $ip, $timestamp, $success, $authorizationType, $user);
	$ip = $_SERVER['REMOTE_ADDR'];
	$timestamp = time()*1000;
	$stmt->execute();
}
?>

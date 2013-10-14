<?php

require_once 'output.php';
require_once 'authentication.php';
require_once 'db_config.php';
require_once 'class/mySQLConnection.php';
$con = new mySQLConnection();

function getApikey(){
	if(isset($_SERVER['HTTP_LIDKOPINGSH_USERNAME']) && isset($_SERVER['HTTP_LIDKOPINGSH_USERNAME'])){
		
		
	}
}

function validateLogin($user, $pw){
	$returning = false;
	
	$sql = "SELECT `hash`, `salt`, `user_id` FROM `user` WHERE `username`=?";
	$stmt = $GLOBALS['con']->prepare($sql);
	$stmt->bind_param("s", $user);
	$stmt->execute();
	
	if($stmt->num_rows === 1){
		$res = $stmt->get_result();
		$row = $res->fetch_assoc();
		$dbHash = $row['hash'];
		$dbSalt = $row['salt'];
		$dbUserID = $row['user_id'];
		
		$saltWithPrefix = sprintf("$2a$%02d$", $GLOBALS['pwHashingConstant']) . $dbSalt;
		$hashWithPrefix = crypt($GLOBALS['userPepper'] . $pw, $saltWithPrefix);
		$hash = substr($hashWithPrefix, 7);
		$returning = ($hash === $dbHash);
	}
	logAccess($returning, LOGIN, (isset($dbUserID) ? $dbUserID : "0"));
	return returning;
}

function genrerateApikey($userID, $deviceID, $expires = null){	
	$apikey = md5(uniqid("lidkopinsh"));
	$hash = md5($GLOBALS['apikeyPepper'] . $apikey);
	
	$sql = "INSERT INTO `apikey` (`hashed_apikey`, `user_id`, `device_id`, `expires`)
		VALUES(?, ?, ?, ?)";
	$stmt = $GLOBALS['con']->prepare($sql);
	$stmt->bind_param("sisi", $hash, $userID, $deviceID, $expires);
	$stmt->execute();
	return $apikey;
}

?>
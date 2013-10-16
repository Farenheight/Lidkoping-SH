<?php

function getApikey(){
	if(isset($_SERVER['HTTP_LIDKOPINGSH_USERNAME']) && isset($_SERVER['HTTP_LIDKOPINGSH_PASSWORD'])){
		if(validateLogin($_SERVER['HTTP_LIDKOPINGSH_USERNAME'], $_SERVER['HTTP_LIDKOPINGSH_PASSWORD'])){
			$sql = "SELECT `user_id` FROM `user` WHERE `username`=?";
			$stmt = $GLOBALS['con']->prepare($sql);
			$stmt->bind_param("s", $_SERVER['HTTP_LIDKOPINGSH_USERNAME']);
			$stmt->execute();
			$res = $stmt->get_result();
			$row = $res->fetch_assoc();
			
			$userID = $row['user_id'];
			$deviceID = "asdf"; //TODO Fix usage of deviceid
			if(isset($_POST['expires'])){ //TODO Check validity of chosen expiration time.
				$apikey = genrerateApikey($userID, $deviceID, $_POST['expires']);
			}else{
				$apikey = genrerateApikey($userID, $deviceID);
			}
			doDie(output(true, null, $apikey));
		}else{
			errorGeneric("Your credentials are not valid, please try again. Please honor the exponential back off.", 43);
		}
	}else{
		errorGeneric("You havn't specified any credentials, please try again.", 44);
	}
}

function validateLogin($user, $pw){
	$returning = false;
	$dbUserID = "";
	
	$sql = "SELECT `hash`, `salt`, `user_id` FROM `user` WHERE `username`=?";
	$stmt = $GLOBALS['con']->prepare($sql);
	$stmt->bind_param("s", $user);
	$stmt->execute();
	$res = $stmt->get_result();
	if($res->num_rows === 1){
		$row = $res->fetch_assoc();
		$dbHash = $row['hash'];
		$dbSalt = $row['salt'];
		$dbUserID = $row['user_id'];
		
		$saltWithPrefix = sprintf("$2a$%02d$", $GLOBALS['pwHashingConstant']) . $dbSalt;
		$hashWithPrefix = crypt($GLOBALS['userPepper'] . $pw, $saltWithPrefix);
		$hash = substr($hashWithPrefix, 7);
		$returning = ($hash === $dbHash);
	}
	logAccess($returning, LOGIN, ($dbUserID ?: "0"));
	return $returning;
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
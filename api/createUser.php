<?php
include_once 'output.php';
include_once 'class/mySQLConnection.php';
include_once 'db_config.php';
$con = new mySQLConnection();

if(false && isset($_GET['user']) && isset($_GET['pw'])){
	createLogin($_GET['user'], $_GET['pw']);
	$GLOBALS['con']->commit();
	echo "Success";
}else{
	echo "Failure";
}

function createLogin($user, $pw, $accessLevel = "a"){	
	// Create a random salt
	$salt = strtr(base64_encode(mcrypt_create_iv(16, MCRYPT_DEV_URANDOM)), '+', '.');
	
	// "$2a$" Means we're using the Blowfish algorithm. The following two digits are the cost parameter.
	$saltWithPrefix = sprintf("$2a$%02d$", $GLOBALS['pwHashingConstant']) . $salt;
	
	// Hash the password with salt and pepper
	$hashWithPrefix = crypt($GLOBALS['userPepper'] . $pw, $saltWithPrefix);
	$hash = substr($hashWithPrefix, 7);
	
	/*echo "<table><tr>";
	echo "<td>Salt</td><td>" . $salt . "</td></tr>";
	echo "<td>Salt (med prefix)</td><td>" . $saltWithPrefix . "</td></tr>";
	echo "<td>Hash</td><td>" . $hash . "</td></tr>";
	echo "<td>Hash (med prefix)</td><td>" . $hashWithPrefix . "</td></tr>";*/
	
	$sql = "INSERT INTO `user` (`hash`, `salt`, `username`, `accesslevel`) VALUES(?, ?, ?, ?)";
	$stmt = $GLOBALS['con']->prepare($sql);
	$stmt->bind_param("ssss", $hash, $salt, $user, $accessLevel);
	$stmt->execute();
}

?>
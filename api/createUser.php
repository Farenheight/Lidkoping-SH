<?php
include_once 'output.php';
include_once 'class/mySQLConnection.php';
include_once 'authentication.php';
include_once 'db_config.php';
$con = new mySQLConnection();

if(isset($_GET['user']) && isset($_GET['pw'])){
	generatePassword($_GET['user'], $_GET['pw']);
}

?>
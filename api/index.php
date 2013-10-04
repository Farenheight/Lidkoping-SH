<?php
ini_set("display_errors", 1);

header('Content-type: text/json; charset=utf-8');

require_once 'output.php';
require_once 'authentication.php';
require_once 'db_config.php';
require_once 'class/mySQLConnection.php';

checkAuthenticated();
$con = new mySQLConnection();

// Set encoding format for database, otherwise values are not retrieved correctly
$stmt = $GLOBALS['con']->prepare("SET NAMES 'utf8'");
$stmt->execute();
$GLOBALS['con']->commit();

if (isset($_POST['getUpdates'])) {
	require_once 'get_updates.php';
	getUpdates();
} else if (isset($_POST['postOrder'])) {
	require_once 'post_order.php';
	postOrder();
} else {
	echo 'Empty respons.';
}
?>
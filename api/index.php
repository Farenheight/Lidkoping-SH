<?php
ini_set("display_errors", 1);
$start = microtime(true);

header('Content-type: text/json; charset=utf-8');

require_once 'output.php';
require_once 'authentication.php';
require_once 'db_config.php';
require_once 'class/mySQLConnection.php';

checkAuthenticated();
$con = new mySQLConnection();

if (isset($_GET['action'])) {
	if ($_GET['action'] === "getUpdates") {
		require_once 'get_updates.php';
		getUpdates();
	} else if ($_GET['action'] === "postOrder") {
		require_once 'post_order.php';
		postOrder();
	} else {
		errorGeneric("No valid action: Provided action (". $_GET['action'] .") does not exist");
	}
} else if (isset($_POST['postOrder'])) {
	require_once 'post_order.php';
	postOrder();
} else if (isset($_POST['getUpdates'])) {
	require_once 'get_updates.php';
	getUpdates();
} else {
	errorGeneric("Empty respons: No methods where triggered, try again");
}
//echo round((microtime(true)-$start)*1000, 2) . "ms";
?>
<?php
ini_set("display_errors", 1);
$start = microtime(true);

header('Content-type: text/json; charset=utf-8');

require_once 'output.php';
require_once 'authentication.php';
require_once 'db_config.php';
require_once 'class/mySQLConnection.php';
require_once 'util.php';
$con = new mySQLConnection();
checkAuthenticated();

if (isset($_GET['action'])) {
	if ($_GET['action'] === "getUpdates") {
		require_once 'get_updates.php';
		getUpdates();
	} else if ($_GET['action'] === "postOrder") {
		require_once 'post_order.php';
		updateOrder();
	} else if ($_GET['action'] === "insertOrder") {
		require_once 'post_order.php';
		insertOrder();
	} else {
		errorGeneric("No valid action: Provided action (". $_GET['action'] .") does not exist");
	}
} else {
	errorGeneric("Empty respons: Specify your action in the URL.");
}
//echo round((microtime(true)-$start)*1000, 2) . "ms";
?>
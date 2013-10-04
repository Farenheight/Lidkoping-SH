<?php
ini_set("display_errors", 1);
$start = microtime(true);

header('Content-type: text/json');

require_once 'authentication.php';
require_once 'db_config.php';
require_once 'exceptions.php';
require_once 'class/mySQLConnection.php';

checkAuthenticated();
$con = new mySQLConnection();

if (isset($_POST['getUpdates'])) {
	require_once 'get_updates.php';
	getUpdates();
} else if (isset($_POST['postOrder'])) {
	require_once 'post_order.php';
	postOrder();
} else {
	echo 'Empty respons.';
}
//echo round((microtime(true)-$start)*1000, 2) . "ms";
?>
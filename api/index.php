<?php
ini_set("display_errors", 1);

header('Content-type: text/json');

require_once 'authentication.php';
require_once 'db_config.php';
require_once 'exceptions.php';

checkAuthenticated();

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
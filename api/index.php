<?php
header('Content-type: text/json');

require_once 'authentication.php';
require_once 'db_config.php';
require_once 'exceptions.php';

checkAuthenticated();

if (isset($_POST['getUpdates'])) {
	require 'get_updates.php';
	getUpdates();
} else if (isset($_POST['postOrder'])) {
	require 'post_order.php';
	postOrder();
} else {
	echo 'Empty respons.';
}
?>
<?php
ini_set("display_errors", 1);
require_once (dirname(__FILE__) . '/../lib/simpletest_1.1.0/autorun.php');
require_once '../output.php';
require_once ('../util.php');
require_once '../db_config.php';
require_once '../class/mySQLConnection.php';
require_once '../get_updates.php';
$con = new mySQLConnection();

class getUpdatesTest extends UnitTestCase {
	function testGetOrders() {
		$url = "http://lidkopingsh/api/";
		
		// Create a stream
		$opts = array(
			'http'=>array(
				'method'=>"POST",
				'header'=>"Content-type: application/x-www-form-urlencoded\r\n" .
						"Lidkopingsh-Authentication: 123456789qwertyuiop\r\n",
				'content' => 'getUpdates=1&data='
			),
		);
		$context = stream_context_create($opts);
		
		// Open the file using the HTTP headers set above
		$file = file_get_contents($url, false, $context);
		
		$jsonData = json_decode($file, true);
		
		$sql = "SELECT `order_id` FROM `order` WHERE `cancelled`=0 AND `archived`=0";
		$stmt = $GLOBALS['con']->prepare($sql);
		$stmt->execute();
		$res = $stmt->get_result();
		
		$this->assertTrue(count($jsonData['results'] === $res->num_rows));
	}
}
?>
<?php

require_once (dirname(__FILE__) . '/../lib/simpletest_1.1.0/autorun.php');
require_once ('../util.php');
require_once '../db_config.php';
require_once '../class/mySQLConnection.php';

$con = new mySQLConnection();
class UtilTest extends UnitTestCase {
	function testOrderNumberGenerator() {
		$orderNum = new OrderNumberGenerator();
		$a = $orderNum->newOrderNumber(time()*1000);
		$b = $orderNum->newOrderNumber(time()*1000);
		$c = $orderNum->newOrderNumber(time()*1000 + (1000*60*60*24*365));
		$d = $orderNum->newOrderNumber(time()*1000 + (1000*60*60*24*365));
		$orderNum->saveChanges();

		$this->assertTrue(is_string($a));
		$this->assertTrue(is_numeric($a));
		$this->assertTrue((intval($a)+1) === intval($b));
		$this->assertTrue((intval($c)+1) === intval($d));
		
		$orderNum2 = new OrderNumberGenerator();
		$e = $orderNum2->newOrderNumber(time()*1000);
		$f = $orderNum2->newOrderNumber(time()*1000);
		
		$this->assertTrue((intval($e)+1) === intval($f));
	}
}
?>

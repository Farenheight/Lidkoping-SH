<?php

require_once (dirname(__FILE__) . '/../lib/simpletest_1.1.0/autorun.php');
require_once ('../util.php');

class UtilTest extends UnitTestCase {
	function testGenNewOrderNumber() {
		$orderNum = new OrderNumber();
		$a = $orderNum->genNewOrderNumber(time()*1000);
		$b = $orderNum->genNewOrderNumber(time()*1000);
		$c = $orderNum->genNewOrderNumber(time()*1000 + (1000*60*60*24*365));

		$this->assertTrue($a === "130000");
		$this->assertTrue($c === "140000");
		$this->assertTrue(is_string($a));
		$this->assertTrue(is_numeric($a));
		$this->assertTrue((intval($a)+1) === intval($b));
	}
}
?>
<?php

require_once (dirname(__FILE__) . '/../lib/simpletest_1.1.0/autorun.php');
require_once ('../util.php');

class UtilTest extends UnitTestCase {
	function testGenNewOrderNumber() {
		$string = genNewOrderNumber(time()*1000);
		$this->assertTrue(is_string($string));
	}

}
?>
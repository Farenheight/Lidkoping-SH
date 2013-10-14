<?php

require_once (dirname(__FILE__) . '/../lib/simpletest_1.1.0/autorun.php');
require_once ('../util.php');
require_once '../output.php';
require_once '../db_config.php';
require_once '../class/mySQLConnection.php';

$con = new mySQLConnection();
class UtilTest extends UnitTestCase {
	function testNewOrderNumber() {
		$util = new Util();
		$a = $util->newOrderNumber(time()*1000);
		$b = $util->newOrderNumber(time()*1000);
		$c = $util->newOrderNumber(time()*1000 + (1000*60*60*24*365));
		$d = $util->newOrderNumber(time()*1000 + (1000*60*60*24*365));
		$util->saveChanges();

		$this->assertTrue(is_string($a));
		$this->assertTrue(is_numeric($a));
		$this->assertTrue((intval($a)+1) === intval($b));
		$this->assertTrue((intval($c)+1) === intval($d));
		
		$util2 = new Util();
		$e = $util2->newOrderNumber(time()*1000);
		$f = $util2->newOrderNumber(time()*1000);
		
		$this->assertTrue((intval($e)+1) === intval($f));
	}
	
	function testGetIdName(){
		$util = new Util();
		$a = array(
			"cemetery" => "Örlösa",
			"deceased" => "Bo"
		);
		$aName = $util->getIdName($a);
		//echo $aName . "<br>";
		$this->assertTrue($aName === "B.Ö.");
		
		$b = array(
			"cemetery" => "Örlösa",
			"deceased" => "Bo"
		);
		$bName = $util->getIdName($b);
		//echo $bName . "<br>";
		$this->assertTrue($bName === "O.Ö.");
		
		$c = array(
			"cemetery" => "Örlösa",
			"deceased" => "Bo"
		);
		$cName = $util->getIdName($c);
		//echo $cName . "<br>";
		$this->assertTrue($cName === "X.Ö.");
	}
}
?>
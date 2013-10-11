<?php
ini_set("display_errors", 1);

require_once (dirname(__FILE__) . '/../lib/simpletest_1.1.0/autorun.php');
require_once '../class/mySQLConnection.php';
require_once ('../output.php');
require_once ('../util.php');
require_once ('../post_order.php');
require_once '../db_config.php';

$con = new mySQLConnection();

class PostOrderTest extends UnitTestCase {
	
	function testInsertOrder() {
		$json = '{"id":1,"timeCreated":1380616350928,"lastTimeUpdate":1380616350928,"cemetery":"Kyrka","orderDate":1380616350928,"orderNumber":"130001","idName":"O.S.","cemeteryBoard":"Board","cemeteryBlock":"Block","cemeteryNumber":"123","customer":{"title":"Dr","name":"Namn Efternamn","address":"Adress","postAddress":"12345 Stad","eMail":"email@test.se","id":1},"images":[{"id":4,"imagePath":"13\/130001_1.png"},{"id":3,"imagePath":"13\/130572_0.png"}],"products":[{"id":1,"materialColor":"Hallandia","description":"Beksrivning","frontWork":"Polerad","type":{"id":1,"name":"Sten"},"stoneModel":"NB 49","sideBackWork":null,"textStyle":"Helvetica nedhuggen i guld","ornament":"Blomma nedhuggen i guld","tasks":[{"status":1,"station":{"id":1,"name":null}},{"status":0,"station":{"id":2,"name":null}},{"status":0,"station":{"id":3,"name":"Gravering"}},{"status":0,"station":{"id":4,"name":null}}]},{"id":2,"materialColor":"Hallandia","description":"Sockel under mark","frontWork":"Polerad","type":{"id":2,"name":"Sockel"},"tasks":[{"status":0,"station":{"id":1,"name":null}}]},{"id":3,"materialColor":"Material","description":null,"frontWork":null,"type":{"id":3,"name":"Vas"}}]}';
		$order = json_decode($json, true);
		
		insertOrder($order);
		
		$this->assertTrue(true);
	}
	
}
?>
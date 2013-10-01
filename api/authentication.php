<?php
function checkAuthenticated(){
	if(!isset($_SERVER['HTTP_LIDKOPINGSH_AUTHENTICATION'])){
		$a = array(
			'error' => array(
				'code' => 401,
				'subcode' => 1,
				'description' => "Access Denied, no API key specified"
			)
		);
		die(json_encode($a, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
	}else if($_SERVER['HTTP_LIDKOPINGSH_AUTHENTICATION'] != $GLOBALS['test_apiKey']){
		$a = array(
			'error' => array(
				'code' => 401,
				'subcode' => 2,
				'description' => "Access Denied, API key is not valid"
			)
		);
		die(json_encode($a, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
	}

}

?>
<?php
function checkAuthenticated(){
	if(!isset($_SERVER['HTTP_LIDKOPINGSH_AUTHENTICATION'])){
		errorGeneric("Access Denied, no API key specified", 10);
	}else if($_SERVER['HTTP_LIDKOPINGSH_AUTHENTICATION'] != $GLOBALS['test_apiKey']){
		errorGeneric("Access Denied, API key is not valid", 11);
	}
}
?>
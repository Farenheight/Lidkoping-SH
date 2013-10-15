<?php
/**
 * Formats the output correctly into a JSON string. All output is sent via this method.
 */
function output($success, $results = null, $message = null, $errorCode = 0) {
	$output = array(
		"success" => $success
	);
	if(!is_null($results)){
		$output['results'] = $results;
	}
	if(!is_null($message)){
		$output['message'] = $message;
	}
	if(!is_null($errorCode)){
		$output['errorCode'] = $errorCode;
	}
	
	return json_encode($output);
}

function errorSql($message, $number) {
	debug_print_backtrace();
	die(output(false, null, $message, $number));
}

function errorGeneric($message, $number = null) {
	doDie(output(false, null, $message, $number));
}

function doDie($string = null){
	$GLOBALS['util']->saveChanges();
	$GLOBALS['con']->commit();
	die($string);
}
?>
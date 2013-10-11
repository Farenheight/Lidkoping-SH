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
	
	echo json_encode($output);
}

function errorSql($message, $number) {
	die(output(false, null, $message, $number));
}

function errorGeneric($message, $number = null) {
	die(output(false, null, $message, $number));
}
?>

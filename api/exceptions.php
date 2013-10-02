<?php


function errorSql($message, $number, $description){
	die($message . ": (" . $number . ") " . $description);
}

function errorSqlConnection($number, $description){
	echo "Failed to connect to MySQL: (" . $number . ") " . $description;
}
?>
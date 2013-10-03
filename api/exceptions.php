<?php

function errorSql($message, $number, $description) {
	die($message . ": (" . $number . ") " . $description);
}

function errorGeneric($message, $number = null) {
	die("Error: " . ($number === null ? "" : ("(" . $number . ") ")) . $message . "<br>");
}

function errorSqlConnection($number, $description) {
	echo "Failed to connect to MySQL: (" . $number . ") " . $description;
}
?>
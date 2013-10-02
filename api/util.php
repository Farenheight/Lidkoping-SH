<?php

function genNewOrderNumber($date) {
	$dateSeconds = $date / 1000;
	$numGenerated = 0;
	$year = date("y", $dateSeconds);
	$zeros = "";
	if ($numGenerated < 10) {
		$zeros = "000";
	} else if ($numGenerated < 100) {
		$zeros = "00";
	} else if ($numGenerated < 1000) {
		$zeros = "0";
	}
	$numGenerated++;
	return $year . $zeros . $numGenerated;
}
?>
<?php

class OrderNumber {
	private $numGenerated;

	public function OrderNumber() {
		$numGenerated = array();
		for ($i = 0; $i < 100; $i++) {
			$this->numGenerated[$i] = 0;
		}
	}

	public function genNewOrderNumber($date) {
		$dateInSeconds = $date / 1000;
		$year = intval(date("y", $dateInSeconds));
		$zeros = "";
		if ($this -> numGenerated[$year] < 10) {
			$zeros = "000";
		} else if ($this -> numGenerated[$year] < 100) {
			$zeros = "00";
		} else if ($this -> numGenerated[$year] < 1000) {
			$zeros = "0";
		}
		$toReturn = strval($year) . $zeros . strval($this -> numGenerated[$year]);
		$this -> numGenerated[$year] = $this -> numGenerated[$year] + 1;
		return $toReturn;
	}

}
?>
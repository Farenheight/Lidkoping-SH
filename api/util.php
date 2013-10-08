<?php

class OrderNumberGenerator {
	private $numGenerated;

	public function OrderNumberGenerator() {
		$numGenerated = array();
		$stmt = $GLOBALS['con']->prepare("SELECT * FROM `generated_ordernumbers` WHERE `count` > 0;");
		$stmt->execute();
		$res = $stmt->get_result();
		
		for ($i = 0; $i < 100; $i++) {
			$this->numGenerated[$i] = 0;
		}
		while($row = $res->fetch_assoc()){
			$this->numGenerated[$row['year']] = $row['count'];
		}
	}

	public function newOrderNumber($date) {
		$dateInSeconds = $date / 1000;
		@$year = intval(date("y", $dateInSeconds));
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
	
	public function saveChanges(){
		$count = count($this->numGenerated);
		for($i = 0; $i < $count; $i++){
			if($this->numGenerated[$i] > 0){
				$sql = "INSERT INTO `generated_ordernumbers` (`year`, `count`) VALUES (?, ?)
				ON DUPLICATE KEY UPDATE `count`=VALUES(`count`);";
				$stmt = $GLOBALS['con']->prepare($sql);
				$stmt->bind_param("ii", $i, $this->numGenerated[$i]);
				$stmt->execute();
			}
		}
		$GLOBALS['con']->commit();
	}

}
?>
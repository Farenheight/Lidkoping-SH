<?php

class Util {
	private $numGenerated;
	private $idNames;

	public function Util() {
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
		
		$stmt2 = $GLOBALS['con']->prepare("SELECT `id_name` FROM `order` WHERE `cancelled`=0 AND `archived`=0");
		$stmt2->execute();
		$res2 = $stmt2->get_result();

		while($row2 = $res->fetch_assoc()){
			$this->idNames[] = $row2['id_name'];
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
	
	public function getIdName($order){
		// Use while. Create a suggestion and check with array. 
		$cemetery = $order['cemetery'];
		$deceased = $order['deceased'];
		
		$cemeteryLetter = strtoupper(substr($cemetery, 0, 1));
		$cemeteryLetterCount = 1;
		while($cemeteryLetter == " "){
			$cemeteryLetter = strtoupper(substr($cemetery, $cemeteryLetterCount, 1));
		}
		
		$proposal = strtoupper((substr($deceased, 0, 1) == " " ? "A" : substr($deceased, 0, 1))) . 
			"." . $cemeteryLetter . ".";
		$crysis = array("X", "Y", "Z", "Ä", "Ö");
		
		$i = 0;
		$crysisNum = 0;
		$secLetterNum = 0;
		$randomCount = 0;
		$crysisArrayCount = sizeof($crysis);
		$deceasedNameCount = sizeof($order['deceased']);
		while(in_array($proposal, $this->idNames)){ // If ID name is taken
			if($i < $deceasedNameCount){
				if(substr($deceased, $i, 1) != " "){
					$proposal = strtoupper(substr($deceased, $i, 1) . "." . $cemeteryLetter . ".");
				}
			}else if($crysisNum < $crysisArrayCount){
				$proposal = strtoupper($crysis[$crysisNum] . "." . $cemeteryLetter . ".");
				$crysisNum++;
			}else if($secLetterNum < $deceasedNameCount-1){
				if(substr($deceased, 0, 1) != " " && substr($deceased, $secLetterNum, 1) != " "){
					$proposal = strtoupper(substr($deceased, 0, 1)) . strtolower(substr($deceased, $secLetterNum, 1)) . "." . $cemeteryLetter . ".";
				}
				$secLetterNum++;
			}else{
				if($randomCount < 10){
					$letter1 = chr(rand(65, 90));
					$letter2 = chr(rand(97, 122));
					$proposal = $letter1 . $letter2 . "." . $cemeteryLetter . ".";
				}else if($randomCount < 20){
					$letter1 = chr(rand(65, 90));
					$letter2 = chr(rand(97, 122));
					$letter3 = chr(rand(97, 122));
					$proposal = $letter1 . $letter2 . $letter3 . "." . $cemeteryLetter . ".";
				}else{
					$letter1 = chr(rand(65, 90));
					$letter2 = chr(rand(97, 122));
					$letter3 = chr(rand(97, 122));
					$letter4 = chr(rand(97, 122));
					$proposal = $letter1 . $letter2 . $letter3 . $letter4 . "." . $cemeteryLetter . ".";
				}
				$randomCount++;
			}
			
			$i++;
		}
		$this->idNames[] = $proposal;
		return $proposal;
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
	}

}
?>
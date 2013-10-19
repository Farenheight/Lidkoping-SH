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
		
		$this->idNames = array();
		$stmt2 = $GLOBALS['con']->prepare("SELECT `id_name` FROM `order` WHERE `cancelled`=0 AND `archived`=0");
		$stmt2->execute();
		$res2 = $stmt2->get_result();

		while($row2 = $res2->fetch_assoc()){
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
	
	/**
	 * Return false if something went wrong (e.g. argument was not an order)
	 * or an string containing the id name on success.
	 */
	public function getIdName($order){
		if(!array_key_exists("cemetery", $order) || !array_key_exists("deceased", $order)){
			return false;
		}
		
		$cemetery = $order['cemetery'];
		$deceased = $order['deceased'];	
		
		if(empty($order['deceased'])){ // For other things then gravestones
			for($j=0; $j<10; $j++){
				$deceased .= chr(rand(65, 90));
			}
		}
		
		$cemeteryLetter = $this->utf8_strtoupper($this->utf8_substr($cemetery, 0, 1));
		$cemeteryLetterCount = 1;
		while($cemeteryLetter == " "){
			$cemeteryLetter = $this->utf8_strtoupper($this->utf8_substr($cemetery, $cemeteryLetterCount, 1));
		}
		
		$proposal = $this->utf8_strtoupper(($this->utf8_substr($deceased, 0, 1) == " " ? "A" : $this->utf8_substr($deceased, 0, 1))) . 
			"." . $cemeteryLetter . ".";
		$crysis = array("X", "Y", "Z", "Ä", "Ö");
		
		$i = 0;
		$crysisNum = 0;
		$secLetterNum = 0;
		$randomCount = 0;
		$crysisArrayCount = sizeof($crysis);
		$deceasedNameCount = strlen($order['deceased']);
		while(in_array($proposal, $this->idNames)){ // If ID name is taken
			//echo " - " . $proposal . " was taken, trying a new one... - ";
			if($i < $deceasedNameCount){
				if($this->utf8_substr($deceased, $i, 1) != " "){
					$proposal = $this->utf8_strtoupper($this->utf8_substr($deceased, $i, 1) . "." . $cemeteryLetter . ".");
				}
			}else if($crysisNum < $crysisArrayCount){
				$proposal = $this->utf8_strtoupper($crysis[$crysisNum] . "." . $cemeteryLetter . ".");
				$crysisNum++;
			}else if($secLetterNum < $deceasedNameCount-1){
				if($this->utf8_substr($deceased, 0, 1) != " " && $this->utf8_substr($deceased, $secLetterNum, 1) != " "){
					$proposal = $this->utf8_strtoupper($this->utf8_substr($deceased, 0, 1)) . $this->utf8_strtolower($this->utf8_substr($deceased, $secLetterNum, 1)) . "." . $cemeteryLetter . ".";
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
		//echo " - " . $proposal . " was available! - ";
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
	
	function utf8_strtoupper($string){
		return mb_strtoupper($string, "UTF-8");
	}
	
	function utf8_strtolower($string) {
		return mb_strtolower($string, "UTF-8");
	}
	
	function utf8_substr($string, $start, $length) {
		return mb_substr($string, $start, $length, "UTF-8");
	}

}
?>

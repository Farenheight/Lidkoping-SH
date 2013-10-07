<?php

/**
 * Prepared statements using SELECT does not need to commit, an result set is obtained at execute.
 * When INSERT'ing or UPDATE'ing, use bind_param and set data. Use commit when ready to send.
 */

class mySQLConnection {
	private $con;
	private $stmt;

	public function mySQLConnection() {
		$this -> con = new mysqli($GLOBALS['db_host'], $GLOBALS['db_username'], $GLOBALS['db_password'], $GLOBALS['db_database']);
		if ($this -> con -> connect_errno) {
			errorSql("Failed to connect: ".$mysqli -> connect_error, $mysqli -> connect_errno);
		}
		$this -> con -> autocommit(FALSE);
		
		// Set encoding format for database, otherwise values are not retrieved correctly
		$stmt = $this -> con -> prepare("SET NAMES 'utf8'");
		$stmt -> execute();
		$this -> con -> commit();
	}

	public function commit() {
		if (!$this -> con -> commit()) {
			errorSql("Commit failed: ".$this -> con -> error, $this -> con -> errno);
		}
	}
	
	public function escape($string){
		return $this->con->escape_string($string);
	}

	public function prepare($statement) {
		if (!($this -> stmt = $this -> con -> prepare($statement))) {
			errorSql("Prepare failed: ".$this -> con -> error, $this -> con -> errno);
		}
		return $this -> stmt;
	}
}
?>
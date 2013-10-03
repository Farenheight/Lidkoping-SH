<?php

class mySQLConnection {
	private $con;
	private $stmt;

	public function mySQLConnection() {
		$this -> con = new mysqli($GLOBALS['db_host'], $GLOBALS['db_username'], $GLOBALS['db_password'], $GLOBALS['db_database']);
		if ($this -> con -> connect_errno) {
			errorSql("Failed to connect", $mysqli -> connect_errno, $mysqli -> connect_error);
		}
		$this -> con -> autocommit(FALSE);
	}

	public function commit() {
		if (!$this -> con -> commit()) {
			errorSql("Commit failed", $this -> con -> errno, $this -> con -> error);
		}
	}
	
	public function escape($string){
		return $this->con->escape_string($string);
	}

	public function prepare($statement) {
		if (!($this -> stmt = $this -> con -> prepare($statement))) {
			errorSql("Prepare failed", $this -> con -> errno, $this -> con -> error);
		}
		return $this -> stmt;
	}
}
?>
<?php

class mySQLConnection {
	private $con;
	private $stmt;

	public function mySQLConnection($host, $username, $password, $db) {
		$this -> con = new mysqli($GLOBALS['db_host'], $GLOBALS['db_user'], $GLOBALS['db_password'], $GLOBALS['db_database']);
		if ($mysqli -> connect_errno) {
			errorSql("Failed to connect", $mysqli -> connect_errno, $mysqli -> connect_error);
		}
		$this -> con -> autocommit(FALSE);
	}

	public function commit() {
		if(!$this -> con -> commit()){
			errorSql("Commit failed", $mysqli -> errno, $mysqli -> error);
		}
	}

	public function prepare($statement) {
		if (!($this -> stmt = $mysqli -> prepare($statement))) {
			errorSql("Prepare failed", $mysqli -> errno, $mysqli -> error);
		}
	}

	public function bind_param($type, $value) {
		if (!$this->stmt -> bind_param($type, $value)) {
			errorSql("Binding parameters failed", $this->stmt->errno, $this->stmt->error);
		}
	}
	
	public function execute(){
		if (!$this->stmt->execute()) {
			errorSql("Execute failed", $this->stmt->errno, $this->stmt->error);
		}
		//$this->stmt->close();
		return $this->stmt;
	}
}
?>
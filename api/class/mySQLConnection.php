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
		$this -> con -> commit();
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
	
	

	/////////

	public function connect() {
		$this -> link = mysql_connect($this -> host, $this -> username, $this -> password);
		if (!$this -> link) {
			echo "Log in problem<br>";
			die('Could not connect: ' . mysql_error());
		}
		mysql_select_db($this -> db, $this -> link);
		mysql_set_charset("UTF8");
	}

	public function fetchQuery($query) {
		$result = mysql_query($query, $this -> link);
		return $result;
	}

	public function commandQuery($query) {
		$status = mysql_query($query, $this -> link);
		if (!$status) {
			echo "Invalid query: " . mysql_error();
		}
	}

	public function disconnect() {
		mysql_close($this -> link);
	}

	public function getLink() {
		return $this -> link;
	}

}
?>
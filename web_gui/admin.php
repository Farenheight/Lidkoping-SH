<?php
include_once '../api/output.php';
include_once '../api/class/mySQLConnection.php';
include_once '../api/db_config.php';
$con = new mySQLConnection();

if(isset($_POST['id']) && isset($_POST['deceased']) && isset($_POST['cemeteryBoard']) && isset($_POST['cemetery'])){
	$archived = isset($_POST['archived']);
	$cancelled = isset($_POST['cancelled']);
	
	$time = time()*1000;
	$sql = "UPDATE `order` SET `deceased`=?, `cemetery_board`=?, `cemetery`=?, `time_last_update`=?, `cancelled`=?, `archived`=? WHERE `order_id`=?";
	$stmt = $GLOBALS['con']->prepare($sql);
	$stmt->bind_param("sssiiii", $_POST['deceased'], $_POST['cemeteryBoard'], $_POST['cemetery'], $time, $cancelled, $archived, $_POST['id']);
	$stmt->execute();
	$GLOBALS['con']->commit();
	echo "Updated";
}

if(isset($_GET['search'])){
	if(empty($_GET['search'])){
		echo '<form name="searchForm" action="" method="GET">
		<input type="text" name="search" />
		<input type="submit" value="Submit" />
		</form>';
	}else{
		
		$sql = "SELECT `order_id`, `order_number` FROM `order` WHERE `id_name`=?";
		$stmt = $GLOBALS['con']->prepare($sql);
		$stmt->bind_param("s", $_GET['search']);
		$stmt->execute();
		$res = $stmt->get_result();
		
		while($row = $res->fetch_assoc()){
			echo '<a href="/web_gui/admin.php?id='. $row['order_id'] .'">'. $row['order_number'] .'</a><br>';
		}
	}
}

$showPage = false;
if(isset($_GET['id'])){
	if(is_numeric($_GET['id'])){
		$sql = "SELECT * FROM `order` WHERE `order_id`=?";
		$stmt = $GLOBALS['con']->prepare($sql);
		$stmt->bind_param("i", $_GET['id']);
		$stmt->execute();
		$res = $stmt->get_result();
		
		if($res->num_rows === 1){
			$showPage = true;
			$row = $res->fetch_assoc();
		}
	}
}

if($showPage):
?>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Redigera order</title>
	</head>
	<body>
		<h1>Redigera Order</h1>
		<form name="edit" action="" method="POST">
			<fieldset>
				<legend>Order info</legend>
				<div>
					<label for="idName">ID namn: </label>
					<span id="idName"><?php echo $row['id_name']; ?></span>
				</div>
				
				<div>
					<label for="deceased">Avlidnes namn</label>
					<input id="deceased" name="deceased" maxlength="50" value="<?php echo $row['deceased'] ?>" type="text" />
				</div>
				
				<div>
					<label for="cemetery">Kyrkogård</label>
					<input id="cemetery" name="cemetery" maxlength="50" value="<?php echo $row['cemetery'] ?>" type="text" />
				</div>
				
				<div>
					<label for="cemeteryBoard">Kyrkogårdsnämnd</label>
					<input id="cemeteryBoard" name="cemeteryBoard" maxlength="50" value="<?php echo $row['cemetery_board'] ?>" type="text" />
				</div>
				
				<div>
					<label for="archived">Archived</label>
					<input id="archived" name="archived" value="archived" type="checkbox" <?php if($row['archived'] != 0) echo "checked" ?> />
				</div>
				
				<div>
					<label for="cancelled">Cancelled</label>
					<input id="cancelled" name="cancelled" value="cancelled" type="checkbox" <?php if($row['cancelled'] != 0) echo "checked" ?> />
				</div>
				
				<div>
					<input type="hidden" name="id" value="<?php echo $_GET['id'] ?>" />
					<input id="submit" value="Submit" type="submit" />
				</div>
			</fieldset>
		</form>
	</body>
</html>
<?php endif; ?>
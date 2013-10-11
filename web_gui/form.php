<?php
require_once '../api/db_config.php';
require_once '../api/class/mySQLConnection.php';

// Store all stations
$sql = "SELECT * FROM `station`";
$con = new mySQLConnection();
$stmt = $con -> prepare($sql);
$stmt -> execute();
$res = $stmt -> get_result();
while ($row = $res -> fetch_assoc()) {
	$stations[$row['station_id']] = $row['name'];
}

// Store all product types
$sql = "SELECT * FROM `product_type`";
$con = new mySQLConnection();
$stmt = $con -> prepare($sql);
$stmt -> execute();
$res = $stmt -> get_result();
while ($row = $res -> fetch_assoc()) {
	$productTypes[$row['product_type_id']] = $row['name'];
}

function generateNewProduct() {
	$str = '<fieldset>
				<legend>
					Produkt (\' + types[productType] + \')
				</legend>
				<input name="products[\' + product + \'][type][id]" type="hidden" value="\' + productType + \'"/>
				<ul>
					<li id="li_11" >
						<label class="description" for="element_11">Produkts Framsidearbete </label>
						<div>
							<input id="element_11" name="products[\' + product + \'][frontWork]" class="element text medium" type="text" maxlength="255" value=""/>
						</div>
					</li>
					<li id="li_12" >
						<label class="description" for="element_12">Produkts Material och Färg </label>
						<div>
							<input id="element_12" name="products[\' + product + \'][materialColor]" class="element text medium" type="text" maxlength="255" value=""/>
						</div>
					</li>
					<li id="li_13" >
						<label class="description" for="element_13">Produkts Beskrivning </label>
						<div>
							<input id="element_13" name="products[\' + product + \'][description]" class="element text medium" type="text" maxlength="255" value=""/>
						</div>
					</li>
					<p><a href="#" onclick="return toggleStoneDetails(\' + product + \')">Visa/Dölj stendetaljer</a></p>
					<div id="stone_\' + product + \'"></div>
					<fieldset>
						<legend>
							Moment
						</legend>
						<div id="task_\' + product + \'_\' + task[product] + \'">
						</div>
						<p>
							<a href="#" onclick="return addTask(\' + product + \')">Lägg till moment</a>
						</p>
					</fieldset>
				</ul>
			</fieldset>
			<p></p>
			<div id="product_\' + (product+1) + \'"></div>';
	return $str;
}

function generateStationSelect() {
	$str = '<li><div>' .
		'<label class="description" for="element_18">Station</label>' .
		'<input id="element_18" type="text" name="products[\' + curProduct + \'][tasks][\' + task[curProduct] + \'][station][name]"' .
		'class="element text medium" list="productName"/>' .
		'<datalist id="productName">';
	foreach ($GLOBALS['stations'] as $id => $name) {
		$str .= "<option value=\"$name\">$name</option>";
	}
	$str .= '</datalist></div></li> <div id="task_\' + curProduct + \'_\' + (task[curProduct]+1) + \'"></div>';
	return $str;
}

function generateStoneDetails() {
	$str = '<li id="li_14" >
				<label class="description" for="element_14">Gravsten Modell </label>
				<div>
					<input id="element_14" name="products[\' + curProduct + \'][stoneModel]" class="element text medium" type="text" maxlength="255" value=""/>
				</div>
			</li>
			<li id="li_15" >
				<label class="description" for="element_15">Gravsten Bearbetning Sidor/Baksida </label>
				<div>
					<input id="element_15" name="products[\' + curProduct + \'][sideBackWork]" class="element text medium" type="text" maxlength="255" value=""/>
				</div>
			</li>
			<li id="li_16" >
				<label class="description" for="element_16">Gravsten Text, Stiltyp och Bearbetning </label>
				<div>
					<input id="element_16" name="products[\' + curProduct + \'][textStyle]" class="element text medium" type="text" maxlength="255" value=""/>
				</div>
			</li>
			<li id="li_17" >
				<label class="description" for="element_17">Gravsten Ornament </label>
				<div>
					<input id="element_17" name="products[\' + curProduct + \'][ornament]" class="element text medium" type="text" maxlength="255" value=""/>
				</div>
			</li>';
	return $str;
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Lägg till order</title>
		<link rel="stylesheet" type="text/css" href="view.css" media="all">
		<script type="text/javascript" src="view.js"></script>
		<script type="text/javascript" src="calendar.js"></script>
		<script>
			var task = [], product, types = <?php echo json_encode($productTypes) ?>;
			task[0] = 0;
			product = 0;
		</script>
	</head>
	<body id="main_body" >

		<img id="top" src="top.png" alt="">
		<div id="form_container">

			<h1><a>Lägg till order</a></h1>
			<form id="form_721061" class="appnitro" enctype="multipart/form-data" method="post" action="formHandler.php">
				<div class="form_description">
					<h2>Lägg till order</h2>
				</div>
				<fieldset>
					<legend>
						Order
					</legend>
					<ul>
						<li id="li_6" >
							<label class="description" for="element_6">Order Datum </label>
							<span>
								<input id="element_6_1" name="orderDateDay" class="element text" size="2" maxlength="2" value="<?php echo @date("d") ?>" type="text">
								/ <label for="element_6_1">DD</label> </span>
							<span>
								<input id="element_6_2" name="orderDateMonth" class="element text" size="2" maxlength="2" value="<?php echo @date("m") ?>" type="text">
								/ <label for="element_6_2">MM</label> </span>
							<span>
								<input id="element_6_3" name="orderDateYear" class="element text" size="4" maxlength="4" value="<?php echo @date("Y") ?>" type="text">
								<label for="element_6_3">YYYY</label> </span>

							<span id="calendar_6"> <img id="cal_img_6" class="datepicker" src="images/calendar.gif" alt="Pick a date."> </span>
							<script type="text/javascript">
								Calendar.setup({
									inputField : "element_6_3",
									baseField : "element_6",
									displayArea : "calendar_6",
									button : "cal_img_6",
									ifFormat : "%B %e, %Y",
									onSelect : selectEuropeDate
								});
							</script>

						</li>
						<li id="li_1" >
							<label class="description" for="element_1">Kyrkogårdsnämnd </label>
							<div>
								<input id="element_1" name="cemeteryBoard" class="element text medium" type="text" maxlength="255" value=""/>
							</div>
						</li>
						<li id="li_2" >
							<label class="description" for="element_2">Kyrkogård </label>
							<div>
								<input id="element_2" name="cemetery" class="element text medium" type="text" maxlength="255" value=""/>
							</div>
						</li>
						<li id="li_7" >
							<label class="description" for="cemeteryBlock">Gravplatsbeteckning </label>
							<div>
								<span> <label for="cemeteryBlock">Kvarter</label>
									<input id="cemeteryBlock" name="cemeteryBlock" class="element text" size="15" value="" type="text">
								</span>
								<span> <label for="cemeteryNumber">Nummer</label>
									<input id="cemeteryNumber" name="cemeteryNumber" class="element text" size="15" value="" type="text">
								</span>
							</div>
						</li>
					</ul>
				</fieldset>
				<p></p>
				<fieldset>
					<legend>
						Kunduppgifter
					</legend>
					<ul>
						<li id="li_9" >
							<label class="description" for="element_9">Kunds Titel </label>
							<div>
								<input id="element_9" name="customer[title]" class="element text medium" type="text" maxlength="255" value=""/>
							</div>
						</li>
						<li id="li_3" >
							<label class="description" for="element_3">Kunds Namn </label>
							<div>
								<input id="element_3" name="customer[name]" class="element text medium" type="text" maxlength="255" value=""/>
							</div>
						</li>
						<li id="li_4" >
							<label class="description" for="element_4">Kunds Gatuadress </label>
							<div>
								<input id="element_4" name="customer[address]" class="element text medium" type="text" maxlength="255" value=""/>
							</div>
						</li>
						<li id="li_5" >
							<label class="description" for="element_5">Kunds Postadress </label>
							<div>
								<input id="element_5" name="customer[postAddress]" class="element text medium" type="text" maxlength="255" value=""/>
							</div>
							<p class="guidelines" id="guide_5">
								<small>123 45 Postort</small>
							</p>
						</li>
						<li id="li_8" >
							<label class="description" for="element_8">Epost </label>
							<div>
								<input id="element_8" name="customer[eMail]" class="element text medium" type="text" maxlength="255" value=""/>
							</div>
						</li>
					</ul>
				</fieldset>
				<p></p>
				<fieldset>
					<legend>
						Bild
					</legend>
					<ul>
						<li id="li_10" >
							<label class="description" for="element_10">Bild </label>
							<div>
								<input id="element_10" name="images[][file]" class="element file" type="file"/>
							</div>
						</li>
					</ul>
				</fieldset>
				<p>
					Lägg till produkt: 
					<?php
					foreach ($productTypes as $id => $name) {
						echo "<a href='#' onclick='return addProduct($id)'>$name</a> ";
					}
					?>
				</p>
				<div id="product_0">
				</div>
				<ul>
					<li class="buttons">
						<input id="saveForm" class="button_text" type="submit" value="Spara" />
					</li>
				</ul>
			</form>
		</div>
		<img id="bottom" src="bottom.png" alt="">
	</body>
</html>
<script type="text/javascript">
	function addProduct(productType) {
		var products = document.getElementById("product_" + product);
		products.innerHTML += '<?php echo preg_replace( "/\r|\n/", "", generateNewProduct()) ?>';
		product++;
		task[product] = 0;
		return false;
	}
	
	function toggleStoneDetails(curProduct) {
		var stoneDetails = document.getElementById("stone_" + curProduct);
		if (stoneDetails.innerHTML == "") {
			stoneDetails.innerHTML = '<?php echo preg_replace( "/\r|\n/", "", generateStoneDetails()) ?>';
		} else {
			stoneDetails.innerHTML = "";
		}
		return false;
	}
	
	function addTask(curProduct) {
		var tasks = document.getElementById("task_" + curProduct + "_" + task[curProduct]);
		tasks.innerHTML += '<?php echo generateStationSelect() ?>';
		task[curProduct]++;
		return false;
	}
</script>
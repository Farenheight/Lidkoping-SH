<?php
$data = $_POST;

// Set order date to timestamp
@$timestamp = mktime(0, 0, 0, $data['orderDateMonth'], $data['orderDateDay'], $data['orderDateYear']) * 1000;
$orderYear = $data['orderDateYear']%1000;
$orderMonth = $data['orderDateMonth'];
$orderDay = $data['orderDateDay'];
$imagePath = "";

$data['orderDate'] = $timestamp;
unset($data['orderDateDay']);
unset($data['orderDateMonth']);
unset($data['orderDateYear']);

// Handle picture
$allowedExts = array("jpg", "jpeg", "png");
$allowedHeaderType = array("image/jpeg", "image/jpg", "image/png");
if ($_FILES["image1"]["error"] > 0) {
	echo "Error: " . $_FILES["image1"]["error"] . "<br>";
} else {
	$temp = explode(".", $_FILES["image1"]["name"]);
	$extension = end($temp);
	if(in_array($_FILES["image1"]["type"], $allowedHeaderType) && in_array($extension, $allowedExts) && ($_FILES["image1"]["size"] < (1024*1024*10))){
		if(!file_exists("../api/pics/$orderYear/" . $orderMonth . $orderDay . "-" . time() . "." . $extension)){
			move_uploaded_file($_FILES["image1"]["tmp_name"], "../api/pics/$orderYear/" . $orderMonth . $orderDay . "-" . time() . "." . $extension);
			$imagePath = "$orderYear/$orderMonth" . "$orderDay-" . time() . ".$extension";
		}else{
			echo "File already exist.";
		}
	}else{
		echo "Invalid file";
	}
}
if($imagePath !== ""){
	$data['images'] = array(
		array(
			"imagePath" => $imagePath
		)
	);
}


// Send JSON object to web api
echo "Skickar: " . json_encode($data) . "<br />";
$url = "http://lidkopingsh/api/?action=insertOrder";

// Create a stream
$opts = array(
	'http' => array(
		'method' => "POST",
		'header' => "Content-type: application/x-www-form-urlencoded\r\n" .
			"Lidkopingsh-Apikey: 97b48bdf2e10caac766c3aad8e0beebe\r\n" .
			"Lidkopingsh-Deviceid: asdf\r\n",
		'content' => 'data=' . json_encode($data)
	)
);
$context = stream_context_create($opts);

// Open the file using the HTTP headers set above
$file = file_get_contents($url, false, $context);

$response = json_decode($file, true);

echo "<br>" . $file;
?>
<?php
$data = $_POST;

// Set order date to timestamp
@$timestamp = mktime(0, 0, 0, $data['orderDateMonth'], $data['orderDateDay'],
	$data['orderDateYear']) * 1000;
$data['orderDate'] = $timestamp;
unset($data['orderDateDay']);
unset($data['orderDateMonth']);
unset($data['orderDateYear']);

// Send JSON object to web api
echo "Skickar: " . json_encode($data) . "<br />";
$url = "http://localhost/api/?action=insertOrder";
		
// Create a stream
$opts = array(
	'http'=>array(
		'method'=>"POST",
		'header'=>"Content-type: application/x-www-form-urlencoded\r\n" .
				"Lidkopingsh-Authentication: 123456789qwertyuiop\r\n",
		'content' => 'data=' . json_encode($data)
	),
);
$context = stream_context_create($opts);

// Open the file using the HTTP headers set above
$file = file_get_contents($url, false, $context);

$response = json_decode($file, true);

var_dump($file);
?>
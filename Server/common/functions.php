<?php
function encode_password($in) {
	return hash('sha256', $in);
}
?>
var point;
var lat;
var lng;
var markers = [];

function initialize() {

	map = new google.maps.Map(document.getElementById('map'), {
		center: {lat: -3.7319029, lng: -38.5267393},
		zoom: 15,
		mapTypeId: google.maps.MapTypeId.HYBRID
	});
	var input = /** @type {!HTMLInputElement} */(document.getElementById('pac-input'));

	var types = document.getElementById('type-selector');

	map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);
	map.controls[google.maps.ControlPosition.TOP_LEFT].push(types);

	var autocomplete = new google.maps.places.Autocomplete(input);
	autocomplete.bindTo('bounds', map);

	var infowindow = new google.maps.InfoWindow();
	var marker = new google.maps.Marker({
		map: map,
		anchorPoint: new google.maps.Point(-3.7319029, -38.5267393)
	});
	
	map.addListener('click', function(event) {
		clicked(event.latLng);
	});

	autocomplete.addListener('place_changed', function() {
		infowindow.close();
		marker.setVisible(false);
		var place = autocomplete.getPlace();
		if (!place.geometry) {
			window.alert("Autocomplete's returned place contains no geometry");
			return;
		}

		// If the place has a geometry, then present it on a map.
		if (place.geometry.viewport) {
			map.fitBounds(place.geometry.viewport);
		} else {
			map.setCenter(place.geometry.location);
			map.setZoom(17);  // Why 17? Because it looks good.
		}
		marker.setIcon(/** @type {google.maps.Icon} */({
			url: place.icon,
			size: new google.maps.Size(71, 71),
			origin: new google.maps.Point(0, 0),
			anchor: new google.maps.Point(17, 34),
			scaledSize: new google.maps.Size(35, 35)
		}));
		marker.setPosition(place.geometry.location);
		marker.setVisible(true);

		var address = '';
		if (place.address_components) {
			address = [
			           (place.address_components[0] && place.address_components[0].short_name || ''),
			           (place.address_components[1] && place.address_components[1].short_name || ''),
			           (place.address_components[2] && place.address_components[2].short_name || '')
			           ].join(' ');
		}

		infowindow.setContent('<div><strong>' + place.name + '</strong><br>' + address);
		infowindow.open(map, marker);
	});

	// Sets a listener on a radio button to change the filter type on Places
	// Autocomplete.
	function setupClickListener(id, types) {
		var radioButton = document.getElementById(id);
		radioButton.addEventListener('click', function() {
			autocomplete.setTypes(types);
		});
	}

	setupClickListener('changetype-all', []);
	setupClickListener('changetype-address', ['address']);
	setupClickListener('changetype-establishment', ['establishment']);
	setupClickListener('changetype-geocode', ['geocode']);
}


function clicked(point) {
	lat = document.getElementById("lat");
	lng = document.getElementById("lng");
	if(point) {
		lat.value = point.lat();
		lng.value = point.lng();
		addMarker(point);
		map.setCenter(point, 15);
	}
} 

function addMarker(location) {
	var marker = new google.maps.Marker({
		position: location,
		map: map
	});
	markers.push(marker);
}

function setMapOnAll(map) {
	for (var i = 0; i < markers.length; i++) {
		markers[i].setMap(map);
	}
}

// Removes the markers from the map, but keeps them in the array.
function clearMarkers() {
	setMapOnAll(null);
}

// Shows any markers currently in the array.
function showMarkers() {
	setMapOnAll(map);
}

// Deletes all markers in the array by removing references to them.
function deleteMarkers() {
	clearMarkers();
	markers = [];
}

function getAddress(overlay, latlng) {
	if (latlng != null) {
		address = latlng;
		geocoder.getLocations(latlng, showAddress);
	}
}

function crossAddress(){
	var geocoder = new google.maps.Geocoder();
	var address = 'Fortaleza, BR';
	if (geocoder) {
		geocoder.geocode({ 'address': address }, function (results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				console.log(results[0].geometry.location);
			} else {
				console.log("Geocoding failed: " + status);
			}
		});
	}    
}


google.maps.event.addDomListener(window, 'load', initialize);
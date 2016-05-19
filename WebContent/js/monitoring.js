var markerlist = [];
var markerRoute = [];
var rotaArray = [];
var polyline;
var id_element = '';

function updateMap() {
	var veicid;

	if (polyline) {
		map.removeOverlay(polyline);
	}
	if (polyline2) {
		map.removeOverlay(polyline2);
	}
	if (!currentVehicle) {
		updateVehicles();
	}
	if (currentVehicle && currentVehicle.id) {
		currentVehicle.showAndCenter();
		updateCurrentVehicle(currentVehicle);
		id_element = 'current_rastro:'+currentVehicle.id;
		veicid = document.getElementById(id_element);
	}
	//if (!currentVehicle) {
		if (!currentVehicle) {
			id_element = 'current_rastro:'+vehicles[0].id;
			veicid = document.getElementById(id_element);
			showAndSelect(veicid.value);
		} else if (veicid.value == currentVehicle.id) {
			showAndSelect(veicid.value);
		}
	//}
	// calculateBounds();

}

function showAndSelect(id) {

	var oldVehicle = currentVehicle;
	currentVehicle = retrieveVehicle(id);
	currentVehicle.showAndCenter();
	updateCurrentVehicle(currentVehicle);
	// checkRemoveCurrentVehicle(oldVehicle);
	var row = document.getElementById('m:' + id + ':current');
	if (row) {
		if (row.checked == true) {
			showRouteVehicleOnMap(id, true);
			// calculateBoundsRoute();
		}
	}
}

function showOnMap(id, checked) {
	if (checked) {
		retrieveVehicle(id).show();
	} else {
		retrieveVehicle(id).hide();
	}
	calculateBoundsRoute();
}

function showRouteVehicleOnMap(id, checked) {

	if (checked) {
		var str = document.getElementById("r:" + id + "");
		if (str.value != "") {
			var points = str.value.split('|');
			var markers = new Array();
			// var markerPoint = null;
			for (var j = 0; j < points.length; j++) {
				var p = points[j].split(';');
				var marker = new GLatLng(p[0], p[1]);
				markerPoint = createMarkerMonitoringRoute(p[0], p[1], p[2],	p[3], p[4], p[5], p[6], p[7], p[8]);
				// map.addOverlay(markerPoint);
				markers.push(marker);
			}
			polyline = new GPolyline(markers);
			map.addOverlay(polyline);
			calculateBoundsRoute(markers);
		} else {
			calculateBoundsRoute();
		}

	} else {
		removeRoutePoints();
	}
}

function showRouteVehicleOnMap222(id, checked) {

	if (checked) {
		var dataRoute = new Array();
		var str = document.getElementById("r:" + id + "");

		if (str) {
			dataRoute = str.value.split('|');
			var j = 0;
			rotaArray = new Array();

			for (var i = 0; i < dataRoute.length; i += 6) {
				rotaArray[j] = new GLatLng(dataRoute[i], dataRoute[i + 1]);
				var lat = dataRoute[i];
				var lng = dataRoute[i + 1];
				var point = dataRoute[i + 2];
				var veic = dataRoute[i + 3];
				var ord = dataRoute[i + 4];
				var dat = dataRoute[i + 5];

				var string = "Cliente:<b> " + point + "</b><br/>";
				string += "Veiculo:<b> " + veic + "</b><br/>";
				string += "Ordem:<b> " + ord + "</b><br/>";
				string += "Data: " + dat + "<br/>";
				var markerIcon = createIcon();
				var markerOptions = {
					icon : markerIcon,
					draggable : false,
					bouncy : false,
					zIndexProcess : function(marker, b) {
						return 1;
					}
				};
				var marker = new GMarker(new GLatLng(lat, lng), markerOptions);
				markerRoute.push(marker2);
				j++;
			}

			var pontosRota = new Array();
			var pontos = new Array();
			ROUTES = new Array();
			POLYLINES = new Array();

			for (var k = 0; k < markerRoute.length; k++) {
				map.addOverlay(markerRoute[k]);
				if (k > 0) {
					pontosRota.push(markerRoute[k - 1].getPoint());
					pontosRota.push(markerRoute[k].getPoint());
					setRoutesListDirections(pontosRota);
				}
				pontos.push(markerRoute[k]);
				pontosRota = [];
			}
			setTimeout(distancia, 2000);
		}

	} else {
		removeRoutePoints();
	}
}

function removeRoutePoints() {

	if (markerRoute.length > 0) {
		for (var i = 0; i < markerRoute.length; i++) {
			map.removeOverlay(markerRoute[i]);
		}
	}
	// document.getElementById("distanciaTempo").innerHTML = "";
	markerRoute = [];
	clearCurrenteRoute2();
}

function showAllOnMap(checked) {
	if (checked) {
		showAllVehicles();
	} else {
		hideAllVehicles();
	}
	calculateBounds();
}

function createIcon2(type) {

	var icon = new GIcon();
	icon.image = "../image?id=" + type;
	icon.iconSize = new GSize(32, 32);
	icon.iconAnchor = new GPoint(16, 16);
	icon.infoWindowAnchor = new GPoint(16, 16);
	return icon;
}

function createMarkerRoute(marker, descricao) {
	GEvent.addListener(marker, "click", function() {
		marker.openInfoWindowHtml(descricao);
	});
	return marker;
}

function createMarker2(marker, nome) {
	GEvent.addListener(marker, "click", function() {
		marker.openInfoWindowHtml("Ponto <b>" + nome + "</b>");
	});
	return marker;
}

function showClientPointsOnMap(checked) {

	var list = new Array();
	var string = document.getElementById('pointsToView');
	list = string.value.split('|');
	if (checked) {

		for (var i = 2; i < list.length; i += 3) {
			var nome = list[i - 2];
			var lat = list[i - 1];
			var lng = list[i];
			markerlist.push(createMarker2(new GMarker(new GLatLng(lat, lng)),
					nome));
		}
		for (var i = 0; i < markerlist.length; i++) {
			map.addOverlay(markerlist[i]);
		}

	} else {
		for (var i = 0; i < markerlist.length; i++) {
			map.removeOverlay(markerlist[i]);
		}
	}
}

function calculateBounds() {

	var zoom = 15;
	var defaultCenter = new GLatLng(-8.772083, -63.881636);
	if (vehicles.length > 0 && (!currentVehicle)) {
		var mbr = new GLatLngBounds();
		for (var i = 0; i < vehicles.length; i++) {
			if (vehicles[i].isVisible()) {
				mbr.extend(vehicles[i].center);
			}
		}
		zoom = (map.getBoundsZoomLevel(mbr) > 20
				|| map.getBoundsZoomLevel(mbr) < 8 ? 15 : map
				.getBoundsZoomLevel(mbr));
		map.setCenter(mbr.getCenter(), zoom);
	} else {
		map.setCenter(defaultCenter, zoom);
	}
}

function checkRemoveCurrentVehicle(oldVehicle) {

	if (currentVehicle && oldVehicle && (currentVehicle.id == oldVehicle.id)) {
		currentVehicle = null;
		removeRastro();
		document.getElementById("currentVehicle").value = "";
		document.getElementById("m:" + oldVehicle.id + ":current").checked = false;
	}
}

function updateCurrentVehicle(currentVehicle) {

	document.getElementById("currentVehicle").value = currentVehicle.id;
	document.getElementById("m:" + currentVehicle.id + ":current").checked = true;
}

var map;
var mapp;

var normalProj;
var waypoints = [];
var gpolys = [];
var myNode;
var newMarkers = [];
var markerDragged;
var isDragged;
var lastIndex;
var prevIndex;
var num;
var cleaned = false;
var geocoder = null;
var pointToRemove;
var color = ["#0000FF","#8A2BE2","#A52A2A","#DEB887","#5F9EA0","#7FFF00", "#D2691E","#6495ED","#DC143C", "#00FFFF","#00008B","#008B8B","#B8860B","#A9A9A9","#006400"];
var pointInterval = 30;

var markersClusterer = [];
var markerClusterer;
var defaultCenter = new GLatLng(-8.772083, -63.881636);
var polygonVehicle;
var polygonShowed;


function refreshMap() {
	if (markerClusterer != null) {
		markerClusterer.clearMarkers();
	}
	var zoom = 14; //parseInt(document.getElementById("zoom").value, 10);
	var size =  40;//parseInt(document.getElementById("size").value, 10);
	var style = "-1";// document.getElementById("style").value;
	zoom = zoom == -1 ? null : zoom;
	size = size == -1 ? null : size;
	style = style == "-1" ? null: parseInt(style, 10);
	markerClusterer = new MarkerClusterer(map, markersClusterer, {maxZoom: zoom, gridSize: size, styles: styles[style]});
}

function loadMaps() {

	alert('maps');
	if (document.getElementById("mapArea")) {
		createEditablePolygon();
		var pontos = document.getElementById('points').value;
		if(pontos){
			var points = pontos.split('$*@');
			for ( var j = 0; j < points.length; j++) {
				var p = points[j].split('#%');
				var mark = createMarker(p[0], p[1], p[2], p[3], p[4], p[5], p[6],p[7], p[8], true);
				mapa.addOverlay(mark);
			}
		}
	
	} else if (document.getElementById("mapEditablePoint")) {
		createEditablePoint();
	}

}

function calculateBoundsArea(){
	var bounds = new GLatLngBounds();
	var paths = polygon.getPaths();
	var path;
	for (var p = 0; p < paths.getLength(); p++) {
		path = paths.getAt(p);
		for (var i = 0; i < path.getLength(); i++) {
			bounds.extend(path.getAt(i));
		}
	}
	var zoom = mapa.getBoundsZoomLevel(bounds) > 20 ? 18 : mapa.getBoundsZoomLevel(bounds);
	zoom = zoom < 8 ? 14 : zoom;
	mapa.setCenter(bounds.getCenter(), mapa.getBoundsZoomLevel(bounds));
}

function showPointsOnMap(){
	map.clearOverlays();
	createMapPoints();
	var pontos = document.getElementById('points').value;
	var markers = new Array();
	if(pontos){
		var points = pontos.split('##$##');
		for (var j = 0; j < points.length; j++) {
			var p = points[j].split('##');
			if(p[0] && p[1]){
				var marker = new GLatLng(p[0], p[1]);
				markers.push(marker);
				var markerPoint = createMarkerSimple(p[0], p[1], p[2]);
				map.addOverlay(markerPoint);
			}
		}
	}
	calculateBoundsRoute(markers);
}

function initialize() {

	map = new google.maps.Map2(document.getElementById("mapp"));
	map.setCenter(new GLatLng(-3.7325370241018394, -38.51085662841797), 15);
	map.addControl(new GLargeMapControl());
	var icon = new GIcon(G_DEFAULT_ICON);
	icon.image = "http://chart.apis.google.com/chart?cht=mm&chs=24x32&chco=FFFFFF,008CFF,000000&ext=.png";
	var elem = document.getElementById('vehicleRoute').value;
	var points = elem.split('##$##');
	var markers = new Array();

	for (var j = 0; j < points.length; j++) {
		var p = points[j].split('##');
		var marker2 = new GMarker(new GLatLng(p[0], p[1]));
			//var markerPoint = createMarker(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7],p[8], p[9], false, param);
			markers.push(marker2);
	}
	markerClusterer = new MarkerClusterer(map, markers);
}

function clusterCurrentMarker(markers){
    markerClusterer = new MarkerClusterer(map, markers, {maxZoom: 14, gridSize: 20, styles: styles["-1"]});
}


function clicked(overlay, latlng) {
	mapp.clearOverlays();
	if (latlng) {
		geocoder.getLocations(latlng, function(addresses) {
			if(addresses.Status.code != 200) {
				alert("reverse geocoder failed to find an address for " + latlng.toUrlValue());
			} else {
				address = addresses.Placemark[0];
				var myHtml = address.address;
				//mapp.openInfoWindow(latlng, myHtml);
		    	mapp.setCenter(latlng, 15);
		        var marker = new GMarker(latlng);
		        mapp.addOverlay(marker);
				document.getElementById("lat").value = latlng.lat();
		        document.getElementById("lng").value = latlng.lng();
				document.getElementById("desc").value = myHtml;
			}
		});
	}
} 

function showAddress() {
	mapp.clearOverlays();
	var address = document.getElementById('endereco').value;
	if (geocoder) {
	  geocoder.getLatLng(
      address,
      function(point) {
    	  mapp.setCenter(point, 15);
          var marker = new GMarker(point);
          mapp.addOverlay(marker);
          document.getElementById("lat").value = point.lat();
          document.getElementById("lng").value = point.lng();
      }
    );
  }
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

/*
 */
function createMarkerRouteVehicle(lat, lng, modelo, placa, velocidade, odometro, ignicao, pprox, dist, dataHora, distptant, dircar,prefixo, se){
	/*var markerIcon = createIconDir(dircar); */
	var markerIcon = createIconMarker(2);
	var markerOptions = { icon:markerIcon };
	var marker = new GMarker(new GLatLng(lat, lng), markerOptions);
	GEvent.addListener(marker, "click", function() {
		var html = "<table style='width:280px'>" +
				"<td>" +
				"<b>Prefixo: </b>" + prefixo +
				"<br/><b>Modelo: </b>" + modelo + 
				"<br/><b>Placa: </b>" + placa + 
				"<br/><b>Ignição: </b>" + ignicao + 
				"<br/><b>Odômetro: </b>" + odometro.substring(0, odometro.length-2).replace(".", ",")  + 
				"<br/><b>Velocidade: </b> " +  velocidade.substring(0, velocidade.length-2).replace(".", ",") +  "(Km/h)"+
				"<br/><b>Ponto Próx.: </b>" + pprox + 
				"<br/><b>Dist.Pt Prox.: </b>" + dist.substring(0, dist.length-2) + "(m)" +
				"<br/><b>Dist.Pt Ant.: </b>" + distptant.substring(0, distptant.length-2) + "(m)" +
				"<br/><b>Data/Hora:</b>" + dataHora + 
				"</td></tr>" +
				"</table><br/><br/>";
		marker.openInfoWindowHtml(html);
	});
	return marker;
}

function createMarkerMonitoringRoute(lat, lng, placa, modelo, odometro, velocidade, ignicao, dataHora, prefixo){
	var markerIcon = createIconMarker(2);
	var markerOptions = { icon:markerIcon };
	var marker = new GMarker(new GLatLng(lat, lng), markerOptions);
	GEvent.addListener(marker, "click", function() {
		var html = "<table style='width:280px'>" +
		"<td>" +
		"<b>Prefixo: </b>" + prefixo +
		"<br/><b>Modelo: </b>" + modelo + 
		"<br/><b>Placa: </b>" + placa + 
		"<br/><b>Ignição: </b>" + ignicao + 
		"<br/><b>Odômetro: </b>" + odometro.substring(0, odometro.length-2).replace(".", ",")  + 
		"<br/><b>Velocidade: </b> " +  velocidade.substring(0, velocidade.length-2).replace(".", ",") +  "(Km/h)"+
		"<br/><b>Ponto Próx.: </b>" + pprox + 
		"<br/><b>Dist. Pt Prox.: </b>" + dist.substring(0, dist.length-2) + "(m)" +
		"<br/><b>Dist. Pt Ant.: </b>" + distptant.substring(0, distptant.length-2) + "(m)" +
		"<br/><b>Data/Hora: <b>" + dataHora + 
		"</td></tr>" +
		"</table><br/><br/>";
marker.openInfoWindowHtml(html);
	});
	return marker;
}

function createMarkerSimple(lat, lng, desc) {
	var marker;
	marker = new GMarker(new GLatLng(lat, lng));
	GEvent.addListener(marker, "click", function() {
	var html = "<table style='width:230px'>" +
				"<td><b>" + modelo + 
				"</b></td></tr>" +
				"</table><br/>";
		marker.openInfoWindowHtml(html);
	});
	return marker;
}

function createIconMarker(se) {
	var icon = new GIcon();
	if (se === 0){
		icon.image = "../images/cars/car22.png";
		img = "";
	} else if (se === 1) {
		icon.image = "../images/cars/car22.png";
	} else if (se === 2){
		icon.image = "../images/cars/car22.png";
	}
	icon.iconSize = new GSize(20, 20);
	icon.iconAnchor = new GPoint(16, 16);
	icon.infoWindowAnchor = new GPoint(16, 16);
	return icon;
}


//icon.image = "http://maps.google.com/mapfiles/dir_"+ 45*dircar +".png";
function createIcon() {
	var icon = new GIcon();
	icon.image = "http://maps.google.com/mapfiles/kml/pal4/icon15.png";
	icon.iconSize = new GSize(20, 20);
	icon.iconAnchor = new GPoint(16, 16);
	icon.infoWindowAnchor = new GPoint(16, 16);
	return icon;
}

function createIconDir(dircar) {

	var icon = new GIcon();
	icon.image = "../images/cars/car22.png";
	icon.iconSize = new GSize(32, 32);
	icon.iconAnchor = new GPoint(16, 16);
	icon.infoWindowAnchor = new GPoint(16, 16);
	return icon;
}

function createMapPoints(){

	map = new google.maps.Map2(document.getElementById("mapp"), {draggableCursor:"crosshair"});
	map.setCenter(new GLatLng(-3.7325370241018394, -38.51085662841797), 14);
	map.addControl(new GMapTypeControl());
	map.addControl(new GSmallMapControl());
	map.addControl(new GScaleControl());
	map.disableDoubleClickZoom();
	map.enableScrollWheelZoom();
	map.enableContinuousZoom();
	map.checkResize();
	geocoder = new GClientGeocoder();
	google.setOnLoadCallback(createMapPoints);
}

function createVeiculoRouteMap(){

	map = new google.maps.Map2(document.getElementById("mapRoute"), {draggableCursor:"crosshair"});
	map.setCenter(new GLatLng(-3.7325370241018394, -38.51085662841797), 14);
	map.addControl(new GMapTypeControl());
	map.addControl(new GSmallMapControl());
	map.addControl(new GScaleControl());
	map.disableDoubleClickZoom();
	map.enableScrollWheelZoom();
	map.enableContinuousZoom();
	map.checkResize();
	geocoder = new GClientGeocoder();
	google.setOnLoadCallback(createVeiculoRouteMap);
}

function createEditablePoint() {

	mapp = new google.maps.Map2(document.getElementById("mapEditablePoint"), {draggableCursor:"crosshair"});
	mapp.addControl(new GMapTypeControl());
	mapp.addControl(new GSmallMapControl());
	mapp.setMapType(G_HYBRID_MAP);
	
	geocoder = new GClientGeocoder();
	mapp.disableDoubleClickZoom();
	mapp.enableScrollWheelZoom();
	mapp.enableContinuousZoom();
	var lat = document.getElementById("lat");
	var lng = document.getElementById("lng");
	GEvent.addListener(mapp, "click", clicked);
	
	GEvent.addListener(mapp, "dblclick", function(overlay, point) {
	mapp.clearOverlays();
		if(point) {
			pointToRemove = point;
			lat.value = point.lat();
			lng.value = point.lng();
			mapp.setCenter(new GLatLng(lat.value, lng.value), 15);
			mapp.addOverlay(new GMarker(new GLatLng(lat.value, lng.value)));
		}
	});

	if (lat.value && lng.value) {
		mapp.setCenter(new GLatLng(lat.value, lng.value), 15);
		mapp.addOverlay(new GMarker(new GLatLng(lat.value, lng.value)));
	} else {
		mapp.setCenter(new GLatLng(-3.7325370241018394, -38.51085662841797), 14);
	}
	google.setOnLoadCallback(createEditablePoint);
}

function createEditablePolygon() {

	mapa = new google.maps.Map2(document.getElementById("mapArea"));
	mapa.addControl(new GMapTypeControl());
	mapa.addControl(new GSmallMapControl());
	mapa.disableDoubleClickZoom();
	mapa.setCenter(new GLatLng(-3.7325370241018394, -38.51085662841797), 15);
	mapa.setMapType(G_HYBRID_MAP);
	mapa.enableScrollWheelZoom();
	var polygon = null;
	
	var element = document.getElementById('polygon');
	var points = Array();
	
	if (element && element.value) {
		var coordinates = element.value.split(',');
		for (var i = 0; i < coordinates.length; i++) {
			var coordinate = coordinates[i].split(' ');
			var point = new GLatLng(parseFloat(coordinate[0]), parseFloat(coordinate[1]), true);
			points.push(point);
		}
		polygon = new GPolygon(points, '#A50E0C', 2, 0.7, '#A50E0C', 0.2);
	} else {
		polygon = new GPolygon([], '#A50E0C', 2, 0.7, '#A50E0C', 0.2);
		polygon.enableDrawing();
	}
	
	polygon.enableEditing({onEvent: "mouseover"});
	polygon.disableEditing({onEvent: "mouseout"});
	
	GEvent.addListener(polygon, "click", function(latlng, index) {
    	if (typeof index == "number") {
    		polygon.deleteVertex(index);
    	}
    });
	GEvent.addListener(polygon, "endline", function() {
    	polygon.setStrokeStyle({weight: 4});
    });
	
	GEvent.addListener(polygon, "lineupdated", function() {
		var element = document.getElementById('polygon');
		var encode = "";
		for (var i = 0; i < polygon.getVertexCount(); i++) {
			var p = polygon.getVertex(i);
			encode += p.lat() + " " + p.lng();
			if (i != (polygon.getVertexCount() -1)) {
				encode += ",";
			}
		}
		element.value = encode;
	});
	mapa.addOverlay(polygon);
	mapa.setCenter(polygon.getBounds().getCenter(), mapa.getBoundsZoomLevel(polygon.getBounds()));
	google.setOnLoadCallback(createEditablePolygon);
}

function getCirclePoints(center, radius){
	var circlePoints = Array();
	var searchPoints = Array();
	with (Math) {
		var rLat = (radius/3963.189) * (180/PI); // miles
		var rLng = rLat/cos(center.lat() * (PI/180));
		for (var a = 0 ; a < 361 ; a++ ) {
			var aRad = a*(PI/180);
			var x = center.lng() + (rLng * cos(aRad));
			var y = center.lat() + (rLat * sin(aRad));
			var point = new GLatLng(parseFloat(y), parseFloat(x),true);
			circlePoints.push(point);
			if (a % pointInterval == 0) {
				searchPoints.push(point);
			}
		}
	}
	searchPolygon = new GPolygon(hcirclePoints, '#0000ff', 1, 1, '#0000ff', 0.2);	
	map.addOverlay(searchPolygon);
	map.setCenter(searchPolygon.getBounds().getCenter(), map.getBoundsZoomLevel(searchPolygon.getBounds()));
	return searchPoints;
}


function calculateBoundsRoute(markers){
	var zoom = 15;
	if(markers){
		if (markers.length > 0) {
			var mbr = new GLatLngBounds();
			for(var i = 0; i < markers.length; i++) {
				mbr.extend(markers[i]);
			}
			zoom = (map.getBoundsZoomLevel(mbr) > 20  ? 18 : map.getBoundsZoomLevel(mbr));
			zoom = zoom < 8 ? 14 : zoom;
			map.setCenter(mbr.getCenter(), zoom);
		}
	} else {
		zoom = zoom < 8 ? 14 : zoom;
		map.setCenter(defaultCenter, zoom);
	}
}

function dragMarker() {
	if (isDragged == 2) {
		markerDragged = this;
		return;
	}
	isDragged = 2;
	if (markerDragged) {
		marker = markerDragged;
		markerDragged = false;
	} else {
		marker = this;
	}
	lastIndex = marker.MyIndex;
	var point = marker.getPoint();
	if (lastIndex > 0) {
		if (lastIndex < waypoints.length - 1) {
			prevIndex = lastIndex - 1;
			GDir2.loadFromWaypoints([waypoints[lastIndex - 1].getPoint(), point.toUrlValue(6), waypoints[lastIndex + 1].getPoint()],{getPolyline:true});
		} else {
			prevIndex = -1; lastIndex = lastIndex - 1;
			GDir2.loadFromWaypoints([waypoints[lastIndex].getPoint(),point.toUrlValue(6)],{getPolyline:true});
		}
	} else if (waypoints.length > 1) {
		prevIndex = -1;
		GDir2.loadFromWaypoints([point.toUrlValue(6),waypoints[1].getPoint()],{getPolyline:true});
	}
}

function getProximity(mouseLatLng, marker) {
	var dist, zoom;
	if (routeNodes.length == 0) {
		dist = 0;
		zoom = map.getZoom();
		if (gpolys.length > 0 && gpolys[0].getVertexCount() > 0 )
			routeNodes.push(normalProj.fromLatLngToPixel(gpolys[0].getVertex(0), zoom));
		for (var i = 0; i < gpolys.length; i++) {
			dist += gpolys[i].getLength();
			for (var j = 1; j < gpolys[i].getVertexCount(); j++) {
				var point = normalProj.fromLatLngToPixel(gpolys[i].getVertex(j), zoom)
				point.MyIndex = i;
				routeNodes.push(point);
			}
		}
		var panel = document.getElementById('panel');
		if (panel) {
			panel.innerHTML = (dist/1000).toFixed(1) + " km";
		}
	}

	if (!mouseLatLng || routeNodes.length <= 1 || isDragged > 0){
		return;
	}

	zoom = map.getZoom();
	alert(zoom);
	var mousePx = normalProj.fromLatLngToPixel(mouseLatLng, zoom);
	var minDist = 999;
	var minX = mousePx.x;
	var minY = mousePx.y;
	if (routeNodes.length > 1) {
		var x,y, d1,d2,d;
		var dx = mousePx.x - routeNodes[0].x;
		var dy = mousePx.y - routeNodes[0].y;
		d2 = dx*dx + dy*dy;
		for (var n = 0 ; ++n < routeNodes.length; ) {
			d1 = d2;
			x = routeNodes[n].x; dx = mousePx.x - x;
			y = routeNodes[n].y; dy = mousePx.y - y;
			d2 = dx*dx + dy*dy;
			dx = x - routeNodes[n-1].x;
			dy = y - routeNodes[n-1].y;
			d = dx*dx + dy*dy;
			var u = ((mousePx.x - x) * dx + (mousePx.y - y) * dy) / d;
			x += (u*dx);
			y += (u*dy);
			dx = mousePx.x - x;
			dy = mousePx.y - y;
			dist = dx*dx + dy*dy;
			if ((d1 - dist) + (d2 - dist) > d) {
				if (d1 < d2) {
					dist = d1;
					x = routeNodes[n-1].x;
					y = routeNodes[n-1].y;
				} else {
					dist = d2;
					x = routeNodes[n].x;
					y = routeNodes[n].y;
				}
			};
			if (minDist > dist) {
				minDist = dist;
				minX = x;
				minY = y;
				myNode.MyIndex = routeNodes[n].MyIndex;
			}
		}var mbr = new GLatLngBounds();
		for(var i = 0; i < markerRoute.length; i++) {
			mbr.extend(markerRoute[i]);
		}
		map.setCenter(mbr.getCenter(), map.getBoundsZoomLevel(mbr));

		if (minDist > 25) {
			myNode.hide();
		} else {
			for (n = waypoints.length; --n >= 0;) {
				var markerPx = normalProj.fromLatLngToPixel(waypoints[n].getPoint(), zoom);
				dx = markerPx.x - minX;
				dy = markerPx.y - minY;
				if (dx*dx + dy*dy < 25) {
					myNode.hide();
					return;
				}
			}
			myNode.setPoint(normalProj.fromPixelToLatLng(new GPoint(minX, minY), zoom));
			myNode.show();
		}
	}
}

function openKML( url ) {
	if (GBrowserIsCompatible()) {
		var geoXml = new GGeoXml( url );
		kmlMap = new google.maps.Map2(document.getElementById('kml_map'));
		kmlMap.setCenter(new GLatLng(-3.7325370241018394, -38.51085662841797), 13);
		kmlMap.addControl(GSmallMapControl);
		kmlMap.addControl(GMapTypeControl);
		kmlMap.addOverlay(geoXml);
	} else {
		window.alert('Navegador não compatível com o Google Maps.\nFavor instalar uma versão mais recente\nou utilizar outro navegador.');
	}
}

var styles = [[{
    url: '../images/people35.png',
    height: 35,
    width: 35,
    opt_anchor: [16, 0],
    opt_textColor: '#FF00FF'
  },
  {
    url: '../images/people45.png',
    height: 45,
    width: 45,
    opt_anchor: [24, 0],
    opt_textColor: '#FF0000'
  },
  {
    url: '../images/people55.png',
    height: 55,
    width: 55,
    opt_anchor: [32, 0]
  }],
  [{
    url: '../images/conv30.png',
    height: 27,
    width: 30,
    anchor: [3, 0],
    textColor: '#FF00FF'
  },
  {
    url: '../images/conv40.png', 
    height: 36,
    width: 40,
    opt_anchor: [6, 0],
    opt_textColor: '#FF0000'
  },
  {
    url: '../images/conv50.png',
    width: 50,
    height: 45,
    opt_anchor: [8, 0]
  }],
  [{
    url: '../images/heart30.png',
    height: 26,
    width: 30,
    opt_anchor: [4, 0],
    opt_textColor: '#FF00FF'
  },
  {
    url: '../images/heart40.png', 
    height: 35,
    width: 40,
    opt_anchor: [8, 0],
    opt_textColor: '#FF0000'
  },
  {
    url: '../images/heart50.png',
    width: 50,
    height: 44,
    opt_anchor: [12, 0]
  }]];

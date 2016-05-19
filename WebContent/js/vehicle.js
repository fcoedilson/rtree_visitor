var currentVehicle;
var currentRastro;
var selected;
var vehicles = new Array();


function Vehicle(id, name, type, lat, lng, visible, select, rastro, descPontoProx, 
		distPontoProx, velocidade, dataHora, placa, ignicao, odometro, distptant, categoria, violouCerca) {

	this.id = id;
	this.visible = visible;

	this.panTo = function() {
		map.panTo(this.center);
		map.setCenter(this.center, 15);
	}

	this.show = function() {
		if (!this.visible) {
			map.addOverlay(this.marker);
			this.visible = true;
			this.select.checked = true;
		}
		if (currentRastro) {
			map.removeOverlay(currentRastro);
			currentRastro = null;
		}
		if (this.rastro && currentVehicle && (currentVehicle.id == this.id)) {
			currentRastro = this.rastro;
			map.addOverlay(currentRastro);
		}
	}

	this.hide = function() {
		if (this.visible) {
			map.removeOverlay(this.marker);
			if (this.rastro) {
				map.removeOverlay(this.rastro);
			}
			this.visible = false;
			this.select.checked = false;
		}
	}

	this.showAndCenter = function() {
		this.show();
		this.panTo();
	}

	this.isVisible = function() {
		return this.visible;
	}

	this.updateMarker = function() {

		var newMarker = createMarkerVeiculo(this.id, this.center, this.name, this.type,  this.descPontoProx, this.distPontoProx, this.velocidade, 
				this.dataHora, this.placa, this.ignicao, this.odometro, this.distptant, this.categoria, this.violouCerca);
		if (this.isVisible()) {
			if (this.marker) {
				map.removeOverlay(this.marker);
			}
			map.addOverlay(newMarker);
		}
		this.marker = newMarker;
	}

	this.updateRastro = function(newRastro) {
		if (this.isVisible()) {
			if (this.rastro) {
				map.removeOverlay(this.rastro);
			}
			if (newRastro && currentVehicle && (currentVehicle.id == this.id)) {
				if (currentRastro) {
					map.removeOverlay(currentRastro);
				}
				currentRastro = newRastro;
				map.addOverlay(currentRastro);
			}
		}
		this.rastro = newRastro;
	}

	this.update = function(name, type, lat, lng, select, rastro, descPontoProx, distPontoProx, velocidade, dataHora, placa, ignicao, odometro, distptant,categoria, violouCerca) {
		this.name = name;
		this.descPontoProx = descPontoProx;
		this.distPontoProx = distPontoProx;
		this.velocidade = velocidade;
		this.dataHora = dataHora;
		this.placa = placa;
		this.categoria = categoria;
		this.violouCerca = violouCerca;
		this.ignicao = ignicao;
		this.odometro = odometro;
		this.distptant = distptant;
		this.type = type;
		this.lat = lat;
		this.lng = lng;
		this.select = select;
		this.select.checked = this.visible;
		this.center = new GLatLng(this.lat, this.lng);
		this.updateRastro(rastro);
		this.updateMarker();
	}
	this.update(name, type, lat, lng, select, rastro, descPontoProx, distPontoProx, velocidade, dataHora, placa, ignicao, odometro, distptant, categoria, violouCerca);
}

function updateVehicles() {
	var i = 0;
	var rastro;
	var m = document.getElementById("m:" + i);
	var r = document.getElementById("r:" + i);
	//var myRadio = jQuery("input[name='m:current']");
	while (m) {
		var v = m.value.split("|");
		rastro = null;
		if (r && r.value && r.value.length > 0) {
			var coords = r.value.split("|");
			if (coords.length > 0) {
				var rastroArray = new Array(coords.length);
				for (var j = 0; j < coords.length; j++) {
					var coord = coords[j].split(' ');
					rastroArray[j] = new GLatLng(coord[0],coord[1]);
				}
				rastro = new GPolyline(rastroArray, '#008000', 7, 0.7);
			}
		}
		
		var vehicle = retrieveVehicle(v[0]);
		var select = document.getElementById("m:" + i + ":select");

		if (vehicle) {
			vehicle.update(v[1], v[2], v[3], v[4], select, rastro, v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14]);
		} else {
			vehicles.push(new Vehicle(v[0], v[1], v[2], v[3], v[4], true, select, rastro, v[5], v[6], v[7], v[8], v[9],v[10],v[11],v[12],v[13],v[14]));
		}

		i++;
		m = document.getElementById("m:" + i);
		r = document.getElementById("r:" + i);
	}
}

function retrieveVehicle(id) {
	for(var i = 0; i < vehicles.length; i++) {
		if (vehicles[i].id == id) {
			return vehicles[i];
		}
	}
}

function showAllVehicles() {
	for(var i = 0; i < vehicles.length; i++) {
		vehicles[i].show();
	}
}

function hideAllVehicles() {
	for(var i = 0; i < vehicles.length; i++) {
		vehicles[i].hide();
	}
}

function removeRastro() {
	if (currentRastro) {
		map.removeOverlay(currentRastro);
	}
	currentRastro = null;
}

function createMarkerVeiculo(idveic, latlng, modelo, tipo, dscpprox, distpprox, velocidade, dataHora, placa, ignicao, odometro, distpant, categoria, violouCerca) {
	var markerIcon = createIconVeiculo(categoria);
	var markerOptions = { icon:markerIcon };
	var marker = new GMarker(latlng, markerOptions);
	var ign = (ignicao == 'true') ? 'ON' : 'OFF';
	var cerca = (violouCerca == 'true' ? 'VIOLADA' : 'NORMAL');
	GEvent.addListener(marker, "click", function() {
		var html = 
		"<table style='width:280px'>" +
		"<td>" +
			"<b>Prefixo: </b>" + categoria + "" +
			"<br/><b>Placa: </b>" + placa +"(" + modelo + ")" +
			"<br/><b>Ignição: </b>" + ign + 
			"<br/><b>Cerca Virtual: </b>" + cerca + 
			"<br/><b>Odômetro: </b>" + odometro.substring(0, odometro.length-2).replace(".", ",")  + 
			"<br/><b>Veloc: </b> " +  velocidade.substring(0, velocidade.length-2).replace(".", ",") +  "(Km/h)"+
			"<br/><b>Pt Próx.: </b>" + dscpprox + 
			"<br/><b>Dist. Pt Prox: </b>" + distpprox.substring(0, distpprox.length-2) + "(m)" +
			"<br/><b>Dist. Pt Ant.: </b>" + distpant.substring(0, distpant.length-2) + "(m)" +
			"<br/><b>Data/Hora: </b>" + dataHora + 
		"</td>" +
		"<tr>" +		
		"<td>" +
		   "<label>Mostrar Rastro</label>" +
		    "<input type='checkbox' id='veicrastro' onclick='showAndSelect("+idveic+")'/>" +
		"</td>" +
		"</tr>" +
		"</table><br/>";
		marker.openInfoWindowHtml(html);
	});
	return marker;
	// (this.center, this.name, this.type,  this.descPontoProx, this.distPontoProx, this.velocidade, this.dataHora, this.placa);
}

function createIconVeiculo() {
	var icon = new GIcon();
	icon.image = "../images/cars/car22.png";
	icon.iconSize = new GSize(32, 32);
	icon.iconAnchor = new GPoint(16, 16);
	icon.infoWindowAnchor = new GPoint(16, 16);
	return icon;
}

function createIconVeiculo(prefix) {
	var icon = new GIcon();
	icon.image = "../image?id=" + prefix;
	icon.iconSize = new GSize(32, 32);
	icon.iconAnchor = new GPoint(16, 16);
	icon.infoWindowAnchor = new GPoint(16, 16);
	return icon;
}

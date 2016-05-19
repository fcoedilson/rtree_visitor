package br.edu.fa7.bean;

import java.util.List;

import org.postgis.LinearRing;
import org.postgis.Point;
import org.postgis.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.edu.fa7.entity.Area;
import br.edu.fa7.service.AreaService;


@Scope("session")
@Component("areaBean")
public class AreaBean extends EntityBean<Integer, Area> {

	@Autowired
	private AreaService service;


	private Polygon polygon;
	private String geoms = new String("");
	private String pontos = new String("");;

	@Override
	protected Area createNewEntity() {
		return new Area();
	}

	@Override
	protected Integer retrieveEntityId(Area entity) {
		return entity.getId();
	}

	@Override
	protected AreaService retrieveEntityService() {
		return this.service;
	}

	@Override
	public String prepareUpdate() {
		this.polygon = this.entity.getGeometry();
		return super.prepareUpdate();
	}

	@Override
	public String prepareSave() {
		this.polygon = null;
		return super.prepareSave();
	}

	@Override
	public String save() {
		this.entity.setGeometry(polygon);
		return super.save();
	}

	@Override
	public String search() {
		return super.search();
	}

	@Override
	public String update() {
		this.entity.setGeometry(this.polygon);
		return super.update();
	}

	public String updateArea() {
		return super.update();
	}

	public boolean isAreaVeiculosState() {
		return AREA_VEICULOS.equals(getCurrentState());
	}

	public String getPolygon() {
		return encodePolygon(polygon);
	}

	public void setPolygon(String polygon) {
		this.polygon = decodePolygon(polygon);
	}

	private String encodePolygon(Polygon polygon) {
		if (polygon != null) {
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < polygon.numPoints(); i++) {
				result.append(polygon.getPoint(i).x);
				result.append(" ");
				result.append(polygon.getPoint(i).y);

				if (i != (polygon.numPoints() - 1)) {
					result.append(",");
				}
			}

			return result.toString();
		}
		return null;
	}

	/**
	 * 
	 * decodifica uma string de pontos em um pol�gono
	 * 
	 * @param mapArea
	 * @return
	 */
	private Polygon decodePolygon(String mapArea) {
		if (mapArea != null) {
			String[] coords = mapArea.split(",");
			Point[] points = new Point[coords.length];
			for (int i = 0; i < coords.length; i++) {
				String[] latlng = coords[i].split(" ");
				Double lat = Double.valueOf(latlng[0].trim());
				Double lng = Double.valueOf(latlng[1].trim());
				points[i] = new Point(lat, lng);
			}
			if (points[0].x != points[coords.length - 1].x
					|| points[0].y != points[coords.length - 1].y) {
				Point[] newCoordinates = new Point[points.length + 1];
				System.arraycopy(points, 0, newCoordinates, 0, points.length);
				points = newCoordinates;
				points[points.length - 1] = new Point(points[0].x, points[0].y);
			}
			LinearRing linearRing = new LinearRing(points);
			LinearRing[] l = new LinearRing[1];
			l[0] = linearRing;
			return new Polygon(l);
		}
		return null;
	}


	public List<Area> getAreas() {
		return service.retrieveAll();
	}

	public String getGeoms() {
		return geoms;
	}

	public void setGeoms(String geoms) {
		this.geoms = geoms;
	}

	public String getPontos() {
		return pontos;
	}

	public void setPontos(String pontos) {
		this.pontos = pontos;
	}
}

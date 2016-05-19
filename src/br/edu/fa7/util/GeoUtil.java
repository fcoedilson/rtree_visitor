package br.edu.fa7.util;

import br.edu.fa7.entity.Ponto;
import br.edu.fa7.entity.Visitor;
import spatialindex.rtree.RTree;
import spatialindex.spatialindex.Point;
import spatialindex.spatialindex.Region;


public class GeoUtil {

	public static void updatePontoProximo(Ponto novo, RTree prioridades, RTree pontos, Float distance) {

		Ponto ponto = novo.getPontoProximo();
		Point point = new Point(new double[]{ponto.getLng(), ponto.getLat()});
		Visitor visitor = new Visitor();
		Ponto pontoProximo = new Ponto();
		pontoProximo.setDistance(Double.MAX_VALUE);
		if (prioridades != null) {
			prioridades.nearestNeighborQuery(1, point, visitor);
			pontoProximo.setId(visitor.data.getIdentifier());
			pontoProximo.setDistance(distancia(point, (Region) visitor.data.getShape()));
		}
		visitor = new Visitor();
		if (pontoProximo.getDistance() > distance) {
			pontos.nearestNeighborQuery(1, point, visitor);
			double distancia = distancia(point, (Region) visitor.data.getShape());
			if (distancia < pontoProximo.getDistance()) {
				pontoProximo.setId(visitor.data.getIdentifier());
				pontoProximo.setDistance(distancia);
			}
		}
		novo.setPontoProximo(pontoProximo);
	}

	public static void updatePontoProximo(Ponto novo, RTree pontos, Float distance) {

		try {
			Ponto ponto = novo.getPontoProximo();
			Point point = new Point(new double[]{ponto.getLng(), ponto.getLat()});
			Visitor visitor = new Visitor();
			Ponto pontoProximo = new Ponto();
			pontoProximo.setDistance(Double.MAX_VALUE);

			visitor = new Visitor();
			if (pontoProximo.getDistance() > distance) {
				pontos.nearestNeighborQuery(1, point, visitor);
				double distancia = distancia(point, (Region) visitor.data.getShape());
				if (distancia < pontoProximo.getDistance()) {
					pontoProximo.setId(visitor.data.getIdentifier());
					pontoProximo.setDistance(distancia);
				}
			}
			novo.setPontoProximo(pontoProximo);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static Double distancia(Point p1, Region  p2) {
		double lat1 = p1.getCoord(0) / (180D / (22D / 7D));
		double lng1 = p1.getCoord(1) / (180D / (22D / 7D));
		double lat2 = p2.getCenter()[0] / (180D / (22D / 7D));
		double lng2 = p2.getCenter()[1] / (180D / (22D / 7D));
		return (double) (long) (6378800D * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lng2 - lng1)));
	}
}
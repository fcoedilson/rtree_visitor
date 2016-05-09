package br.org.sigafrota.util;

/*
# Copyright 2010 - Prefeitura Municipal de Fortaleza
#
# Este arquivo é parte do Sistema Integrado de Gestão e Acompanhamento de Frotas
# SIGAFrota
# 
# O SIGAFrota é um software livre; você pode redistribui-lo e/ou modifica-lo
# dentro dos termos da Licença Pública Geral GNU como publicada pela
# Fundação do Software Livre (FSF); na versão 2 da Licença.
#
# Este programa é distribuido na esperança que possa ser util, mas SEM
# NENHUMA GARANTIA; sem uma garantia implicita de ADEQUAÇÂO a qualquer
# MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU
# para maiores detalhes.
#
# Você deve ter recebido uma cópia da Licença Pública Geral GNU, sob o
# título "LICENCA.txt", junto com este programa, se não, escreva para a
# Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,
*/

import spatialindex.rtree.RTree;
import spatialindex.spatialindex.Point;
import spatialindex.spatialindex.Region;
import br.org.sigafrota.entity.Ponto;
import br.org.sigafrota.entity.Transmissao;
import br.org.sigafrota.entity.Visitor;

public class GeoUtil {

	public static void updatePontoProximo(Transmissao transmissao, RTree prioridades, RTree pontos) {

		Ponto ponto = transmissao.getPontoProximo();
		Point point = new Point(new double[]{ponto.getX(), ponto.getY()});
		Visitor visitor = new Visitor();
		Ponto pontoProximo = new Ponto();
		pontoProximo.setDistancia(Float.MAX_VALUE);
		if (prioridades != null) {
			prioridades.nearestNeighborQuery(1, point, visitor);
			pontoProximo.setPontoId(visitor.data.getIdentifier());
			pontoProximo.setDistancia(distancia(point, (Region) visitor.data.getShape()));
		}
		visitor = new Visitor();
		if (pontoProximo.getDistancia() > 50) {
			pontos.nearestNeighborQuery(1, point, visitor);
			float distancia = distancia(point, (Region) visitor.data.getShape());
			if (distancia < pontoProximo.getDistancia()) {
				pontoProximo.setPontoId(visitor.data.getIdentifier());
				pontoProximo.setDistancia(distancia);
			}
		}
		transmissao.setPontoProximo(pontoProximo);
	}

	public static void updatePontoProximo(Transmissao transmissao, RTree pontos) {

		try {
			Ponto ponto = transmissao.getPontoProximo();
			Point point = new Point(new double[]{ponto.getX(), ponto.getY()});
			Visitor visitor = new Visitor();
			Ponto pontoProximo = new Ponto();
			pontoProximo.setDistancia(Float.MAX_VALUE);

			visitor = new Visitor();
			if (pontoProximo.getDistancia() > 50) {
				pontos.nearestNeighborQuery(1, point, visitor);
				float distancia = distancia(point, (Region) visitor.data.getShape());
				if (distancia < pontoProximo.getDistancia()) {
					pontoProximo.setPontoId(visitor.data.getIdentifier());
					pontoProximo.setDistancia(distancia);
				}
			}
			transmissao.setPontoProximo(pontoProximo);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static Float distancia(Point p1, Region  p2) {
		double lat1 = p1.getCoord(0) / (180D / (22D / 7D));
		double lng1 = p1.getCoord(1) / (180D / (22D / 7D));
		double lat2 = p2.getCenter()[0] / (180D / (22D / 7D));
		double lng2 = p2.getCenter()[1] / (180D / (22D / 7D));
		return (float) (long) (6378800D * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lng2 - lng1)));
	}
}
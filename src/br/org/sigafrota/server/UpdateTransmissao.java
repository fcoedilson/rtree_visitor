package br.org.sigafrota.server;

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

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import spatialindex.rtree.RTree;
import br.org.sigafrota.entity.Ponto;
import br.org.sigafrota.entity.Transmissao;
import br.org.sigafrota.util.GeoUtil;
import br.org.sigafrota.util.GprsPostgresql;

public class UpdateTransmissao {

	public static final Logger log = Logger.getLogger(UpdateTransmissao.class);

	public static void main(String[] args) {

		long timeIni;
		long timeFim;
		Connection conn = null;
		while (true) {
			timeIni = System.currentTimeMillis();
			timeFim = System.currentTimeMillis();
			try {

				log.info("Iniciando conexão Postgresql...");
				conn =  GprsPostgresql.getConn();
				log.info("Conexão Postgres: OK");
				log.info("Iniciando criação do index...");
				
				List<Ponto> pontos = GprsPostgresql.retrievePontos();
				Map<Integer, RTree> pontosMap = GprsPostgresql.findPontos(pontos, 0);
				log.info("Criação do index: OK");
				
				Date ini = new Date();
				System.out.println("Begin: " + DateUtil.parseAsString("dd/MM/yyyy HH:mm:ss", ini));
				while ((timeFim - timeIni) < (20 * DateUtil.MINUTE_IN_MILLIS)) {
					List<Transmissao> transmissoes = GprsPostgresql.findTransmissoes(conn);
					for (Transmissao transmissao : transmissoes) {
						//GeoUtil.updatePontoProximo(transmissao, pontos.get(0), pontos.get(0));
						GeoUtil.updatePontoProximo(transmissao, pontosMap.get(0));
						GprsPostgresql.updateTransmissao(conn, transmissao);
					}
					System.out.println("[Total de transmissões atualizadas: " + transmissoes.size() +" ]");
					sleep(9000);
					timeFim = System.currentTimeMillis();
				}
				Date fim = new Date();
				System.out.println("End: " + DateUtil.parseAsString("dd/MM/yyyy HH:mm:ss", fim));
				System.out.println("Duration: " + DateUtil.tempoEntreDatasLong(ini, fim));
				System.out.println(DateUtil.parseDateAsString(new Date()) + "[Atualização bem sucedida!!]");	

			} catch (Exception e) {
				log.error(e.getMessage(), e);
				System.out.println(DateUtil.parseDateAsString(new Date()) + "[Não foi possivel atualizar transmissões]");			
			} finally {
				GprsPostgresql.close(conn);
			}
			log.info("[Reiniciando a conexão e recriando o index conter...]");
		}
	}

	private static void sleep(long time) {

		try {

			Thread.sleep(time);

		} catch (InterruptedException e) {}
	}
}
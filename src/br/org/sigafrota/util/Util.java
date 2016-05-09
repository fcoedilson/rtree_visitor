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

public class Util {

	public static final String INSERT_TRANSMISSAO = "INSERT INTO TB_TRANSMISSAO(codveiculo, x, y,velocidade, data_transmissao) VALUES (?, ?, ?, ?, ?)";



	public static final String FIND_VEICULOS = "SELECT V.CODVEICULO FROM VEICULO V";

	public static final String FIND_VEICULOS_SEM_TRANSMISSAO =
			" SELECT V.CODVEICULO, V.DSC_VEICULO, P.DESC_PONTO, U.DIST_PONTO, U.DATA_TRANSMISSAO " +
					" FROM ULTIMA_TRANSMISSAO U " +
					" INNER JOIN VEICULO V ON U.SEQ_VEICULO = V.SEQ_VEICULO " +
					" INNER JOIN PONTO P ON U.SEQ_PONTO = P.SEQ_PONTO " +
					" WHERE TIMESTAMPDIFF(HOUR, DATA_TRANSMISSAO, now()) > 24";


	public static final String INSERT_TRANSMISSAO_ALLFIELDS = "INSERT INTO TB_TRANSMISSAO VALUES (0, ?, 0, GeomFromText(?), 0, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0, ?)";
	

}

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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ReceiverSMS  {

	public static void main(String[] args) {

		try {
			Class.forName("org.postgresql.Driver").newInstance();
			Statement smnt     = null;;
			String dbHost      = "localhost";
			String database    = "pmf_desv";
			String dbUsername  = "sgf";
			String dbPassword  = "sgf";
			String smsReceiver = "+558591482315";
			String message  = "Hello world";

			String dbUrl = "jdbc:postgresql://"+ dbHost +"/" + database +"";

			Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			smnt = conn.createStatement();

			String sqlInsert =
					"INSERT INTO smsapp.messageout (receiver,msg,status) VALUES '" + smsReceiver + "','"+ message +"','send')";

			if(smnt.executeUpdate(sqlInsert) != 0){
				System.out.println("OK");

			}
			else{
				System.out.println("ERROR");
			}

			smnt.close();
			conn.close();
		}catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
		}
	}

}


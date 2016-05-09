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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Date;

import br.org.sigafrota.util.GprsPostgresql;


public class Servidor implements Runnable {

	private static final int SERVER_PORT = 9001;
	private Socket s;

	public Servidor(Socket s) {
		this.s = s;
	}

	@Override
	public void run() {

		try {
			InputStream is = s.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read;
			byte[] bytes = new byte[4096];
			while ((read = is.read(bytes)) != -1) {
				baos.write(bytes, 0, read);
			}
			String dadorecebido = baos.toString();
			if(!dadorecebido.equals("")){
				Date dataleitura = new Date();
				String subresult = dadorecebido.substring(0,22);
				String[] subarray =  subresult.split(",");
				String imei = subarray[0].replace("imei:", "");
				System.out.println(imei);
				Integer codveiculo = GprsPostgresql.findveiculoByImei(imei);
				
				if(codveiculo > 0 || codveiculo == null){
					GprsPostgresql.gravarDados(dadorecebido, imei, dataleitura);
					GprsPostgresql.gravarDadosFormatados(dadorecebido, imei, codveiculo, dataleitura);
				} else {
					String message = "imei:" + imei + " não associado com veículo";
					GprsPostgresql.gravarDados(dadorecebido, imei, dataleitura);
					GprsPostgresql.gravarErroMessage(message, new Date());
					System.out.println();
				}
			}

			OutputStream out = s.getOutputStream();

			if (bytes != null) {
				try {
					out.write(bytes, 0, bytes.length);
				} catch (IOException e) {
					GprsPostgresql.gravarErroMessage(e.toString(), new Date());
					System.out.println(e.getMessage());
				}
			} 
			out.close();

		} catch (Exception e) {
			GprsPostgresql.gravarErroMessage(e.toString(), new Date());
			System.out.println("[Erro ocorrido na Thread: " + e.getMessage() + "]");
		}
	}

	public static void teste() throws SQLException{
		int id = GprsPostgresql.consultarMaxId();
		String result = 
				"imei:353451044448155,tracker,1109071859,8596027202,F,215938.000,A,0344.7387,S,03832.9530,W,0.00,,;" +
				"##,imei:353451044448155,A;" +
				"imei:353451044448155,tracker,1109071859,8596027202,F,215946.000,A,0344.7390,S,03832.9525,W,0.28,137.99,;" +
				"imei:353451044448155,tracker,1109071859,8596027202,F,215956.000,A,0344.7387,S,03832.9520,W,0.42,8.40,;" +
				"##,imei:353451044448155,A;" +
				"imei:353451044448155,tracker,1109071900,8596027202,F,220006.000,A,0344.7339,S,03832.9524,W,0.94,343.98,;" +
				"imei:353451044448155,tracker,1109071900,8596027202,F,220016.000,A,0344.7327,S,03832.9527,W,0.37,173.45,;" +
				"##,imei:353451044448155,A;" +
				"imei:353451044448155,tracker,1109071900,8596027202,F,220026.000,A,0344.7344,S,03832.9534,W,0.20,114.12,;" +
				"imei:353451044448155,tracker,1109071900,8596027202,F,220036.000,A,0344.7358,S,03832.9533,W,0.07,347.73,;" +
				"##,imei:353451044448155,A;" +
				"imei:353451044448155,tracker,1109071900,8596027202,F,220046.000,A,0344.7383,S,03832.9538,W,0.21,144.62,;"; 
		///GprsPostgresql.consultar(id);
		String subresult = result.substring(0,22);
		String[] subarray =  subresult.split(",");
		String imei = subarray[0].replace("imei:", "");
		int codveiculo = GprsPostgresql.findveiculoByImei(imei);
		GprsPostgresql.gravarDadosFormatados(result, imei, codveiculo, new Date());
	}

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {

		ServerSocket server;
		Socket client;

		try {
			server = new ServerSocket(SERVER_PORT);
			System.out.println("[Server is ready]");
			while ((client = server.accept()) != null) {
				String host = client.getInetAddress().getHostName();
				System.out.println("[Connected to " + host + "]");
				Servidor reader = new Servidor(client);
				Thread t = new Thread(reader);
				t.setDaemon(true);
				t.start();
			}
		} catch (Exception e) {
			Date data = new Date();
			GprsPostgresql.gravarErroMessage(e.toString(), data);
			System.out.println(e.getMessage());
		}
	}

}

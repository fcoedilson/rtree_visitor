package br.org.sigafrota.client;
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
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class Cliente {

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {

		String host;
		//host = "201.49.44.41";
		//host = "174.142.214.78";
		//host = "172.30.116.21";
		host = "177.19.67.44";
		int port = 80;
		int opt = 0;

		while (opt != 1) {
			opt = JOptionPane.showOptionDialog(null, null, "Opções", JOptionPane.DEFAULT_OPTION, 
																	 JOptionPane.PLAIN_MESSAGE, null, 
																	 new Object[]{"Próxima Mensagem", "Fechar"}, "Próxima Mensagem");

			if (opt == 0) {
				Socket s = null;
				try {
					s = new Socket(host, port);
					OutputStream out = s.getOutputStream();
                    System.out.println(out);
					//int id = Postgresql.consultarMaxId() + 1;
					//out.write(id);
					//out.flush();
					

					byte[] bytes = new byte[4096];
					InputStream is = s.getInputStream();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int read;
					while ((read = is.read(bytes)) != -1) {
						baos.write(bytes, 0, read);
					}
					System.out.println(baos.toByteArray());
					//Postgresql.gravar(baos.toByteArray());

				} catch (Exception e) {
					System.out.println(e.getMessage());
				} finally {
					if (s != null) {
						s.close();
						s = null;
					}
				}
			}
		}

	}

}

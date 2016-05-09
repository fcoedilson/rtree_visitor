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

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {

	public static void sendEmail(String to, String subject, String msg) throws Exception  {

		Properties props = System.getProperties();
		props.put("mail.smtp.host", "smtp.google.com");
		props.put("mail.smtp.auth", "true");

		Authenticator authenticator = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("fcoedilson", "otm@2015");
			}
		};

		Session session = Session.getInstance(props, authenticator);

		MimeMessage mimeMessage = new MimeMessage(session);
		mimeMessage.setFrom(new InternetAddress("fcoedilson@gmail.com"));
		mimeMessage.setRecipients(Message.RecipientType.TO, new InternetAddress[] {new InternetAddress(to)});
		mimeMessage.setSubject(subject);
		mimeMessage.setText(msg);
		mimeMessage.setSentDate(new Date());
		Transport.send(mimeMessage);
	}
}
package br.edu.fa7.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Util{

	public static final String MAIL = "fcoedilson@gmail.com";
	public static final String USUARIO_LOGADO = "usuarioLogado";
	public static final String SESSION_OPEN = "sessionOpened";
	public static final String CONECTED_IP = "conectedIp";
	public static final String ADMIN = "ADMIN";
	public static final Integer DEFAULT_SRID = 54004;


	public static boolean isUserInRole(String role) {
		return getRequest().isUserInRole(role);
	}

	public static HttpSession getSession() {
		HttpServletRequest request = getRequest();
		return request.getSession();
	}

	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}


	public static String md5(String valor) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			BigInteger hash = new BigInteger(1, md.digest(valor.getBytes()));
			String s = hash.toString(16);
			if (s.length() %2 != 0) s = "0" + s;
			return s;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}


}
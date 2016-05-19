package br.edu.fa7.bean;

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
//import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.edu.fa7.util.Util;

@Scope("session")
@Component("controlBean")
public class ControlStateBean extends BaseStateBean implements Serializable {


	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(ControlStateBean.class);
	private String localTimeZone = "GMT-03:00";


	public Date getServerDate(){

		return new Date();
	}

//	@PostConstruct
//	public String init() throws ServletException, IOException {
//
//		if(!isUserIn()){
//			if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("roleAnonymous")) {
//				Util.getSession().setAttribute(Util.CONECTED_IP, Util.getRequest().getRemoteAddr());
//			}
//			return SUCCESS;
//		} else {
//			return FAIL;
//		}
//	}


	public String voltar() {
		if (Util.getSession() != null) {
			Util.getSession().invalidate();
		}
		return SUCCESS;
	}


	@SuppressWarnings("unchecked")
	protected static <E> E getUsuarioService(Class<E> clazz, HttpSession session) {
		ApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext());
		return (E) wac.getBean("usuarioService");
	}

	@SuppressWarnings("unchecked")
	protected static <E> E getLogUsuarioService(Class<E> clazz, HttpSession session) {
		ApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext());
		return (E) wac.getBean("logUsuarioService");
	}

	public String getLocalTimeZone() {
		return localTimeZone;
	}
	public void setLocalTimeZone(String localTimeZone) {
		this.localTimeZone = localTimeZone;
	}
}
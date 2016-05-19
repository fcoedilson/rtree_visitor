package br.edu.fa7.util;

import java.util.Map;
import java.util.ResourceBundle;

import javax.el.ELResolver;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JsfUtil {

	private static JsfUtil instance;

	public static synchronized JsfUtil getInstance() {
		if (instance == null) {
			instance = new JsfUtil();
		}
		return instance;
	}

	public Object getSessionBean(String beanName){
		FacesContext context = FacesContext.getCurrentInstance();
		ELResolver resolver = context.getApplication().getELResolver();
		Object object = resolver.getValue(context.getELContext(), null, beanName);
		return object;
	}

	public ExternalContext getExternalContext() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return facesContext.getExternalContext();
	}

	public ServletContext getServletContext() {
		return (ServletContext) FacesContext.getCurrentInstance()
		.getExternalContext().getContext();
	}

	@SuppressWarnings("rawtypes")
	public String getRequestParameter(String parameterName) {
		Map paramMap = getExternalContext().getRequestParameterMap();
		return (String) paramMap.get(parameterName);
	}

	@SuppressWarnings("rawtypes")
	public Object getRequestAttribute(String attributeName) {
		Map attrMap = getExternalContext().getRequestMap();
		return attrMap.get(attributeName);
	}

	@SuppressWarnings("rawtypes")
	public Object getSessionAttribute(String attributeName) {
		Map attrMap = getExternalContext().getSessionMap();
		return attrMap.get(attributeName);
	}

	@SuppressWarnings("rawtypes")
	public Object getApplicationAttribute(String attributeName) {
		Map reqAttrMap = getExternalContext().getApplicationMap();
		return reqAttrMap.get(attributeName);
	}

	public void addGlobalMessage(String message) {
		FacesMessage facesMessage = new FacesMessage(message);
		FacesContext.getCurrentInstance().addMessage(null, facesMessage);
	}
	
	/**
	 * Adiciona mensagens de erro que ser�o exibidas nas p�ginas
	 * para isto, utiliza mensagens definidas no messages.properties,
	 * obtendo a messagem de acordo com o valor passado na vari�vel key
	 * @param key
	 */
	public void addErrorMessage(String key) {
		ResourceBundle rb = ResourceBundle.getBundle("messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
		FacesMessage facesMessage = new FacesMessage(rb.getString(key));
		FacesContext.getCurrentInstance().addMessage(null, facesMessage);
	}
	

	public void addGlobalPropMessage(String propName, Object[] parametros) {
		String propFileName = getContextParam("properties_file");
		String msg = PropertiesUtil.getInstance().getProperty(propFileName, propName, parametros);
		FacesMessage facesMessage = new FacesMessage(msg);
		FacesContext.getCurrentInstance().addMessage("", facesMessage);
	}

	public HttpServletResponse getResponse(FacesContext context) {
		return (HttpServletResponse) context.getExternalContext().getResponse();
	}

	public HttpServletRequest getRequest(FacesContext context) {
		return (HttpServletRequest) context.getExternalContext().getRequest();
	}

	public String getInitParameter(FacesContext context, String parameter) {
		return context.getExternalContext().getInitParameter(parameter);
	}

	public String getContextParam(String paramName){
		String paramValue = JsfUtil.getInstance().getInitParameter(
				FacesContext.getCurrentInstance(), paramName); 
		return paramValue;
	}

}

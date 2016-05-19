package br.edu.fa7.bean;

import br.edu.fa7.util.Util;

public abstract class BaseStateBean extends BaseBean {

	private String currentState;

	public boolean isOpened() {
		Object tmp = Util.getSession().getAttribute("isOpen");
		if (tmp == null) {
			closePopup();
			return false;
		} else {
			return (Boolean) tmp;
		}
	}

	public boolean isOpenSession(){
		Object o = Util.getSession().getAttribute("sessionOpened");
		if(o == null){
			return false;
		} else {
			return (Boolean)o;
		}
	}

	public String openPopup() {
		Util.getSession().setAttribute("isOpen", Boolean.TRUE);
		return SUCCESS;
	}


	public String closePopup() {
		Util.getSession().setAttribute("isOpen", Boolean.FALSE);
		return SUCCESS;
	}
	
	public boolean isUserIn(){
		Object tmp = Util.getSession().getAttribute("userIn");
		if (tmp == null) {
			return false;
		} else {
			return (Boolean) tmp;
		}
	}

	protected String currentBeanName() {
		return this.getClass().getSimpleName();
	}

	protected void setCurrentBean(String bean) {
		Util.getSession().setAttribute("currentBean", bean);
	}

	protected void setCurrentState(String state) {
		this.currentState = state;
	}

	public String getCurrentBean() {
		return (String) Util.getSession().getAttribute("currentBean");
	}

	public String getCurrentState() {
		return this.currentState == null ? "" : this.currentState;
	}
}
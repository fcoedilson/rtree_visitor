package br.edu.fa7.dao;

import java.util.HashMap;
import java.util.Map;

import br.edu.fa7.entity.User;

public class UserDAO extends GenericDAO<User> {

	private static final long serialVersionUID = 1L;

	public UserDAO() {
		super(User.class);
	}

	public User findUserByEmail(String email){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("email", email);     

		return super.findOneResult(User.FIND_BY_EMAIL, parameters);
	}
}
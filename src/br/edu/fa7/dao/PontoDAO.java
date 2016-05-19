package br.edu.fa7.dao;

import java.util.HashMap;
import java.util.Map;

import br.edu.fa7.entity.Ponto;

public class PontoDAO extends GenericDAO<Ponto> {

	private static final long serialVersionUID = 1L;

	public PontoDAO() {
		super(Ponto.class);
	}

	public Ponto findByProximo(int pontoId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("pontoId", pontoId);

		return super.findOneResult(Ponto.FIND_BY_PROX, parameters);
	}

	public void delete(Ponto ponto) {
		super.delete(ponto.getId(), Ponto.class);
	}
}

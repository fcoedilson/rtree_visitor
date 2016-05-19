package br.edu.fa7.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.postgis.Geometry;

import com.sun.istack.internal.NotNull;


@SuppressWarnings("serial")
@Entity
@Table (name="pontos")
@NamedQuery(name = "Ponto.findByPontoProximo", 
	query = "select p from Ponto p left join fetch p.pontoProximo where p.id = :pontoId")
public class Ponto implements Serializable {
	
	
	public static final String FIND_BY_PROX = "Ponto.findByPontoProximo";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private Double lng;
	private Double lat;
	private Double distance;
	private String nome;
	private String descricao;
	
	@Type(type="br.edu.fa7.util.GeometryType")
	@Column(name = "the_geom", nullable = false)
	private Geometry geometry;
	
	@NotNull
	@Column(name="ponto_prox_id")
	private Ponto pontoProximo;

	public Ponto() {}

	public Ponto(Double lng, Double lat) {
		this.lng = lng;
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Ponto getPontoProximo() {
		return pontoProximo;
	}

	public void setPontoProximo(Ponto pontoProximo) {
		this.pontoProximo = pontoProximo;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ponto other = (Ponto) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public Ponto clone() {
		Ponto ponto = new Ponto();
		ponto.id = this.id;
		ponto.lng = this.lng;
		ponto.lat = this.lat;
		ponto.nome = this.nome;
		return ponto;
	}
}
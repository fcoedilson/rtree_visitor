package br.org.sigafrota.entity;

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

public class Ponto implements Cloneable {

	private int pontoId;
	private float x;
	private float y;
	private float distancia;
	private String descricao;

	public Ponto() {}

	public Ponto(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public int getPontoId() {
		return pontoId;
	}

	public void setPontoId(int pontoId) {
		this.pontoId = pontoId;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getDistancia() {
		return distancia;
	}

	public void setDistancia(float distancia) {
		this.distancia = distancia;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pontoId;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ponto other = (Ponto) obj;
		if (pontoId != other.pontoId)
			return false;
		return true;
	}

	public Ponto clone() {
		Ponto ponto = new Ponto();
		ponto.pontoId = this.pontoId;
		ponto.x = this.x;
		ponto.y = this.y;
		ponto.descricao = this.descricao;
		return ponto;
	}
}
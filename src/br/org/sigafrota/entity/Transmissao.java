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

import java.util.Date;

public class Transmissao {

	private int codtransmissao;
	private int codponto;
	private String descricaoPonto;
	private String veiculoNome;
	private float distanciaPonto;
	private float x;
	private float y;
	private float velocidade;
	private int codveiculo;
	private Float velocidadeMaxima;
	private Date dataTransmissao;
	private Integer distanciaMovimentacao;
	private String email;
	private transient Ponto pontoProximo;

	public int getCodtransmissao() {
		return codtransmissao;
	}

	public void setCodtransmissao(int codtransmissao) {
		this.codtransmissao = codtransmissao;
	}

	public int getCodponto() {
		return codponto;
	}

	public void setCodponto(int codponto) {
		this.codponto = codponto;
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

	public float getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(float velocidade) {
		this.velocidade = velocidade;
	}

	public int getCodveiculo() {
		return codveiculo;
	}

	public void setCodveiculo(int codveiculo) {
		this.codveiculo = codveiculo;
	}

	public float getDistanciaPontoTransmissao() {
		return distanciaPonto;
	}

	public void setDistanciaPontoTransmissao(float distanciaPontoTransmissao) {
		this.distanciaPonto = distanciaPontoTransmissao;
	}

	public String getDescricaoPonto() {
		return descricaoPonto;
	}

	public void setDescricaoPonto(String descricaoPonto) {
		this.descricaoPonto = descricaoPonto;
	}

	public Date getDataTransmissao() {
		return dataTransmissao;
	}

	public void setDataTransmissao(Date dataTransmissao) {
		this.dataTransmissao = dataTransmissao;
	}

	public String getVeiculoNome() {
		return veiculoNome;
	}

	public void setVeiculoNome(String veiculoNome) {
		this.veiculoNome = veiculoNome;
	}

	public Float getVelocidadeMaxima() {
		return velocidadeMaxima;
	}

	public void setVelocidadeMaxima(Float velocidadeMaxima) {
		this.velocidadeMaxima = velocidadeMaxima;
	}

	public Integer getDistanciaMovimentacao() {
		return distanciaMovimentacao;
	}

	public void setDistanciaMovimentacao(Integer distanciaMovimentacao) {
		this.distanciaMovimentacao = distanciaMovimentacao;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public Ponto getPontoProximo() {
		if (pontoProximo == null) {
			pontoProximo = new Ponto(x, y);
		}
		return pontoProximo;
	}

	public void setPontoProximo(Ponto pontoProximo) {
		this.pontoProximo = pontoProximo;
	}
}
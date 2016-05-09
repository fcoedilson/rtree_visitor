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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spatialindex.rtree.RTree;
import spatialindex.spatialindex.Point;
import spatialindex.spatialindex.SpatialIndex;
import spatialindex.storagemanager.PropertySet;
import br.org.sigafrota.entity.Ponto;
import br.org.sigafrota.entity.Transmissao;
import br.org.sigafrota.server.DateUtil;

public class GprsPostgresql {

	private static final String server_url = "jdbc:postgresql://localhost:5432/sgf";
	private static final String user = "sgf";
	private static final String password = "sgf";

	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConn() {
		return getConn(server_url);
	}

	public static Connection getConn(String url) {

		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public static void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (Exception e) {}
	}

	public static void close(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (Exception e) {}
	}

	public static void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (Exception e) {}
	}

	/*	imei character varying, status character varying, datageracao timestamp, datastore timestamp, 
		telefautorizado character varying, gpstatus character varying, notid1 character varying,
		comm character varying, latitude float, longitude float, speed float, direction float, */
	public static void gravarDadosFormatados(String dadorecebido, String imei, Integer veiculo, Date dataleitura){

		Connection conn = null;
		PreparedStatement stmt = null;

		String sql = "INSERT INTO tb_dadolido(imei, statusrastreador, datatransmissao, phoneadmin, sinalgps, " +
				"minsec, command, latitude, longitude, speed, direction, dataleitura, codveiculo) " +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

		dadorecebido = dadorecebido.replace("##,", "");
		ArrayList<String> positions = new ArrayList<String>();

		String[] dadoArray = dadorecebido.split(";");
		for (int i = 0; i < dadoArray.length; i++) {
			String[] substring =  dadoArray[i].split(",");
			if(substring.length > 2){
				positions.add(dadoArray[i]);
			}
		}

		try {
			conn = getConn();
			stmt = conn.prepareStatement(sql);

			for (String position : positions) {
				position = position.replace("imei:", "");
				String[] fields = position.split(",");

				String dateformat = fields[2].substring(0,2) + "-"+
						fields[2].substring(2,4) + "-" + 
						fields[2].substring(4,6) + " " +
						fields[2].substring(6,8) + ":" +
						fields[2].substring(8,fields[2].length());

				fields[5] = fields[5].replace(".", "#"); 
				String[] fieldsec =  fields[5].split("#");
				fields[5] = fields[5].replace("#","."); 
				String seconds = fieldsec[0].substring(fieldsec[0].length()-2, fieldsec[0].length()); // SECONDS

				Float lat = Float.valueOf(fields[7].substring(0,2)) + Float.valueOf(fields[7].substring(2,fields[7].length()))/60 ; // FORMAT LATITUDE
				Float lng = Float.valueOf(fields[9].substring(0,3)) + Float.valueOf(fields[9].substring(3,fields[9].length()))/60;  // FORMAT LONGITUDE


				if(fields[8].trim().toUpperCase().equals("S")) lat = lat*(-1);
				if(fields[10].trim().toUpperCase().equals("W")) lng = lng*(-1);

				Float speed = fields.length > 11 ? Float.valueOf(fields[11]) : 0F;
				Float direction = fields.length > 12 ? Float.valueOf(fields[12]) : 0F;

				stmt.setString(1, fields[0]); // IMEI
				stmt.setString(2, fields[1]); // STATUS
				stmt.setTimestamp(3, new Timestamp(DateUtil.parseArenaStringAsDateTime("20" + dateformat +":"+ seconds).getTime())); // DATA TRANSMISSÃO
				stmt.setString(4, fields[3]); // FONE_AUTHORIZATED
				stmt.setString(5, fields[4]); // STATUS GPS
				stmt.setString(6, fields[5]); // MINUTOS E SEGUNDOS
				stmt.setString(7, fields[6]); // COMMAND
				stmt.setFloat(8, lat); // LATITUDE
				stmt.setFloat(9, lng); // LONGITUDE
				stmt.setFloat(10, speed*1.852F); // SPEED NO => KM/H
				stmt.setFloat(11, direction); // DIRECTION
				stmt.setTimestamp(12, new Timestamp((dataleitura).getTime())); // CURRENT DATE
				stmt.setInt(13, veiculo); // CODIGO VEICULO
				stmt.execute();
			}

		} catch (SQLException e) {
			Date data = new Date();
			gravarErroMessage(e.toString(), data);
			System.out.println(DateUtil.parseAsString("yyyy-mm-dd HH:mm:ss", data)  + "[Não foi possivel incluir a mensagem no banco de dados]");
			return;
		} finally {
			close(stmt);
			close(conn);
		}

		System.out.println(DateUtil.parseAsString("yyyy-mm-dd HH:mm:ss", new Date())  +"[Mensagem gravada com sucesso]");
	}

	public static void gravarDados(String dadorecebido, String imei, Date datatransmissao) throws SQLException {

		String sql = "INSERT INTO tb_dadorecebido (imei, dadorecebido, datatransmissao) VALUES (?,?,?)";
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConn();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, imei);
			stmt.setString(2, dadorecebido);
			stmt.setTimestamp(3, new Timestamp((datatransmissao).getTime())); // DATA TRANSMISSAO
			stmt.execute();
		} catch (SQLException e) {
			Date data = new Date();
			gravarErroMessage(e.toString(), data);			
			System.out.println(DateUtil.parseAsString("yyyy-mm-dd HH:mm:ss", data)  +"[Não foi possivel incluir a mensagem no banco de dados]");
			return;
		} finally {
			close(stmt);
			close(conn);
		}
		System.out.println(DateUtil.parseAsString("yyyy-mm-dd HH:mm:ss", new Date())  +"[Mensagem gravada com sucesso]");
	}

	public static String consultar(int id) throws SQLException {

		String sql = "SELECT dadorecebido FROM tb_dadorecebido WHERE coddadorecebido = ?";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String result = null;

		try {
			conn = getConn();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getString("dadorecebido");
				System.out.println(result);
			}
		} catch (SQLException e) {
			Date data = new Date();
			gravarErroMessage(e.toString(), data);
			System.out.println(DateUtil.parseAsString("yyyy-mm-dd HH:mm:ss", data)  +"[Não foi possivel recuperar a mensagem do banco de dados]");
		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}
		return result;
	}

	public static void gravarErroMessage(String message, Date data){

		String queryErro = "INSERT INTO tb_gprsgatewaylog(descerro) values(?)";
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConn();
			stmt = conn.prepareStatement(queryErro);
			stmt.setString(1,  DateUtil.parseAsString("yyyy-mm-dd HH:mm:ss", data)  +"$" + message);
			stmt.execute();
		} catch (Exception e) {
			System.out.println("[Não possível gravar mensagem de erro]");
		}
	}

	public static int findveiculoByImei(String imei) {

		String sql = "SELECT codveiculo FROM tb_veiculorastreador WHERE imei = ?";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int id = 0;
		try {
			conn = getConn(server_url);
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, imei);
			rs = stmt.executeQuery();
			if (rs.next()) {
				id = rs.getInt("codveiculo");
			}

		} catch (SQLException e) {
			System.out.println("[Não foi possivel recuperar código do veiculo - imei:] "+ imei +"");
			return 0;
		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}

		return id;
	}

	public static int consultarMaxId() {

		String sql = "SELECT MAX(coddadorecebido) AS coddadorecebido FROM tb_dadorecebido";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int id = 0;

		try {
			conn = getConn(server_url);
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				id = rs.getInt("coddadorecebido");
			}
		} catch (SQLException e) {
			System.out.println("[Não foi possivel incluir a mensagem no banco de dados]");
			return 0;
		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}
		return id;
	}
	
	public static Map<Integer, RTree> findPontos(List<Ponto> points, Integer rootPoint) throws Exception {

		Map<Integer, RTree> result = new HashMap<Integer, RTree>();
		try {
			for (Ponto p : points) {
				Ponto ponto = p;
				if (!result.containsKey(rootPoint)) {
					PropertySet propertySet = new PropertySet();
					propertySet.setProperty("IndexCapacity", 5);
					propertySet.setProperty("LeafCapactiy", 5);
					result.put(rootPoint, new RTree(propertySet, SpatialIndex.createMemoryStorageManager(null)));
				}
				Point point = new Point(new double[]{ponto.getX(), ponto.getY()});
				result.get(rootPoint).insertData(ponto.getDescricao().getBytes(), point, ponto.getPontoId());
			}

		} catch (Exception e) {
			System.out.println(DateUtil.parseDateAsString(new Date()) + "[Não foi possivel montar a árvore RTree de pontos]");
			e.printStackTrace();
		}
		return result;
	}

	public static Map<Integer, RTree> findPontos(Connection conn) throws Exception {

		String findpontos = "SELECT codponto, descponto, x, y FROM tb_ponto";
		Integer start = 0;

		Map<Integer, RTree> result = new HashMap<Integer, RTree>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(findpontos);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Ponto ponto = new Ponto();
				ponto.setPontoId(rs.getInt("codponto"));
				ponto.setX(rs.getFloat("X"));
				ponto.setY(rs.getFloat("Y"));
				ponto.setDescricao(rs.getString("descponto"));

				if (!result.containsKey(start)) {
					PropertySet propertySet = new PropertySet();
					propertySet.setProperty("IndexCapacity", 5);
					propertySet.setProperty("LeafCapactiy", 5);
					result.put(start, new RTree(propertySet, SpatialIndex.createMemoryStorageManager(null)));
				}
				Point point = new Point(new double[]{ponto.getX(), ponto.getY()});
				result.get(start).insertData(ponto.getDescricao().getBytes(), point, ponto.getPontoId());
			}

		}  catch (SQLException e) {
			System.out.println(DateUtil.parseDateAsString(new Date()) + "[Não foi possivel montar a árvore RTree de pontos]");
		} finally {
			close(rs);
			close(stmt);
		}
		return result;
	}
	
	public static List<Ponto> retrievePontos(){
		
		String findpontos = "SELECT codponto, descponto, x, y FROM tb_ponto";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Ponto> result = new ArrayList<Ponto>();
		
		try {
			conn = getConn(server_url);
			stmt = conn.prepareStatement(findpontos);
			rs = stmt.executeQuery();
			while(rs.next()) {
				Ponto p = new Ponto();
				int pid = rs.getInt("codponto");
				String descp = rs.getString("descponto");
				float x = rs.getFloat("x");
				float y = rs.getFloat("y");
				p.setPontoId(pid);
				p.setDescricao(descp);
				p.setX(x);
				p.setY(y);
				result.add(p);
			}
		} catch (SQLException e) {
			System.out.println(DateUtil.parseDateAsString(new Date()) + "[Não foi possivel realizar a consulta no banco de dados]");
		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}
		return result;
	}

	public static List<Transmissao> findTransmissoes(Connection conn) throws Exception {

		String FIND_TRANSMISSOES = "SELECT T.CODTRANSMISSAO, T.CODVEICULO, T.DATA_TRANSMISSAO, X, Y " +
				"FROM TB_TRANSMISSAO T LEFT JOIN TB_CADVEICULO V ON T.CODVEICULO = V.CODVEICULO " +
				"WHERE CODPONTO IS NULL ORDER BY T.DATA_TRANSMISSAO LIMIT 1000;";

		List<Transmissao> result = new ArrayList<Transmissao>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(FIND_TRANSMISSOES);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Transmissao transmissao = new Transmissao();
				transmissao.setCodtransmissao(rs.getInt("CODTRANSMISSAO"));
				transmissao.setCodveiculo(rs.getInt("CODVEICULO"));
				transmissao.setDataTransmissao(rs.getTimestamp("DATA_TRANSMISSAO"));
				transmissao.setX(rs.getFloat("X"));
				transmissao.setY(rs.getFloat("Y"));
				result.add(transmissao);
			}

		} finally {
			close(rs);
			close(stmt);
		}
		return result;
	}

	public static boolean updateTransmissao(Connection conn, Transmissao transmissao) throws Exception {

		String updateTransmissao = "UPDATE tb_transmissao SET codponto = ?, dist_ponto = ? WHERE codtransmissao = ?";
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(updateTransmissao);
			stmt.setInt(1, transmissao.getPontoProximo().getPontoId());
			stmt.setInt(2, (int) transmissao.getPontoProximo().getDistancia());
			stmt.setInt(3, transmissao.getCodtransmissao());
			return stmt.execute();
		} catch (SQLException e) {
			System.out.println(DateUtil.parseDateAsString(new Date()) + "[Não foi possivel realizar a consulta no banco de dados]");
			return false;
		} finally {
			close(stmt);
		}
	}
}

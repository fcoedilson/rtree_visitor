package br.edu.fa7.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.fa7.entity.Ponto;
import spatialindex.rtree.RTree;
import spatialindex.spatialindex.Point;
import spatialindex.spatialindex.SpatialIndex;
import spatialindex.storagemanager.PropertySet;


public class Pg {

	private static final String server_url = "jdbc:postgresql://localhost:5432/rtree";
	private static final String user = "postgres";
	private static final String password = "postgres";

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
				Point point = new Point(new double[]{ponto.getLng(), ponto.getLat()});
				result.get(rootPoint).insertData(ponto.getDescricao().getBytes(), point, ponto.getId());
			}

		} catch (Exception e) {
			System.out.println(DateUtil.parseDateAsString(new Date()) + "[Não foi possivel montar a árvore RTree de pontos]");
			e.printStackTrace();
		}
		return result;
	}

	public static Map<Integer, RTree> findPontos(Connection conn) throws Exception {

		String findpontos = "SELECT id, descricao, distance, lng, lat from pontos";
		Integer start = 0;

		Map<Integer, RTree> result = new HashMap<Integer, RTree>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(findpontos);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Ponto ponto = new Ponto();
				ponto.setId(rs.getInt("id"));
				ponto.setLng(rs.getDouble("lng"));
				ponto.setLat(rs.getDouble("lat"));
				ponto.setDescricao(rs.getString("descricao"));

				if (!result.containsKey(start)) {
					PropertySet propertySet = new PropertySet();
					propertySet.setProperty("IndexCapacity", 5);
					propertySet.setProperty("LeafCapactiy", 5);
					result.put(start, new RTree(propertySet, SpatialIndex.createMemoryStorageManager(null)));
				}
				Point point = new Point(new double[]{ponto.getLng(), ponto.getLat()});
				result.get(start).insertData(ponto.getDescricao().getBytes(), point, ponto.getId());
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
		
		String findpontos = "SELECT id, descricao, distance, lng, lat, ponto_prox_id FROM  pontos";
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
				int pid = rs.getInt("id");
				String descp = rs.getString("descricao");
				double lng = rs.getFloat("lng");
				double lat = rs.getFloat("lat");
				double distance = rs.getFloat("distance");
				p.setId(pid);
				p.setDescricao(descp);
				p.setLng(lng);
				p.setLat(lat);
				p.setDistance(distance);
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

	public static boolean updateTransmissao(Connection conn, Ponto novo) throws Exception {

		String updateTransmissao = "UPDATE pontos SET ponto_proximo_id = ?, distance = ? WHERE id = ?";
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(updateTransmissao);
			stmt.setInt(1, novo.getPontoProximo().getId());
			stmt.setDouble(2, novo.getPontoProximo().getDistance());
			stmt.setInt(3, novo.getId());
			return stmt.execute();
		} catch (SQLException e) {
			System.out.println(DateUtil.parseDateAsString(new Date()) + "[Não foi possivel realizar a consulta no banco de dados]");
			return false;
		} finally {
			close(stmt);
		}
	}
}

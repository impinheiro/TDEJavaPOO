package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.loan.EquipmentDao;
import db.DB;
import db.DbException;
import loans.Equipment;

public class EquipmentDaoJDBC implements EquipmentDao {
	private Connection conn;

	public EquipmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Equipment obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO Equipment "
					+ "(description, purchase_date, weight, width, length, maintenance_history, condition_status) "
					+ "VALUES" + "(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			ArrayList<String> getHistory = obj.getHistory();
			String history = String.join("/ ", getHistory);
			st.setString(1, obj.getDescription());
			st.setDate(2, new java.sql.Date(obj.getPurchaseDate().getTime()));
			st.setDouble(3, obj.getWeight());
			st.setDouble(4, obj.getWidth());
			st.setDouble(5, obj.getLength());
			st.setString(6, history);
			st.setString(7, obj.getConditionStatus());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Nenhuma linha foi afetada");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void update(Equipment obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE Equipment "
					+ "SET description = ?, "
					+ "purchase_date = ?, "
					+ "weight = ?, "
					+ "width = ?, "
					+ "length = ?, "
					+ "maintenance_history = ?, "
					+ "condition_status = ? "
					+ "WHERE equipment_id = ?");
			
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy ");
			LocalDate now = LocalDate.now();
			now.format(dtf);
			String timeNow = now.toString();
			ArrayList<String> getHistory = obj.getHistory();
			String history = String.join(" / "+timeNow+" --> ", getHistory);
			st.setString(1, obj.getDescription());
			st.setDate(2, new java.sql.Date(obj.getPurchaseDate().getTime()));
			st.setDouble(3, obj.getWeight());
			st.setDouble(4, obj.getWidth());
			st.setDouble(5, obj.getLength());
			st.setString(6, history);
			st.setString(7, obj.getConditionStatus());
			st.setInt(8, obj.getId());

			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"DELETE FROM Equipment "
					+ "WHERE "
					+ "equipment_id = ?"
					);
			
			st.setInt(1, id);
			int rows = st.executeUpdate();
			
			if(rows == 0) { 
				throw new DbException("Nenhum ID encontrado");
			}
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Equipment findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"select *,description as Equipamento" + ",purchase_date as Aquisição, " + "weight as Peso, "
							+ "width as Largura, " + "length as Comprimento, " + "maintenance_history as Manuntenção, "
							+ "condition_status as Conservação " + "from Equipment " + "where equipment_id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Equipment obj = instantiateEquipment(rs);

				return obj;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException("Erro inesperado" + e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

	private Equipment instantiateEquipment(ResultSet rs) throws SQLException {
		Equipment obj = new Equipment();
		String historyString = rs.getString("maintenance_history");
		ArrayList<String> history = new ArrayList<>(Arrays.asList(historyString.split("/ ")));
		obj.setId(rs.getInt("equipment_id"));
		obj.setDescription(rs.getString("description"));
		obj.setPurchaseDate(rs.getDate("purchase_date"));
		obj.setHistory(history);
		obj.setLength(rs.getDouble("length"));
		obj.setWeight(rs.getDouble("weight"));
		obj.setWidth(rs.getDouble("width"));
		obj.setConditionStatus(rs.getString("condition_status"));
		return obj;
	}

	@Override
	public List<Equipment> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"select *,description as Equipamento " + ",purchase_date as Aquisição, " + "weight as Peso, "
							+ "width as Largura, " + "length as Comprimento, " + "maintenance_history as Manuntenção, "
							+ "condition_status as Conservação " + "from Equipment " + "order by description");
			rs = st.executeQuery();

			List<Equipment> list = new ArrayList<>();
			Map<Integer, Equipment> map = new HashMap<>();

			while (rs.next()) {
				Equipment equip = map.get(rs.getInt("equipment_id"));
				if (equip == null) {
					equip = instantiateEquipment(rs);
					map.put(rs.getInt("equipment_id"), equip);
				}

				Equipment obj = instantiateEquipment(rs);
				list.add(obj);
			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

}

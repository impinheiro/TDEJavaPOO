package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.loan.DaoFactory;
import dao.loan.EmployeeDao;
import dao.loan.EquipmentDao;
import dao.loan.LoansDao;
import db.DB;
import db.DbException;
import loans.Employee;
import loans.Equipment;
import loans.Loans;

public class LoansDaoJDBC implements LoansDao {
	private Connection conn;

	public LoansDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Loans obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO Loan "
					+ "(checkout_datetime, notes, employee_id, equipment_id)"
					+ "VALUES "
					+ "(?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS
					 );
			
			LocalDateTime now = obj.getDeparture();
			Timestamp timestamp = Timestamp.valueOf(now);
			st.setTimestamp(1, timestamp);
			st.setString(2, obj.getNotes());
			st.setInt(3, obj.getEmployee().getId());
			st.setInt(4, obj.getEquipment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else { 
				throw new DbException("Nenhuma linha foi afetada!");
			}
		}catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}


	}

	@Override
	public void update(Loans obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE Loan "
					+ "SET checkout_datetime = ?, "
					+ "return_datetime = ?, "
					+ "notes = ?, "
					+ "employee_id = ?, "
					+ "equipment_id = ? "
					+ "WHERE loan_id = ?");
			
			LocalDateTime now = obj.getDeparture();
			Timestamp checkout = Timestamp.valueOf(now);
			st.setTimestamp(1, checkout);
			LocalDateTime now2 = obj.getReturnTime();
			Timestamp returnTime = Timestamp.valueOf(now2);
			st.setTimestamp(2, returnTime);
			st.setString(3, obj.getNotes());
			st.setInt(4, obj.getEmployee().getId());
			st.setInt(5, obj.getEquipment().getId());
			st.setInt(6, obj.getId());

			int rowsUpdated = st.executeUpdate();
			
			if(rowsUpdated > 0) {
				System.out.println("Update realizado com sucesso!");
			} else {
				System.out.println("Emprestimo nao encontrado!");
			}

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
			st = conn.prepareStatement("DELETE FROM Loan WHERE loan_id = ?");
			
			st.setInt(1, id);
			
			int rowsAffected = st.executeUpdate();
			if ( rowsAffected == 0) {
				throw new DbException("Nenhuma linha foi encontrada");
			}
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Loans findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT *, " + "checkout_datetime AS Saida, " + "return_datetime AS Retorno, "
					+ "notes AS Observacoes " + "FROM Loan "
					+ "INNER JOIN Equipment ON Loan.equipment_id = Equipment.equipment_id "
					+ "INNER JOIN Employee ON Loan.employee_id = Employee.employee_id " + "WHERE loan_id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Equipment equipment = instantiateEquipment(rs);
				Employee employee = instantiateEmployee(rs);
				Loans obj = instantiateLoan(rs, equipment, employee);
				return obj;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException("Erro inesperado: " + e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Loans instantiateLoan(ResultSet rs, Equipment equip, Employee employee) throws SQLException {
		Loans obj = new Loans();
		if (rs.getTimestamp("return_datetime") != null) {
		obj.setId(rs.getInt("loan_id"));
		Timestamp checkout = rs.getTimestamp("checkout_datetime");
		LocalDateTime conversionCheckout = checkout.toLocalDateTime();
		obj.setDeparture(conversionCheckout);
		Timestamp returnDate = rs.getTimestamp("return_datetime");
		LocalDateTime returnConversion = returnDate.toLocalDateTime();
		obj.setReturnTime(returnConversion);
		obj.setNotes(rs.getString("notes"));
		obj.setEquipment(equip);
		obj.setEmployee(employee);
		}else {
			obj.setId(rs.getInt("loan_id"));
			Timestamp checkout = rs.getTimestamp("checkout_datetime");
			LocalDateTime conversionCheckout = checkout.toLocalDateTime();
			obj.setDeparture(conversionCheckout);
			obj.setNotes(rs.getString("notes"));
			obj.setEquipment(equip);
			obj.setEmployee(employee);
		}
		return obj;
	}

	private Equipment instantiateEquipment(ResultSet rs) throws SQLException {
		Equipment obj = new Equipment();
		EquipmentDao equipDao = DaoFactory.createEquipmentDao();
		obj = equipDao.findById(rs.getInt("equipment_id"));
		return obj;
	}

	private Employee instantiateEmployee(ResultSet rs) throws SQLException {
		Employee obj = new Employee();
		EmployeeDao employeeDao = DaoFactory.createEmployeeDao();
		obj = employeeDao.findById(rs.getInt("employee_id"));
		return obj;
	}

	
	

	@Override
	public List<Loans> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("select* " + "from Loan " + "order by checkout_datetime ");

			rs = st.executeQuery();

			List<Loans> list = new ArrayList<>();
			Map<Integer, Loans> map = new HashMap<>();

			while (rs.next()) {
				Loans loan = map.get(rs.getInt("loan_id"));
				if (loan == null) {
					Equipment equipment = instantiateEquipment(rs);
					Employee employee = instantiateEmployee(rs);
					loan = instantiateLoan(rs, equipment, employee);
					map.put(rs.getInt("loan_id"), loan);
				}
				if(rs.getTimestamp("return_datetime") == null) {
				Equipment equipment = instantiateEquipment(rs);
				Employee employee = instantiateEmployee(rs);
				Loans obj = instantiateLoan(rs, equipment, employee);
				list.add(obj);
				}
			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
		public boolean isEquipmentAvailable(Integer id) {
			PreparedStatement st = null;
			ResultSet rs = null;
			try {
				st = conn.prepareStatement("SELECT COUNT(*) FROM Loan WHERE equipment_id = ? AND return_datetime IS NULL");

				st.setInt(1, id);

				rs = st.executeQuery();

				if (rs.next()) {
					int count = rs.getInt(1);
					return count == 0;
				}
				return false;
			} 
			catch(SQLException e) {
				throw new DbException(e.getMessage());
			}	finally {
				DB.closeResultSet(rs);
				DB.closeStatement(st);
			}
	}

}

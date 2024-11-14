package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.loan.EmployeeDao;
import db.DB;
import db.DbException;
import loans.Employee;


public class EmployeeDaoJDBC implements EmployeeDao {
	
	private Connection conn;
	
	public EmployeeDaoJDBC(Connection conn) {
		this.conn=conn;
	}

	@Override
	public void insert(Employee obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO Employee "
					+ "(name, role, hire_date)"
					+ "VALUES "
					+ "(?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS
					 );
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getRole());
			st.setDate(3, new java.sql.Date(obj.getAdmission().getTime()));
			
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
	public void update(Employee obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE Employee "
					+ "SET Employee.name = ?, "
					+ "role = ?, "
					+ "hire_date = ? "
					+ "WHERE employee_id = ?");

			st.setString(1, obj.getName());
			st.setString(2, obj.getRole());
			st.setDate(3, new java.sql.Date(obj.getAdmission().getTime()));
			st.setInt(4, obj.getId());

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
			st = conn.prepareStatement("DELETE FROM Employee WHERE employee_id = ?");
			
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
	public Employee findById(Integer id) {
		PreparedStatement st= null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT *, name as Nome, "
					+ "role as Funcao, "
					+ "hire_date as Admissao "
					+ "FROM Employee "
					+ "WHERE employee_id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if(rs.next()) {
				Employee obj = instantiateEmployee(rs);
				
				return obj;
			}
			return null;
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	
	private Employee instantiateEmployee(ResultSet rs) throws SQLException {
		Employee obj = new Employee();
		obj.setId(rs.getInt("employee_id"));
		obj.setName(rs.getString("name"));
		obj.setRole(rs.getString("role"));
		obj.setAdmission(rs.getDate("hire_date"));
		return obj;
	}

	@Override
	public List<Employee> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
							"select *,name as Nome, " 
							+ "role as Função, " 
							+ "hire_date as Admissão "
							+ "from Employee "
							+ "order by employee_id ");
			
				
			rs = st.executeQuery();

			List<Employee> list = new ArrayList<>();
			Map<Integer, Employee> map = new HashMap<>();

			while (rs.next()) {
				Employee employee = map.get(rs.getInt("employee_id"));
				if (employee == null) {
					employee = instantiateEmployee(rs);
					map.put(rs.getInt("employee_id"), employee);
				}

				Employee obj = instantiateEmployee(rs);
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

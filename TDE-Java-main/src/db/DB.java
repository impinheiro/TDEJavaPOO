package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Properties;


public class DB {

	private static Connection conn = null;

	public static Connection getConnection() {
		if (conn == null) {

			try {
				Properties props = loadProperties();
				String url = props.getProperty("dburl");
				conn = DriverManager.getConnection(url, props);
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		return conn;
	}

	public static void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new DbException("Erro inesperado" + e.getMessage());
			}
		}
	}

	private static Properties loadProperties() {
		try (FileInputStream fs = new FileInputStream("db.properties")) {
			Properties props = new Properties();
			props.load(fs);
			return props;
		} catch (IOException e) {
			throw new DbException(e.getMessage());
		}
	}

	public static void closeStatement(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	public static void getEmployee() {
		Connection conn = getConnection();
		Statement st = null;
		ResultSet rs = null;
		try {

			st = conn.createStatement();

			rs = st.executeQuery("SELECT*FROM Employee");

			while (rs.next()) {
				System.out.println(rs.getInt("employee_id") + ", " + rs.getString("name") + ", " + rs.getString("role")
						+ ", " + rs.getDate("hire_date"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			closeConnection();
		}

	}

	public static void setEmployee(String name, String role, String hireDate) {
		Connection conn = getConnection();
		PreparedStatement pSt = null;

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			pSt = conn.prepareStatement(

					"INSERT INTO Employee " + "(name, role, hire_date) " + "VALUES " + "(?,?,?)",
					Statement.RETURN_GENERATED_KEYS);

			pSt.setString(1, name);
			pSt.setString(2, role);
			pSt.setDate(3, new java.sql.Date(sdf.parse(hireDate).getTime()));

			int rowsAffected = pSt.executeUpdate();
			if (rowsAffected > 0) {
				ResultSet rs = pSt.getGeneratedKeys();
				while (rs.next()) {
					int id = rs.getInt(1);
					System.out.println("Done!" + "ID: " + id);
				}
			}

		} catch (SQLException | java.text.ParseException e) {
			throw new DbException(e.getMessage());
		} finally {
			closeStatement(pSt);
			closeConnection();
		}
	}

}
//public static void registerLoan(String checkoutDate, String notes, int employeeId, int equipmentId) {
//Connection conn = getConnection();
//PreparedStatement st = null;
//
//try {
//	conn.setAutoCommit(false);
//	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//	Timestamp checkoutTimestamp = new Timestamp(sdf.parse(checkoutDate).getTime());
//
//	st = conn.prepareStatement("INSERT INTO Loan " + "(checkout_datetime, notes, employee_id, equipment_id) "
//			+ "VALUES " + "(?, ?, ?, ?)");
//
//	st.setTimestamp(1, checkoutTimestamp);
//	st.setString(2, notes);
//	st.setInt(3, employeeId);
//	st.setInt(4, equipmentId);
//
//	st.executeUpdate();
//	conn.commit();
//} catch (SQLException | ParseException e) {
//	try {
//		conn.rollback();
//		throw new DbException("Ocorreu um erro "+ e.getMessage() + ",voltando ao estado anterior");
//	} catch (SQLException e1) {
//		// TODO Auto-generated catch block
//		throw new DbException("Erro ao tentar voltar ao estado anterior. "+ "Erro: "+ e1.getMessage());
//	}
//	
//} finally {
//	closeStatement(st);
//	closeConnection();
//}
//}
//
//public static void loanReturnDate(String returnDate, int loanId, ConservationState condition) {
//Connection conn = getConnection();
//PreparedStatement st = null;
//PreparedStatement stEquip = null;
//
//try {
//	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:m:ss");
//	Timestamp returnDateTimestamp = new Timestamp(sdf.parse(returnDate).getTime());
//	
//	st = conn.prepareStatement(
//			"UPDATE Loan "
//			+"SET return_datetime = ?"
//			+"WHERE "
//			+"(loan_id= ?)"
//			);
//	
//	stEquip = conn.prepareStatement("UPDATE Equipment "
//			+"SET condition_status = ?"
//			+"WHERE "
//			+"(loan_id = ?)"
//			);
//	
//	
//	st.setTimestamp(1, returnDateTimestamp);
//	st.setInt(2, loanId);
//	stEquip.setString(1, condition.name());
//	stEquip.setInt(2, loanId);
//	
//	st.executeUpdate();
//	stEquip.executeUpdate();
//	
//} catch (SQLException | ParseException e) {
//	e.printStackTrace();
//} finally {
//	closeStatement(st);
//	closeStatement(stEquip);
//	closeConnection();
//}
//}

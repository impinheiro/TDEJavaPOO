package dao.loan;

import dao.impl.EmployeeDaoJDBC;
import dao.impl.EquipmentDaoJDBC;
import dao.impl.LoansDaoJDBC;
import db.DB;

public class DaoFactory {

	public static EquipmentDao createEquipmentDao() {
		
		return new EquipmentDaoJDBC(DB.getConnection());
	}
	
	public static EmployeeDao createEmployeeDao() {
		return new EmployeeDaoJDBC(DB.getConnection());
	}
	
	public static LoansDao createLoansDao() {
		return new LoansDaoJDBC(DB.getConnection());
	}
}

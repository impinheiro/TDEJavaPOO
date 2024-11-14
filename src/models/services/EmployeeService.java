package models.services;

import java.util.List;

import dao.loan.DaoFactory;
import dao.loan.EmployeeDao;
import loans.Employee;

public class EmployeeService {
	
	private EmployeeDao dao = DaoFactory.createEmployeeDao();
	
	
	public List<Employee> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(Employee obj) {
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public Employee findById(Integer id) {
		return dao.findById(id);
		
	}
	public boolean alreadyExists(String name) {
	    return dao.findAll().stream().anyMatch(employee -> employee.getName().equals(name));
	}
}

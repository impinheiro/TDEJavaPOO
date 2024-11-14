package models.services;

import java.util.List;

import dao.loan.DaoFactory;
import dao.loan.EmployeeDao;
import loans.Employee;
import model.exceptions.EmployeeInLoanException;

public class EmployeeService {
	
	private EmployeeDao dao = DaoFactory.createEmployeeDao();
	LoanService loanService = new LoanService();
	
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
	
	public void remove(Employee obj) {
	    // Verifica se o empregado está em um empréstimo ativo
	    if (loanService.isEmployeeInLoan(obj)) {
	        // Exibe uma mensagem de erro ou lança uma exceção
	        throw new EmployeeInLoanException("O empregado está envolvido em um empréstimo ativo e não pode ser removido.");
	    } else {
	        // Se não houver empréstimo ativo, realiza a remoção
	        dao.deleteById(obj.getId());
	    }
	}
}

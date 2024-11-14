package models.services;

import java.time.LocalDateTime;
import java.util.List;

import dao.loan.DaoFactory;
import dao.loan.LoansDao;
import loans.Employee;
import loans.Equipment;
import loans.Loans;

public class LoanService {

	
	LoansDao dao = DaoFactory.createLoansDao();
	
	

	public List<Loans> findAll() {
		return dao.findAll();
	}
	public void saveOrUpdate(Loans obj) {
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	public boolean isEquipmentAlreadyLoaned(Equipment equipment) {
	    // Buscar todos os empréstimos
	    List<Loans> loans = findAll();  // Supondo que findAll retorna todos os empréstimos
	    for (Loans loan : loans) {
	        // Verificar se o equipamento no empréstimo é o mesmo e se a data de retorno é nula ou no futuro
	        if (loan.getEquipment().equals(equipment) && 
	            (loan.getReturnTime() == null || loan.getReturnTime().isAfter(LocalDateTime.now()))) {
	            return true; // Equipamento ainda está emprestado
	        }
	    }
	    return false; // Equipamento não está mais emprestado
	}
	public boolean isEmployeeInLoan(Employee employee) {
	    List<Loans> loans = dao.findAll();
	    for (Loans loan : loans) {
	        if (loan.getEmployee().equals(employee) && (loan.getReturnTime() == null || loan.getReturnTime().isAfter(LocalDateTime.now()))) {
	            return true; // O empregado está em um empréstimo ativo
	        }
	    }
	    return false; // O empregado não tem empréstimos ativos
	}
	public boolean isEquipmentInLoan(Equipment equipment) {
	    List<Loans> loans = dao.findAll();
	    for (Loans loan : loans) {
	        if (loan.getEquipment().equals(equipment) && (loan.getReturnTime() == null || loan.getReturnTime().isAfter(LocalDateTime.now()))) {
	            return true; // O equipamento está em um empréstimo ativo
	        }
	    }
	    return false; // O equipamento não tem empréstimos ativos
	}

}

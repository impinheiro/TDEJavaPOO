package models.services;

import java.util.List;

import dao.loan.DaoFactory;
import dao.loan.EquipmentDao;
import loans.Equipment;
import model.exceptions.EquipmentInLoanException;

public class EquipmentService {
	private EquipmentDao dao = DaoFactory.createEquipmentDao();
	LoanService loanService = new LoanService();
	public List<Equipment> findAll() {
		return dao.findAll();
	}

	public void saveOrUpdate(Equipment obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		} else {
			dao.update(obj);
		}
	}

	public Equipment findById(Integer id) {
		return dao.findById(id);
	}

	public List<Equipment> findAvailable() {
		return dao.findAvailable();
	}
	
	public void remove(Equipment obj) {
	    // Verifica se o equipamento está em um empréstimo ativo
	    if (loanService.isEquipmentInLoan(obj)) {
	        // Exibe uma mensagem de erro ou lança uma exceção
	        throw new EquipmentInLoanException("O equipamento está envolvido em um empréstimo ativo e não pode ser removido.");
	    } else {
	        // Se não houver empréstimo ativo, realiza a remoção
	        dao.deleteById(obj.getId());
	    }
	}
}

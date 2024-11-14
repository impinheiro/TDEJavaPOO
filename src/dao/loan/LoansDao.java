package dao.loan;

import java.util.List;

import loans.Employee;
import loans.Loans;

public interface LoansDao {
	void insert(Loans obj);
	void update(Loans obj);
	void deleteById(Integer id);
	Loans findById(Integer id);
	List<Loans> findAll();
	boolean isEquipmentAvailable(Integer id);

}

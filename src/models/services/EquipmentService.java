package models.services;

import java.util.List;

import dao.loan.DaoFactory;
import dao.loan.EquipmentDao;
import loans.Equipment;

public class EquipmentService {
	private EquipmentDao dao = DaoFactory.createEquipmentDao();
	public List<Equipment> findAll(){
		return dao.findAll();
	}
	public void saveOrUpdate(Equipment obj) {
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public Equipment findById(Integer id) {
		return dao.findById(id);
	}

}

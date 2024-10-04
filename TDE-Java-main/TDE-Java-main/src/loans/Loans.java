package loans;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Loans implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private LocalDateTime departure;

	private LocalDateTime returnTime;

	private Employee employee;

	private Equipment equipment;

	private String notes;

	public Loans() {

	}

	public Loans(Integer id, String departure, Employee employee, Equipment equipment,
			String notes) throws ParseException {
		
		this.id = id;
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	    this.departure =  LocalDateTime.parse(departure, formatter);
		this.employee = employee;
		this.equipment = equipment;
		this.notes = notes;
	}
	public Loans(Integer id, String departure,String returnTime, Employee employee, Equipment equipment,
			String notes) throws ParseException {
		
		this.id = id;
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	    this.departure =  LocalDateTime.parse(departure, formatter);
	    LocalDateTime returnDateTime = LocalDateTime.parse(returnTime, formatter);
		this.returnTime = returnDateTime;
		this.employee = employee;
		this.equipment = equipment;
		this.notes = notes;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDateTime getDeparture() {
		return departure;
	}

	public void setDeparture(LocalDateTime departure) {
		this.departure = departure;
	}

	public LocalDateTime getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(LocalDateTime returnTime) {
		this.returnTime = returnTime;
	}
	

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Loans other = (Loans) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Loans [id=" + id + ", departure=" + departure + ", returnTime=" + returnTime + ", employee=" + employee
				+ ", equipment=" + equipment + ", notes=" + notes + "]";
	}
	
	

}

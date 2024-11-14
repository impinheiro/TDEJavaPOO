package loans;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Employee implements Serializable {


	private static final long serialVersionUID = 1L;

	private Integer id;

	private String name;

	private String role;

	private Date admission;

	public Employee() {

	}

	public Employee(Integer id, String name, String role, String admission) throws ParseException{
		this.id = id;
		this.name = name;
		this.role = role;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		
		java.sql.Date sqlDate = new java.sql.Date(sdf.parse(admission).getTime());
		this.admission = sqlDate;
		
		
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Date getAdmission() {
		return admission;
	}

	public void setAdmission(Date admission) {
		this.admission = admission;
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
		Employee other = (Employee) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", role=" + role + ", admission=" + admission + "]";
	}
	
	
	
	
	

}

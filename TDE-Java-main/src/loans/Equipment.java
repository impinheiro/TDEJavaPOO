package loans;

import java.io.Serializable;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class Equipment implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String description;

	private Date purchaseDate;

	private double weight;

	private double width;

	private double length;

	private ArrayList<String> history;

	private String conditionStatus;
	
	private Boolean isAvailable;

	public Equipment() {

	}

	public Equipment(Integer id, String description, String purchaseDate, double weight, double width, double length,
			ArrayList<String> history, String conditionStatus) throws ParseException {

		this.id = id;
		this.description = description;
		this.weight = weight;
		this.width = width;
		this.length = length;
		this.history = history;
		this.conditionStatus = conditionStatus;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		java.sql.Date sqlDate = new java.sql.Date(sdf.parse(purchaseDate).getTime());
	    this.purchaseDate = sqlDate;

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date date) {
		this.purchaseDate = date;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	

	public ArrayList<String> getHistory() {
		return history;
	}

	public void setHistory(ArrayList<String> history) {
		this.history = history;
	}

	public String getConditionStatus() {
		return conditionStatus;
	}

	public void setConditionStatus(String string) {
		this.conditionStatus = string;

	}
	

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
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
		Equipment other = (Equipment) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Equipment [id=" + id + ", description=" + description + ", purchaseDate=" + purchaseDate + ", weight="
				+ weight + ", width=" + width + ", length=" + length + ", history=" + history + "condition_status="+conditionStatus;
	}

}
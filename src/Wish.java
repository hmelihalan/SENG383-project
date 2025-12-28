import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
public class Wish {
	  String type; // WISH1 OR WISH2
	  String wishID; // Changed back to String
	  String title;
	  String desc;
	  LocalDate start_date;
	  LocalDate end_date;
	  LocalTime start_time;
	  LocalTime end_time;
	  int requiredLvl;
	 

	  public enum WishStatus {
	  PENDING,
	  APPROVED
	  }
	 

	  WishStatus status;
	 

	  public Wish(String type, String wishID, String title, String desc, LocalDate start_date, LocalDate end_date,
	  LocalTime start_time, LocalTime end_time, int requiredLvl) {
	  this.type = type;
	  this.wishID = wishID;
	  this.title = title;
	  this.desc = desc;
	  this.start_date = start_date;
	  this.end_date = end_date;
	  this.start_time = start_time;
	  this.end_time = end_time;
	  this.requiredLvl = requiredLvl;
	  this.status = WishStatus.PENDING;
	  }
	

	  @Override
	  public String toString() {
	  StringBuilder sb = new StringBuilder();
	  sb.append("Wish{ID=").append(wishID)
	    .append(", Type=").append(type)
	    .append(", Title=").append(title)
	    .append(", Description=\"").append(desc).append("\"")
	    .append(", Status=").append(status);
	 

	  if (type.equals("WISH2")) {
	   sb.append(", Start=")
	     .append(start_date != null ? start_date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "null")
	     .append(" ")
	     .append(start_time != null ? start_time.format(DateTimeFormatter.ofPattern("HH:mm")) : "null")
	     .append(", End=")
	     .append(end_date != null ? end_date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "null")
	     .append(" ")
	     .append(end_time != null ? end_time.format(DateTimeFormatter.ofPattern("HH:mm")) : "null");
	  }
	 

	  sb.append(", Required Level=").append(requiredLvl).append("}");
	 

	  return sb.toString();
	  }
}

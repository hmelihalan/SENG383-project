import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Task {
	
	String type; //TASK1 OR TASK2
	int taskID;
	String assigner; //T FOR TEACHER F FOR FAMILY
	String title;
	String desc;
	LocalDate deadlineDate;
	LocalTime deadlineTime;
	
	LocalDate start_date;
	LocalDate end_date;
	LocalTime start_time;
	LocalTime end_time;
	
	int pointVal;
	
	TaskStatus status; // Track task status
	
	public enum TaskStatus {
	    PENDING,   // Task is assigned but not completed
	    COMPLETED, // Task is completed by the child but not approved
	    APPROVED   // Task is reviewed and approved by a parent
	}
	
	
		public Task(String type, int taskID, String assigner, String title, String desc, LocalDate deadlineDate,
			LocalTime deadlineTime, LocalDate start_date, LocalDate end_date, LocalTime start_time, LocalTime end_time,
			int pointVal) {
		super();
		this.type = type;
		this.taskID = taskID;
		this.assigner = assigner;
		this.title = title;
		this.desc = desc;
		this.deadlineDate = deadlineDate;
		this.deadlineTime = deadlineTime;
		this.start_date = start_date;
		this.end_date = end_date;
		this.start_time = start_time;
		this.end_time = end_time;
		this.pointVal = pointVal;
		this.status = TaskStatus.PENDING;
	}
	
		@Override
		  public String toString() {
		  StringBuilder sb = new StringBuilder();
		  sb.append("Task{ID=").append(taskID)
		    .append(", Type=").append(type)
		    .append(", Assigner=").append(assigner)
		    .append(", Title=").append(title)
		    .append(", Description=\"").append(desc).append("\"")
		    .append(", Status=").append(status);
		 

		  if (type.equals("TASK1")) {
		   sb.append(", Deadline=")
		     .append(deadlineDate != null ? deadlineDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "null")
		     .append(" ")
		     .append(deadlineTime != null ? deadlineTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "null");
		  } else if (type.equals("TASK2")) {
		   sb.append(", Start=")
		     .append(start_date != null ? start_date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "null")
		     .append(" ")
		     .append(start_time != null ? start_time.format(DateTimeFormatter.ofPattern("HH:mm")) : "null")
		     .append(", End=")
		     .append(end_date != null ? end_date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "null")
		     .append(" ")
		     .append(end_time != null ? end_time.format(DateTimeFormatter.ofPattern("HH:mm")) : "null");
		  }
		 

		  sb.append(", Points=").append(pointVal).append("}");
		 

		  return sb.toString();
		  }
		
		
}

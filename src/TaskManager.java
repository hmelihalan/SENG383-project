import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.lang.Integer;
import java.nio.file.Paths;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;




public class TaskManager {
	  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	  private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	  private static final String TASKS_FILE_PATH = Paths.get("src", "resources", "Tasks.txt").toString();
	
	public static HashMap<Integer, Task> tasks = new HashMap<>();

	public static void addTask(String taskCommand) {
		 Task newTask = parseTask(taskCommand);
	        if (newTask != null) {
	            tasks.put(newTask.taskID, newTask);
	            System.out.println("Task added: " + newTask);
	            recordTasksToFile();
	        } else {
	            System.out.println("Invalid task format: " + taskCommand);
	        }

		// TODO Auto-generated method stub


	}

	public static void completeTask(String taskCommand) {
		String[] parts = taskCommand.split(" ");
		int curID = Integer.parseInt(parts[1]);
		Task curTask = tasks.get(curID);
		 if (curTask != null) {
             curTask.status = Task.TaskStatus.COMPLETED;
             System.out.println("Task " + curID + " marked as completed.");
             recordTasksToFile();
         } else {
             System.out.println("Task " + curID + " not found.");
         }
		
		
		// TODO Auto-generated method stub

	}

	public static void approveTask(String taskCommand) {
		 String[] parts = taskCommand.split(" ");
	        try {
	            int taskID = Integer.parseInt(parts[1]);
	            int rating = Integer.parseInt(parts[2]);
	            Task task = tasks.get(taskID);
	            if (task != null) {
	                task.status = Task.TaskStatus.APPROVED;
	                int pointsGained = calculateAwardedPoints(task.pointVal,rating);
	                Child.pointsTotal += pointsGained;
	                Child.levelCalculator();
	                System.out.println("Task " + taskID + " approved with rating " + rating + " Points gained: " + pointsGained + " Total points: " + Child.pointsTotal);
	                recordTasksToFile();
	            } else {
	                System.out.println("Task " + taskID + " not found.");
	            }
	        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
	            System.out.println("Invalid approve task format: " + taskCommand);
	        }
		// TODO Auto-generated method stub

	}

	private static Task parseTask(String taskCommand) {
		try {
			// Splitting the command into parts
			//String[] parts = taskCommand.split(" ");
			String[] parts = taskCommand.split("\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
			LocalDate deadlineDate = null;
			LocalTime deadlineTime = null;
			 
			
			LocalDate startDate = null;
			LocalDate endDate = null;
			LocalTime startTime = null;
			LocalTime  endTime = null;
			
			int pointVal = 0;
			
			String type = parts[0]; // ADD_TASK1 or ADD_TASK2
			String assigner = parts[1]; // "T" for Teacher, "F" for Family
			String taskIDs = parts[2];
			
			int taskID = Integer.parseInt(taskIDs); // Task ID
			
			String title = parts[3].replace("\"", ""); // Title
			String desc = parts[4].replace("\"", ""); // Description
			
			
			// Check if the task has a start and end time (TASK2)
			if(type.equals("ADD_TASK1")) {
				 startDate = null;
				 endDate = null;
				 startTime = null;
				 endTime = null;
				 deadlineDate = LocalDate.parse(parts[5]); // Deadline date
				 deadlineTime = LocalTime.parse(parts[6]); // Deadline time
				 pointVal = Integer.parseInt(parts[7]); // Points
			}
			
			else if (type.equals("ADD_TASK2")) {
				deadlineDate = null;
				deadlineTime = null;
				startDate = LocalDate.parse(parts[5]); // Start date
				startTime = LocalTime.parse(parts[6]); // Start time
				endDate = LocalDate.parse(parts[7]); // End date
				endTime = LocalTime.parse(parts[8]); // End time
				pointVal = Integer.parseInt(parts[9]); // Points
			}

			String shortType = type.substring(4); // Extract "TASK1" or "TASK2"
		
			return new Task(shortType, taskID, assigner, title, desc, deadlineDate, deadlineTime, startDate, endDate, startTime,endTime,pointVal);
		} catch (Exception e) {
			System.out.println("Error parsing task: " + taskCommand);
			e.printStackTrace();
			return null;
		}
		
	}
	
	 private static int calculateAwardedPoints(int taskPoints, int rating) {
	        double percentage = (double) rating / 5.0; // Calculate percentage (e.g., 4 stars = 0.8)
	        return (int) (taskPoints * percentage); // Calculate awarded points (e.g., 100 points * 0.8 = 80 points)
	    }
	
	
	 private static void recordTasksToFile() {
		  try (BufferedWriter writer = new BufferedWriter(new FileWriter(TASKS_FILE_PATH))) { // Overwrite
		   for (Task task : tasks.values()) {
		    writer.write(taskToLine(task));
		    writer.newLine();
		   }
		  } catch (IOException e) {
		   System.err.println("Error writing tasks to file: " + e.getMessage());
		  }
		  }
	
	 
	 private static String taskToLine(Task task) {
		  String line = task.type + " " + task.assigner + " " + task.taskID + " \"" + task.title + "\" \""
		    + task.desc + "\" " + (task.deadlineDate != null ? dateFormatter.format(task.deadlineDate) : "null") + " "
		    + (task.deadlineTime != null ? timeFormatter.format(task.deadlineTime) : "null") + " "
		    + (task.start_date != null ? dateFormatter.format(task.start_date) : "null") + " "
		    + (task.end_date != null ? dateFormatter.format(task.end_date) : "null") + " "
		    + (task.start_time != null ? timeFormatter.format(task.start_time) : "null") + " "
		    + (task.end_time != null ? timeFormatter.format(task.end_time) : "null") + " "
		    + task.pointVal + " " + task.status; // Includes status
		 

		  return line;
		  }
	 
	 
	 
	 public static void loadTasksFromFile() {
		  try (BufferedReader reader = new BufferedReader(new FileReader(TASKS_FILE_PATH))) {
		   String line;
		   while ((line = reader.readLine()) != null) {
		    Task task = parseTaskFromFileLine(line);
		    if (task != null) {
		     tasks.put(task.taskID, task);
		    }
		   }
		  } catch (IOException e) {
		   System.err.println("Error loading tasks from file: " + e.getMessage());
		  }
		  }
	 
	 private static Task parseTaskFromFileLine(String line) {
		 String[] parts = line.split("\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
		  if (parts.length < 8) {
		   System.err.println("Invalid task data in file: " + line);
		   return null;
		  }
		 

		  try {
		   String type = parts[0];
		   String assigner = parts[1];
		   int taskID = Integer.parseInt(parts[2]);
		   String title = parts[3].replace("\"", "");
		   String desc = parts[4].replace("\"", "");
		   LocalDate deadlineDate = parts[5].equals("null") ? null : LocalDate.parse(parts[5], dateFormatter);
		   LocalTime deadlineTime = parts[6].equals("null") ? null : LocalTime.parse(parts[6], timeFormatter);
		 

		   LocalDate startDate = null;
		   LocalDate endDate = null;
		   LocalTime startTime = null;
		   LocalTime endTime = null;
		 

		   if (type.startsWith("TASK2") && parts.length >= 12) { // Explicit check for ADD_TASK2
		    startDate = parts[7].equals("null") ? null : LocalDate.parse(parts[7], dateFormatter);
		    endDate = parts[8].equals("null") ? null : LocalDate.parse(parts[8], dateFormatter);
		    startTime = parts[9].equals("null") ? null : LocalTime.parse(parts[9], timeFormatter);
		    endTime = parts[10].equals("null") ? null : LocalTime.parse(parts[10], timeFormatter);
		   } else if (type.startsWith("TASK1") && parts.length >= 8) { // Explicit check for ADD_TASK1
		    startDate = null;
		    endDate = null;
		    startTime = null;
		    endTime = null;
		   }
		 

		   int pointVal = Integer.parseInt(parts[parts.length - 2]);
		   Task.TaskStatus status = Task.TaskStatus.valueOf(parts[parts.length - 1]);
		 

		   return new Task(type, taskID, assigner, title, desc, deadlineDate, deadlineTime, startDate, endDate, startTime,
		     endTime, pointVal);
		  } catch (DateTimeParseException e) {
		   System.err.println("Error parsing date/time from file: " + line + " - " + e.getMessage());
		  } catch (NumberFormatException e) {
		   System.err.println("Error parsing number from file: " + line + " - " + e.getMessage());
		  } catch (IllegalArgumentException e) {
		   System.err.println("Error parsing task status from file: " + line + " - " + e.getMessage());
		  } catch (Exception e) {
		   System.err.println("Error parsing task from file line: " + line + " - " + e.getMessage());
		  }
		  return null;
		 
		  }
	 
	 public static void listAllTasks() {
		  System.out.println("--- All Tasks ---");
		  if (tasks.isEmpty()) {
		   System.out.println("No tasks to display.");
		  } else {
		   for (Task task : tasks.values()) {
		    System.out.println(task); // Use the modified toString()
		   }
		  }
		  System.out.println("-----------------");
		  }
	 
}

	
	
	/* I'm planning to store the task and wish lists as a list data structure in the memory. Hashmap,linkedlist,array etc. That way
	 * handling wishes and tasks will be easier*/


/* DO NOT FORGET TO ADD A recordToFile METHOD. It will be called at the end of
 *  each method that modifies or adds a task and it'll take the current tasks ID.
 *  Inside the file recorder method the method will get the needed task by 
 *  it's id from the hashmap and record that task into the file
 *  
 *  a similar method will also be called at startup to record the task and wish files into the hashmap(memory)
 *  
 *  
 *   */ 




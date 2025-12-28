 import java.time.LocalDate;
 import java.time.LocalTime;
 import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Map;
 import java.io.*;
 import java.nio.file.Paths;
 
public class WishManager {
	 public static Map<String, Wish> wishes = new HashMap<>(); // Changed key back to String
	  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	  private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	  private static final String WISHES_FILE_PATH = Paths.get("src", "resources", "Wishes.txt").toString();
	 

	  public static void addWish(String commandLine) {
	  Wish newWish = parseWish(commandLine);
	  if (newWish != null) {
	   wishes.put(newWish.wishID, newWish);
	   System.out.println("Wish added: " + newWish);
	   recordWishesToFile();
	  } else {
	   System.out.println("Invalid wish format: " + commandLine);
	  }
	  }
	  
	  private static Wish parseWish(String commandLine) {
		  String[] parts = commandLine.split("\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
		  if (parts.length < 3) {
		   System.out.println("Invalid wish format: " + commandLine);
		   return null;
		  }
		 

		  try {
			  
			   LocalDate start_date = null;
			   LocalDate end_date = null;
			   LocalTime start_time = null;
			   LocalTime end_time = null;
			  
			  
			  
		   String type = parts[0];
		   String wishId = parts[1]; // Changed back to String
		   String title = parts[2].replace("\"", "");
		   String desc = parts[3].replace("\"", "");

		   if(type.equals("ADD_WISH1")) {
			   start_date = null;
			   end_date = null;
			   start_time = null;
			   end_time = null;
		   }

		   else if (type.equals("ADD_WISH2")) {
				
			   start_date = LocalDate.parse(parts[4]); // Start date
			   start_time = LocalTime.parse(parts[5]); // Start time
			   end_date = LocalDate.parse(parts[6]); // End date
			   end_time = LocalTime.parse(parts[7]); // End time
				
			}
		 

		   return new Wish(type.substring(4), wishId, title, desc, start_date, end_date, start_time, end_time, 0);
		 

		  } catch (Exception e) {
		   System.out.println("Error parsing wish: " + commandLine + " - " + e.getMessage());
		   return null;
		  }
		  }
	
	  public static void approveWish(String commandLine) {
		  String[] parts = commandLine.split("\\s+");
		  try {
		   if (parts.length < 2) {
		    System.out.println("Invalid approve wish format: " + commandLine);
		    return;
		   }
		   String wishId = parts[1]; // Changed back to String
		   String approvalStatus = parts[2];
		   Wish wish = wishes.get(wishId);
		 

		   if (wish != null) {
		    if (approvalStatus.equalsIgnoreCase("APPROVED")) {
		     wish.status = Wish.WishStatus.APPROVED;
		     if (parts.length == 4) {
		      wish.requiredLvl = Integer.parseInt(parts[3]);
		     }
		     System.out.println("Wish " + wishId + " approved.");
		    } else if (approvalStatus.equalsIgnoreCase("REJECTED")) {
		     wishes.remove(wishId);
		     System.out.println("Wish " + wishId + " rejected.");
		    } else {
		     System.out.println("Invalid approval status: " + approvalStatus);
		    }
		    recordWishesToFile();
		   } else {
		    System.out.println("Wish " + wishId + " not found.");
		   }
		  } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
		   System.out.println("Invalid approve wish format: " + commandLine);
		  }
		  }
	  
	  public static void listAllWishes() {
		  System.out.println("--- All Wishes ---");
		  if (wishes.isEmpty()) {
		   System.out.println("No wishes to display.");
		  } else {
		   for (Wish wish : wishes.values()) {
		    System.out.println(wish);
		   }
		  }
		  System.out.println("-----------------");
		  }
	  
	  private static void recordWishesToFile() {
		  try (BufferedWriter writer = new BufferedWriter(new FileWriter(WISHES_FILE_PATH))) {
		   for (Wish wish : wishes.values()) {
		    writer.write(wishToLine(wish));
		    writer.newLine();
		   }
		  } catch (IOException e) {
		   System.err.println("Error writing wishes to file: " + e.getMessage());
		  }
		  }
	  
	  private static String wishToLine(Wish wish) {
		  String line = wish.type + " " + wish.wishID + " \"" + wish.title + "\" \""
		    + wish.desc + "\" " + (wish.start_date != null ? dateFormatter.format(wish.start_date) : "null") + " "
		    + (wish.end_date != null ? dateFormatter.format(wish.end_date) : "null") + " "
		    + (wish.start_time != null ? timeFormatter.format(wish.start_time) : "null") + " "
		    + (wish.end_time != null ? timeFormatter.format(wish.end_time) : "null") + " "
		    + wish.requiredLvl + " " + wish.status;
		  return line;
		  }
	  

	  public static void loadWishesFromFile() {
	  try (BufferedReader reader = new BufferedReader(new FileReader(WISHES_FILE_PATH))) {
	   String line;
	   while ((line = reader.readLine()) != null) {
	    Wish wish = parseWishFromFileLine(line);
	    if (wish != null) {
	     wishes.put(wish.wishID, wish);
	    }
	   }
	  } catch (IOException e) {
	   System.err.println("Error loading wishes from file: " + e.getMessage());
	  }
	  }
	  
	  
	  private static Wish parseWishFromFileLine(String line) {
		  String[] parts = line.split("\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
		  if (parts.length < 6) {
		   System.err.println("Invalid wish data in file: " + line);
		   return null;
		  }
		 

		  try {
		   String type = parts[0];
		   String wishId = parts[1]; // Changed back to String
		   String title = parts[2].replace("\"", "");
		   String desc = parts[3].replace("\"", "");
		 

		   LocalDate start_date = parts[4].equals("null") ? null : LocalDate.parse(parts[4], dateFormatter);
		   LocalDate end_date = parts[5].equals("null") ? null : LocalDate.parse(parts[5], dateFormatter);
		   LocalTime start_time = parts.length > 6 && !parts[6].equals("null") ? LocalTime.parse(parts[6], timeFormatter) : null;
		   LocalTime end_time = parts.length > 7 && !parts[7].equals("null") ? LocalTime.parse(parts[7], timeFormatter) : null;
		 

		   int requiredLvl = Integer.parseInt(parts[parts.length - 2]);
		   Wish.WishStatus status = Wish.WishStatus.valueOf(parts[parts.length - 1]);
		 

		   return new Wish(type, wishId, title, desc, start_date, end_date, start_time, end_time, requiredLvl);
		  } catch (DateTimeParseException e) {
		   System.err.println("Error parsing date/time from file: " + line + " - " + e.getMessage());
		  } catch (NumberFormatException e) {
		   System.err.println("Error parsing number from file: " + line + " - " + e.getMessage());
		  } catch (IllegalArgumentException e) {
		   System.err.println("Error parsing wish status from file: " + line + " - " + e.getMessage());
		  } catch (Exception e) {
		   System.err.println("Error parsing wish from file line: " + line + " - " + e.getMessage());
		  }
		  return null;
		  }
	  
	/* I'm planning to store the task and wish lists as a list data structure in the memory. Hashmap,linkedlist,array etc. That way
	 * handling wishes and tasks will be easier*/

}

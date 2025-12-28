
public class Child {
	public static int pointsTotal;
	public static int level;
	
	
	
	 public static void levelCalculator() {
		  int averagePoints = pointsTotal; // Assuming pointsTotal holds the average
		 

		  if (averagePoints >= 0 && averagePoints < 40) {
		   level = 1;
		  } else if (averagePoints >= 40 && averagePoints < 60) {
		   level = 2;
		  } else if (averagePoints >= 60 && averagePoints < 80) {
		   level = 3;
		  } else if (averagePoints >= 80) {
		   level = 4;
		  } else {
		   level = 0; // Or any default value for invalid points
		  }
		  }
	
	 public static void addBudgetCoin(String commandLine) { // Changed parameter
		  String[] parts = commandLine.split("\\s+");
		  if (parts.length == 2 && parts[0].equals("ADD_BUDGET_COIN")) {
		   try {
		    int amount = Integer.parseInt(parts[1]);
		    if (amount > 0) {
		     pointsTotal += amount;
		     System.out.println("Added " + amount + " coins to the budget. Current budget: " + pointsTotal);
		    } else {
		     System.out.println("Invalid amount. Amount must be positive.");
		    }
		   } catch (NumberFormatException e) {
		    System.out.println("Invalid amount format: " + parts[1]);
		   }
		  } else {
		   System.out.println("Invalid ADD_BUDGET_COIN format: " + commandLine);
		  }
		  }
	
	
	
	 public static void printBudget() {
		  System.out.println("Current budget: " + pointsTotal + " points ");
		  }
		 

		  public static void printStatus() {
		  System.out.println("Current level: " + level);
		  System.out.println("Total points: " + pointsTotal);
		  }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

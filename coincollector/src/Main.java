import java.sql.SQLOutput;
import java.util.*;

class Pair<A,B>{
    public A first;
    public B second;
    public Pair(A first,B second){
        this.first = first;
        this.second = second;
    }
    @Override
    public String toString(){
        return "("+first+","+second+")";
    }
}


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HashMap<String, ArrayList<Pair<Integer, Integer>>> inputs = new HashMap<>();
        ArrayList<List<String>> list1 = new ArrayList<>();
        ArrayList<List<String>> list2 = new ArrayList<>();
        ArrayList<List<String>> list3 = new ArrayList<>();

        list1.add(Arrays.asList("Zulian", "Razzashi", "Hakkari"));           // Set I
        list2.add(Arrays.asList("Sandfury", "Skullsplitter", "Bloodscalp")); // Set II
        list3.add(Arrays.asList("Gurubashi", "Vilebranch", "Witherbark"));   // Set III


        inputs.put("Zulian",new ArrayList<>());
        inputs.put("Razzashi",new ArrayList<>());
        inputs.put("Hakkari" ,new ArrayList<>());
        inputs.put("Sandfury",new ArrayList<>());
        inputs.put("Skullsplitter",new ArrayList<>());
        inputs.put("Bloodscalp",new ArrayList<>());
        inputs.put("Gurubashi",new ArrayList<>());
        inputs.put("Vilebranch",new ArrayList<>());
        inputs.put("Witherbark",new ArrayList<>());


       int N = sc.nextInt();
       int M = sc.nextInt();

       for (int i = 0; i < M; i++) {
           String name = sc.next();
           int quantity = sc.nextInt();
           int price = sc.nextInt();

           inputs.get(name).add(new Pair<>(quantity, price));
       }

       for (ArrayList<Pair<Integer, Integer>> i : inputs.values()) {
           System.out.println(i);
       }
       for (String i : inputs.keySet()) {
            System.out.println(i);
            System.out.println(inputs.get(i));
        }




    }

}


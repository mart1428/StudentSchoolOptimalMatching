import java.util.ArrayList;
import java.util.Arrays;

public class Participant {
    private String name;                //name
    private int[] rankings;             //rankings of participants
    private ArrayList<Integer> matches = new ArrayList<Integer>();      //match indices
    private int regret;                 //total regret
    private int maxMatches;             //max # of allowed matches/openings

    //methods to handle matches
    public void clearMatches(){
        //clear all matches
        matches = new ArrayList<Integer>();
    }

    public int findRankingByID(int k){
        //find rank of participant k
        for(int i = 0; i < rankings.length; i++){ //find ranking by id
            if(rankings[i] == k){
                return i;
            }
        }
        return -1;
    }

    public int getWorstMatch(){
        //find the worst-matched participant
        int worstMatchIndex = 0;
        if(matches.size() > 1) {
            for (int i = 0; i < maxMatches; i++) {
                if(findRankingByID(matches.get(worstMatchIndex) + 1) < findRankingByID(matches.get(i) + 1)){ //compare ranks
                    worstMatchIndex = i;
                }
            }
        }
        return worstMatchIndex;
    }

    public void unmatch(int k){
        //remove the match with participant k
        for(int i = 0; i < matches.size(); i++) {   //remove participant k from the list
            if(matches.get(i) == k) {
                matches.remove(i);
                break;
            }
        }
    }

    public boolean matchExists(int k){
        //check if match to participant k exists
        for(int i = 0; i < matches.size(); i++){
            if(matches.get(i) == k){
                return true;
            }
        }

        return false;
    }

    public int getSingleMatchedRegret(int k){
        //get regret from match with k
        int kRegret = findRankingByID(k) - 1;
        return kRegret;
    }

    public void calcRegret(){
        //calculate total regret over all matches
        for(int i = 0; i < matches.size(); i++){        //calc total regret
            regret += findRankingByID(matches.get(i) + 1);
        }
    }

    //methods to edit data from the user
    public void editInfo(ArrayList<? extends Participant> P){
        //method is overridden in child classes
        String prompt = "\nName: ";
        setName(Pro5_mart1428.getString(prompt));

        prompt = "\nMaximum number of matches: ";
        setMaxMatches(Pro5_mart1428.getInteger(prompt, 1, P.size()));
    }

    public void editRankings(ArrayList<? extends Participant> P){
        //general ranking edits
        setNParticipants(P.size());
        System.out.println("\nParticipant " + this.getName() + "'s rankings:");

        for(int i = 0; i < P.size(); i++){ //Assigning ranks for each school
            boolean setFlag;
            String prompt = "School " + P.get(i).getName() + ": ";
            int intInput = Pro5_mart1428.getInteger(prompt, 1, P.size());
            setFlag = true;

            for(int j = 0; j < P.size(); j++){ //determine whether there is an error in the input
                if(rankings[j] == intInput){
                    System.out.println("ERROR: Rank " + intInput + " already used!\n");
                    i--;
                    setFlag = false;
                    break;
                }
            }

            if(setFlag){ //if there is no error on the input
                this.setRanking(i, intInput);
            }
        }
    }

    //print methods
    public void print(ArrayList<? extends Participant> P){
        //method is overridden in child classes
        if(getNMatches() != 0){ //if school has been assigned to the student
            String s = "";
            for(int i = 0; i < getMaxMatches(); i++){
                s += P.get(getMatch(i)).getName();
                if(i < getMaxMatches() - 1){
                    s += ", ";
                }
            }
            System.out.format("%-40s%8d%8.2f  %-40s", getName(), getMaxMatches(), "-", s);
        }else { //if school has not been assigned to student
            System.out.format("%-40s%8d%8.2f  %-40s", getName(), getMaxMatches(), "-", "-");
        }

        if(P.size() != 0) {
            printRankings(P);
        }else{
            System.out.println("-");
        }
    }

    public void printRankings(ArrayList<? extends Participant> P){
        //Printing rankings in order separated by comma
        String s = "";
        for(int i = 0; i < P.size(); i++){
            s += P.get(this.getRanking(i) - 1).getName();

            if(i < P.size() - 1){
                s += ", ";
            }
        }
        System.out.print(s + "\n");
    }

    public void getMatchNames(ArrayList<? extends Participant> P){
        //get matches names
        String s = "";
        for(int i = 0; i < matches.size(); i++){
            s += P.get(matches.get(i));

            if(i < P.size() - 1){
                s += ", ";
            }
        }
        System.out.println(s);
    }

    //check if this participant has valid info
    public boolean isValid(){
        //method is overridden in child classes
        if(getMaxMatches() >= 1){
            return true;
        }else{
            return false;
        }
    }

//    public void sortMatches(){
//        matches.sort(null);
//    }

    //constructors
    public Participant(){
        this(null, 0, 0);
    }

    public Participant(String name, int maxMatches, int nParticipants){
        this.name = name;
        this.maxMatches = maxMatches;
        this.rankings = new int[nParticipants];
        this.regret = 0;
        matches = new ArrayList<Integer>(maxMatches);
    }

    //getters
    public String getName(){
        return this.name;
    }

    public int getRanking(int i){
        return rankings[i];
    }

    public int getMatch(int i){
        return matches.get(i);
    }

    public int getRegret(){
        return regret;
    }

    public int getMaxMatches(){
        return maxMatches;
    }

    public int getNMatches(){
        return matches.size();
    }

    public int getNParticipants(){
        //return length of rankings array
        return rankings.length;
    }

    //setters
    public void setName(String name){
        this.name = name;
    }

    public void setRanking(int i, int r){
        this.rankings[i] = r;
    }

    public void setMatch(int m){
        matches.add(m);
    }

    public void setRegret(int r){
        this.regret = r;
    }

    public void setNParticipants(int n){
        //set rankings array size
        this.rankings = new int[n];
    }

    public void setMaxMatches(int n){
        this.maxMatches = n;
    }
}

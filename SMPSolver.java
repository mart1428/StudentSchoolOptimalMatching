import java.util.ArrayList;

public class SMPSolver {
    private ArrayList<Participant> S = new ArrayList<Participant>(); //suitors
    private ArrayList<Participant> R = new ArrayList<Participant>(); //receivers
    private double avgSuitorRegret;                                  //average suitor regret
    private double avgReceiverRegret;                                //average receiver regret
    private double avgTotalRegret;                                   //average total regret
    private boolean matchesExist;                                    //whether or not matches exist
    private boolean stable;                                          //whether or not matching is stable
    private long compTimes;                                          //computation time
    private boolean suitorFirst;                                     //whether to print suitor stats first

    //Quiz


    //methods for matching
    public void clearMatches(){
        //clear out existing matches
        S = new ArrayList<>();
        R = new ArrayList<>();
        avgSuitorRegret = 0;
        avgReceiverRegret = 0;
        avgTotalRegret = 0;
        matchesExist = false;
    }

    public boolean matchingCanProceed(){
        //check that matching rules are satisfied
        if(S.size() < 1){
            System.out.println("\nERROR: No suitors are loaded!\n");
        }else if(R.size() < 1){
            System.out.println("\nERROR: No receivers are loaded!\n");
        }else if(getNSuitorOpenings() != getNReceiverOpenings()){
            System.out.println("\nERROR: The number of suitor and receiver openings must be equal!\n");
        }else{
            return true;
        }
        return false;
    }

    public boolean match(){
        //Gale-Shapley Algorithm to match; students are suitors
        long startTime = System.currentTimeMillis();

        if(matchingCanProceed()){
            int i = 0;      //helper
            int j = 0;      //helper
            boolean exitFlag = false;

            do{
                if(S.get(i).getNMatches() < S.get(i).getMaxMatches()) {     //check if the suitor can propose more matches
                    if (makeProposal(i, S.get(i).getRanking(j) - 1)) {  //suitor propose receiver according to the rank
                    } else {        //if the proposal is rejected, go to next rank
                        j++;
                        exitFlag = false;
                    }
                }else{      //if the suitor can't propose more matches, go to next suitor
                    i++;
                    j = 0;
                }

                if(i == S.size()){      //check if it has iterated until the end of the list
                    exitFlag = true;
                }


                if(exitFlag){       //recheck if the system can exit the iteration
                    for(int k = 0; k < S.size(); k++){
                        if(S.get(k).getMaxMatches() > S.get(k).getNMatches()){  //check if there is any suitor that has not had a match
                            exitFlag = false;       //if not, then go to the iteration again
                            i = k;
                            j = 0;
                            break;
                        }
                    }
                }


            }while(!exitFlag);

//            int i = 0;
//            int j = 0;
//
//            for (j = 0; j < R.size(); j++) {
//                for (i = 0; i < S.size(); i++) {
//                    if(S.get(i).getNMatches() < S.get(i).getMaxMatches()) {
//                        for(int k = 0; k < j + 1; k++) {
//                            if(makeProposal(i, S.get(i).getRanking(k) - 1)){
//                                break;
//                            }
//                        }
//                    }
//                }
//            }



            matchesExist = true;
            stable = determineStability();      //determine stability
            calcRegrets();              //calculate the regrets
            compTimes = System.currentTimeMillis() - startTime;     //print time
            printStats();               //print statistics

            if(S.size() > R.size()) {
                System.out.println(S.size() + " matches made in " + compTimes + "ms!\n");

            }else{
                System.out.println(R.size() + " matches made in " + compTimes + "ms!\n");
            }
            return true;
        }else{
            matchesExist = false;
            return false;
        }
    }

    private boolean makeProposal(int suitor, int receiver){
        //suitor proposes

        if(R.get(receiver).getNMatches() < R.get(receiver).getMaxMatches()){        //check if receiver has a match slot
            makeEngagement(suitor, receiver, -1);       //if yes then they're engaged
            return true;
        }else if(R.get(receiver).findRankingByID(R.get(receiver).getMatch(R.get(receiver).getWorstMatch()) + 1) + 1 > (R.get(receiver).findRankingByID(suitor + 1)+1)){
            //if no then compare rankings
            makeEngagement(suitor, receiver, R.get(receiver).getMatch(R.get(receiver).getWorstMatch()));    //if receiver prefers current suitor than the old suitor then they're engaged
            return true;
        }else{
            return false;
        }
    }

    private void makeEngagement(int suitor, int receiver, int oldSuitor){
        //make suitor-receiver engagement, break receiver-oldSuitor engagement

        if(oldSuitor != -1){        //check if the receiver has oldSuitor
            S.get(oldSuitor).unmatch(receiver);
            R.get(receiver).unmatch(oldSuitor);
        }

        //match them
        S.get(suitor).setMatch(receiver);
        R.get(receiver).setMatch(suitor);
    }

    public void calcRegrets(){
        //calculate regrets
        avgSuitorRegret = 0;            //reset the averages
        avgReceiverRegret = 0;
        avgTotalRegret = 0;

        for(int i = 0; i < S.size(); i++){  //calculate suitor averages
            S.get(i).calcRegret();
            avgSuitorRegret += S.get(i).getRegret();
        }

        for(int i = 0; i < R.size(); i++){      //calculate receiver averages
            R.get(i).calcRegret();
            avgReceiverRegret += R.get(i).getRegret();
        }

        avgTotalRegret = (avgSuitorRegret+avgReceiverRegret)/(S.size() + R.size());     //calculate avg total regret
        avgSuitorRegret /= S.size();                //calculate avg suitor regret
        avgReceiverRegret /= R.size();              //calculate avg receiver regret
    }

    public boolean determineStability(){
        //calculate if a matching is stable
        if(S.size() > R.size()) {       //check if the suitors are students
            for (int i = 0; i < S.size(); i++) {      //go through all of the students
                for (int j = 0; j < S.get(i).getRegret(); j++) { // go through all of the student's preference before its current school
                    if (R.get(S.get(i).getRanking(j) - 1).findRankingByID(R.get(S.get(i).getRanking(j) - 1).getMatch(0) + 1) > R.get(S.get(i).getRanking(j) - 1).findRankingByID(i + 1)) {
                        //check if they could "cheat"
                        return false;
                    }
                }
            }
        }else{
            for (int i = 0; i < R.size(); i++) {      //go through all of the students
                for (int j = 0; j < R.get(i).getRegret(); j++) { // go through all of the student's preference before its current school
                    if (S.get(R.get(i).getRanking(j) - 1).findRankingByID(S.get(R.get(i).getRanking(j) - 1).getMatch(0) + 1) > S.get(R.get(i).getRanking(j) - 1).findRankingByID(i + 1)) {
                        //check if they could "cheat"
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //print methods
    public void print(){
        //print the matching results and statistics
        if(matchesExist) {
            printMatches();
            printStats();
        }else{
            System.out.println("ERROR: No matches exist!\n");
        }
    }

    public void printMatches(){
        //print matches
        if(matchesExist()) {
            System.out.println("Matches:\n" +
                    "--------");

            if(R.size() < S.size()) {
                for (int i = 0; i < R.size(); i++) {        //go through each receiver and print its match
//                    R.get(i).sortMatches();
                    String s = "";
                    String matchNames = "";
                    for(int j = 0; j < R.get(i).getMaxMatches(); j++){
                        matchNames += S.get(R.get(i).getMatch(j)).getName();
                        if(j < R.get(i).getMaxMatches() - 1){
                            matchNames += ", ";
                        }
                    }
                    s += R.get(i).getName() + ": " + matchNames;
                    System.out.println(s);
                }
            }else{
                for (int i = 0; i < S.size(); i++) {        //go through each suitor and print its match
                    String s = "";
                    String matchNames = "";
                    for(int j = 0; j < S.get(i).getMaxMatches(); j++){
                        matchNames += R.get(S.get(i).getMatch(j)).getName();
                        if(j < S.get(i).getMaxMatches() - 1){
                            matchNames += ", ";
                        }
                    }
                    s += S.get(i).getName() + ": " + matchNames;
                    System.out.println(s);
                }
            }
        }
    }

    public void printStats(){
        //print matching statistics
        String s;
        s = (isStable()) ? "Yes" : "No";
        System.out.format("\nStable matching? %s\n" +
                        "Average suitor regret: %.2f\n" +
                        "Average receiver regret: %.2f\n" +
                        "Average total regret: %.2f\n\n",
                s, getAvgSuitorRegret(), getAvgReceiverRegret(), getAvgTotalRegret());
    }

    public void printStatsRow(String rowHeading){
        //print stats as row
        if(suitorFirst){
            System.out.format("%-17s%11s%21.2f%21.2f%21.2f%21s\n", "Student optimal", isStable() ? "Yes" : "No", getAvgReceiverRegret(), getAvgSuitorRegret(), getAvgTotalRegret(), getTime());
        }else{
            System.out.format("%-17s%11s%21.2f%21.2f%21.2f%21s\n", "School optimal", isStable() ? "Yes" : "No", getAvgSuitorRegret(), getAvgReceiverRegret(), getAvgTotalRegret(), getTime());
        }
    }


    //constructors
    public SMPSolver(){
        avgSuitorRegret = 0;
        avgReceiverRegret = 0;
        avgTotalRegret = 0;
        matchesExist = false;
        stable = false;
        compTimes = 0;
        suitorFirst = false;
    }

    //getters
    public double getAvgSuitorRegret(){
        return Math.round(this.avgSuitorRegret*100d)/100d;
    }

    public double getAvgReceiverRegret(){
        return Math.round(this.avgReceiverRegret*100d)/100d;
    }

    public double getAvgTotalRegret(){
        return Math.round(this.avgTotalRegret*100d)/100d;
    }

    public boolean matchesExist(){
        return matchesExist;
    }

    public boolean isStable(){
        return stable;
    }

    public long getTime(){
        return compTimes;
    }

    public int getNSuitorOpenings() {
        int suitorOpenings = 0;
        for(int i = 0; i < S.size(); i++){
            suitorOpenings += S.get(i).getMaxMatches();
        }
        return suitorOpenings;
    }

    public int getNReceiverOpenings(){
        int receiverOpenings = 0;
        for(int i = 0; i < R.size(); i++){
            receiverOpenings += R.get(i).getMaxMatches();
        }
        return receiverOpenings;
    }

    //setters
    public void setMatchesExist(boolean b){
        matchesExist = b;
    }

    public void setSuitorFirst(boolean b){
        suitorFirst = b;
    }

    public void setParticipants(ArrayList<? extends Participant> S, ArrayList<? extends Participant> R){
        clearMatches();
        for(int i = 0; i < S.size(); i++){
            S.get(i).setRegret(0);
            this.S.add(S.get(i));
        }

        for(int i = 0; i < R.size(); i++){
            R.get(i).setRegret(0);
            this.R.add(R.get(i));
        }
    }
}

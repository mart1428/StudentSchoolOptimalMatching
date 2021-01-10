import java.util.ArrayList;

public class School extends Participant{
    private double alpha;       //GPA weight

    //Quiz

    //get new info from the user; cannot be inherited or overridden from parent
    public void editSchoolInfo(ArrayList<Student> S, boolean canEditRankings){
        String prompt;

        prompt = "\nName: ";
        setName(Pro5_mart1428.getString(prompt));

        prompt = "GPA weight: ";
        setAlpha(Pro5_mart1428.getDouble(prompt, 0, 1));

        prompt = "Maximum number of matches: ";
        setMaxMatches(Pro5_mart1428.getInteger(prompt, 1, S.size()));

        if(canEditRankings){ //if rankings has been set before then recalculate the rankings
            calcRankings(S);
        }

        if (getRegret() != 0) {
            calcRegret();
        }
    }

    public void calcRankings(ArrayList<Student> S){
        //calc rankings from alpha
        ArrayList<Double> score = new ArrayList<>();              //initialize a new arraylist for the scores
        setNParticipants(S.size());                         //re-initialize rankings array size

        for(int i = 0; i < S.size(); i++){
            score.add((alpha * S.get(i).getGPA() + (1 - alpha) * S.get(i).getES()));
        }

        for(int i = 0; i < S.size(); i++){
            int index = findMax(score);
            setRanking(i, index + 1);
            score.set(index, -1d);
        }
    }

    private int findMax(ArrayList<Double> L){//finding the maximum score in the array and returns the index
        int index = 0;

        for (int i = 0; i < L.size(); i++) {
            if (L.get(i) > L.get(index)) {
                index = i;
            }
        }
        return index;
    }

    public void print(ArrayList<? extends Participant> S){
        //print school row
        if(getNMatches() != 0){ //if school has been assigned to the student
            String s = "";
            for(int i = 0; i < getMaxMatches(); i++){
                s += S.get(getMatch(i)).getName();
                if(i < getMaxMatches() - 1){
                    s += ", ";
                }
            }
            System.out.format("%-40s%8d%8.2f  %-40s", getName(), getMaxMatches(), alpha, s);
        }else { //if school has not been assigned to student
            System.out.format("%-40s%8d%8.2f  %-40s", getName(), getMaxMatches(), alpha, "-");
        }

        if(S.size() != 0) {
            printRankings(S);
        }else{
            System.out.println("-");
        }
    }

    public boolean isValid(){
        //check if this school has valid info
        if(alpha >= 0 && alpha <= 1 && super.getMaxMatches() >= 1){
            return true;
        }else{
            return false;
        }
    }

    //constructors
    public School(){
        this(null, 0, 0, 0);
    }

    public School(String name, double alpha, int maxMatches, int nStudents){
        super(name, maxMatches, nStudents);
        this.alpha = alpha;
    }

    //getters
    public double getAlpha(){
        return this.alpha;
    }

    //setters
    public void setAlpha(double alpha){
        this.alpha = alpha;
    }
}

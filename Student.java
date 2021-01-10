import java.util.ArrayList;

public class Student extends Participant{
    private double GPA; //GPA
    private int ES;     //Extracurricular Score


    //Quiz


    //methods
    public void editInfo(ArrayList<School> H, boolean canEditRankings){
      //user info
        String prompt;

        System.out.println();
        prompt = "Name: ";
        setName(Pro5_mart1428.getString(prompt));

        prompt = "GPA: ";
        setGPA(Pro5_mart1428.getDouble(prompt, 0.00, 4.00));

        prompt = "Extracurricular score: ";
        setES(Pro5_mart1428.getInteger(prompt, 0, 5));

        prompt = "Maximum number of matches: ";
        setMaxMatches(Pro5_mart1428.getInteger(prompt, 1, H.size()));

        String choice;
        String error;
        prompt = "Edit rankings (y/n): ";
        error = "ERROR: Choice must be 'y' or 'n'!";
        choice = Pro5_mart1428.getString(prompt, error).toLowerCase();
        if(canEditRankings) {
            if (choice.equals("y")) { //if user wants to edit the rankings preference
                editRankings(H);
                if (getRegret() != 0) {
                    calcRegret();
                }
                System.out.println();
            }
        }
    }

    public void print(ArrayList<? extends Participant> H){
        //print student row
        if(getNMatches() != 0){ //if school has been assigned to the student
            System.out.format("%-40s%8.2f%4d  %-40s", this.getName(), this.getGPA(), this.getES(), H.get(getMatch(0)).getName());
        }else { //if school has not been assigned to student
            System.out.format("%-40s%8.2f%4d  %-40s", this.getName(), this.getGPA(), this.getES(), "-");
        }
        printRankings(H);
    }

    public boolean isValid(){
        //check if this student has valid info
        if(GPA >= 0 && GPA <= 4){
            if(ES >= 0 && ES <= 5){
                for(int i = 0; i < super.getNParticipants() - 1; i++){
                    for (int j = i + 1; j < super.getNParticipants(); j++) { //determine whether there is an error in the input
                        if (super.getRanking(j) == super.getRanking(i)){
                            System.out.println(super.getRanking(j) + " " + super.getRanking(i));
                            return false;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //constructors
    public Student(){
        this(null, 0, 0, 0);
    }

    public Student(String name, double GPA, int ES, int nSchools){
        super(name, 1, nSchools);
        this.GPA = GPA;
        this.ES = ES;
    }

    //getters
    public double getGPA(){
        return GPA;
    }

    public int getES(){
        return ES;
    }

    //setters
    public void setGPA(double GPA){
        this.GPA = GPA;
    }

    public void setES(int ES){
        this.ES = ES;
    }

}

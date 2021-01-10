import java.io.*;
import java.util.ArrayList;

public class Pro5_mart1428 {
    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        String choice;
        String prompt;

        ArrayList<Student> S = new ArrayList<>();
        ArrayList<School> H = new ArrayList<>();

        int nStudents = 0;
        int nSchools = 0;

        SMPSolver solver1 = new SMPSolver();
        SMPSolver solver2 = new SMPSolver();

        boolean matchExist = false;

        do{
            System.out.println("JAVA STABLE MARRIAGE PROBLEM v3\n");
            displayMenu();
            prompt = "Enter choice: ";
            choice = getString(prompt).toUpperCase();

            switch(choice){
                case "L":
                    nSchools = loadSchools(H);
                    nStudents = loadStudents(S, H);
                    for(int i = 0; i < H.size(); i++){ //calculate rankings for schools
                        H.get(i).calcRankings(S);
                    }
                    break;

                case "E":
                    editData(S, H);
                    break;

                case "P":
                    if(S.size() > 0) { //check if students are loaded
                        System.out.println("\nSTUDENTS:\n");
                        printStudents(S, H);
                    }else{
                        System.out.println("\nERROR: No students are loaded!");
                    }
                    if(H.size() > 0) { //check if schools are loaded
                        System.out.println("\nSCHOOLS:\n");
                        printSchools(S, H);
                        System.out.println();
                    }else{
                        System.out.println("\nERROR: No schools are loaded!\n");
                    }
                    break;

                case "M":
                    System.out.println("\nSTUDENT-OPTIMAL MATCHING");
                    solver1.setParticipants(S, H);
                    solver1.match();

                    System.out.println("SCHOOL-OPTIMAL MATCHING");
                    solver2.setParticipants(copySchools(S, H), copyStudents(S, H));
                    solver2.match();

                    matchExist = true;

                    System.out.println(H.get(0).calcMedianRegret());
                    break;

                case "D":
                    if(matchExist) {
                        System.out.println("\nSTUDENT-OPTIMAL SOLUTION\n");
                        solver1.print();
                        System.out.println("SCHOOL-OPTIMAL SOLUTION\n");
                        solver2.print();
                    }
                    break;

                case "X":
                    if(solver1.matchesExist() && solver2.matchesExist()) {
                        printComparison(solver1, solver2);
                    }else{
                        System.out.println("\nERROR: No matches exist!\n");
                    }
                    break;

                case "R": //clear database
                    S = new ArrayList<>();
                    H = new ArrayList<>();
                    nStudents = 0;
                    nSchools = 0;
                    solver1 = new SMPSolver();
                    solver2 = new SMPSolver();
                    matchExist = false;
                    System.out.println("\nDatabase cleared!\n");
                    break;

                case "Q":
                    systemExit();
                    break;

                default:
                    System.out.println("\nERROR: Invalid menu choice!\n");
                    break;
            }

        }while(true);

    }

    public static void displayMenu(){
        //display the menu
        String s =  "L - Load students and schools from file\n" +
                "E - Edit students and schools\n" +
                "P - Print students and schools\n" +
                "M - Match students and schools using Gale-Shapley algorithm\n" +
                "D - Display matches\n" +
                "X - Compare student-optimal and school-optimal matches\n" +
                "R - Reset database\n" +
                "Q - Quit\n";
        System.out.println(s);
    }

    public static int loadStudents(ArrayList<Student> S, ArrayList<School> H) throws IOException{
        //Load student information from a user-provided file and return the number of the new schools. New schools are added to be list of existing schools.
        int nStudents = S.size();
        int nSchools = H.size();
        int loadedStudents = 0;


        String prompt = "\nEnter student file name (0 to cancel): ";
        while(true) {
            String fName = getString(prompt);   //get file name
            if(fName.equals("0")){  //check if the user wants to cancel loading process
                System.out.println("\nFile loading process canceled.\n");
                return nStudents;
            }

            File file = new File(fName);
            if (file.exists()) { //check if the file exists in the directory
                ArrayList<String> lines = new ArrayList<>();
                BufferedReader fReader = new BufferedReader(new FileReader(file));
                while(fReader.ready()){ //read all of the lines
                    lines.add(fReader.readLine());
                }

                nStudents += lines.size();
                try {                   //try to catch an error in loading and parsing the lines
                    for (int i = 0; i < nStudents; i++) {
                        String[] row = lines.get(i).split(",");
                        Student newStudent = new Student(row[0], Double.parseDouble(row[1]), Integer.parseInt(row[2]), nSchools);
                        for (int j = 0; j < nSchools; j++) {
                            newStudent.setRanking(j, Integer.parseInt(row[(3 + j)]));
                        }

                        if (newStudent.isValid()) { //check if the student met the conditions
                            S.add(newStudent);
                            loadedStudents += 1;
                        }
                    }

                }catch (Exception e){ //if there is an error, cancel loading process
                    nStudents = 0;
                    S.clear();
                }

                System.out.println("\n" + loadedStudents + " of " + lines.size() + " students loaded!\n");
                return nStudents;

            }else{ //if the file is not found in the directory
                System.out.println("\nERROR: File not found!");
            }
        }
    }

    public static int loadSchools(ArrayList<School> H) throws IOException{
        //Load school information from a user-provided file and return the number of new schools. New schools are added to the list of existing schools
        int nSchools = H.size();
        int loadedSchools = 0;


        String prompt ="\nEnter school file name (0 to cancel): ";
        while(true) {
            String fName = getString(prompt); //get file name
            if(fName.equals("0")){          //check if the user wants to exit
                System.out.println("\nFile loading process canceled.");
                return nSchools;
            }

            File file = new File(fName);
            if(file.exists()){              //check if the file exists in the directory
                BufferedReader fReader = new BufferedReader(new FileReader(file));
                ArrayList<String> lines = new ArrayList<>();

                while(fReader.ready()) {    //read all of the lines
                    lines.add(fReader.readLine());
                }

                for(int i = 0; i < lines.size(); i++){
                    String[] row = lines.get(i).split(",");

                    School newSchool = new School(row[0], Double.parseDouble(row[1]), Integer.parseInt(row[2]), 1);

                    if(newSchool.isValid()){        //check if the data is valid
                        H.add(newSchool);
                        loadedSchools += 1;
                    }
                }

                nSchools += loadedSchools;
                System.out.println("\n" + loadedSchools + " of " + lines.size() + " schools loaded!");
                return nSchools;

            }else{
                System.out.println("\nERROR: File not found!");
            }
        }
    }

    public static void editData(ArrayList<Student> S, ArrayList<School> H){
        //Sub-area menu to edit students and schools.
        String choice;
        String prompt;

        int nStudents = S.size();
        int nSchools = H.size();

        do { //repeat menu until user enters a menu option
            System.out.println("\nEdit data");
            System.out.println("---------");
            System.out.println("S - Edit students\n" +
                    "H - Edit high schools\n" +
                    "Q - Quit\n");
            prompt = "Enter choice: ";
            choice = getString(prompt).toUpperCase();

            switch (choice) { //switching system based on the choice
                case "S":
                    if(nStudents > 0) {
                        editStudents(S, H); //go to editStudents subsection
                    }else{
                        System.out.println("\nERROR: No students are loaded!");
                    }
                    break;

                case "H":
                    if(nSchools > 0) {
                        editSchools(S, H);   //go to editSchools subsection
                    }else{
                        System.out.println("\nERROR: No schools are loaded!");
                    }
                    break;

                case "Q":
                    System.out.println();
                    break;

                default:
                    System.out.println("\nERROR: Invalid menu choice!");
            }
        }while(!choice.equals("Q"));
    }

    public static void editStudents(ArrayList<Student> S, ArrayList<School> H){
        //Sub-area to edit students. The edited student's regret is updated if needed. Any existing school rankings and regrets are re-calculated after editing a student.
        int index;
        String prompt;

        int nStudents = S.size();
        int nSchools = H.size();

        boolean rankingsSet;

        if(H.size() > 0 && S.size() > 0){
            rankingsSet = true;
        }else{
            rankingsSet = false;
        }

        do {    //repeat until user enters 0
            System.out.println();
            printStudents(S, H); //go to printStudents subsection
            prompt = "Enter student (0 to quit): ";
            index = getInteger(prompt, 0, S.size());

            if (index != 0) {
                S.get(index - 1).editInfo(H, rankingsSet); //go to student editInfo section

                if (rankingsSet) {
                    for (int i = 0; i < nSchools; i++) { //recalculate ranks and regrets for school
                        H.get(i).calcRankings(S);
                    }
                }
            }
        }while(index != 0);
    }

    public static void editSchools(ArrayList<Student> S, ArrayList<School> H){
        //Sub-area to edit schools. Any existing rankings and regret for the edited school are updated.
        int index;
        String prompt;

        int nSchools = S.size();
        int nStudents = H.size();

        boolean rankingsSet;

        if(H.size() > 0 && S.size() > 1){
            rankingsSet = true;
        }else{
            rankingsSet = false;
        }

        do { //repeat until user enters 0
            System.out.println();
            printSchools(S, H);  //go to printSchools subsection
            prompt = "Enter school (0 to quit): ";
            index = getInteger(prompt, 0, nSchools);


            if (index != 0) {
                H.get(index - 1).editSchoolInfo(S, rankingsSet); //go to schools editInfo section
            }
        }while(index != 0);
    }

    public static void printStudents(ArrayList<Student> S, ArrayList<School> H){
        //Print students to the screen, including matched student (if one exists).
        String line = "";
        System.out.format(" %-4s%-40s%8s%4s  %-40s%-22s\n", "#", "Name", "GPA", "ES", "Assigned school", "Preferred school order"); //table format
        for(int i = 0; i < 123; i++){
            line += "-";
        }

        System.out.println(line);
        for (int i = 0; i < S.size(); i++) {
            if(i + 1 < 10) {
                System.out.format("  %d. ", i + 1);
            }else if(i + 1 < 100){
                System.out.format(" %d. ", i + 1);
            }else{
                System.out.format("%d. ", i + 1);
            }
            S.get(i).print(H);
        }

        System.out.println(line);
    }

    public static void printSchools(ArrayList<Student> S, ArrayList<School> H){
        //Print schools to the screen, including matched student (if one exist).
        String line = "";
        System.out.format(" %-4s%-40s%8s%8s  %-40s%-23s\n", "#", "Name", "# spots", "Weight", "Assigned students", "Preferred student order"); //table format
        for(int i = 0; i < 126; i++){
            line += "-";
        }

        System.out.println(line);
        for (int i = 0; i < H.size(); i++) {
            if(i + 1 < 10) {
                System.out.format("  %d. ", i + 1);
            }else if(i + 1 < 100){
                System.out.format(" %d. ", i + 1);
            }else{
                System.out.format("%d. ", i + 1);
            }
            H.get(i).print(S);
        }

        System.out.println(line );
    }

    public static void printComparison(SMPSolver GSS, SMPSolver GSH){
        //Print comparison of the student-optimal and school-optimal solutions.
        String line = "";
        for(int i = 0; i < 112; i++){
            line += "-";
        }

        String stability;
        String schoolRegret;
        String studentRegret;
        String totalRegret;
        String time;

        if(GSS.isStable() == GSH.isStable()){
            stability = "Tie";
        }else{
            stability = GSS.isStable() ? "Student-opt" : "School-opt";
        }

        if(GSS.getAvgReceiverRegret() == GSH.getAvgSuitorRegret()){
            schoolRegret = "Tie";
        }else{
            schoolRegret = GSS.getAvgReceiverRegret() < GSH.getAvgSuitorRegret() ? "Student-opt" : "School-opt";
        }

        if(GSS.getAvgSuitorRegret() == GSH.getAvgReceiverRegret()){
            studentRegret = "Tie";
        }else{
            studentRegret = GSS.getAvgSuitorRegret() < GSH.getAvgReceiverRegret() ? "Student-opt" : "School-opt";
        }

        if(GSS.getAvgTotalRegret() == GSH.getAvgTotalRegret()){
            totalRegret = "Tie";
        }else{
            totalRegret = GSS.getAvgTotalRegret() < GSH.getAvgTotalRegret() ? "Student-opt" : "School-opt";
        }

        if(GSS.getTime() == GSH.getTime()){
            time = "Tie";
        }else{
            time = GSS.getTime() < GSH.getTime() ? "Student-opt" : "School-opt";
        }

        System.out.format("\n%-17s%11s%21s%21s%21s%21s\n","Solution", "Stable", "Avg school regret", "Avg student regret", "Avg total regret", "Comp time (ms)");
        System.out.println(line);
        System.out.format("%-17s%11s%21.2f%21.2f%21.2f%21s\n", "Student optimal", GSS.isStable() ? "Yes" : "No", GSS.getAvgReceiverRegret(), GSS.getAvgSuitorRegret(), GSS.getAvgTotalRegret(), GSS.getTime());
        System.out.format("%-17s%11s%21.2f%21.2f%21.2f%21s\n", "School optimal", GSH.isStable() ? "Yes" : "No", GSH.getAvgSuitorRegret(), GSH.getAvgReceiverRegret(), GSH.getAvgTotalRegret(), GSH.getTime());
        System.out.println(line);
        System.out.format("%-17s%11s%21s%21s%21s%21s\n", "WINNER", stability, schoolRegret, studentRegret, totalRegret, time);
        System.out.println(line + "\n");
    }

    public static int getInteger(String prompt, int LB, int UB) {
        //Get an integer in the range [LB, UB] from the user. Prompt the user repeatedly until a valid value is entered

        String error;

        if(UB == Integer.MAX_VALUE){
            error = "ERROR: Input must be an integer in [" +LB + ", infinity]!\n"; //Custom error message
        }else {
            error = "ERROR: Input must be an integer in [" + LB + ", " + UB + "]!\n";   //Custom error message
        }

        boolean exit_flag;      //To inform when to exit the loop
        int input = 0;

        do {
            //Do-While loop to keep prompting for a proper input value
            try {   //Try-catch statement to get exception
                System.out.print(prompt);
                String strInput = reader.readLine();
                input = Integer.parseInt(strInput);
                exit_flag = true;
            } catch (Exception e) {
                System.out.println();
                System.out.println(error);
                exit_flag = false;
            }

            if((input < LB || input > UB) && exit_flag){
                System.out.println("\n" + error);
                exit_flag = false;
            }

        }while(!exit_flag);

        return input;   //Return the value to the designated variable
    }

    public static double getDouble(String prompt, double LB, double UB){
        //Get a real number in the range [LB, UB] from the user. Prompt the user repeatedly until a valid value is entered

        String error;

        boolean exit_flag;          //To inform when to exit the loop
        double input = 0;

        do {
            //Do-While loop to keep prompting for a proper input value
            try {   //Try-catch statement to get exception
                System.out.print(prompt);
                String strInput = reader.readLine();
                input = Double.parseDouble(strInput);
                exit_flag = true;
            } catch (Exception e) {
                System.out.println();
                System.out.format("ERROR: Input must be a real number in [%.2f, %.2f]!\n\n", (float)LB, (float)UB);
                exit_flag = false;
            }

            if((input < LB || input > UB) && exit_flag){
                System.out.println();
                System.out.format("ERROR: Input must be a real number in [%.2f, infinity]!\n\n", (float)LB);
                exit_flag = false;
            }

        }while(!exit_flag);

        return input;       //Return value to the designated variable
    }

    public static String getString(String prompt, String error){
        //get string with custom error message
        String input = null;
        boolean exit_flag = false;
        System.out.print(prompt);

        do {
            try {
                input = reader.readLine();
                exit_flag = true;
            } catch (IOException e) {
                System.out.println(error);
                System.out.println();
                exit_flag = false;
            }
        }while(!exit_flag);

        return input;
    }

    public static String getString(String prompt){
        //get string without custom error message
        return getString(prompt, null);
    }

    public static void systemExit(){
        //Exit the system
        System.out.println("\nHasta luego!");
        System.exit(1);
    }

    public static ArrayList<School> copySchools(ArrayList<Student> S, ArrayList<School> H){
        //copy school list
        ArrayList<School> newList = new ArrayList<School>();
        for(int i = 0; i < H.size(); i++){
            School newSchool = new School(H.get(i).getName(), H.get(i).getAlpha(), H.get(i).getMaxMatches(), H.get(i).getNParticipants());
            newSchool.calcRankings(S);
            newList.add(newSchool);
        }
        return newList;
    }

    public static ArrayList<Student> copyStudents(ArrayList<Student> S, ArrayList<School> H){
        //copy student list
        ArrayList<Student> newList = new ArrayList<>();
        for(int i = 0; i < S.size(); i++){
            Student newStudent = new Student(S.get(i).getName(), S.get(i).getGPA(), S.get(i).getES(), S.get(i).getNParticipants());
            for (int j = 0; j < S.get(i).getNParticipants(); j++) {
                newStudent.setRanking(j, S.get(i).getRanking(j));
            }
            newList.add(newStudent);
        }
        return newList;
    }
}

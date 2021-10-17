/*
 * This is a Java program for automated timetable schedular using Constraint 
 * Programming and Genetic Algorithm
 */
package automatedtimetableschedular;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JOptionPane;

/**
 *
 * @author user
 */
public class CreateTimeTable {
    
    Connection con;
    Statement stmt;
    ResultSet rs;
    
    ArrayList<String>[][] aryListDayTime;
    ArrayList<String>[][] aryListNewDayTime;
    ArrayList<String> aryListClassMeetings;
    HashSet<String> setClassMeetings;
    int No_Of_Available_Classroom;
    int countConstraint1, countConstraint2, countConstraint3, countConstraint4;
    int sumConstraint1, sumConstraint2, sumConstraint3, sumConstraint4;
    int cost;
    String semester;
    ArrayList<String> notAllocatedCourse;
    /**
     * Creates new Timetable form
     */
    public CreateTimeTable(int semest) {
        initComponents(semest);
        DoConnect();
    }

    private void initComponents(int semest){
      aryListDayTime = new ArrayList[5][10];
      aryListNewDayTime = new ArrayList[5][5];
      for (int i = 0; i < 5; i++){
          for(int j = 0; j < 10; j++){
              aryListDayTime[i][j] = new ArrayList<String>();              
          }
      }
      aryListClassMeetings = new ArrayList<String>();
      initializeCounters();
      No_Of_Available_Classroom = 9;
      setClassMeetings = new HashSet<String>();
      if(semest == 1){ semester = "Harmattan"; }
      if(semest == 2){ semester = "Rain"; }
      notAllocatedCourse = new ArrayList<String>();
      notAllocatedCourse.add("ICS 290"); notAllocatedCourse.add("ICS 390"); notAllocatedCourse.add("ICS 499");
      notAllocatedCourse.add("CSC 499"); notAllocatedCourse.add("LIS 210"); notAllocatedCourse.add("LIS 306");
      notAllocatedCourse.add("LIS 490"); notAllocatedCourse.add("MAC 232"); notAllocatedCourse.add("MAC 308"); 
      notAllocatedCourse.add("LIS 330"); notAllocatedCourse.add("MAC 499"); 
    }
    public void initializeCounters(){
      countConstraint1 = 0; sumConstraint1 = 0;
      countConstraint2 = 0; sumConstraint2 = 0;
      countConstraint3 = 0; sumConstraint3 = 0;
      cost = 0; countConstraint4 = 0; sumConstraint4 = 0;
    }
    
    private void DoConnect(){
        try {  
        String host = "jdbc:derby://localhost:1527/DbTimetable";
        String uName = "bprogrammer";
        String uPass = "password";
        //String uName = "BProgrammer";
        //String uPass = "bprogrammer";
        con = DriverManager.getConnection(host, uName, uPass);
        
        //stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        stmt = con.createStatement();
        String sql = "SELECT Course_Code FROM TblCourse where Semester = '" + semester + "'";
        rs = stmt.executeQuery(sql);
        while (rs.next()){
            //check if the course is not member of set of unallocated courses
            if(!notAllocatedCourse.contains(rs.getString("Course_Code"))){
             setClassMeetings.add(rs.getString("Course_Code"));//create a set of Course code
            }
        }
        rs.close();
        stmt.close();
         Iterator<String> setIterator = setClassMeetings.iterator();
         while(setIterator.hasNext()){
            aryListClassMeetings.add(setIterator.next());// add the set item into the arraylist   
         }
             
        
      }
      catch (SQLException err) {
          JOptionPane.showMessageDialog(null, err.getMessage());
      }
    }
    
    public void generateInitialPopulation(){
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] timeSlots = {"8.00am - 9.00am", "9.00am - 10.00am", "10.00am - 11.00am", "11.00am - 12.00pm",
                           "12.00pm - 1.00pm", "1.00pm - 2.00pm", "2.00pm - 3.00pm", "3.00pm - 4.00pm", "4.00pm - 5.00pm", "5.00pm - 6.00pm"};
            
        System.out.println("\nThe Available Class meetings: " + aryListClassMeetings);
         System.out.println("\nThe Total number of Class meetings: " + aryListClassMeetings.size());
            
      try{  //allocation of class meetings to days and time
          Collections.shuffle(aryListClassMeetings);
        Iterator<String> listIterator = aryListClassMeetings.iterator();
         while(listIterator.hasNext()){
             for (int i = 0; i < 5; i++){
                for(int j = 0; j < 10; j++){
                  if((i == 2 && j == 8)||(i == 2 && j == 9)) { aryListDayTime[i][j].add("GAMES"); }
                  else if((i == 4 && j == 4)||(i == 4 && j == 7)) { aryListDayTime[i][j].add(""); }
                  else if((i == 4 && j == 5)||(i == 4 && j == 6)) { aryListDayTime[i][j].add("Jumat Service"); }
                  else 
                    if(listIterator.hasNext()){
                        String strTemp = listIterator.next();
                        aryListDayTime[i][j].add(strTemp);                    
                        if(j < 9) { aryListDayTime[i][++j].add(strTemp); }
                    }
                }
             }
               
         }
          System.out.printf("\n******* The initial TimeTable for %s Semester ************ ", semester);
          for (int i = 0; i < 5; i++){
               System.out.printf("\n******* %s:", days[i]);
                for(int j = 0; j < 10; j++){
                    System.out.printf("\n%s:  %s ", timeSlots[j], aryListDayTime[i][j]);
                    
                }
          }
          //Creating a new list to get all elements in arListDayTime @ Location (0,2,4,6,8)
          //aryListNewDayTime = new ArrayList[5][5];
          for (int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
              aryListNewDayTime[i][j] = new ArrayList<String>();
              int cnt = aryListDayTime[i][j*2].size();
              for(int k =0; k < cnt; k++){
                  aryListNewDayTime[i][j].add(aryListDayTime[i][j*2].get(k));
              }
           }
         }
      }
      catch (Exception err) {
         JOptionPane.showMessageDialog(null, err.getMessage());  
      }

    }
    
    public void allocateClassMeetings(){
        
      try{  //allocation of class meetings to days and time
          Collections.shuffle(aryListClassMeetings);
        Iterator<String> listIterator = aryListClassMeetings.iterator();
         while(listIterator.hasNext()){
             for (int i = 0; i < 5; i++){
                for(int j = 0; j < 10; j++){
                  if((i == 2 && j == 8)||(i == 2 && j == 9)) { aryListDayTime[i][j].add("GAMES"); }
                  else if((i == 4 && j == 4)||(i == 4 && j == 7)) { aryListDayTime[i][j].add(""); }
                  else if((i == 4 && j == 5)||(i == 4 && j == 6)) { aryListDayTime[i][j].add("Jumat Service"); }
                  else 
                    if(listIterator.hasNext()){
                        String strTemp = listIterator.next();
                        aryListDayTime[i][j].add(strTemp);                    
                        if(j < 9) { aryListDayTime[i][++j].add(strTemp); }
                    }
                }
             }
               
         }
          
          //Creating a new list to get all elements in arListDayTime @ Location (0,2,4,6,8)
          //aryListNewDayTime = new ArrayList[5][5];
          for (int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
              aryListNewDayTime[i][j] = new ArrayList<String>();
              int cnt = aryListDayTime[i][j*2].size();
              for(int k =0; k < cnt; k++){
                  aryListNewDayTime[i][j].add(aryListDayTime[i][j*2].get(k));
              }
           }
         }
      }
      catch (Exception err) {
         JOptionPane.showMessageDialog(null, err.getMessage());  
      }

    }
    
    private boolean isCourseMemberOfGroup(String Course){
        //for each course chack if course belongs to a group classmeeting or not
        boolean isGroupMember = false;
      try{
           stmt = con.createStatement();
           String sql = "SELECT Course_Code FROM TblStudentsGroup where Course_Code = '" + Course + "' And Semester = '" + semester + "'";
           rs = stmt.executeQuery(sql);
           if (rs.next()){
           //is the course belong to a group
            isGroupMember = true;
           }
           rs.close();
           stmt.close();           
      }
      catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
      return isGroupMember;
    }
    
    public void repairClassMeetings(){
       //this is a method which will resolve by repairing issues such clashing of classes between
      //individual students class meetings and group class meetings
     try{  
       for (int i = 0; i < 5; i++){
          for(int j = 0; j < 5; j++){
            if((i == 2 && j == 4)) { continue; }
            if((i == 4 && j == 2)||(i == 4 && j == 3)) { continue; }
            int cnt = aryListNewDayTime[i][j].size();
              for(int k =0; k < cnt; k++){
                 String Course = aryListNewDayTime[i][j].get(k);
                 if(isCourseMemberOfGroup(Course)){
                     //treat as group course using the version 1
                     arrangeCourseAsGroup(Course, i, j, k, false);
                 }
                 else{
                     //treat as individual course using the version 1
                     arrangeCourseAsIndividual(Course, i, j, k, false);
                 }
              } 
              aryListNewDayTime[i][j].trimToSize();
          }
       }
     }     
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      }     
    } 
   
//this is version 1  
    private void arrangeCourseAsIndividual(String course, int i, int j, int k, boolean lStatus){
     //this method is repair the class meeting for each individual student     
        
        ArrayList<String> classList = new ArrayList<String>();
        ArrayList<String> levelList = new ArrayList<String>();
     try{  
              
              int cnt = aryListNewDayTime[i][j].size();
              for(int l =0; l < cnt; l++){
                 String Course = aryListNewDayTime[i][j].get(l);
                 //get the department and level of students that offered this course from DB and store
                //in another 2 lists
                stmt = con.createStatement();
                String sql = "SELECT Department, Level FROM TblCourse where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                rs = stmt.executeQuery(sql);
                if (rs.next()){
                 //is the department and level for the course
                 classList.add(rs.getString("Department"));
                 levelList.add(rs.getString("Level"));
                }
                rs.close();
                stmt.close();
              }
              //check whether or not the class list or level list contains elements that are duplicate
              if(checkDuplicateCourse(course, classList, levelList, i, j, k, lStatus)){
                 //classList.clear();
                // levelList.clear();
                countConstraint1++; sumConstraint1 += 5;
              }
     }
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      }    
    }
    
 //this is version 2  
    private void arrangeCourseAsIndividual(String course, int i, int j, int k, String lId){
     //this method is repair the class meeting for each individual student
        String classItem = "";
        String levelItem = "";
     try{  
               //get the department and level of student that offered this course from DB and store
                //in another 2 string item
                stmt = con.createStatement();
                String sql = "SELECT Department, Level FROM TblCourse where Course_Code = '" + course +"' And Semester = '" + semester + "'";
                rs = stmt.executeQuery(sql);
                if (rs.next()){
                 //is the department and level for the course
                 classItem = rs.getString("Department");
                 levelItem = rs.getString("Level");
                }
                rs.close();
                stmt.close();
             
              //check whether or not the class list or level list contains elements that are duplicate
              if(addToNewClassMeeting(course, classItem, levelItem, i, j, lId)){
              aryListNewDayTime[i][j].remove(k);//remove after adding
              }
     }
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      }    
    }
 
  //this is version 3   
    private void arrangeCourseAsIndividual(String course, int i, int j, int k, int nn, int mm, String lId){
     //this method is repair the class meeting for each individual student
        String classItem = "";
        String levelItem = "";
     try{  
              
              //get the department and level of student that offered this course from DB and store
                //in another 2 string item
                stmt = con.createStatement();
                String sql = "SELECT Department, Level FROM TblCourse where Course_Code = '" + course +"' And Semester = '" + semester + "'";
                rs = stmt.executeQuery(sql);
                if (rs.next()){
                 //is the department and level for the course
                 classItem = rs.getString("Department");
                 levelItem = rs.getString("Level");
                }
                rs.close();
                stmt.close();
             
               //add to new class meetings
              if(addToNewClassMeeting(course, classItem, levelItem, i,j, nn, mm, lId)){
              aryListNewDayTime[i][j].remove(k);//remove after adding
              }
              
     }
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      }    
    }
 
           
    private boolean checkDuplicateCourse(String Course, ArrayList<String> lstClass, ArrayList<String> lstLevel, int i, int j, int k, boolean lStatus){
       //This is the first version 
        int m, n;
        String classItem;
        String levelItem;
        if(lStatus == false){ //if not lecturer
          for(n = 0; n < lstClass.size(); n++){
             if (n == k) { continue; } //at the current course  
            if(lstClass.get(k).equalsIgnoreCase(lstClass.get(n))){
                //if department is equal for two courses belonging to the same class meeting then
                if(lstLevel.get(k).equalsIgnoreCase(lstLevel.get(n))){
                  //check if they are of the same level then remove the course and add to another
                    //String item = aryListNewDayTime[i][j].get(m);
                    classItem = lstClass.get(k);
                    levelItem = lstLevel.get(k);
                    if(addToNewClassMeeting(Course, classItem, levelItem, i,j)){
                    aryListNewDayTime[i][j].remove(k);//remove after adding
                    return true; //duplicate found and repair as appropriate
                    }
                }
            }  
          }  
        }
        else{ //if lecturer status is true for unavailability
            classItem = lstClass.get(k);
            levelItem = lstLevel.get(k);
            if(addToNewClassMeeting(Course, classItem, levelItem, i,j)){
            aryListNewDayTime[i][j].remove(k);//remove after adding
            }
        }
        return false;
    }
    
    
 //this is version 1   
    private boolean addToNewClassMeeting(String item, String classItem, String levelItem, int i,int j){
       int k, l, f;
       ArrayList<String> newClassList = new ArrayList<String>();
       ArrayList<String> newLevelList = new ArrayList<String>();
       ArrayList<String> groupClassList = new ArrayList<String>();
       ArrayList<String> groupLevelList = new ArrayList<String>();
       boolean foundMatch, foundGroupMatch;
     try{
       for(k =0; k < 5; k++){
          for(l = 0; l < 5; l++){
            if((k == 2 && l == 4)) { continue; } //Games day
            if((k == 4 && l == 2)||(k == 4 && l == 3)) { continue; }  //Jumat Service
            //check the current index k, l if equal to i, j
            if(k == i && l == j) { continue; }
            //get the department and level of students that offered this course from DB and store
              //in another 2 new lists
            foundGroupMatch = false;
              int cnt = aryListNewDayTime[k][l].size();
              for(f =0; f < cnt; f++){
                 String Course = aryListNewDayTime[k][l].get(f);
                 //for each course check if course belongs to group or not
                 if(isCourseMemberOfGroup(Course)){
                        //treat the group course check if the classItem and LevelItem belongs to group
                        //get the group information for the current course
                           groupClassList.clear();
                           groupLevelList.clear();
                           stmt = con.createStatement();
                           String sql = "SELECT * FROM TblStudentsGroup where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                           rs = stmt.executeQuery(sql);
                           if(rs.next()){
                            //is the list of departments and level for the course
                             if(rs.getInt("CSC")== 1){
                              groupClassList.add("Computer Science");
                              groupLevelList.add(rs.getString("CSC_Level"));
                             }
                             if(rs.getInt("MAC")== 1){
                              groupClassList.add("Mass Communication");
                              groupLevelList.add(rs.getString("MAC_Level"));
                             }
                             if(rs.getInt("LIS")== 1){
                              groupClassList.add("Library and Information Science");
                              groupLevelList.add(rs.getString("LIS_Level"));
                             }
                             if(rs.getInt("ICS")== 1){
                              groupClassList.add("Information and Communication Science");
                              groupLevelList.add(rs.getString("ICS_Level"));
                             }
                             if(rs.getInt("TCS")== 1){
                              groupClassList.add("Telecommunication Science");
                              groupLevelList.add(rs.getString("TCS_Level"));
                             }
                           }
                           rs.close();
                           stmt.close();
                           
                           //match ClassItem and LevelItem with the course group infor
                           for(int ll =0; ll < groupClassList.size(); ll++){ 
                                if((classItem.equalsIgnoreCase(groupClassList.get(ll)))&& (levelItem.equalsIgnoreCase(groupLevelList.get(ll)))){
                                //if match found, stop loop and get another course 
                                    foundGroupMatch = true; //duplicate found and repair as appropriate
                                    break; 
                                    }
                            }
                           if(foundGroupMatch){ break; } //go out of outer loop                               

                    }
                    else{
                            //treat as individual course using the version 1
                           
                           stmt = con.createStatement();
                           String sql = "SELECT Department, Level FROM TblCourse where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                           rs = stmt.executeQuery(sql);
                           if (rs.next()){
                            //is the department and level for the course
                            newClassList.add(rs.getString("Department"));
                            newLevelList.add(rs.getString("Level"));
                           }
                           rs.close();
                           stmt.close();
                    }
              }
              
              foundMatch = false;
              if(!foundGroupMatch){
                    for(f =0; f < newClassList.size(); f++){
                      if((classItem.equalsIgnoreCase(newClassList.get(f)))&& (levelItem.equalsIgnoreCase(newLevelList.get(f)))){
                      //if department and level are equal for the course to be added and courses already belonging to the same class meeting then
                        //don't add item into this class list, set found as true
                          foundMatch = true;
                          break;
                      }
                    }
                      if(!foundMatch)
                      {  //to add item then check for number of classroom available
                          if(aryListNewDayTime[k][l].size() < No_Of_Available_Classroom){
                              //add item to this place
                              aryListNewDayTime[k][l].add(item);
                              return true;
                          }                   
                      }
              }
              newClassList.clear();
              newLevelList.clear();  
          }
       }
     }
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      }
     return false;
    }
 
//this is version 2   
    private boolean addToNewClassMeeting(String item, String classItem, String levelItem, int i, int j, String lId){
       int k, l, f, cnt;
       ArrayList<String> newClassList = new ArrayList<String>();
       ArrayList<String> newLevelList = new ArrayList<String>();
       ArrayList<String> newLecturerId = new ArrayList<String>();
       ArrayList<String> groupClassList = new ArrayList<String>();
       ArrayList<String> groupLevelList = new ArrayList<String>();
       String sql, Course;
       boolean foundGroupMatch;
     try{
       for(k =0; k < 5; k++){
          for(l = 0; l < 5; l++){
            if((k == 2 && l == 4)) { continue; } //Games day
            if((k == 4 && l == 2)||(k == 4 && l == 3)) { continue; }  //Jumat Service
            //check the current index k, l if equal to i, j
            if(k == i && l == j) { continue; }
            //get the department and level of students that offered this course from DB and store
              //in another 2 new lists
              foundGroupMatch = false;
              cnt = aryListNewDayTime[k][l].size();
              for(f =0; f < cnt; f++){
                 Course = aryListNewDayTime[k][l].get(f);
                 //for each course check if course belongs to group or not
                 
                 if(isCourseMemberOfGroup(Course)){
                        //treat the group course check if the classItem and LevelItem belongs to group
                        //get the group information for the current course
                           groupClassList.clear();
                           groupLevelList.clear(); 
                           stmt = con.createStatement();
                           sql = "SELECT * FROM TblStudentsGroup where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                           rs = stmt.executeQuery(sql);
                           if(rs.next()){
                            //is the list of departments and level for the course
                             if(rs.getInt("CSC")== 1){
                              groupClassList.add("Computer Science");
                              groupLevelList.add(rs.getString("CSC_Level"));
                             }
                             if(rs.getInt("MAC")== 1){
                              groupClassList.add("Mass Communication");
                              groupLevelList.add(rs.getString("MAC_Level"));
                             }
                             if(rs.getInt("LIS")== 1){
                              groupClassList.add("Library and Information Science");
                              groupLevelList.add(rs.getString("LIS_Level"));
                             }
                             if(rs.getInt("ICS")== 1){
                              groupClassList.add("Information and Communication Science");
                              groupLevelList.add(rs.getString("ICS_Level"));
                             }
                             if(rs.getInt("TCS")== 1){
                              groupClassList.add("Telecommunication Science");
                              groupLevelList.add(rs.getString("TCS_Level"));
                             }
                           }
                           rs.close();
                           stmt.close();
                           
                           //match ClassItem and LevelItem with the course group infor
                           for(int ll =0; ll < groupClassList.size(); ll++){ 
                                if((classItem.equalsIgnoreCase(groupClassList.get(ll)))&& (levelItem.equalsIgnoreCase(groupLevelList.get(ll)))){
                                //if match found, stop loop and get another course 
                                    foundGroupMatch = true; //duplicate found and repair as appropriate
                                    break; 
                                    }
                            }
                           if(foundGroupMatch){ break; } //go out of outer loop                               

                    } 
                    else {
                            //for each course get class and level
                       stmt = con.createStatement();
                       sql = "SELECT Department, Level FROM TblCourse where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                       rs = stmt.executeQuery(sql);
                       if (rs.next()){
                        //is the department and level for the course
                        newClassList.add(rs.getString("Department"));
                        newLevelList.add(rs.getString("Level"));
                       }
                       rs.close();
                       stmt.close();
                    }
              }
              if(!foundGroupMatch)
              {    
                    //get the list of lecturers teaching this courses from DB and store in another list
                        for(f =0; f < cnt; f++){
                           Course = aryListNewDayTime[k][l].get(f);
                           //for each course get the lecturer id
                           stmt = con.createStatement();
                           sql = "SELECT Lecturer_Id FROM TblLecturerCourseAssignment where Course_Code = '" + Course + "' And Semester = '" + semester + "'";
                           rs = stmt.executeQuery(sql);
                          while (rs.next()){
                              //create a list of lecturers
                              newLecturerId.add(rs.getString("Lecturer_Id"));
                          }
                          rs.close();
                          stmt.close();  
                        }

                        boolean foundMatch = false;
                        for(f = 0; f < newClassList.size(); f++){
                          if((classItem.equalsIgnoreCase(newClassList.get(f)))&& (levelItem.equalsIgnoreCase(newLevelList.get(f)))){
                          //if department and level are equal for the course to be added and courses already belonging to the same class meeting then
                            //don't add item into this class list, set found as true
                              foundMatch = true;
                              break;
                          }
                        }
                          if(!foundMatch)
                          { //check if the list of lecturer doesn't contain the lecturer id
                              boolean foundMatchLecturerId = false;
                              for(f = 0; f < newLecturerId.size(); f++){
                                  if(lId.equalsIgnoreCase(newLecturerId.get(f))){
                                  //if lecturer_Id found do not add
                                  //don't add item into this class meeting, set found as true
                                      foundMatchLecturerId = true;
                                      break;
                                  }
                              }

                              if(!foundMatchLecturerId){
                               //to add item then check for number of classroom available
                                  if(aryListNewDayTime[k][l].size() < No_Of_Available_Classroom){
                                     //add item to this place
                                     aryListNewDayTime[k][l].add(item);
                                     return true;
                                  }                   
                              }
                          }
              }
              newClassList.clear();
              newLevelList.clear();
              newLecturerId.clear();
          }
       }
     }
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      }
     return false;
    }
    
 //this is version 3   
    private boolean addToNewClassMeeting(String item, String classItem, String levelItem, int i,int j, int nn, int mm, String lId){
       int k, l, f, cnt;
       ArrayList<String> newClassList = new ArrayList<String>();
       ArrayList<String> newLevelList = new ArrayList<String>();
       ArrayList<String> newLecturerId = new ArrayList<String>();
       ArrayList<String> groupClassList = new ArrayList<String>();
       ArrayList<String> groupLevelList = new ArrayList<String>();
       String sql, Course;
       boolean foundGroupMatch;
     try{
       for(k =0; k < 5; k++){
          for(l = 0; l < 5; l++){
            if((k == 2 && l == 4)) { continue; } //Games day
            if((k == 4 && l == 2)||(k == 4 && l == 3)) { continue; }  //Jumat Service
            //check the current index k, l if equal to i, j or equal to nn, mm
            if(k == i && l == j) { continue; }
            if(k == nn && l == mm) { continue; }
            //get the department and level of students that offered this course from DB and store
              //in another 2 new lists
               foundGroupMatch = false;
               cnt = aryListNewDayTime[k][l].size();
               for(f =0; f < cnt; f++){
                 Course = aryListNewDayTime[k][l].get(f);
                 //for each course check if course belongs to group or not
                 
                    if(isCourseMemberOfGroup(Course)){
                        //treat the group course check if the classItem and LevelItem belongs to group
                        //get the group information for the current course
                           groupClassList.clear();
                           groupLevelList.clear(); 
                           stmt = con.createStatement();
                           sql = "SELECT * FROM TblStudentsGroup where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                           rs = stmt.executeQuery(sql);
                           if(rs.next()){
                            //is the list of departments and level for the course
                             if(rs.getInt("CSC")== 1){
                              groupClassList.add("Computer Science");
                              groupLevelList.add(rs.getString("CSC_Level"));
                             }
                             if(rs.getInt("MAC")== 1){
                              groupClassList.add("Mass Communication");
                              groupLevelList.add(rs.getString("MAC_Level"));
                             }
                             if(rs.getInt("LIS")== 1){
                              groupClassList.add("Library and Information Science");
                              groupLevelList.add(rs.getString("LIS_Level"));
                             }
                             if(rs.getInt("ICS")== 1){
                              groupClassList.add("Information and Communication Science");
                              groupLevelList.add(rs.getString("ICS_Level"));
                             }
                             if(rs.getInt("TCS")== 1){
                              groupClassList.add("Telecommunication Science");
                              groupLevelList.add(rs.getString("TCS_Level"));
                             }
                           }
                           rs.close();
                           stmt.close();
                           
                           //match ClassItem and LevelItem with the course group infor
                           for(int ll =0; ll < groupClassList.size(); ll++){ 
                                if((classItem.equalsIgnoreCase(groupClassList.get(ll)))&& (levelItem.equalsIgnoreCase(groupLevelList.get(ll)))){
                                //if match found, stop loop and get another course 
                                    foundGroupMatch = true; //duplicate found and repair as appropriate
                                    break; 
                                    }
                            }
                           if(foundGroupMatch){ break; } //go out of outer loop                               

                    } 
                    else {
                            //for each course get class and level
                                //for each course chack if course belongs to group or not
                             stmt = con.createStatement();
                             sql = "SELECT Department, Level FROM TblCourse where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                             rs = stmt.executeQuery(sql);
                             if (rs.next()){
                              //is the department and level for the course
                              newClassList.add(rs.getString("Department"));
                              newLevelList.add(rs.getString("Level"));
                             }
                             rs.close();
                             stmt.close();
                    }
                 }
              if(!foundGroupMatch)
              {    
                                //get the list of lecturers teaching this courses from DB and store in another list
                               for(f =0; f < cnt; f++){
                                Course = aryListNewDayTime[k][l].get(f);
                                //for each course get the lecturer id
                                stmt = con.createStatement();
                                sql = "SELECT Lecturer_Id FROM TblLecturerCourseAssignment where Course_Code = '" + Course + "' And Semester = '" + semester + "'";
                                rs = stmt.executeQuery(sql);
                               while (rs.next()){
                                   //create a list of lecturers
                                   newLecturerId.add(rs.getString("Lecturer_Id"));
                               }
                               rs.close();
                               stmt.close();  
                             }

                             boolean foundMatch = false;
                             for(f =0; f < newClassList.size(); f++){
                               if((classItem.equalsIgnoreCase(newClassList.get(f)))&& (levelItem.equalsIgnoreCase(newLevelList.get(f)))){
                               //if department and level are equal for the course to be added and courses already belonging to the same class meeting then
                                 //don't add item into this class list, set found as true
                                   foundMatch = true;
                                   break;
                               }
                             }
                               if(!foundMatch)
                               { //check if the list of lecturer doesn't contain the lecturer id
                                   boolean foundMatchLecturerId = false;
                                   for(f = 0; f < newLecturerId.size(); f++){
                                       if(lId.equalsIgnoreCase(newLecturerId.get(f))){
                                       //if lecturer_Id found do not add
                                       //don't add item into this class meeting, set found as true
                                           foundMatchLecturerId = true;
                                           break;
                                       }
                                   }

                                   if(!foundMatchLecturerId){
                                   //to add item then check for number of classroom available
                                       if(aryListNewDayTime[k][l].size() < No_Of_Available_Classroom){
                                          //add item to this place
                                          aryListNewDayTime[k][l].add(item);
                                          return true;
                                       }               
                                   }
                               }
              }
              newClassList.clear();
              newLevelList.clear();
              newLecturerId.clear();
          }
       }
     }
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      }
     return false;
    }
    
  //this is version 1  for group
    private void arrangeCourseAsGroup(String course, int i, int j, int k, boolean lStatus){
       //this method is repair the class meeting for each student in a group
        
        ArrayList<String> classList = new ArrayList<String>();
        ArrayList<String> levelList = new ArrayList<String>();
        ArrayList<String> groupClassList = new ArrayList<String>();
        ArrayList<String> groupLevelList = new ArrayList<String>();
     try{  
         //get the group information for the current course
         stmt = con.createStatement();
         String sql = "SELECT * FROM TblStudentsGroup where Course_Code = '" + course +"' And Semester = '" + semester + "'";
         rs = stmt.executeQuery(sql);
         if(rs.next()){
          //is the list of departments and level for the course
           if(rs.getInt("CSC")== 1){
            groupClassList.add("Computer Science");
            groupLevelList.add(rs.getString("CSC_Level"));
           }
           if(rs.getInt("MAC")== 1){
            groupClassList.add("Mass Communication");
            groupLevelList.add(rs.getString("MAC_Level"));
           }
           if(rs.getInt("LIS")== 1){
            groupClassList.add("Library and Information Science");
            groupLevelList.add(rs.getString("LIS_Level"));
           }
           if(rs.getInt("ICS")== 1){
            groupClassList.add("Information and Communication Science");
            groupLevelList.add(rs.getString("ICS_Level"));
           }
           if(rs.getInt("TCS")== 1){
            groupClassList.add("Telecommunication Science");
            groupLevelList.add(rs.getString("TCS_Level"));
           }
         }
         rs.close();
         stmt.close();
          //get the department and level the remaining courses in the same class meeting from DB and store
          //in another 2 lists
           
          if(lStatus == false){ //if not lecturer
              int cnt = aryListNewDayTime[i][j].size();
              for(int l =0; l < cnt; l++){
                  if (l == k) { continue; } //this is current course
                 String Course = aryListNewDayTime[i][j].get(l);
                 //for each course chack if course belongs to group or not
                stmt = con.createStatement();
                sql = "SELECT Department, Level FROM TblCourse where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                rs = stmt.executeQuery(sql);
                if (rs.next()){
                 //is the department and level for the course
                 classList.add(rs.getString("Department"));
                 levelList.add(rs.getString("Level"));
                }
                rs.close();
                stmt.close();
              }
              //check whether or not the class list or level list contains elements that are duplicate
              // in the current group information of this active course
              for(int f =0; f < classList.size(); f++){
               for(int l =0; l < groupClassList.size(); l++){ 
                if((classList.get(f).equalsIgnoreCase(groupClassList.get(l)))&& (levelList.get(f).equalsIgnoreCase(groupLevelList.get(l)))){
                //check if they are of the same level then remove the course and add to another
                    //duplicate found then add to new cell
                    if(addToNewClassMeeting(course, groupClassList, groupLevelList, i, j)){
                    aryListNewDayTime[i][j].remove(k);//remove after adding
                    countConstraint2++; //add counter
                    sumConstraint2 += 10;
                    return; //duplicate found and repair as appropriate
                    }
                }
               }
              }
          }
          else
          {
              addToNewClassMeeting(course, groupClassList, groupLevelList, i, j);
              aryListNewDayTime[i][j].remove(k);//remove after adding
          }
     }
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      } 
    }
    
 //this is version 2  for group
    private void arrangeCourseAsGroup(String course, int i, int j, int k, String lId){
       //this method is repair the class meeting for each student in a group
        ArrayList<String> groupClassList = new ArrayList<String>();
        ArrayList<String> groupLevelList = new ArrayList<String>();
     try{  
         //get the group information for the current course
         stmt = con.createStatement();
         String sql = "SELECT * FROM TblStudentsGroup where Course_Code = '" + course +"' And Semester = '" + semester + "'";
         rs = stmt.executeQuery(sql);
         if(rs.next()){
          //is the list of departments and level for the course
           if(rs.getInt("CSC")== 1){
            groupClassList.add("Computer Science");
            groupLevelList.add(rs.getString("CSC_Level"));
           }
           if(rs.getInt("MAC")== 1){
            groupClassList.add("Mass Communication");
            groupLevelList.add(rs.getString("MAC_Level"));
           }
           if(rs.getInt("LIS")== 1){
            groupClassList.add("Library and Information Science");
            groupLevelList.add(rs.getString("LIS_Level"));
           }
           if(rs.getInt("ICS")== 1){
            groupClassList.add("Information and Communication Science");
            groupLevelList.add(rs.getString("ICS_Level"));
           }
           if(rs.getInt("TCS")== 1){
            groupClassList.add("Telecommunication Science");
            groupLevelList.add(rs.getString("TCS_Level"));
           }
         }
         rs.close();
         stmt.close();
          //add to new list with the group information
              if(addToNewClassMeeting(course, groupClassList, groupLevelList, i, j, lId)){
              aryListNewDayTime[i][j].remove(k);//remove after adding
              }
          
     }
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      } 
    }
  //this is version 3  for group
    private void arrangeCourseAsGroup(String course, int i, int j, int k, int nn, int mm, String lId){
       //this method is repair the class meeting for each student in a group
        ArrayList<String> groupClassList = new ArrayList<String>();
        ArrayList<String> groupLevelList = new ArrayList<String>();
     try{  
         //get the group information for the current course
         stmt = con.createStatement();
         String sql = "SELECT * FROM TblStudentsGroup where Course_Code = '" + course +"' And Semester = '" + semester + "'";
         rs = stmt.executeQuery(sql);
         if(rs.next()){
          //is the list of departments and level for the course
           if(rs.getInt("CSC")== 1){
            groupClassList.add("Computer Science");
            groupLevelList.add(rs.getString("CSC_Level"));
           }
           if(rs.getInt("MAC")== 1){
            groupClassList.add("Mass Communication");
            groupLevelList.add(rs.getString("MAC_Level"));
           }
           if(rs.getInt("LIS")== 1){
            groupClassList.add("Library and Information Science");
            groupLevelList.add(rs.getString("LIS_Level"));
           }
           if(rs.getInt("ICS")== 1){
            groupClassList.add("Information and Communication Science");
            groupLevelList.add(rs.getString("ICS_Level"));
           }
           if(rs.getInt("TCS")== 1){
            groupClassList.add("Telecommunication Science");
            groupLevelList.add(rs.getString("TCS_Level"));
           }
         }
         rs.close();
         stmt.close();
          //add to new list with the group information
            if(addToNewClassMeeting(course, groupClassList, groupLevelList, i, j, nn, mm, lId)){
            aryListNewDayTime[i][j].remove(k);//remove after adding
            }
              
        
     }
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      } 
    }
 
  //this is version 1 for group addtonewclassmeeting
    private boolean addToNewClassMeeting(String item, ArrayList<String> grpClassItem, ArrayList<String> grpLevelItem, int i, int j){
       int k, l, f, cnt;
       ArrayList<String> newClassList = new ArrayList<String>();
       ArrayList<String> newLevelList = new ArrayList<String>();
       ArrayList<String> groupClassList = new ArrayList<String>();
        ArrayList<String> groupLevelList = new ArrayList<String>();
        boolean foundGroupMatch;
        String Course, sql;
     try{
       for(k =0; k < 5; k++){
          for(l = 0; l < 5; l++){
            if((k == 2 && l == 4)) { continue; } //Games day
            if((k == 4 && l == 2)||(k == 4 && l == 3)) { continue; }  //Jumat Service
              //check the current index k, l if equal to i, j
            if(k == i && l == j) { continue; }
            //get the department and level of students that offered this course from DB and store
              //in another 2 new lists
              foundGroupMatch = false;
               cnt = aryListNewDayTime[k][l].size();
               for(f =0; f < cnt; f++){
                 Course = aryListNewDayTime[k][l].get(f);
                 //for each course check if course belongs to group or not
                 
                    if(isCourseMemberOfGroup(Course)){
                        //treat the group course check if the classItem and LevelItem belongs to group
                        //get the group information for the current course
                           groupClassList.clear();
                           groupLevelList.clear(); 
                           stmt = con.createStatement();
                           sql = "SELECT * FROM TblStudentsGroup where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                           rs = stmt.executeQuery(sql);
                           if(rs.next()){
                            //is the list of departments and level for the course
                             if(rs.getInt("CSC")== 1){
                              groupClassList.add("Computer Science");
                              groupLevelList.add(rs.getString("CSC_Level"));
                             }
                             if(rs.getInt("MAC")== 1){
                              groupClassList.add("Mass Communication");
                              groupLevelList.add(rs.getString("MAC_Level"));
                             }
                             if(rs.getInt("LIS")== 1){
                              groupClassList.add("Library and Information Science");
                              groupLevelList.add(rs.getString("LIS_Level"));
                             }
                             if(rs.getInt("ICS")== 1){
                              groupClassList.add("Information and Communication Science");
                              groupLevelList.add(rs.getString("ICS_Level"));
                             }
                             if(rs.getInt("TCS")== 1){
                              groupClassList.add("Telecommunication Science");
                              groupLevelList.add(rs.getString("TCS_Level"));
                             }
                           }
                           rs.close();
                           stmt.close();
                           
                           //match ClassItem and LevelItem with the course group infor
                           for(int ll =0; ll < groupClassList.size(); ll++){
                              for(int n =0; n < grpClassItem.size(); n++){  
                                if((grpClassItem.get(n).equalsIgnoreCase(groupClassList.get(ll)))&& (grpLevelItem.get(n).equalsIgnoreCase(groupLevelList.get(ll)))){
                                //if match found, stop loop and get another course 
                                    foundGroupMatch = true; //duplicate found and repair as appropriate
                                    break; 
                                }
                              } if(foundGroupMatch){ break; } //go out of outer loop
                            }
                           if(foundGroupMatch){ break; } //go out of outer loop                               

                    } 
                    else {  
            
                            //for each course chack if course belongs to group or not
                           stmt = con.createStatement();
                           sql = "SELECT Department, Level FROM TblCourse where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                           rs = stmt.executeQuery(sql);
                           if (rs.next()){
                            //is the department and level for the course
                            newClassList.add(rs.getString("Department"));
                            newLevelList.add(rs.getString("Level"));
                           }
                           rs.close();
                           stmt.close();
                    }
              }
              if(!foundGroupMatch){ 
                    boolean foundMatch = false;
                    for(f =0; f < newClassList.size(); f++){
                      for(int n =0; n < grpClassItem.size(); n++){ 
                        if((newClassList.get(f).equalsIgnoreCase(grpClassItem.get(n)))&& (newLevelList.get(f).equalsIgnoreCase(grpLevelItem.get(n)))){
                        //check if they are of the same level then remove the course and add to another
                        //if department and level are equal for the course to be added and courses already belonging to the same class meeting then
                          //don't add item into this class list, set found as true
                            foundMatch = true;
                            break;
                        }
                      } if(foundMatch) {break;}
                    }
                      if(!foundMatch)
                      {  //to add item then check for number of classroom available
                          if(aryListNewDayTime[k][l].size() < No_Of_Available_Classroom){
                              //add item to this place
                              aryListNewDayTime[k][l].add(item);
                              return true;
                          }                   
                      }
              }
              newClassList.clear();
              newLevelList.clear();  
          }
       }
     }
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      }
     return false;
    }
 
//this is version 2 for group addtonewclassmeeting
    private boolean addToNewClassMeeting(String item, ArrayList<String> grpClassItem, ArrayList<String> grpLevelItem, int i, int j, String lId){
       int k, l, f, cnt;
       ArrayList<String> newClassList = new ArrayList<String>();
       ArrayList<String> newLevelList = new ArrayList<String>();
       ArrayList<String> newLecturerId = new ArrayList<String>();
       ArrayList<String> groupClassList = new ArrayList<String>();
       ArrayList<String> groupLevelList = new ArrayList<String>();
       String Course, sql;
       boolean foundGroupMatch;
     try{
       for(k =0; k < 5; k++){
          for(l = 0; l < 5; l++){
            if((k == 2 && l == 4)) { continue; } //Games day
            if((k == 4 && l == 2)||(k == 4 && l == 3)) { continue; }  //Jumat Service
              //check the current index k, l if equal to i, j
            if(k == i && l == j) { continue; }
            //get the department and level of students that offered this course from DB and store
              //in another 2 new lists
              foundGroupMatch = false;
               cnt = aryListNewDayTime[k][l].size();
               for(f =0; f < cnt; f++){
                 Course = aryListNewDayTime[k][l].get(f);
                 //for each course check if course belongs to group or not
                 
                    if(isCourseMemberOfGroup(Course)){
                        //treat the group course check if the classItem and LevelItem belongs to group
                        //get the group information for the current course
                           groupClassList.clear();
                           groupLevelList.clear(); 
                           stmt = con.createStatement();
                           sql = "SELECT * FROM TblStudentsGroup where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                           rs = stmt.executeQuery(sql);
                           if(rs.next()){
                            //is the list of departments and level for the course
                             if(rs.getInt("CSC")== 1){
                              groupClassList.add("Computer Science");
                              groupLevelList.add(rs.getString("CSC_Level"));
                             }
                             if(rs.getInt("MAC")== 1){
                              groupClassList.add("Mass Communication");
                              groupLevelList.add(rs.getString("MAC_Level"));
                             }
                             if(rs.getInt("LIS")== 1){
                              groupClassList.add("Library and Information Science");
                              groupLevelList.add(rs.getString("LIS_Level"));
                             }
                             if(rs.getInt("ICS")== 1){
                              groupClassList.add("Information and Communication Science");
                              groupLevelList.add(rs.getString("ICS_Level"));
                             }
                             if(rs.getInt("TCS")== 1){
                              groupClassList.add("Telecommunication Science");
                              groupLevelList.add(rs.getString("TCS_Level"));
                             }
                           }
                           rs.close();
                           stmt.close();
                           
                           //match ClassItem and LevelItem with the course group infor
                           for(int ll =0; ll < groupClassList.size(); ll++){
                              for(int n =0; n < grpClassItem.size(); n++){  
                                if((grpClassItem.get(n).equalsIgnoreCase(groupClassList.get(ll)))&& (grpLevelItem.get(n).equalsIgnoreCase(groupLevelList.get(ll)))){
                                //if match found, stop loop and get another course 
                                    foundGroupMatch = true; //duplicate found and repair as appropriate
                                    break; 
                                }
                              } if(foundGroupMatch){ break; } //go out of outer loop
                            }
                           if(foundGroupMatch){ break; } //go out of outer loop                               

                    } 
                    else {  

                            //for each course get class and level information 
                           stmt = con.createStatement();
                           sql = "SELECT Department, Level FROM TblCourse where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                           rs = stmt.executeQuery(sql);
                           if (rs.next()){
                            //is the department and level for the course
                            newClassList.add(rs.getString("Department"));
                            newLevelList.add(rs.getString("Level"));
                           }
                           rs.close();
                           stmt.close();
                    }
              }
                if(!foundGroupMatch){
                            //to add item then check for number of classroom available
                        //get the list of lecturers teaching this courses from DB and store in another list
                       for(f =0; f < cnt; f++){
                          Course = aryListNewDayTime[k][l].get(f);
                          //for each course get the lecturer id
                          stmt = con.createStatement();
                          sql = "SELECT Lecturer_Id FROM TblLecturerCourseAssignment where Course_Code = '" + Course + "' And Semester = '" + semester + "'";
                          rs = stmt.executeQuery(sql);
                         while (rs.next()){
                             //create a list of lecturers
                             newLecturerId.add(rs.getString("Lecturer_Id"));
                         }
                         rs.close();
                         stmt.close();  
                       }

                       boolean foundMatch = false;
                       for(f =0; f < newClassList.size(); f++){
                         for(int n =0; n < grpClassItem.size(); n++){ 
                         if((newClassList.get(f).equalsIgnoreCase(grpClassItem.get(n)))&& (newLevelList.get(f).equalsIgnoreCase(grpLevelItem.get(n)))){
                         //check if they are of the same level then remove the course and add to another
                         //if department and level are equal for the course to be added and courses already belonging to the same class meeting then
                           //don't add item into this class list, set found as true
                             foundMatch = true;
                             break;
                         }
                         } if(foundMatch) {break;}
                       }
                         if(!foundMatch)
                         {  //check if the list of lecturer doesn't contain the lecturer id
                             boolean foundMatchLecturerId = false;
                             for(f = 0; f < newLecturerId.size(); f++){
                                 if(lId.equalsIgnoreCase(newLecturerId.get(f))){
                                 //if lecturer_Id found do not add
                                 //don't add item into this class meeting, set found as true
                                     foundMatchLecturerId = true;
                                     break;
                                 }
                             }

                             if(!foundMatchLecturerId){
                                 if(aryListNewDayTime[k][l].size() < No_Of_Available_Classroom){
                                    //add item to this place
                                    aryListNewDayTime[k][l].add(item);
                                    return true;
                                 }                   
                             }
                         }
                }
              newClassList.clear();
              newLevelList.clear();
              newLecturerId.clear();
          }
       }
     }
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      }
     return false;
    }
    
  //this is version 3 for group addtonewclassmeeting  
    private boolean addToNewClassMeeting(String item, ArrayList<String> grpClassItem, ArrayList<String> grpLevelItem, int i, int j, int nn, int mm, String lId){
       int k, l, f, cnt;
       ArrayList<String> newClassList = new ArrayList<String>();
       ArrayList<String> newLevelList = new ArrayList<String>();
       ArrayList<String> newLecturerId = new ArrayList<String>();
       ArrayList<String> groupClassList = new ArrayList<String>();
       ArrayList<String> groupLevelList = new ArrayList<String>();
       String Course, sql;
       boolean foundGroupMatch;
     try{
       for(k =0; k < 5; k++){
          for(l = 0; l < 5; l++){
            if((k == 2 && l == 4)) { continue; } //Games day
            if((k == 4 && l == 2)||(k == 4 && l == 3)) { continue; }  //Jumat Service
              //check the current index k, l if equal to i, j, or equal to nn and mm
            if(k == i && l == j) { continue; }
            if(k == nn && l == mm) { continue; }
            //get the department and level of students that offered this course from DB and store
              //in another 2 new lists
              foundGroupMatch = false;
               cnt = aryListNewDayTime[k][l].size();
               for(f =0; f < cnt; f++){
                 Course = aryListNewDayTime[k][l].get(f);
                 //for each course check if course belongs to group or not
                 
                    if(isCourseMemberOfGroup(Course)){
                        //treat the group course check if the classItem and LevelItem belongs to group
                        //get the group information for the current course
                           groupClassList.clear();
                           groupLevelList.clear(); 
                           stmt = con.createStatement();
                           sql = "SELECT * FROM TblStudentsGroup where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                           rs = stmt.executeQuery(sql);
                           if(rs.next()){
                            //is the list of departments and level for the course
                             if(rs.getInt("CSC")== 1){
                              groupClassList.add("Computer Science");
                              groupLevelList.add(rs.getString("CSC_Level"));
                             }
                             if(rs.getInt("MAC")== 1){
                              groupClassList.add("Mass Communication");
                              groupLevelList.add(rs.getString("MAC_Level"));
                             }
                             if(rs.getInt("LIS")== 1){
                              groupClassList.add("Library and Information Science");
                              groupLevelList.add(rs.getString("LIS_Level"));
                             }
                             if(rs.getInt("ICS")== 1){
                              groupClassList.add("Information and Communication Science");
                              groupLevelList.add(rs.getString("ICS_Level"));
                             }
                             if(rs.getInt("TCS")== 1){
                              groupClassList.add("Telecommunication Science");
                              groupLevelList.add(rs.getString("TCS_Level"));
                             }
                           }
                           rs.close();
                           stmt.close();
                           
                           //match ClassItem and LevelItem with the course group infor
                           for(int ll =0; ll < groupClassList.size(); ll++){
                              for(int n =0; n < grpClassItem.size(); n++){  
                                if((grpClassItem.get(n).equalsIgnoreCase(groupClassList.get(ll)))&& (grpLevelItem.get(n).equalsIgnoreCase(groupLevelList.get(ll)))){
                                //if match found, stop loop and get another course 
                                    foundGroupMatch = true; //duplicate found and repair as appropriate
                                    break; 
                                }
                              } if(foundGroupMatch){ break; } //go out of outer loop
                            }
                           if(foundGroupMatch){ break; } //go out of outer loop                               

                    } 
                    else {  

                        //individual class and level found
                       stmt = con.createStatement();
                       sql = "SELECT Department, Level FROM TblCourse where Course_Code = '" + Course +"' And Semester = '" + semester + "'";
                       rs = stmt.executeQuery(sql);
                       if (rs.next()){
                        //is the department and level for the course
                        newClassList.add(rs.getString("Department"));
                        newLevelList.add(rs.getString("Level"));
                       }
                       rs.close();
                       stmt.close();
                    }
              }
              if(!foundGroupMatch){
                    //get the list of lecturers teaching this courses from DB and store in another list
                    for(f =0; f < cnt; f++){
                       Course = aryListNewDayTime[k][l].get(f);
                       //for each course get the lecturer id
                       stmt = con.createStatement();
                       sql = "SELECT Lecturer_Id FROM TblLecturerCourseAssignment where Course_Code = '" + Course + "' And Semester = '" + semester + "'";
                       rs = stmt.executeQuery(sql);
                      while (rs.next()){
                          //create a list of lecturers
                          newLecturerId.add(rs.getString("Lecturer_Id"));
                      }
                      rs.close();
                      stmt.close();  
                    }


                    boolean foundMatch = false;
                    for(f =0; f < newClassList.size(); f++){
                      for(int n =0; n < grpClassItem.size(); n++){ 
                      if((newClassList.get(f).equalsIgnoreCase(grpClassItem.get(n)))&& (newLevelList.get(f).equalsIgnoreCase(grpLevelItem.get(n)))){
                      //check if they are of the same level then remove the course and add to another
                      //if department and level are equal for the course to be added and courses already belonging to the same class meeting then
                        //don't add item into this class list, set found as true
                          foundMatch = true;
                          break;
                      }
                      } if(foundMatch) {break;}
                    }
                      if(!foundMatch)
                      {  //check if the list of lecturer doesn't contain the lecturer id
                          boolean foundMatchLecturerId = false;
                          for(f = 0; f < newLecturerId.size(); f++){
                              if(lId.equalsIgnoreCase(newLecturerId.get(f))){
                              //if lecturer_Id found do not add
                              //don't add item into this class meeting, set found as true
                                  foundMatchLecturerId = true;
                                  break;
                              }
                          }

                          if(!foundMatchLecturerId){
                          //to add item then check for number of classroom available
                              if(aryListNewDayTime[k][l].size() < No_Of_Available_Classroom){
                                 //add item to this place
                                 aryListNewDayTime[k][l].add(item);
                                 return true;
                              }                 
                          }
                      }
              }
              newClassList.clear();
              newLevelList.clear();  
              newLecturerId.clear();
          }
       }
     }
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      }
     return false;
    }
    
    public void repairLecturer(){
     //this is a method which will resolve by repairing issues such clashing of classes between
      //individual lecturer availability and students' class meetings
       ArrayList<String> lecturerList = new ArrayList<String>();
       ArrayList<String> lecturerCourseList = new ArrayList<String>();
       ArrayList<String> courseLecturerList = new ArrayList<String>();
       boolean lecturerNotAvailable;
       String dayNotAvailable = "", timeNotAvailable = "";
       int n, m, i, j, k, l, p = 0, ii = 0, jj = 0, cnt;
       String course;
    try{  
      //get the list of all lecturers for this semester by lecturer-id
      stmt = con.createStatement();
      String sql = "SELECT Lecturer_Id FROM TblLecturerCourseAssignment where Semester = '" + semester + "'";
      rs = stmt.executeQuery(sql);
      while (rs.next()){
       //create a list of lecturers
         lecturerList.add(rs.getString("Lecturer_Id"));
      }
      rs.close();
      stmt.close();
      //for each lecturer in the list
      Iterator<String> iterLecturer = lecturerList.iterator(); 
      while(iterLecturer.hasNext()){
        //get the list of courses taken by this lecturer for this semester
        String lecturerId = iterLecturer.next(); 
        stmt = con.createStatement();
        sql = "SELECT Course_Code FROM TblLecturerCourseAssignment where Lecturer_Id = '" + lecturerId + "' And Semester = '" + semester + "'";
        rs = stmt.executeQuery(sql);
        while (rs.next()){
         //create a list of courses teach by this lecturer 
         lecturerCourseList.add(rs.getString("Course_Code"));
        }
        rs.close();
        stmt.close();  
          //get the lecturer availabilty information
        lecturerNotAvailable = false;
        stmt = con.createStatement();
        sql = "SELECT Day_Not_Available, Time_Not_Available FROM TblLecturerAvailability where Lecturer_Id = '" + lecturerId + "'";
        rs = stmt.executeQuery(sql);
        if (rs.next()){
         //create a list of lecturers day and time not available
         dayNotAvailable = rs.getString("Day_Not_Available");
         timeNotAvailable = rs.getString("Time_Not_Available");
         lecturerNotAvailable = true;
        }
        rs.close();
        stmt.close();        
        //check if the no of course taken by lecturer is just one
        if(lecturerCourseList.size() == 1){
            //check if the lecturer information is specified in the Table of Lecturer not available 
            //at a particular day and time
            if(lecturerNotAvailable){
                //get the day and time not available and convert to specified index n and m respectively      
                 n = getIndexDay(dayNotAvailable);
                 m = getIndexTime(timeNotAvailable);  
                //get the location of this single taken by the lecturer from class meeting schedule
                course = lecturerCourseList.get(0); //the course code
                //check the list of courses in the location specified by day n and time m in the 
                //classmeetings and compare if there is one matching this course taken by the lecturer
                cnt = aryListNewDayTime[n][m].size();
                for(k =0; k < cnt; k++){
                    if(course.equalsIgnoreCase(aryListNewDayTime[n][m].get(k))){
                     //take the course out and perform repair classmeetings()method
                            if(isCourseMemberOfGroup(course)){
                               //treat as group course using version 1
                               arrangeCourseAsGroup(course, n, m, k, true);//lecturer status is true
                            }
                            else{
                               //treat as individual course using version 1
                               arrangeCourseAsIndividual(course, n, m, k, true);//lecturer status is true
                            }
                        countConstraint3++;
                        sumConstraint3 += 15;
                        break;
                    }
                }
                aryListNewDayTime[n][m].trimToSize();
            }
        }
        else {
            //check if a lecturer is taking more than one course
            //for each course available in the lecturer list
            for (l = 0; l < lecturerCourseList.size(); l++){
                //get the location of this course taken by the lecturer from class meeting schedule
                course = lecturerCourseList.get(l); //the course code
                //check if the course is not member of set of unallocated courses
                  if (notAllocatedCourse.contains(course)) { continue; }
                //locate where the course is schedule in day & Time slot
                for (i = 0; i < 5; i++){
                     for(j = 0; j < 5; j++){
                        if((i == 2 && j == 4)) { continue; } //Games day
                        if((i == 4 && j == 2)||(i == 4 && j == 3)) { continue; }  //Jumat Service
                            if(aryListNewDayTime[i][j].contains(course)){
                            p = aryListNewDayTime[i][j].indexOf(course);//get d location of course
                            ii = i; jj = j;
                        }
                     }
                }
                //check if other courses in list where the course apear, do not taken by the same lecturer
                //for each other courses at location day ii and time jj get the lecturer id
                 cnt = aryListNewDayTime[ii][jj].size();
                  for(k = 0; k < cnt; k++){
                    if (k == p) {continue;} //location of the present course
                    stmt = con.createStatement();
                    sql = "SELECT Lecturer_Id FROM TblLecturerCourseAssignment where Course_Code = '" + aryListNewDayTime[ii][jj].get(k) + "' And Semester = '" + semester + "'";
                    rs = stmt.executeQuery(sql);
                    while (rs.next()){
                        //create a list of lecturers
                        courseLecturerList.add(rs.getString("Lecturer_Id"));
                    }
                    rs.close();
                    stmt.close();  
                  }

                //check if this course lecturerid appear in the courseLecturerlist
                if(courseLecturerList.contains(lecturerId)){
                    //remove the current course and perform repair classmeetings()method
                    //Hence, first check if the lecturer information is specified in the Table of Lecturer not available 
                    //at a particular day and time  
                    if(lecturerNotAvailable){
                        //get the day and time not available and convert to specified index n and m respectively      
                        n = getIndexDay(dayNotAvailable);
                        m = getIndexTime(timeNotAvailable);  
                        if((ii == n) && (jj == m)){
                        //that the course is schedule at wrong date, remove and perform repair classmeetings()method
                            if(isCourseMemberOfGroup(course)){
                            //treat as group course using version 2
                                arrangeCourseAsGroup(course, n, m, p, lecturerId);
                            }
                            else{
                                //treat as individual course using version 2
                                arrangeCourseAsIndividual(course, n, m, p, lecturerId);
                            }
                            countConstraint4++; sumConstraint4 += 20;
                        }
                        else{
                            //that the course is schedule at wrong date, remove and perform repair classmeetings()method
                            if(isCourseMemberOfGroup(course)){
                                //treat as group course using version 3
                                arrangeCourseAsGroup(course, ii, jj, p, n, m, lecturerId);
                            }
                            else{
                               //treat as individual course using version 3
                               arrangeCourseAsIndividual(course, ii, jj, p, n, m, lecturerId);
                            }
                            countConstraint4++; sumConstraint4 += 20; 
                
                        }
                    }
                    else {  
                            if(isCourseMemberOfGroup(course)){
                                //treat as group course using version 2
                                arrangeCourseAsGroup(course, ii, jj, p, lecturerId);
                            }
                            else{
                                //treat as individual course using version 2
                                arrangeCourseAsIndividual(course, ii, jj, p, lecturerId);
                            }
                            countConstraint3++; sumConstraint3 += 15;
                    }
                }
                else{
                    //lecturer_Id not appear or not doubled booked(i.e. no two courses taken by this lecturer in the same dayTime slot
                    //check if the lecturer information is specified in the Table of Lecturer not available 
                    //at a particular day and time
                   if(lecturerNotAvailable){
                        //get the day and time not available and convert to specified index n and m respectively      
                          n = getIndexDay(dayNotAvailable);
                          m = getIndexTime(timeNotAvailable);  
                          if((ii == n) && (jj == m)){
                           //that the course is schedule at wrong date, remove and perform repair classmeetings()method
                              if(isCourseMemberOfGroup(course)){
                                //treat as group course using veersion 2
                                arrangeCourseAsGroup(course, n, m, p, lecturerId);
                             }
                             else{
                                //treat as individual course using version 2
                                arrangeCourseAsIndividual(course, n, m, p, lecturerId);
                             }
                              countConstraint3++; sumConstraint3 += 15;
                          }
                    }   
                }
    
                aryListNewDayTime[ii][jj].trimToSize();
                courseLecturerList.clear();
            }//end for l                 
        }//end else        
      }//end while each lecturer        
     }//end try
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      }
    } 
    
    private int getIndexDay(String dayNotAvailable){  
        int i = 0;
        if(dayNotAvailable.equalsIgnoreCase("Monday")) {i = 0;}
        else if(dayNotAvailable.equalsIgnoreCase("Tuesday")) {i = 1;}
        else if(dayNotAvailable.equalsIgnoreCase("Wednesday")) {i = 2;}
        else if(dayNotAvailable.equalsIgnoreCase("Thursday")){ i = 3;}
        else if(dayNotAvailable.equalsIgnoreCase("Friday")) {i = 4;}
        return i;
    }
    
    private int getIndexTime(String timeNotAvailable){  
        int i = 0;
        //one hour trime interval
        String[] timeSlots = {"8.00am - 9.00am", "9.00am - 10.00am", "10.00am - 11.00am", "11.00am - 12.00pm",
        "12.00pm - 1.00pm", "1.00pm - 2.00pm", "2.00pm - 3.00pm", "3.00pm - 4.00pm", "4.00pm - 5.00pm", "5.00pm - 6.00pm"};
        //two hours time interval
        String[] timeSlots1 = {"8.00am - 10.00am", "10.00am - 12.00pm", "12.00pm - 2.00pm", "2.00pm - 4.00pm", "4.00pm - 6.00pm"};
        if((timeNotAvailable.equalsIgnoreCase(timeSlots[0]))||(timeNotAvailable.equalsIgnoreCase(timeSlots[1]))||(timeNotAvailable.equalsIgnoreCase(timeSlots1[0]))) { i = 0; }
        else if((timeNotAvailable.equalsIgnoreCase(timeSlots[2]))||(timeNotAvailable.equalsIgnoreCase(timeSlots[3]))||(timeNotAvailable.equalsIgnoreCase(timeSlots1[1]))) { i = 1; }
        else if((timeNotAvailable.equalsIgnoreCase(timeSlots[4]))||(timeNotAvailable.equalsIgnoreCase(timeSlots[5]))||(timeNotAvailable.equalsIgnoreCase(timeSlots1[2]))) { i = 2; }
        else if((timeNotAvailable.equalsIgnoreCase(timeSlots[6]))||(timeNotAvailable.equalsIgnoreCase(timeSlots[7]))||(timeNotAvailable.equalsIgnoreCase(timeSlots1[3]))) { i = 3; }
        else if((timeNotAvailable.equalsIgnoreCase(timeSlots[8]))||(timeNotAvailable.equalsIgnoreCase(timeSlots[9]))||(timeNotAvailable.equalsIgnoreCase(timeSlots1[4]))) { i = 4; }
        return i;
    }
    
    public void assignClassrroms(){
      //Assigning classroom to well arrange classmeetings in the timetable
      //for each classmeeting in day-time slots
        int i, j, k, cnt, x;
        int numberOfStudents = 0;
        String sql;
        ArrayList<String> classroomId = new ArrayList<String>();
        ArrayList<Integer> classroomCapacity = new ArrayList<Integer>();
        ArrayList<Integer> classroomAvailable = new ArrayList<Integer>();
        ArrayList<Integer> classroomStudent = new ArrayList<Integer>();
     try{
      //get the list of available classrooms from db 
      stmt = con.createStatement();
      sql = "SELECT Classroom_Id, Classroom_Capacity FROM TblClassroom";
      rs = stmt.executeQuery(sql);
      while(rs.next()){
        //get number of students for the course
        classroomId.add(rs.getString("Classroom_Id"));
        classroomCapacity.add(rs.getInt("Classroom_Capacity"));
        classroomAvailable.add(0);
      }
       rs.close();
       stmt.close();   
      for (i = 0; i < 5; i++){
       for(j = 0; j < 5; j++){
         if((i == 2 && j == 4)) { continue; } //Games day
         if((i == 4 && j == 2)||(i == 4 && j == 3)) { continue; }  //Jumat Service   
        cnt = aryListNewDayTime[i][j].size();
        for(k =0; k < cnt; k++){  
          String course = aryListNewDayTime[i][j].get(k);
           //get d number of students offering this course
           stmt = con.createStatement();
           sql = "SELECT Expected_Number_Of_Students FROM TblCourseStudentNumber where Course_Code = '" + course + "'";
           rs = stmt.executeQuery(sql);
           if(rs.next()){
            //get number of students for the course
            numberOfStudents = rs.getInt("Expected_Number_Of_Students");
           }
            rs.close();
            stmt.close();
           //match the number of students with the classroom capacity
            //for each classroom
            classroomStudent.clear();
           for(int c = 0; c < classroomId.size(); c++){
               x = classroomCapacity.get(c) - numberOfStudents;
             //check if classroom is available  
               if(classroomAvailable.get(c) == 1){ //class not available
                   x = 10000; //store big number in x
               }
               else { //if classroom is available
                   if(x < 0) { x = 10000; } //store big number in x when classroom size is lesser than number of students
               }
               classroomStudent.add(x);
           }  
           //get the fittest classroom with minimum value x in the list classroomStudent
                int l = minimumValue(classroomStudent);
               //get the index of this minimum value x and fix the classroom at the location
               int m = classroomStudent.indexOf(l); 
               //attach this classroom to the course
               course = course + " (" + classroomId.get(m)+ ")"; // assign classroom
               classroomAvailable.set(m, 1);//fix or occupy this classroom
               aryListNewDayTime[i][j].set(k, course); //reset the course with classroom            
        }
        reset(classroomAvailable);
      }
     }
     }//end try
     catch (SQLException er) {
          JOptionPane.showMessageDialog(null, er.getMessage());
      }
     catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.getMessage());  
      }
    }
    
    private int minimumValue(ArrayList<Integer> Y){
        int i, minValue;
        minValue = Y.get(0);
        for(i = 1; i < Y.size(); i++){
            if(minValue > Y.get(i)){
                minValue = Y.get(i);
            }
        }
        return minValue;
    } 
    
    private void reset(ArrayList<Integer> A){
        //reset classroom availability as available = 0
        for (int i = 0; i < A.size(); i++){
            A.set(i, 0);
        }
    }
    
    public int getCost(){
        cost = sumConstraint1 + sumConstraint2 + sumConstraint3;
        return cost;
    } 
    
    public void displayResult(){
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] timeSlots = {"8.00am - 9.00am", "9.00am - 10.00am", "10.00am - 11.00am", "11.00am - 12.00pm",
        "12.00pm - 1.00pm", "1.00pm - 2.00pm", "2.00pm - 3.00pm", "3.00pm - 4.00pm", "4.00pm - 5.00pm", "5.00pm - 6.00pm"};
        //This is to generate the final Timetable
        System.out.printf("\n\n******* The New TimeTable for %s Semester after evaluation ************ ", semester);
          for (int i = 0; i < 5; i++){
               System.out.printf("\n******* %s:", days[i]);
                for(int j = 0; j < 10; j++){
                   if((i == 4 && j == 4)||(i == 4 && j == 7)) 
                   { System.out.printf("\n%s:  %s ", timeSlots[j], aryListNewDayTime[i][2]); }  //Jumat Service   
                   else if((i == 4 && j == 5)||(i == 4 && j == 6)) 
                   { System.out.printf("\n%s:  %s ", timeSlots[j], aryListNewDayTime[i][3]); }  //Jumat Service   
                   else { System.out.printf("\n%s:  %s ", timeSlots[j], aryListNewDayTime[i][j/2]);}
                    
                }
          }
    }
    
 /*
    public static void main(String args[]){
        int sem = 0;
        JOptionPane.showMessageDialog(null, "Welcome to an Automated Timetable Schedular");
        do
        { 
          sem = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter the semester: 1 - Harmattan, 2 - Rain"));
        }while((sem < 1) || (sem > 2));
        System.out.println("********* Welcome to Automated TimeTable Application  ***********");
        System.out.println("\n** This application is developed based on Constraint Programming and Genetic Algorithm  **");
        System.out.println("\n***** Population Size = 3:  *****");
        //create Timetable 1
        System.out.println("\n***** Timetable 1 Generation *****");
        CreateTimeTable createTimeTable1 = new CreateTimeTable(sem);
        createTimeTable1.generateInitialPopulation();
        //create Timetable 2
        CreateTimeTable createTimeTable2 = new CreateTimeTable(sem);
        createTimeTable2.allocateClassMeetings();
        //create Timetable 2
        CreateTimeTable createTimeTable3 = new CreateTimeTable(sem);
        createTimeTable3.allocateClassMeetings();
        int i = 0;
        while(i < 5){
        //repair Timetable 1    
        createTimeTable1.initializeCounters();
        createTimeTable1.repairClassMeetings();
        createTimeTable1.repairLecturer();
        //repair Timetable 2
        createTimeTable2.initializeCounters();
        createTimeTable2.repairClassMeetings();
        createTimeTable2.repairLecturer();        
        //repair Timetable 3
        createTimeTable3.initializeCounters();
        createTimeTable3.repairClassMeetings();
        createTimeTable3.repairLecturer();
        ++i;
        }

        createTimeTable1.assignClassrroms();
        createTimeTable1.displayResult();
        System.out.println("\n***** The cost for Timetable 1: " + createTimeTable1.getCost());
               
        //generate Timetable 2
        System.out.println("\n\n***** Timetable 2 Generation *****");
        createTimeTable2.assignClassrroms();
        createTimeTable2.displayResult();
        System.out.println("\n***** The cost for Timetable 2: " + createTimeTable2.getCost());
        
        //generate Timetable 3
        System.out.println("\n\n***** Timetable 3 Generation *****");
        createTimeTable3.assignClassrroms();
        createTimeTable3.displayResult();
        System.out.println("\n***** The cost for Timetable 3: " + createTimeTable3.getCost()) ;
        
        //Getting the  best timatable out of the population of three timetable using the minimum cost
        int best = createTimeTable1.getCost();
        if(best < createTimeTable2.getCost() && best < createTimeTable3.getCost()) 
         { //display the best resulting timetable 
            System.out.printf("\n\n******* The best Timetable is Timetable 1 for %s semester: **********", createTimeTable1.semester); 
            createTimeTable1.displayResult(); 
            System.out.println("\n***** The cost for the best Timetable is: " + createTimeTable1.getCost());
         }
        else if(best > createTimeTable2.getCost() && createTimeTable2.getCost() < createTimeTable3.getCost()) 
         { //display the best resulting timetable 
            System.out.printf("\n\n******* The best Timetable is Timetable 2 for %s semester: **********", createTimeTable2.semester); 
            createTimeTable2.displayResult(); 
            System.out.println("\n***** The cost for the best Timetable is: " + createTimeTable2.getCost());
         }
        else if(best > createTimeTable3.getCost() && createTimeTable2.getCost() > createTimeTable3.getCost()) 
         { //display the best resulting timetable
            System.out.printf("\n\n******* The best Timetable is Timetable 3 for %s semester: **********", createTimeTable3.semester); 
            createTimeTable3.displayResult(); 
            System.out.println("\n***** The cost for the best Timetable is: " + createTimeTable3.getCost());
         }
        
    } */
}

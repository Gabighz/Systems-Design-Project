

/**
* Class for a student object
*
* @author Amy Smith
*/

import java.sql.*;
import java.io.*; 
import java.util.*; 


public class Student {

  public static final String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
  public static List<Object> student = new ArrayList<Object>();

  public Student() {

  }
  
  /**
  * Method that collects all of the students data into one ArrayList so it can be displayed easily
  * 
  * @param regNo   The student's registration number.
  */
  public static void setViewStudent(int regNo) {
    student.clear();
    Statement statement = null;

    try (Connection con = DriverManager.getConnection(DB)) {
        statement = con.createStatement();
          
        ResultSet periodOfStudy = statement.executeQuery("SELECT * FROM PeriodOfStudy WHERE RegNo = " + regNo + ";");
        while (periodOfStudy.next()) {
            List<Object> gradeArray = new ArrayList<Object>();
            String level = periodOfStudy.getString("Level");
            char label = periodOfStudy.getString("Label").charAt(0);
            Double meanGrade = Teacher.meanGrade(regNo, label);
              
            student.add(level);
              
            ResultSet moduleCodes = statement.executeQuery("SELECT * FROM Approval WHERE RegNo = " + regNo + " AND Level = " + level + ";");
            while (moduleCodes.next()) {
                String code = moduleCodes.getString("moduleCode");
                gradeArray.add(code);
                  
                ResultSet grades = statement.executeQuery("SELECT * FROM Grades WHERE RegNo = " + regNo + " AND moduleCode = " + code + ";");
                String initial = grades.getString("InitialGrade");
                String resit = grades.getString("ResitGrade");
                if(resit.isEmpty() || resit == "")
                    gradeArray.add(initial);
                else
                    gradeArray.add(resit);
            }
              
            student.add(gradeArray);
            student.add(meanGrade);
              
        }
        statement.close();

    } catch (SQLException ex) {
        ex.printStackTrace();

    }
      
  }
  
  /**
   * Returns the student's data so it can be displayed in the user interface
   * 
   * @return student    An arrayList containing information for each level of study in the form: 
   *                    level of study, arraylist of modules and grades, mean grade for period of study
   */
  public static List<Object> getViewStudent() {
      return student;
  }
  

}

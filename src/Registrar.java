/**
* Class for a registrar object
*
* @author Amy Smith
*/

import java.sql.*;

public class Registrar {

  public static final String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
  public static Boolean checkCredit = false;
  public static String checkRegistrations;
  public static ResultSet results;

  public Registrar() {

  }

  /**
   * Adds a new student.
   *
   * @param title               The student's title.
   * @param forename            The student's forename.
   * @param surname             The student's surname.
   */
  public static void addStudent(String title, String forename, String surname, String tutor, String degreeCode) {
    Statement statement = null;
    String startEmail = forename.toUpperCase().charAt(0) + surname.toUpperCase().charAt(0) + surname.substring(1);

    try (Connection con = DriverManager.getConnection(DB)) {
        statement = con.createStatement();
        
        //Calculating new registration number
        ResultSet number = statement.executeQuery("SELECT COUNT(*) From Students;");
        number.last();
        int regNo = number.getRow();
        number.beforeFirst();
        regNo = regNo++;
        
        //Counting how many students with same surname and first letter of forename to make unique email address
        ResultSet count = statement.executeQuery("SELECT COUNT(*) FROM Students WHERE Surname = " + surname + " AND LEFT(Forename, 1)  = " + forename.charAt(0) + " ;");
        count.last();
        int previous = count.getRow();
        count.beforeFirst();
        
        String email = startEmail + (previous++) + "@Sheffield.ac.uk";

        //Inserting new student into the database
        String updateString = String.format("(?, ?, ?, ?, ?, ?)", title, surname, forename, regNo, email, tutor, degreeCode);
        statement.executeUpdate("INSERT INTO Students VALUES " + updateString + ";");
        
        //Adding Compulsary student modules to grades
        ResultSet modules = statement.executeQuery("SELECT * FROM Modules WHERE Degree = " + degreeCode + " AND Core  = 1;");
        while (modules.next()) {
            String moduleCode = modules.getString("ModuleCode");
            
            updateString = String.format("(?, ?)", moduleCode, regNo);
            statement.executeUpdate("INSERT INTO Grades (ModuleCode, RegNo) VALUES " + updateString + ";");
        }
        
        //setting results to the updated table so it can be viewed through user interface
        results = statement.executeQuery("SELECT * FROM Students;");
        
        statement.close();

    } catch (SQLException ex) {
        ex.printStackTrace();

    }
  }
  
  /**
   * Removes a student.
   *
   * @param RegNo     The student's registration number
   */
  public static void removeStudent(int regNo) {
    Statement statement = null;

    try (Connection con = DriverManager.getConnection(DB)) {
        statement = con.createStatement();
        
        statement.executeUpdate("DELETE * FROM Students WHERE RegNo = ?;", regNo);
        
        results = statement.executeQuery("SELECT * FROM Students;"); 
        
        statement.close();

    } catch (SQLException ex) {
        ex.printStackTrace();

    }
  }
  
  /**
  * Creates a period of study.
  *
  * @param RegNo        The student's registration number
  * @param startDate    The start date of the period of study
  * @param endDate      The end date for the period of study
  * @param level        The level of study
  * @param degreeCode   The code of the degree
  */
  public static void createPeriodOfStudy(int regNo, Date startDate, Date endDate, char level, String degreeCode) {

      Statement statement = null;

      try (Connection con = DriverManager.getConnection(DB)) {
          statement = con.createStatement();
          
          //Calculating new letter label
          ResultSet letter = statement.executeQuery("SELECT MAX(label) FROM PeriodOfStudy;");
          
          char label;
          if(letter.wasNull())
              label = 'A';
          else
              label = (char) (letter.getString("Label").charAt(0) + 1);
          
          //Adding new period of study
          String updateString = String.format("(?, ?, ?, ?, ?, ?)", label, startDate, endDate, level, degreeCode, regNo);
          statement.executeUpdate("INSERT INTO PeriodOfStudy VALUES " + updateString + " ;");
          
          results = statement.executeQuery("SELECT * FROM PeriodOfStudy;");
                  
          statement.close();

      } catch (SQLException ex) {
          ex.printStackTrace();

      }
  }
  
  
  /**
   * Method to retrieve the sql table that has been changed
   * 
   * @return results    The sql resultSet that contains the table that has been changed
   */
  public static ResultSet displayResults() {
      return results;
  }
  
  /**
   * Adds an optional module.
   *
   * @param RegNo        The student's registration number
   * @param ModuleCode   The module code
   */
  public static void addStudentModule(int regNo, String moduleCode) {
      Statement statement = null;

      try (Connection con = DriverManager.getConnection(DB)) {
          statement = con.createStatement();
              
          //Only adds the module if it is suitable
          if(suitableModule(regNo, moduleCode)) {
              String updateString = String.format("(?, ?)", moduleCode, regNo);
              statement.executeUpdate("INSERT INTO Grades (ModuleCode, RegNo) VALUES " + updateString + " ;");
          }
          
          results = statement.executeQuery("SELECT * FROM Grades;");
          
          statement.close();

      } catch (SQLException ex) {
          ex.printStackTrace();

      }
    }

  /**
   * Removes a module.
   *
   * @param RegNo        The student's registration number
   * @param ModuleCode   The module code
   */
    public static void removeStudentModule(int regNo, String moduleCode) {
      Statement statement = null;

      try (Connection con = DriverManager.getConnection(DB)) {
          statement = con.createStatement();
          
          //testing if module is optional
          ResultSet module = statement.executeQuery("SELECT * FROM Approval WHERE ModuleCode = " + moduleCode + ";");
          
          if(!module.wasNull()) {
              int core = Integer.parseInt(module.getString("Core"));
              
              //Only deletes the module if it is not core (is optional)
              if(core == 0)
                  statement.executeUpdate("DELETE * FROM Grades WHERE  RegNo = " + regNo + " AND ModuleCode = " + moduleCode + " ;");
          }
              
          results = statement.executeQuery("SELECT * FROM Grades;");
          
          statement.close();

      } catch (SQLException ex) {
          ex.printStackTrace();

      }
    }
    
    /**
     * Method that tests whether the module is suitable for the student to take
     * 
     * @param regNo         The students registration number
     * @param moduleCode    The module code
     * @return              returns true if all requirements are met
     */
    public static boolean suitableModule(int regNo, String moduleCode) {
        Statement statement = null;
        int core = 2;
        char moduleLevel = '\u0000';
        char studentLevel = '\u0000';
        String moduleDegree = "";
        String studentDegree = "";

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            
            //getting variables for testing if module is optional, is at student's level, and is part of their degree
            ResultSet periodOfStudy = statement.executeQuery("SELECT Max(Label) FROM PeriodOfStudy WHERE RegNo = " + regNo + ";");
            ResultSet module = statement.executeQuery("SELECT * FROM Approval WHERE ModuleCode = " + moduleCode + ";");
            if(!module.wasNull() && !periodOfStudy.wasNull()) {
                core = Integer.parseInt(module.getString("Core"));
                moduleLevel = module.getString("Level").charAt(0);
                studentLevel = periodOfStudy.getString("Level").charAt(0);
                moduleDegree = module.getString("DegreeCode");
                studentDegree = periodOfStudy.getString("DegreeCode");
            }
            
            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }
        
        //returns true if module meets all the requirements
        if(core == 0 && (moduleLevel != '\u0000' && moduleLevel == studentLevel) && (studentDegree != "" && studentDegree == moduleDegree))
            return true;
        else 
            return false;
    }
    
    public static String getCheckRegistrations() {
        return checkRegistrations;
    }

    /**
     * Checks if a student's credits add up to the correct number.
     *
     * @param RegNo        The student's registration number
     */
    public static void setCheckCredit(int regNo) {
      Statement statement = null;

      try (Connection con = DriverManager.getConnection(DB)) {
          statement = con.createStatement();
          
          //getting modules student is taking
          ResultSet modules = statement.executeQuery("SELECT ModuleCode FROM Grades WHERE RegNo = " + regNo + ";");
          
          if(!modules.wasNull()) {
              //getting total credits from modules
              int credits = 0;
              while (modules.next()) {
                  String modCredits = modules.getString("Credits");
                  credits = credits + (Integer.parseInt(modCredits));
              }
              
              //getting number of credits student should have
              int level =  Integer.parseInt(statement.executeQuery("SELECT Level FROM PeriodOfStudy WHERE RegNo = " + regNo + ";").getString("Level"));
              int levelCredits = 0;
              if(level == 4) 
                  levelCredits = 180;
              else
                  levelCredits = 120;
              
              //comparing total module credits with expected
              if (levelCredits == credits)
                  checkCredit = true;
              else
                  checkCredit = false;
          }
          
          statement.close();

      } catch (SQLException ex) {
          ex.printStackTrace();

      }
    }

    
    /**
     * Returns whether the student has the correct total module credits.
     * 
     * @return checkCredit   boolean that shows whether credits are as expected
     */
    public static boolean getCheckCredit() {
      return checkCredit;
    }


}

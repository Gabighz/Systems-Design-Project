/**
* Class for a registrar object
*
* @author Amy Smith
*/

import java.sql.*;

public class Registrar {

  public final String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
  public Boolean checkCredit = false;
  public ResultSet results;

  public Registrar() {

  }

  /**
   * Adds a new student.
   *
   * @param title               The student's title.
   * @param forename            The student's forename.
   * @param surname             The student's surname.
   */
  public void addStudent(String title, String forename, String surname, String tutor, String degreeCode) {
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
  public void removeStudent(int regNo) {
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
  public void createPeriodOfStudy(int regNo, Date startDate, Date endDate, char level, String degreeCode) {

      Statement statement = null;

      try (Connection con = DriverManager.getConnection(DB)) {
          statement = con.createStatement();
          
          //Calculating new letter label
          ResultSet letter = statement.executeQuery("SELECT MAX(label) FROM PeriodOfStudy;");
          char label = letter.getString("Label").charAt(0);
          
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
  public ResultSet displayResults() {
      return results;
  }
  
  /**
   * Adds an optional module.
   *
   * @param RegNo        The student's registration number
   * @param ModuleCode   The module code
   */
  public void addStudentModule(int regNo, String moduleCode) {
      Statement statement = null;

      try (Connection con = DriverManager.getConnection(DB)) {
          statement = con.createStatement();
          
          String updateString = String.format("(?, ?)", moduleCode, regNo);
          statement.executeUpdate("INSERT INTO Grades (ModuleCode, RegNo) VALUES " + updateString + " ;");
          
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
    public void removeStudentModule(int regNo, String ModuleCode) {
      Statement statement = null;

      try (Connection con = DriverManager.getConnection(DB)) {
          statement = con.createStatement();
          
          statement.executeUpdate("DELETE * FROM Grades WHERE  RegNo = " + regNo + " AND ModuleCode = " + ModuleCode + " ;");
          
          results = statement.executeQuery("SELECT * FROM Grades;");
          
          statement.close();

      } catch (SQLException ex) {
          ex.printStackTrace();

      }
    }

    /**
     * Checks if a student's credits add up to the correct number.
     *
     * @param RegNo        The student's registration number
     */
    public void setCheckCredit(int regNo) {
      Statement statement = null;

      try (Connection con = DriverManager.getConnection(DB)) {
          statement = con.createStatement();
          
          //getting modules student is taking
          ResultSet modules = statement.executeQuery("SELECT ModuleCode FROM Grades WHERE RegNo = " + regNo + ";");
          
          //getting total credits from modules
          int credits = 0;
          while (modules.next()) {
              String modCredits = modules.getString("Credits");
              credits = credits + (Integer.parseInt(modCredits));
          }
          
          //getting number of credits student should have
          String level =  (statement.executeQuery("SELECT Level FROM PeriodOfStudy WHERE RegNo = " + regNo + ";")).getString("Level");
          int levelCredits = 0;
          if(level == "P") 
              levelCredits = 180;
          else if (level == "U" )
              levelCredits = 120;
          
          //comparing total module credits with expected
          if (levelCredits == credits)
              checkCredit = true;
          else
              checkCredit = false;
          
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
    public boolean getCheckCredit() {
      return checkCredit;
    }


}

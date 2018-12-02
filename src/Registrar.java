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
        
        ResultSet number = statement.executeQuery("SELECT COUNT(*) From Students;");
        number.last();
        int regNo = number.getRow();
        number.beforeFirst();
        regNo = regNo++;
        
        ResultSet count = statement.executeQuery("SELECT COUNT(*) FROM Students WHERE Surname = " + surname + " AND LEFT(Forename, 1)  = " + forename.charAt(0) + " ;");
        count.last();
        int previous = count.getRow();
        count.beforeFirst();
        
        String email = startEmail + (previous++) + "@Sheffield.ac.uk";

        String updateString = String.format("(?, ?, ?, ?, ?, ?)", title, surname, forename, regNo, email, tutor, degreeCode);
        statement.executeUpdate("INSERT INTO Students VALUES " + updateString + ";");
        
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
  * Creates period of study.
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
          
          ResultSet letter = statement.executeQuery("SELECT MAX(label) FROM PeriodOfStudy;");
          char label = letter.getString("Label").charAt(0);
          
          String updateString = String.format("(?, ?, ?, ?, ?, ?)", label, startDate, endDate, level, degreeCode, regNo);
          statement.executeUpdate("INSERT INTO PeriodOfStudy VALUES " + updateString + " ;");
          
          results = statement.executeQuery("SELECT * FROM PeriodOfStudy;");
                  
          statement.close();

      } catch (SQLException ex) {
          ex.printStackTrace();

      }
  }
  
  public ResultSet displayResults() {
      return results;
  }

  



}

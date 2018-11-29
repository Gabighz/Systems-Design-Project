/**
 * This class constructs a Teacher object.
 *
 * @author Alexandre Gauthier
 */

import java.sql.*;

public class Teacher {

    /**
     * Constructs a Teacher
     */
    public Teacher() {

    }

    /**
     * Method to convert a Teacher to a string.
     *
     * @return String to print out the Teacher.
     */
    public String toString() {
        return "This is a Teacher.";
    }

    /**
     * Adds grade.
     *
     * @param grade The grade to add.
     * @param regNo The student registration number.
     * @param moduleCode The module code.
     * @param resit If the grade is a resit grade or not.
     */
    public void addGrade(Float grade, int regNo, String moduleCode, Boolean resit) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        Float initialGrade = null;
        Float resitGrade = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            if (resit)
                resitGrade = grade;
            else
                initialGrade = grade;

            String toExecute = String.format("INSERT INTO Grade (initialGrade, resitGrade, regNo, moduleCode) " +
                    "VALUES('%s', '%s', '%s', '%s') ON DUPLICATE KEY UPDATE initialGrade='%s', resitGrade='%s'",
                    initialGrade, resitGrade, regNo, moduleCode, initialGrade, resitGrade);
            statement.executeUpdate(toExecute);

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Updates grade.
     *
     * @param grade The grade to update.
     * @param regNo The student registration number.
     * @param moduleCode The module code.
     * @param resit If the grade is a resit grade or not.
     */
    public void updateGrade(Float grade, int regNo, String moduleCode, Boolean resit) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            String toExecute;

            if (resit) {
                toExecute = String.format("UPDATE Grade SET resitGrade='%s' " +
                        "WHERE regNo='%s' AND moduleCode='%s'", grade, regNo, moduleCode);
            }
            else {
                toExecute = String.format("UPDATE Grade SET initialGrade='%s' " +
                        "WHERE regNo='%s' AND moduleCode='%s'", grade, regNo, moduleCode);
            }

            statement.executeUpdate(toExecute);
            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns a mean grade of a student of a period of study.
     *
     * @param regNo The student registration number.
     * @param label label of the period of study.
     * @returns mean grade.
     */
    public Float meanGrade(int regNo, char label) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }


    /**
     * View student status.
     *
     * @param regNo The student registration number.
     * @returns The student status.
     */
    /** public String viewStatus(int regNo) { return Student.viewStatus(regNo); } */
}

/**
 * This class constructs an Administrator object.
 *
 * It can add and remove user accounts, granting suitable privileges to
 * users to perform only the designated tasks for their role.
 *
 * It can add and remove university departments from the system.
 *
 * It can add and remove degree courses, linking them to the one or many departments that teach
 * the degree, indicating the lead department for the degree.
 *
 * It can add and remove modules, linking them to the degrees and levels of study for which they are
 * approved (stating whether core or not).
 *
 * @author Gabriel Ghiuzan
 */

import java.sql.*;


public class Administrator {

    // Stores the connection link to the database
    public static final String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";

    /**
     * Constructs an Administrator
     */
    public Administrator() {

    }

    /**
     * Method to convert an Administrator to a string.
     *
     * @return String to print out the Administrator.
     */
    public String toString() {
        return "This is an Administrator.";
    }

    /**
     * Adds a new user account.
     *
     * @param role               The role of the owner of the account.
     * @param emailAddress       The email address of the new user account.
     * @param passwordToHash     The unhashed password of the new user account.
     */
    public void addUser(String role, String emailAddress, String passwordToHash) {

        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            String hashedPassword = BCrypt.hashpw(passwordToHash, BCrypt.gensalt());

            String toInsert = String.format("INSERT INTO Accounts VALUES ('%s', '%s', '%s')", emailAddress, hashedPassword, role);

            statement.executeUpdate(toInsert);

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

    /**
     * Deletes a user account.
     *
     * @param emailAddress The email address of the account to be deleted.
     */
    public void removeUser(String emailAddress) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            String toRemove = String.format("'%s'", emailAddress);
            statement.execute("DELETE FROM Accounts WHERE Email=" + toRemove);
            statement.execute("DELETE FROM Students WHERE Email=" + toRemove);

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

    /**
     * Adds a new department.
     *
     * @param name The name of the department to be added.
     * @param code The code of the department to be added.
     */
    public void addDepartment(String name, String code) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            String toInsert = String.format("INSERT INTO Departments VALUES ('%s', '%s')", name, code);
            statement.executeUpdate(toInsert);

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

    /**
     * Deletes a department.
     *
     * @param code The code of the department to be deleted.
     */
    public void removeDepartment(String code) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            String toDelete = String.format("DELETE FROM Departments WHERE DepartmentCode='%s'", code);
            statement.executeUpdate(toDelete);

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

    /**
     * Links a partner department to a degree.
     *
     * @param degreeCode     The code of the degree to be for which it is approved.
     * @param departmentCode The code of the partner department.
     */
    public void partnerDepartment(String degreeCode, String departmentCode) {

        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            String toInsert = String.format("INSERT INTO partnerDepartments VALUES ('%s', '%s')", degreeCode, departmentCode);
            statement.executeUpdate(toInsert);

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

    /**
     * Adds a new degree.
     *
     * @param name The name of the degree to be added.
     * @param code The code of the degree to be added.
     */
    public void addDegree(String name, String code) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            String leadDepartment = code.substring(0, 3);

            String toInsert = String.format("INSERT INTO Degrees VALUES ('%s', '%s', '%s')", name, code, leadDepartment);
            statement.executeUpdate(toInsert);

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

    /**
     * Deletes a degree.
     *
     * @param code The code of the degree to be deleted.
     */
    public void removeDegree(String code) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            String toDelete = String.format("DELETE FROM Degrees WHERE DegreeCode='%s'", code);
            statement.executeUpdate(toDelete);

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

    /**
     * Adds a new module.
     *
     * @param name         The name of the module to be added.
     * @param code         The code of the module to be added.
     * @param calendarType Specifies if a module is taught taught either in autumn, in spring, over
     *                     the summer or across the academic year. E.g. "AUTUMN", "SPRING", "SUMMER", "ACADEMIC YEAR".
     * @param credits      The number of credits the module carries, by default:
     *                     20 credits in level 1-3, 15 in level 4,
         *                 40 credits for undergraduate dissertations, 60 credits for masters' dissertations
     */
    public void addModule(String name, String code, String calendarType, int credits) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            String toInsert = String.format("INSERT INTO Modules VALUES ('%s', '%s', '%s', '%d')", name, code, calendarType, credits);
            statement.executeUpdate(toInsert);

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

    /**
     * Links a module to a degree and a level of study. Also specifies if it is core or not.
     *
     * @param moduleCode The code of the module to be linked.
     * @param degreeCode The code of the degree to be for which it is approved.
     * @param level      The level of study for which it is approved.
     * @param isCore     Whether the module is core or not.
     */
    public void linkModule(String moduleCode, String degreeCode, int level, boolean isCore) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        int core;
        if (isCore)
            core = 1;
        else
            core = 0;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            String toInsert = String.format("INSERT INTO Approval VALUES ('%s', '%s', '%c', '%s')", moduleCode, degreeCode, level, core);
            statement.executeUpdate(toInsert);

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

    /**
     * Deletes a module.
     *
     * @param code The code of the module to be deleted.
     */
    public void removeModule(String code) {

        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            String toDelete = String.format("'%s'", code);
            statement.executeUpdate("DELETE FROM Approval WHERE ModuleCode=" + toDelete);
            statement.executeUpdate("DELETE FROM Modules WHERE ModuleCode=" + toDelete);

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

}
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

public class Administrator extends Account {

    /**
     * Constructs an Administrator
     *
     * @param emailAddress The email address of the administrator.
     * @param password     The password of the administrator.
     * @param title        The title the administrator has, such as Mister.
     * @param forename     The forename of the administrator.
     * @param surname      The surname of the administrator.
     */
    public Administrator(String emailAddress, String password, String title, String forename, String surname) {
        super(emailAddress, password, title, forename, surname);

    }

    /**
     * Method to convert an Administrator to a string.
     *
     * @return String to print out the Administrator.
     */
    public String toString() {
        String result = "This is an Administrator.\n";
        result += super.toString();
        return result;
    }

    /**
     * Adds a new user account.
     *
     * @param accountType The type of the account be created. Used in granting privileges.
     * @param emailAddress The email address of the new user account.
     * @param password     The password of the new user account.
     */
    public void addUser(String accountType, String emailAddress, String password) {

        createRoles();

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            String toCreate = String.format("CREATE USER '%s'@'localhost' IDENTIFIED BY '%s'", emailAddress, password);
            statement.execute(toCreate);

            String toGrant = "";

            if (accountType.toLowerCase().equals("student")) {
                toGrant = String.format("GRANT 'student' TO '%s'@'localhost' WHERE Email = %s", emailAddress, emailAddress);

            } else {
                toGrant = String.format("GRANT '%s' TO '%s'@'localhost'", accountType.toLowerCase(), emailAddress);

            }

            statement.execute(toGrant);

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

            String toRemove = String.format("DROP USER %s CASCADE", emailAddress);
            statement.execute(toRemove);

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

            String toInsert = String.format("('%s', '%s')", name, code);
            statement.executeUpdate("INSERT INTO Departments " + "VALUES " + toInsert);

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
            statement.executeUpdate("DELETE FROM Departments " + "WHERE Code = " + code);

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

    /**
     * Creates user roles if they don't yet exist
     */
    private void createRoles() {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            statement.execute("IF DATABASE_PRINCIPAL_ID('administrator') IS NULL" +
                    " BEGIN" +
                    " CREATE ROLE administrator" +
                    " GRANT INSERT, DROP, DELETE ON * . * TO administrator WITH GRANT OPTION" +
                    " END");

            statement.execute("IF DATABASE_PRINCIPAL_ID('registrar') IS NULL" +
                    " BEGIN" +
                    " CREATE ROLE registrar" +
                    " GRANT ALL ON Students TO registrar" +
                    " END");

            statement.execute("IF DATABASE_PRINCIPAL_ID('teacher') IS NULL" +
                    " BEGIN" +
                    " CREATE ROLE teacher" +
                    " GRANT ALL ON Students TO teacher" +
                    " END");

            statement.execute("IF DATABASE_PRINCIPAL_ID('student') IS NULL" +
                    " BEGIN" +
                    " CREATE ROLE student" +
                    " GRANT SELECT ON Students TO student" +
                    " END");

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

}
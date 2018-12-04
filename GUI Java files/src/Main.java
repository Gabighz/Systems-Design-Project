import java.sql.*;

/**
 * This class handles log-in and *to be completed*
 *
 * @author Gabriel Ghiuzan
 */

public class Main {

    // Stores the connection link to the database
    public static final String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";

    /**
     * Takes a user email address and password as input.
     * Then it checks this data against the Accounts table.
     *
     * Hashing should be deterministic. Therefore, if the passwords match and the same hashing is used,
     * then their hashed versions should match.
     *
     * @param emailAddress       The given email address for logging in.
     * @param inputPassword     The corresponding password for logging in.
     *
     * @return Whether the log-in was successful or not.
     */
    public static boolean logIn(String emailAddress, String inputPassword) {

        Statement statement = null;
        boolean success = false;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            String toExecute = String.format("SELECT * FROM Accounts WHERE Email='%s'", emailAddress);
            ResultSet resultSet = statement.executeQuery(toExecute);

            if (resultSet == null) {
                System.out.println("This email address is not registered.");

            } else {

                String email = null;
                String hashedPassword = null;
                String role = null;

                while (resultSet.next())
                {
                    email = resultSet.getString("Email");
                    hashedPassword = resultSet.getString("Password");
                    role = resultSet.getString("Role");
                }

                if (BCrypt.checkpw(inputPassword, hashedPassword)){
                    System.out.println("Passwords match.");
                    success = true;

                } else {
                    System.out.println("You have entered a wrong password.");

                }

            }

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

        return success;

    }

    public static void main(String[] args){
        Administrator admin = new Administrator();
        admin.removeUser("gghiuzan");
        admin.addUser("student", "gghiuzan", "1234");
        logIn("gghiuzan", "1234");

    }
}

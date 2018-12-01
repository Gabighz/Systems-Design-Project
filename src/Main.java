import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * This class handles log-in and *to be completed*
 *
 * @author Gabriel Ghiuzan
 */

public class Main {
    /**
     * Takes a user email address and password as input.
     * Then it checks this data against the Account table.
     *
     * Hashing should be deterministic. Therefore, if the passwords match and the same hashing is used,
     * then their hashed veresultSetions should match.
     *
     * @param emailAddress       The given email address for logging in.
     * @param passwordToHash     The corresponding password for logging in.
     */
    public Boolean logIn(String emailAddress, String passwordToHash) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;
        Boolean success = false;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            String toExecute = String.format("SELECT * FROM Accounts WHERE Email='%s'", emailAddress);
            ResultSet resultSet = statement.executeQuery(toExecute);

            if (resultSet == null) {
                System.out.println("This email address is not registered.");

            } else {

                String email = null;
                String password = null;
                byte[] salt = null;
                String role = null;

                while (resultSet.next())
                {
                    email = resultSet.getString("Email");
                    password = resultSet.getString("Password");
                    salt = resultSet.getBytes("Salt");
                    role = resultSet.getString("Role");
                }

                MessageDigest md = null;

                try {
                    md = MessageDigest.getInstance("SHA-512");
                    md.update(salt);

                } catch (NoSuchAlgorithmException e) {
                    System.err.println("SHA-512 is not a valid message digest algorithm");

                }

                byte[] hashedPassword = null;

                try {
                    hashedPassword = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));

                } catch (NullPointerException e) {
                    System.err.println("Digest is a null pointer.");

                }

                if (password.equals(hashedPassword.toString())) {
                    System.out.println("Log-in successful!");
                    success = true;

                } else {
                    System.out.println("Wrong password");
                    
                }

            }

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

        return success;

    }

    public static void main(String[] args){

    }
}

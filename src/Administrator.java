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

public class Administrator extends Account {

    /**
     * Constructs an Administrator
     * @param emailAddress The email address of the administrator.
     * @param password The password of the administrator.
     * @param title The title the administrator has, such as Mister.
     * @param forename The forename of the administrator.
     * @param surname The surname of the administrator.
     */
    public Administrator (String emailAddress, String password, String title, String forename, String surname) {
        super (emailAddress, password, title, forename, surname);

    }

    /**
     * Method to convert an Administrator to a string.
     *
     * @return String to print out the Administrator.
     */
    public String toString () {
        String result = "This is an Administrator.\n";
        result += super.toString ();
        return result;
    }

    /**
     * Adds a new user account.
     * Granting privileges should be inherent to the type of the account created. For example, the front-end should see
     * that a given account is a Student object. From that account, a user should only be able to see a 'view status' screen.
     *
     * @param accountType The type of the account to be created, e.g. administrator or student.
     * @param emailAddress The email address of the new user account.
     * @param password The password of the new user account.
     * @param title The title the new user account has, such as Mister or Doctor.
     * @param forename The forename of the new user account.
     * @param surname The surname of the new user account.
     *
     * @return A new account of intended type, e.g. an administrator or student account.
     */
    public Account addUser(String accountType, String emailAddress, String password, String title, String forename, String surname) {
        Account newAccount;

        switch (accountType) {
            case "administrator":
                newAccount = new Administrator (emailAddress, password, title, forename, surname);
                break;
            case "registrar":
                newAccount = new Registrar (emailAddress, password, title, forename, surname);
                break;
            case "teacher":
                newAccount = new Teacher (emailAddress, password, title, forename, surname);
                break;
            case "student":
                newAccount = new Student (emailAddress, password, title, forename, surname);
                break;
            default:
                newAccount = null; // or could throw an exception
                break;
        }

        return newAccount;

    }

}
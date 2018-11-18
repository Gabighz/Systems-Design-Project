/**
 * This class constructs an Account object.
 *
 * It has get methods for title, forename, and surname.
 * It has no set methods, given that the data stored in the object shouldn't be changed.
 *
 * @author Gabriel Ghiuzan
 */

public class Account {

    /*
     * The email address of the Account
     */
    protected String emailAddress;

    /*
     * The password of the Account
     */
    protected String password;

    /*
     * The title of the Account, such as Mister or Doctor.
     */
    protected String title;

    /*
     * The forename of the Account
     */
    protected String forename;

    /*
     * The surname of the Account
     */
    protected String surname;

    /**
     * Constructs an Account
     * @param emailAddress The email address of the administrator.
     * @param password The password of the administrator.
     * @param title The title the administrator has, such as Mister.
     * @param forename The forename of the administrator.
     * @param surname The surname of the administrator.
     */
    public Account (String emailAddress, String password, String title, String forename, String surname) {
        this.emailAddress = emailAddress;
        this.password = password;
        this.title = title;
        this.forename = forename;
        this.surname = surname;

    }

}
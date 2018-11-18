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

}
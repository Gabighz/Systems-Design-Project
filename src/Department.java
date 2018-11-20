/**
 * This class constructs a Department object.
 *
 * It has get methods for the name and the code of the department.
 * It has no set methods, given that the data stored in the object shouldn't be changed.
 *
 * @author Gabriel Ghiuzan
 */

public class Department {

    /*
     * The name of the Department
     */
    private String name;

    /*
     * The code of the Department
     */
    private String code;

    /**
     * Constructs a Department
     * @param name The name of the department.
     * @param code The code of the department.
     */
    public Department (String name, String code) {
        this.name = name;
        this.code = code;

    }

    /**
     * The method returns a string suitable for printing.
     *
     * @return String to print out the Department.
     */
    public String toString () {
        String result = "";
        result += "The name of the department is: " + name + "\n";
        result += "The code of the department is: " + code + "\n";
        return result;
    }

    /**
     * @return The name of the department.
     */
    public String getName () {
        return name;

    }

    /**
     * @return The code of the department.
     */
    public String getCode () {
        return code;

    }


}

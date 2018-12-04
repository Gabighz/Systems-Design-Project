/**
 * This class constructs a Student object.
 *
 * @author Alexandre Gauthier
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Student {

    final static String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";

    /**
     * View student status.
     *
     * @param regNo The student registration number.
     * @returns The student status.
     */
    public static String get(int regNo) {
        return "1";
    }



    public static void main(String[] args) {
    }
}

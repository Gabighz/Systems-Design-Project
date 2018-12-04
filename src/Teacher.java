/**
 * This class constructs a Teacher object.
 *
 * @author Alexandre Gauthier
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Teacher {

    final static String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";

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
    public static void addGrade(Double grade, int regNo, String moduleCode, Boolean resit) {
        String toExecute;
        if (resit) {
            toExecute = String.format("INSERT INTO Grades (ResitGrade, RegNo, ModuleCode) " +
                            "VALUES('%s', '%s', '%s') ON DUPLICATE KEY UPDATE ResitGrade='%s'",
                    grade, regNo, moduleCode, grade);
        }
        else {
            toExecute = String.format("INSERT INTO Grades (InitialGrade, RegNo, ModuleCode) " +
                            "VALUES('%s', '%s', '%s') ON DUPLICATE KEY UPDATE InitialGrade='%s'",
                    grade, regNo, moduleCode, grade);
        }

        Statement statement = null;
        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            statement.executeUpdate(toExecute);
            statement.close();
        }
        catch (SQLException ex) {
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
    public static void updateGrade(Double grade, int regNo, String moduleCode, Boolean resit) {
        String toExecute;
        if (resit) {
            toExecute = String.format("UPDATE Grades SET ResitGrade='%s' " +
                    "WHERE regNo='%s' AND moduleCode='%s'", grade, regNo, moduleCode);
        }
        else {
            toExecute = String.format("UPDATE Grades SET InitialGrade='%s' " +
                    "WHERE RegNo='%s' AND ModuleCode='%s'", grade, regNo, moduleCode);
        }

        Statement statement = null;
        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            statement.executeUpdate(toExecute);
            statement.close();
        }
        catch (SQLException ex) {
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
    public static Double meanGrade(int regNo, char label) {

        Statement statement = null;
        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            String toExecute = String.format("SELECT * FROM PeriodOfStudy WHERE RegNo='%s' AND Label='%s';",
                    regNo, label);
            ResultSet resultSet = statement.executeQuery(toExecute);

            List<String> list = new ArrayList();
            while (resultSet.next())
                list.add(resultSet.getString("DegreeCode"));
            String degreeCode = list.get(0);

            resultSet.beforeFirst();
            list = new ArrayList();
            while (resultSet.next())
                list.add(resultSet.getString("Level"));
            String level = list.get(0);

            toExecute = String.format("SELECT * FROM Approval WHERE DegreeCode='%s' AND Level='%s';",
                    degreeCode, level);
            resultSet = statement.executeQuery(toExecute);

            List<String> moduleCodes = new ArrayList();
            while (resultSet.next())
                moduleCodes.add(resultSet.getString("ModuleCode"));

            toExecute = String.format("SELECT * FROM Grades WHERE RegNo='%s';", regNo);
            resultSet = statement.executeQuery(toExecute);

            List<Double> resitGrades = new ArrayList();
            while (resultSet.next()) {
                Double resitGrade = resultSet.getDouble("ResitGrade");
                if (resultSet.wasNull())
                    resitGrades.add(null);
                else
                    resitGrades.add(resitGrade);
            }

            resultSet.beforeFirst();
            List<Double> initialGrades = new ArrayList();
            while (resultSet.next()) {
                Double initialGrade = resultSet.getDouble("InitialGrade");
                if (resultSet.wasNull())
                    initialGrades.add(null);
                else
                    initialGrades.add(initialGrade);
            }

            resultSet.beforeFirst();
            List<String> gradeModuleCodes = new ArrayList();
            while (resultSet.next())
                gradeModuleCodes.add(resultSet.getString("ModuleCode"));

            statement.close();

            int count = 0;
            Double meanGrade = 0.0;
            for (int i = 0; i < resitGrades.size(); i++) {
                if (moduleCodes.contains(gradeModuleCodes.get(i))) {
                    if (resitGrades.get(i) != null) {
                        meanGrade += resitGrades.get(i);
                        count++;
                    }
                    else if (initialGrades.get(i) != null) {
                        meanGrade += initialGrades.get(i);
                        count++;
                    }
                }
            }
            if(count != 0) {
                meanGrade /= count;
                return meanGrade;
            }
            else {
                return null;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Checks if a student can progress to the next level of study.
     *
     * @param meanGrade The student mean grade for a period of study.
     * @param degreeClass The student degree.
     * @returns true if student can progress, false if not.
     */
    public static Boolean progress(int meanGrade, String degreeClass) {

        if (degreeClass.equals("1-year MSc") || degreeClass.equals("MComp") || degreeClass.equals("MEng")) {
            if (meanGrade < 49.5)
                return false;
            else
                return true;
        }
        else if (degreeClass.equals("BSc") || degreeClass.equals("BEng")) {
            if (meanGrade < 39.5)
                return false;
            else
                return true;
        }
        return null;

    }



    /**
     * Returns the student degree result.
     *
     * @param meanGrade The student mean grade for a period of study.
     * @param degreeClass The student degree.
     * @returns the student degree result.
     */
    public static String degreeResult(int meanGrade, String degreeClass) {
        if (degreeClass.equals("1-year MSc")) {
            if (meanGrade < 49.5)
                return "fail";
            else if (meanGrade < 59.5)
                return "pass";
            else if (meanGrade < 69.5)
                return "merit";
            else
                return "distinction";
        }
        else if (degreeClass.equals("BSc") || degreeClass.equals("BEng")) {
            if (meanGrade < 39.5)
                return "fail";
            else if (meanGrade < 44.5)
                return "pass (non-honours)";
            else if (meanGrade < 49.5)
                return "third class";
            else if (meanGrade < 59.5)
                return "lower second";
            else if (meanGrade < 69.5)
                return "upper second";
            else
                return "first class";
        }
        else if (degreeClass.equals("MComp") || degreeClass.equals("MEng")) {
            if (meanGrade < 49.5)
                return "fail";
            else if (meanGrade < 59.5)
                return "lower second";
            else if (meanGrade < 69.5)
                return "upper second";
            else
                return "first class";
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



    public static void main(String[] args) {
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            Administrator admin = new Administrator();

            admin.addUser("student", "email@email", "12345");
            statement.executeUpdate("INSERT INTO Students VALUES ('Mr', 'A', 'G', '123', 'email@email','tutor');");
            admin.addDepartment("Computer Science", "COM");
            admin.addDegree("Bsc Comp", "COMU03");
            admin.addModule("Systems Design and Security", "COM1001", "AUT", 20);
            admin.linkModule("COM1001", "COMU03", '1', true);
            statement.executeUpdate("INSERT INTO PeriodOfStudy VALUES ('A', 1, 2, '1', 'COMU03', '123');");

            Teacher.addGrade(78.1, 123, "COM1001", false);
            Teacher.updateGrade(67.1, 123, "COM1001", false);
            System.out.println(Teacher.meanGrade(123, 'A'));

            statement.executeUpdate("DELETE FROM PeriodOfStudy WHERE RegNo=123;");
            statement.executeUpdate("DELETE FROM Grades WHERE RegNo=123 AND ModuleCode='COM1001';");
            admin.removeModule("COM1001");
            admin.removeDegree("COMU03");
            admin.removeDepartment("COM");
            statement.executeUpdate("DELETE FROM Students WHERE Email='email@email';");
            admin.removeUser("email@email");

            statement.close();
            System.out.println("Statement closed.");
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

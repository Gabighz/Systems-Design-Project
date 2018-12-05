/**
 * This class constructs a Teacher object.
 *
 * @author Alexandre Gauthier
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Teacher {

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
    public void addGrade(Double grade, int regNo, String moduleCode, Boolean resit) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            String toExecute;

            if (resit) {
                toExecute = String.format("INSERT INTO Grades (ResitGrade, RegNo, ModuleCode) " +
                                "VALUES('%s', '%s', '%s') ON DUPLICATE KEY UPDATE ResitGrade='%s'",
                        grade, regNo, moduleCode, grade);
            }
            else{
                toExecute = String.format("INSERT INTO Grades (InitialGrade, RegNo, ModuleCode) " +
                                "VALUES('%s', '%s', '%s') ON DUPLICATE KEY UPDATE InitialGrade='%s'",
                        grade, regNo, moduleCode, grade);
            }
            statement.executeUpdate(toExecute);

            statement.close();

        } catch (SQLException ex) {
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
    public void updateGrade(Double grade, int regNo, String moduleCode, Boolean resit) {

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            String toExecute;

            if (resit) {
                toExecute = String.format("UPDATE Grades SET ResitGrade='%s' " +
                        "WHERE regNo='%s' AND moduleCode='%s'", grade, regNo, moduleCode);
            }
            else {
                toExecute = String.format("UPDATE Grades SET InitialGrade='%s' " +
                        "WHERE RegNo='%s' AND ModuleCode='%s'", grade, regNo, moduleCode);
            }

            statement.executeUpdate(toExecute);
            statement.close();

        } catch (SQLException ex) {
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

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {

            statement = con.createStatement();

            String toExecute = String.format("SELECT * FROM PeriodOfStudy WHERE RegNo='%s' AND Label='%s';",
                    regNo, label);
            ResultSet resultSet = statement.executeQuery(toExecute);

            Array array = resultSet.getArray("DegreeCode");
            Object[] objects = (Object[]) array.getArray();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                list.add(objects[i].toString());
            }
            String degreeCode = list.get(0);

            array = resultSet.getArray("Level");
            objects = (Object[]) array.getArray();
            list = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                list.add(objects[i].toString());
            }
            String level = list.get(0);

            toExecute = String.format("SELECT * FROM Approval WHERE DegreeCode='%s' AND Level='%s';",
                    degreeCode, level);
            resultSet = statement.executeQuery(toExecute);

            array = resultSet.getArray("ModuleCode");
            objects = (Object[]) array.getArray();
            List<String> moduleCodes = new ArrayList<>();
            for (int i = 0; i < moduleCodes.size(); i++) {
                moduleCodes.add(objects[i].toString());
            }

            toExecute = String.format("SELECT * FROM Grades WHERE RegNo='%s';", regNo);
            resultSet = statement.executeQuery(toExecute);
            statement.close();

            array = resultSet.getArray("ResitGrade");
            objects = (Object[]) array.getArray();
            List<Double> resitGrades = new ArrayList<>();
            for (int i = 0; i < objects.length; i++) {
                resitGrades.add((double) objects[i]);
            }

            array = resultSet.getArray("InitialGrade");
            objects = (Object[]) array.getArray();
            List<Double> initialGrades = new ArrayList<>();
            for (int i = 0; i < objects.length; i++) {
                initialGrades.add((double) objects[i]);
            }

            array = resultSet.getArray("moduleCode");
            objects = (Object[]) array.getArray();
            List<String> gradeModuleCodes = new ArrayList<>();
            for (int i = 0; i < objects.length; i++) {
                gradeModuleCodes.add(objects[i].toString());
            }

            int count = 0;
            Double meanGrade = 0.0;
            for (int i = 0; i < resitGrades.size(); i++) {
                if (moduleCodes.equals(gradeModuleCodes)) {
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

        } catch (SQLException ex) {
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
    public Boolean progress(int meanGrade, String degreeClass) {

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
    public String degreeResult(int meanGrade, String degreeClass) {

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

        Administrator admin = new Administrator();
        admin.addUser("student", "email@email", "12345");

        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            statement.executeUpdate("INSERT INTO Students VALUES ('Mr', 'A', 'G', '123', 'email@email','tutor');");

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        admin.addDepartment("Computer Science", "COM");
        admin.addDegree("Bsc Comp", "COMU03");
        admin.addModule("Systems Design and Security", "COM1001", "AUT", 20);
        admin.linkModule("COM1001", "COMU03", '1', true);

        Teacher teacher = new Teacher();
        teacher.addGrade(78.1, 123, "COM1001", false);
        teacher.updateGrade(67.1, 123, "COM1001", false);

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            statement.executeUpdate("DELETE FROM Grades WHERE RegNo=123 AND ModuleCode='COM1001';");

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        admin.removeModule("COM1001");
        admin.removeDegree("COMU03");
        admin.removeDepartment("COM");

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();
            statement.executeUpdate("DELETE FROM Students WHERE Email='email@email';");

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        admin.removeUser("email@email");

    }
}

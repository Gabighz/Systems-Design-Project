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
     * @return mean grade.
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

            if(count == 0) {
                statement.close();
                return null;
            }
            else {
                meanGrade /= count;
                toExecute = String.format("UPDATE PeriodOfStudy SET MeanGrade='%s' WHERE RegNo='%s' AND Label='%s';",
                        meanGrade, regNo, label);
                statement.executeUpdate(toExecute);
                statement.close();
                return meanGrade;
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
     * @param regNo The student registration number.
     * @param label The label of the student period of study.
     * @return true if student can progress, false if not.
     */
    public static Boolean progress(int regNo, char label) {
        Statement statement = null;
        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            String toExecute = String.format("SELECT * FROM PeriodOfStudy WHERE RegNo='%s' AND Label='%s';",
                    regNo, label);
            ResultSet resultSet = statement.executeQuery(toExecute);

            List<String> list = new ArrayList<>();
            while (resultSet.next())
                list.add(resultSet.getString("DegreeCode"));
            String degreeCode = list.get(0);

            resultSet.beforeFirst();
            list = new ArrayList<>();
            while (resultSet.next())
                list.add(resultSet.getString("Level"));
            String level = list.get(0);

            toExecute = String.format("SELECT * FROM Approval WHERE DegreeCode='%s' AND Level='%s';",
                    degreeCode, level);
            resultSet = statement.executeQuery(toExecute);

            List<String> moduleCodes = new ArrayList<>();
            while (resultSet.next())
                moduleCodes.add(resultSet.getString("ModuleCode"));

            toExecute = String.format("SELECT * FROM Grades WHERE RegNo='%s';", regNo);
            resultSet = statement.executeQuery(toExecute);

            List<Double> resitGrades = new ArrayList<>();
            while (resultSet.next()) {
                Double resitGrade = resultSet.getDouble("ResitGrade");
                if (resultSet.wasNull())
                    resitGrades.add(null);
                else
                    resitGrades.add(resitGrade);
            }

            resultSet.beforeFirst();
            List<Double> initialGrades = new ArrayList<>();
            while (resultSet.next()) {
                Double initialGrade = resultSet.getDouble("InitialGrade");
                if (resultSet.wasNull())
                    initialGrades.add(null);
                else
                    initialGrades.add(initialGrade);
            }

            resultSet.beforeFirst();
            List<String> gradeModuleCodes = new ArrayList<>();
            while (resultSet.next())
                gradeModuleCodes.add(resultSet.getString("ModuleCode"));

            Double passGrade;
            Integer creditPass;
            if (level.equals("4")) {
                passGrade = 50.0;
                creditPass = 20;
            }
            else {
                passGrade = 40.0;
                creditPass = 15;
            }

            List<Double> failedGrades = new ArrayList<>();
            List<Integer> credits = new ArrayList<>();
            for (int i = 0; i < gradeModuleCodes.size(); i++) {
                if (moduleCodes.contains(gradeModuleCodes.get(i))) {
                    if (resitGrades.get(i) != null) {
                        if (resitGrades.get(i) < passGrade) {
                            failedGrades.add(resitGrades.get(i));

                            toExecute = String.format("SELECT * FROM Modules WHERE ModuleCode='%s';",
                                    gradeModuleCodes.get(i));
                            resultSet = statement.executeQuery(toExecute);

                            List<Integer> creditList = new ArrayList<>();
                            while (resultSet.next())
                                creditList.add(resultSet.getInt("Credits"));
                            credits.add(creditList.get(0));
                        }

                    }
                    else if (initialGrades.get(i) != null) {
                        if (initialGrades.get(i) < passGrade) {
                            failedGrades.add(initialGrades.get(i));

                            toExecute = String.format("SELECT * FROM Modules WHERE ModuleCode='%s';",
                                    gradeModuleCodes.get(i));
                            resultSet = statement.executeQuery(toExecute);

                            List<Integer> creditList = new ArrayList<>();
                            while (resultSet.next())
                                creditList.add(resultSet.getInt("Credits"));
                            credits.add(creditList.get(0));
                        }
                    }
                }
            }
            statement.close();

            Double meanGrade = meanGrade(regNo, label);
            if (meanGrade != null) {
                if (meanGrade < passGrade)
                    return false;
                else if (failedGrades.size() > 1)
                    return false;
                else if (failedGrades.size() == 1) {
                    if (failedGrades.get(0) < (passGrade - 10.0) || credits.get(0) > creditPass)
                        return false;
                }
            }

            return true;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Calculates the overall degree result of a student
     *
     * @param registrationNumber The registration number of the student.
     *
     * @return A string representing the overall degree result
     */
    public static void degreeOverall(int registrationNumber) {

        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            String toExecute = String.format("SELECT MeanGrade FROM PeriodOfStudy WHERE RegNo='%s';", registrationNumber);

            ResultSet gradesOverall = statement.executeQuery(toExecute);

            List<Double> grades = new ArrayList();
            while (gradesOverall.next()){
                grades.add(gradesOverall.getDouble("MeanGrade"));

            }

            double overallResult = (grades.stream().mapToDouble(Double::doubleValue).sum()) / grades.size();

            String toInsert = String.format("INSERT INTO Students (OverallGrade) VALUES ('%.2f')", overallResult);
            statement.executeUpdate(toInsert);

            // OR
            // return degreeResult(overallResult, degreeType) and have a String return type for degreeOverall

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

    /**
     * Returns the student degree result.
     *
     * @param meanGrade The student mean grade for a period of study.
     * @param degreeClass The student degree.
     * @return the student degree result.
     */
    public static String degreeResult(Double meanGrade, String degreeClass) {
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

    public static void main(String[] args) {
        Statement statement;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            Administrator.addUser("student", "email@email", "12345");
            statement.executeUpdate("INSERT INTO Students VALUES ('Mr', 'A', 'G', '123', 'email@email','tutor', NULL);");
            Administrator.addDepartment("Computer Science", "COM");
            Administrator.addDegree("BSc", "Bsc Comp", "COMU03");
            Administrator.addModule("Systems Design and Security", "COM1001", "AUT", 20);
            Administrator.linkModule("COM1001", "COMU03", '1', true);
            statement.executeUpdate("INSERT INTO PeriodOfStudy VALUES ('A', 1, 2, '1', 'COMU03', '123', NULL);");

            Teacher.addGrade(78.1, 123, "COM1001", false);
            Teacher.updateGrade(67.1, 123, "COM1001", false);
            System.out.println(Teacher.meanGrade(123, 'A'));
            System.out.println(Teacher.progress(123, 'A'));

            statement.executeUpdate("DELETE FROM PeriodOfStudy WHERE RegNo=123;");
            statement.executeUpdate("DELETE FROM Grades WHERE RegNo=123 AND ModuleCode='COM1001';");
            Administrator.removeModule("COM1001");
            Administrator.removeDegree("COMU03");
            Administrator.removeDepartment("COM");
            statement.executeUpdate("DELETE FROM Students WHERE Email='email@email';");
            Administrator.removeUser("email@email");

            statement.close();
            System.out.println("Statement closed.");
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

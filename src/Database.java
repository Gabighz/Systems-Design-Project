/**
 * This class creates a database.
 *
 * @author Alexandre Gauthier
 */

import java.sql.*;

public class Database {
    public static void main() {
        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS Students" +
                    "(" +
                    "Title VARCHAR(4) NOT NULL, " +
                    "Forename VARCHAR(255) NOT NULL, " +
                    "Surname VARCHAR(255) NOT NULL, " +
                    "RegNo VARCHAR(255) NOT NULL, " +
                    "Tutor VARCHAR(255), " +
                    "PRIMARY KEY(RegNo)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS Departments" +
                    "(" +
                    "DepartmentCode  VARCHAR(3) NOT NULL, " +
                    "Name  VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY(DepartmentCode)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS Degrees" +
                    "(" +
                    "DegreeCode  VARCHAR(6) NOT NULL, " +
                    "Name  VARCHAR(255) NOT NULL, " +
                    "DepartmentCode VARCHAR(3) NOT NULL, " +
                    "PRIMARY KEY(DegreeCode), " +
                    "FOREIGN KEY(DepartmentCode) REFERENCES Departments(DepartmentCode)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS Modules" +
                    "(" +
                    "ModuleCode  VARCHAR(7) NOT NULL, " +
                    "Name  VARCHAR(255) NOT NULL, " +
                    "CalendarType VARCHAR(13), " +
                    "Credits INT(2), " +
                    "PRIMARY KEY(ModuleCode)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS Approval" +
                    "(" +
                    "ModuleCode  VARCHAR(7) NOT NULL, " +
                    "DegreeCode  VARCHAR(6) NOT NULL, " +
                    "Level  CHAR(1), " +
                    "Core  BOOLEAN, " +
                    "PRIMARY KEY(ModuleCode, DegreeCode, Level), " +
                    "FOREIGN KEY(ModuleCode) REFERENCES Modules(ModuleCode), " +
                    "FOREIGN KEY(DegreeCode) REFERENCES Degrees(DegreeCode)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS Grades" +
                    "(" +
                    "InitialGrade  REAL, " +
                    "ResitGrade  REAL, " +
                    "ModuleCode  VARCHAR(7) NOT NULL, " +
                    "RegNo VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY(ModuleCode,RegNo)" +
                    "FOREIGN KEY(ModuleCode) REFERENCES Modules(ModuleCode)" +
                    "FOREIGN KEY(RegNo) REFERENCES Students(RegNo)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS PeriodOfStudy" +
                    "(" +
                    "Label  VARCHAR(1) NOT NULL, " +
                    "StartDate  REAL NOT NULL, " +
                    "EndDate  REAL NOT NULL, " +
                    "DegreeCode  VARCHAR(6) NOT NULL, " +
                    "RegNo VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY(DegreeCode,RegNo)" +
                    "FOREIGN KEY(DegreeCode) REFERENCES Degrees(DegreeCode)" +
                    "FOREIGN KEY(RegNo) REFERENCES Students(RegNo)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS Level" +
                    "(" +
                    "Name  VARCHAR(255) NOT NULL, " +
                    "LevelCode  VARCHAR(1) NOT NULL, " +
                    "DegreeCode  VARCHAR(6) NOT NULL, " +
                    "PRIMARY KEY(DegreeCode,LevelCode)" +
                    "FOREIGN KEY(DegreeCode) REFERENCES Degrees(DegreeCode)" +
                    ");");

            statement.execute("IF DATABASE_PRINCIPAL_ID('administrator') IS NULL" +
                    " BEGIN" +
                    " CREATE ROLE administrator" +
                    " GRANT INSERT, DROP, DELETE, CREATE TABLE ON * . * TO administrator WITH GRANT OPTION" +
                    " END");

            statement.execute("IF DATABASE_PRINCIPAL_ID('registrar') IS NULL" +
                    " BEGIN" +
                    " CREATE ROLE registrar" +
                    " GRANT ALL ON Students TO registrar" +
                    " END");

            statement.execute("IF DATABASE_PRINCIPAL_ID('teacher') IS NULL" +
                    " BEGIN" +
                    " CREATE ROLE teacher" +
                    " GRANT UPDATE, SELECT ON Students TO teacher" +
                    " END");

            statement.execute("IF DATABASE_PRINCIPAL_ID('student') IS NULL" +
                    " BEGIN" +
                    " CREATE ROLE student" +
                    " GRANT SELECT ON Students TO student" +
                    " END");


            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

/**
 * This class resets the database.
 *
 * @author Alexandre Gauthier
 */

import java.sql.*;

public class Database {
    public static void main (String args[]) {
        String DB = "jdbc:mysql://stusql.dcs.shef.ac.uk/team030?user=team030&password=71142c41";
        Statement statement = null;

        try (Connection con = DriverManager.getConnection(DB)) {
            statement = con.createStatement();

            statement.execute("DROP TABLE IF EXISTS Level;");
            statement.execute("DROP TABLE IF EXISTS PeriodOfStudy;");
            statement.execute("DROP TABLE IF EXISTS Grades;");
            statement.execute("DROP TABLE IF EXISTS Approval;");
            statement.execute("DROP TABLE IF EXISTS Modules;");
            statement.execute("DROP TABLE IF EXISTS PartnerDepartments;");
            statement.execute("DROP TABLE IF EXISTS Degrees;");
            statement.execute("DROP TABLE IF EXISTS Departments;");
            statement.execute("DROP TABLE IF EXISTS Students;");
            statement.execute("DROP TABLE IF EXISTS Accounts;");

            statement.execute("CREATE TABLE IF NOT EXISTS Accounts" +
                    "(" +
                    "Email VARCHAR(255) NOT NULL, " +
                    "Password VARCHAR(255) NOT NULL, " +
                    "Salt VARCHAR(255) NOT NULL, " +
                    "Role VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY(Email)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS Students" +
                    "(" +
                    "Title VARCHAR(5) NOT NULL, " +
                    "Forename VARCHAR(255) NOT NULL, " +
                    "Surname VARCHAR(255) NOT NULL, " +
                    "RegNo INT NOT NULL, " +
                    "Email VARCHAR(255) NOT NULL, " +
                    "Tutor VARCHAR(255), " +
                    "PRIMARY KEY(RegNo), " +
                    "FOREIGN KEY(Email) REFERENCES Accounts(Email) " +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS Departments" +
                    "(" +
                    "Name  VARCHAR(255) NOT NULL, " +
                    "DepartmentCode  VARCHAR(4) NOT NULL, " +
                    "PRIMARY KEY(DepartmentCode)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS Degrees" +
                    "(" +
                    "Name  VARCHAR(255) NOT NULL, " +
                    "DegreeCode  VARCHAR(7) NOT NULL, " +
                    "DepartmentCode VARCHAR(4) NOT NULL, " +
                    "PRIMARY KEY(DegreeCode), " +
                    "FOREIGN KEY(DepartmentCode) REFERENCES Departments(DepartmentCode)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS PartnerDepartments" +
                    "(" +
                    "DegreeCode  VARCHAR(7) NOT NULL, " +
                    "DepartmentCode VARCHAR(4) NOT NULL, " +
                    "PRIMARY KEY(DegreeCode,DepartmentCode), " +
                    "FOREIGN KEY(DegreeCode) REFERENCES Degrees(DegreeCode), " +
                    "FOREIGN KEY(DepartmentCode) REFERENCES Departments(DepartmentCode)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS Modules" +
                    "(" +
                    "Name  VARCHAR(255) NOT NULL, " +
                    "ModuleCode  VARCHAR(8) NOT NULL, " +
                    "CalendarType VARCHAR(13), " +
                    "Credits INT(3), " +
                    "PRIMARY KEY(ModuleCode)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS Approval" +
                    "(" +
                    "ModuleCode  VARCHAR(8) NOT NULL, " +
                    "DegreeCode  VARCHAR(7) NOT NULL, " +
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
                    "ModuleCode  VARCHAR(8) NOT NULL, " +
                    "RegNo INT NOT NULL, " +
                    "PRIMARY KEY(ModuleCode,RegNo), " +
                    "FOREIGN KEY(ModuleCode) REFERENCES Modules(ModuleCode), " +
                    "FOREIGN KEY(RegNo) REFERENCES Students(RegNo)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS PeriodOfStudy" +
                    "(" +
                    "Label  CHAR(1) NOT NULL, " +
                    "StartDate  REAL NOT NULL, " +
                    "EndDate  REAL NOT NULL, " +
                    "Level  CHAR(1), " +
                    "DegreeCode  VARCHAR(7) NOT NULL, " +
                    "RegNo INT NOT NULL, " +
                    "PRIMARY KEY(Label,RegNo), " +
                    "FOREIGN KEY(DegreeCode) REFERENCES Degrees(DegreeCode), " +
                    "FOREIGN KEY(RegNo) REFERENCES Students(RegNo)" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS Level" +
                    "(" +
                    "Name  VARCHAR(255) NOT NULL, " +
                    "LevelCode  CHAR(1) NOT NULL, " +
                    "PRIMARY KEY(LevelCode) " +
                    ");");

            /*statement.executeUpdate("CREATE ROLE administrator;");
            statement.execute("GRANT INSERT, DROP, DELETE, CREATE TABLE ON * . * TO administrator WITH GRANT OPTION;");

            statement.execute("CREATE OR REPLACE ROLE registrar;");
            statement.execute("GRANT ALL ON Students TO registrar;");

            statement.execute("CREATE OR REPLACE ROLE teacher;");
            statement.execute("GRANT UPDATE, SELECT ON Students TO teacher;");

            statement.execute("CREATE OR REPLACE ROLE student;");
            statement.execute("GRANT SELECT ON Students TO student;");*/



            statement.close();
            System.out.println("Statement closed.");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

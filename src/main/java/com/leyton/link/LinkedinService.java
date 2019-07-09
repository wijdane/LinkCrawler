package com.leyton.link;

import com.leyton.link.SQLiteConnection.SqliteConnection;
import com.opencsv.CSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LinkedinService {
    public static final String LINKEDIN_CREATION_PROFIL = "https://www.linkedin.com";
    public static final String GENERATOR_EMAIL_URL = "https://generator.email/";
    // class variable
    final static String lexicon = "abcdefghijklmnopqrstuvwxyz";
    static String currentCompagnyName = null;
    final static java.util.Random rand = new java.util.Random();
    final static Set<String> identifiers = new HashSet<>();

    final static String[] names = new String[]{"Korbyn",
            "Markus", "Ammar", "Karim", "Faycel", "Malek", "Youssef",
            "Chance",
            "Anderson", "Bowie",
            "Deangelo",
            "Harlem", "Forest" +
            "Benedict", "Boden",
            "Camdyn", "Liam",
            "Noah",
            "William",
            "James", "Luna", "Langona", "Syspa", "Rosalina", "Hafida", "Amila", "Samida", "Abdellah", "Mohammed", "Ahmed", "Abdeali", "Sosan", "Samira", "Abdelhak", "Kabira", "Romana", "Jomana"};
    final static String[] lastNames = new String[]{"PECK", "SANFORD", "LEWIS", "WALLER", "EDWARDS", "EVANS", "PHILLIPS", "TURNER", "NELSON", "GONZALEZ", "ADAMS", "YOUNG", "ALLEN", "HALL", "CLARK", "RODRIGUEZ", "Gafner", "Gagan", "Gagas", "Gage", "Gagel", "Gagen", "Gager", "Gagliano", "Gagliardi", "Gagliardo", "Gaglio", "Gaglione", "Gagnard", "Gagne", "Kabira", "Romana", "Jomana"};

    final static String[] titles = new String[]{"Marketing", "Coordinator",
            "Medical Assistant",
            "Web Designer",
            "Dog Trainer",
            "President of Sales",
            "Nursing Assistant",
            "Project Manager",
            "Librarian"};


    public Integer createLinkedInAccount(WebDriver driver) throws InterruptedException, IOException {
        WebElement firstNameElement = null;
        WebElement lastNameElement = null;
        WebElement joinMailElement = null;
        WebElement joinPasswordElement = null;
        WebElement joinButton = null;

        driver.get(LINKEDIN_CREATION_PROFIL);
        LinkedinNumberEmployees.waitingForInfo();
        driver.findElement(By.className("nav__button-tertiary")).click();
        LinkedinNumberEmployees.waitingForInfo();
        firstNameElement = driver.findElement(By.xpath("//*[@id=\"first-name\"]"));
        lastNameElement = driver.findElement(By.xpath("//*[@id=\"last-name\"]"));
        joinMailElement = driver.findElement(By.xpath("//*[@id=\"join-email\"]"));
        joinPasswordElement = driver.findElement(By.xpath("//*[@id=\"join-password\"]"));
        joinButton = driver.findElement(By.xpath("//*[@id=\"uno-reg-join\"]/div/div/div/div[2]/div[1]/div[1]/div[2]/form/fieldset/button"));

        String name = findName();
        firstNameElement.sendKeys(name);
        LinkedinNumberEmployees.waitingForInfo();
        String lastName = findLastName();
        lastNameElement.sendKeys(lastName);
        LinkedinNumberEmployees.waitingForInfo();
        String email = name + "." + lastName + ((int) (Math.random() * 1000)) + "@gmail.com";
        joinMailElement.sendKeys(email);
        LinkedinNumberEmployees.waitingForInfo();
        String password = generatePswd(15).toString();
        joinPasswordElement.sendKeys(password);
        LinkedinNumberEmployees.waitingForInfo();
        joinButton.click();

        // continue
        //driver.get("https://www.linkedin.com/feed/");
        LinkedinNumberEmployees.waitingForInfo();
        if (!driver.getCurrentUrl().contains("login")) {
            // TODO connected
            //-----------------------------Load Names From CSV-----------------------
            Connection connection = SqliteConnection.Connector();
            String sqlQueryInsert = "INSERT INTO linkedCN(compagny_name,nbre_total,nbre_research,nbre_engineers) VALUES (?,?,?,?)";
            String sqlQuerySelect = "SELECT compagny_name,nbre_total,nbre_research,nbre_engineers FROM linkedCN";
            LinkedList<List<String>> name_compagny = new LinkedList<>();
            try {
                try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/datasource/namecompagny.csv"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(",");
                        name_compagny.add(new LinkedList<String>(Arrays.asList(values)));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            try (
                    FileWriter fw = new FileWriter("linkedInData.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter writer = new PrintWriter(bw)) {
                //-----------------------------END Load Names From CSV-----------------------
                name_compagny.forEach(names -> {
                    Iterator<String> nomCompagnies = names.iterator();
                    nomCompagnies.forEachRemaining(compagnyName -> {
                        currentCompagnyName = compagnyName;
                        String infos = null;
                        String nbre_total = null;
                        String nbre_research = null;
                        String nbre_engineers = null;
                        //******************************Searching- Get Number of Employees*****************************
                        infos = currentCompagnyName;
                        System.out.println("******************" + currentCompagnyName + "*************************************");
                        try {
                            LinkedinNumberEmployees.waitingForInfo();
                            nbre_total = LinkedinNumberEmployees.getNumberEmployees(driver, currentCompagnyName);
                            infos += ": NbrTotal " + nbre_total;
                            System.out.println(nbre_total);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (org.openqa.selenium.NoSuchElementException e) {
                            nomCompagnies.hasNext();
                        }
                        //**********PhD Number****************
                        try {
                            LinkedinNumberEmployees.waitingForInfo();
                            nbre_research = LinkedinNumberEmployees.getNumberEmployeesFilter(driver, currentCompagnyName, "phd");
                            infos += ", NbrPhD " + nbre_research;
                            System.out.println("PhD: " + nbre_research);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (org.openqa.selenium.NoSuchElementException ec) {
                            nomCompagnies.hasNext();
                        }
                        //**********Software Number****************
                        try {
                            Thread.sleep(1000);
                            nbre_engineers = LinkedinNumberEmployees.getNumberEmployeesFilter(driver, currentCompagnyName, "software");
                            infos += ", NbrSoftware " + nbre_engineers;
                            writer.write(infos);
                            writer.write("**********************************");
                            System.out.println("Software: " + nbre_engineers);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (NoSuchElementException e) {
                            nomCompagnies.hasNext();
                        }
                        try {
                            PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryInsert);
                            preparedStatement.setString(1, currentCompagnyName);
                            preparedStatement.setString(2, nbre_total);
                            preparedStatement.setString(3, nbre_research);
                            preparedStatement.setString(4, nbre_engineers);
                            preparedStatement.execute();
                            System.out.println("Succès! (coté SQLite)");

                            //dead code
                         /*   if (compagnyName.equals(currentCompagnyName)) {
                                nomCompagnies.remove();
                            }*/
                            System.out.println("End treatement: " + nomCompagnies);
                        } catch (SQLException e) {
                            System.out.println("Fail! (coté SQLite)");
                            e.printStackTrace();
                        }
                    });
                });
            }


            System.out.println("Clear array");
            name_compagny.clear();
            File file = new File("./linkedinData.csv");
            try {
                System.out.println("Creating CSV File...");
                FileWriter outputfile = new FileWriter(file);
                CSVWriter writer = new CSVWriter(outputfile, ',');
                List<String[]> FromDBtoCSVArray = new ArrayList<>();
                ResultSet resultSet = connection.createStatement().executeQuery(sqlQuerySelect);
                while (resultSet.next()) {
                    String[] donnees = new String[]{resultSet.getString(1), resultSet.getString(2)
                            , resultSet.getString(3), resultSet.getString(4)};
                    FromDBtoCSVArray.add(donnees);
                }
                System.out.println("Adding data into CSV File...");
                String[] header = {"Compagny Name", "Nbre Total", "Nbre Researchers", "Nbre Engineers"};
                writer.writeNext(header);
                writer.writeAll(FromDBtoCSVArray);
                System.out.println("Done!");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Fail (SELECT)!");
                e.printStackTrace();
            }
            //TODO end
            System.out.println(email + "\n" + password);
            return 1;
        }
        return 0;
    }


    public String findLastName() {
        return lastNames[(int) (Math.random() * 17)];
    }

    public String findName() {
        return names[(int) (Math.random() * 17)];
    }

    public char[] generatePswd(int len) {
        String charsCaps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String chars = "abcdefghijklmnopqrstuvwxyz";
        String nums = "0123456789";
        String symbols = "!@#$%^&*_=+-/€.?<>)";

        String passSymbols = charsCaps + chars + nums + symbols;
        Random rnd = new Random();

        char[] password = new char[len];
        for (int i = 0; i < len; i++) {
            password[i] = passSymbols.charAt(rnd.nextInt(passSymbols.length()));

        }
        System.out.println(" password : " + password);
        return password;
    }

}

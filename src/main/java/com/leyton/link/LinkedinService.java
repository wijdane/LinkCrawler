package com.leyton.link;

import com.leyton.link.SQLiteConnection.SqliteConnection;
import com.opencsv.CSVWriter;
import com.sun.xml.internal.ws.server.DefaultResourceInjector;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

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


    public Integer createLinkedInAccount(WebDriver driver) throws InterruptedException, IOException, SQLException {
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
        WebDriverWait wait = new WebDriverWait(driver, 100);
        Thread.sleep(4000);

        List<WebElement> elements = driver.findElements(By.cssSelector(".neptune-grid a"));
        if(elements.size()>0){
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".neptune-grid a")));
            driver.findElement(By.cssSelector(".neptune-grid a")).click();
        }
        else{
            driver.get("https://www.linkedin.com/feed/");
        }


        LinkedinNumberEmployees.waitingForInfo();
        if (!driver.getCurrentUrl().contains("login")) {
            // TODO connected
            //-----------------------------Load Names From CSV-----------------------
            Connection connection = SqliteConnection.Connector();
            String sqlQueryInsert = "INSERT INTO linkedCN(compagny_name,nbre_total,nbre_research,nbre_engineers) VALUES (?,?,?,?)";
            String sqlQuerySelect = "SELECT compagny_name,nbre_total,nbre_research,nbre_engineers FROM linkedCN";
            LinkedList<List<String>> name_compagny = new LinkedList<>();
            try (
                    FileWriter fw = new FileWriter("linkedInData.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter writer = new PrintWriter(bw)) {
                //-----------------------------END Load Names From CSV-----------------------
                String sqlQuerySelectEntreprise = "SELECT entreprise FROM dataset";
                ResultSet resultSetEntreprise = connection.createStatement().executeQuery(sqlQuerySelectEntreprise);
                while (resultSetEntreprise.next()) {

                    String companyname = resultSetEntreprise.getString(1);
                    System.out.println("start DB :"+companyname);
                    String infos = null;
                    String nbre_total = null;
                    String nbre_research = null;
                    String nbre_engineers = null;
                    //******************************Searching- Get Number of Employees*****************************
                    infos = companyname;
                    System.out.println("******************" + companyname + "*************************************");
                    try {
                        LinkedinNumberEmployees.waitingForInfo();
                        nbre_total = LinkedinNumberEmployees.getNumberEmployees(driver, companyname);
                        infos += ": NbrTotal " + nbre_total;
                        System.out.println(infos);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //**********PhD Number****************
                    try {
                        LinkedinNumberEmployees.waitingForInfo();
                        nbre_research = LinkedinNumberEmployees.getNumberEmployeesFilter(driver, companyname, "phd");
                        infos += ", NbrPhD " + nbre_research;
                        System.out.println("PhD: " + nbre_research);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //**********Software Number****************
                    try {
                        Thread.sleep(1000);
                        nbre_engineers = LinkedinNumberEmployees.getNumberEmployeesFilter(driver, companyname, "software");
                        infos += ", NbrSoftware " + nbre_engineers;
                        writer.write(infos);
                        writer.write("**********************************");
                        System.out.println("Software: " + nbre_engineers);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryInsert);
                        preparedStatement.setString(1, companyname);
                        preparedStatement.setString(2, nbre_total);
                        preparedStatement.setString(3, nbre_research);
                        preparedStatement.setString(4, nbre_engineers);
                        preparedStatement.execute();
                        System.out.println("Succès! (coté SQLite)");
                        Thread.sleep(277);
                        PreparedStatement st = connection.prepareStatement("DELETE FROM dataset WHERE entreprise = ?");
                        st.setString(1, companyname);
                        st.executeUpdate();
                        System.out.println("Suppression est faite avec succès !");
                    } catch (SQLException e) {
                        System.out.println("Fail! (coté SQLite)");
                        e.printStackTrace();
                    }
                }
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


        public String findLastName () {
            return lastNames[(int) (Math.random() * 17)];
        }

        public String findName () {
            return names[(int) (Math.random() * 17)];
        }

        public char[] generatePswd ( int len){
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

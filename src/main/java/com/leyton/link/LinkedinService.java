package com.leyton.link;

import com.leyton.link.SQLiteConnection.DbUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LinkedinService {
    public static final String LINKEDIN_CREATION_PROFIL = "https://www.linkedin.com";
    // class variable
    public static final String NAV_SEARCH_BAR_INPUT = ".nav-search-bar input";
    public static final String SEARCH_VERTICAL_DROPDOWN = ".search-vertical-filter__filter-item";
    public static final String COMPANIES_SELECTOR = ".search-vertical-filter__filter-item .search-vertical-filter__dropdown-list-item-button--COMPANIES";
    public static final String COMPANY_SELECTOR = ".search-results__list li a";
    public static final String NUMBER_EMPLOYEES_SELECTOR = ".link-without-visited-state span";


    public boolean createLinkedInAccount(WebDriver driver) throws Exception {
        WebElement firstNameElement = null;
        WebElement lastNameElement = null;
        WebElement joinMailElement = null;
        WebElement joinPasswordElement = null;
        WebElement joinButton = null;

        driver.get(LINKEDIN_CREATION_PROFIL);
        randomWait();
        driver.findElement(By.className("nav__button-tertiary")).click();
        randomWait();
        firstNameElement = driver.findElement(By.xpath("//*[@id=\"first-name\"]"));
        lastNameElement = driver.findElement(By.xpath("//*[@id=\"last-name\"]"));
        joinMailElement = driver.findElement(By.xpath("//*[@id=\"join-email\"]"));
        joinPasswordElement = driver.findElement(By.xpath("//*[@id=\"join-password\"]"));
        joinButton = driver.findElement(By.xpath("//*[@id=\"uno-reg-join\"]/div/div/div/div[2]/div[1]/div[1]/div[2]/form/fieldset/button"));

        String name = LinkUtils.findName();
        firstNameElement.sendKeys(name);
        randomWait();
        String lastName = LinkUtils.findLastName();
        lastNameElement.sendKeys(lastName);
        randomWait();
        String email = name + "." + lastName + ((int) (Math.random() * 1000)) + "@gmail.com";
        joinMailElement.sendKeys(email);
        randomWait();
        String password = LinkUtils.generatePswd(15).toString();
        joinPasswordElement.sendKeys(password);
        randomWait();
        joinButton.click();

        // continue
        WebDriverWait wait = new WebDriverWait(driver, 100);
        Thread.sleep(4000);

        List<WebElement> elements = driver.findElements(By.cssSelector(".neptune-grid a"));
        if (elements.size() > 0) {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".neptune-grid a")));
            driver.findElement(By.cssSelector(".neptune-grid a")).click();
        } else {
            driver.get("https://www.linkedin.com/feed/");
        }

        return !driver.getCurrentUrl().contains("login");
    }

    public boolean searchForCompanies(WebDriver driver) throws Exception {

        waitElement(driver,null);
            //-----------------------------Load Names From CSV-----------------------
        ResultSet resultSetEntreprise = DbUtils.getData();
        int notFoundCompaniesNumber = 0;
        while (resultSetEntreprise.next()) {
            try {

                String companyname = resultSetEntreprise.getString(1);
                notFoundCompaniesNumber++;

                String nbre_total = null;
                String nbre_research = null;
                String nbre_ingenieur = null;


                searchForCompany(driver, companyname);

                selectCompany(driver);

                waitElement(driver,COMPANY_SELECTOR);

                List<WebElement> companies = driver.findElements(By.cssSelector(COMPANY_SELECTOR));

                if(companies.size()>0){

                    nbre_total = getCompanyEmployees(driver, companies);
                    System.out.println(companyname+" ==> "+ nbre_total);
                }

                // if there is probleme in search open new session
                if(notFoundCompaniesNumber>10){
                    return false;
                }

                notFoundCompaniesNumber=0;
                DbUtils.saveToDB(companyname, nbre_total, nbre_research, nbre_ingenieur);

                }catch (SQLException e) {
                        System.out.println("Fail SQL !");
                }
                catch (NoSuchElementException | ElementNotInteractableException | ElementNotSelectableException ex){
                 System.out.println("DOM probleme not grave ==> so continue"+ex);
                }
                catch (TimeoutException ex){
                    System.out.println("element not found time out !");
                }
                catch (Exception ex){
                    System.out.println("fatal Exception :"+ex);
                    // return false mean open new session
                    return false;
                }

            }
            // return true mean work is done
            return true;
    }

    private String getCompanyEmployees(WebDriver driver, List<WebElement> companies) throws InterruptedException {
        String nbre_total;
        companies.get(0).click();


        waitElement(driver, NUMBER_EMPLOYEES_SELECTOR);

        nbre_total = driver.findElement(By.cssSelector(NUMBER_EMPLOYEES_SELECTOR)).getText();
        return nbre_total;
    }

    private void searchForCompany(WebDriver driver, String companyname) throws InterruptedException {
        waitElement(driver,NAV_SEARCH_BAR_INPUT);
        WebElement searchInput = driver.findElement(By.cssSelector(NAV_SEARCH_BAR_INPUT));
        searchInput.clear();
        searchInput.sendKeys(companyname);
        searchInput.sendKeys(Keys.ENTER);
    }

    private boolean selectCompany(WebDriver driver)  {
        try {
            waitElement(driver, SEARCH_VERTICAL_DROPDOWN);

            List<WebElement> filterButtons = driver.findElements(By.cssSelector(SEARCH_VERTICAL_DROPDOWN));
            filterButtons.get(filterButtons.size() - 1).click();


            waitElement(driver, COMPANIES_SELECTOR);

            driver.findElement(By.cssSelector(COMPANIES_SELECTOR)).click();
        }catch (Exception ex){
            return false;
        }

        return true;
    }

    private void waitElement(WebDriver driver, String element) throws InterruptedException{
        if(element!=null) {
            WebDriverWait wait = new WebDriverWait(driver, 6);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(element)));
        }
        randomWait();
    }

    private WebElement getSelectedElement(List<WebElement> list,WebDriver driver) {

        for(WebElement selectable : list){
            WebElement webElement = list.get(0);
            if(LinkUtils.isCompany(webElement.getText())){
                return webElement;
            }
        }
        return null;
    }

    public static void randomWait(){
        try {
            Thread.sleep((long) (1000+Math.random()*1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

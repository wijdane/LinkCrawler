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
    public static final String SEARCH_FILTERS_BAR_ALL_FILTERS = ".search-filters-bar__all-filters";
    public static final String SEARCH_TYPEAHEAD_V2_HIT = ".search-typeahead-v2__hit";
    public static final String SEARCH_ADVANCED_FACETS_BUTTON_APPLY = ".search-advanced-facets__button--apply";
    public static final String SEARCH_RESULTS_TOTAL = ".search-results__total";
    public static int MAX_TRY_NUMBER = 10;


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
        WebDriverWait wait = new WebDriverWait(driver, 10);
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
            String companyname = null;
            try {

                companyname = resultSetEntreprise.getString(1);
                notFoundCompaniesNumber++;

                String nbre_total = null;
                String nbre_research = null;
                String nbre_ingenieur = null;


                searchForCompany(driver, companyname);

                selectCompany(driver);

                waitElement(driver,COMPANY_SELECTOR);

                List<WebElement> companies = driver.findElements(By.cssSelector(COMPANY_SELECTOR));

                if(companies.size()==0) {
                    DbUtils.deleteCompany(companyname,DbUtils.getConnector());
                    continue;
                }
                nbre_total = selectCompanyLocation(driver, companies,"France");
                System.out.println(companyname+" ==> "+ nbre_total);

                // if there is probleme in search open new session
                if(notFoundCompaniesNumber> MAX_TRY_NUMBER){
                    MAX_TRY_NUMBER+=5;
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

    private String selectCompanyLocation(WebDriver driver,List<WebElement> companies, String location) throws InterruptedException {

        String  total_of_emplyees= null;
        try {
            companies.get(0).click();
            waitElement(driver, NUMBER_EMPLOYEES_SELECTOR);
            WebElement see_details=driver.findElement(By.cssSelector(NUMBER_EMPLOYEES_SELECTOR));
            System.out.println("website Or link "+see_details.getText());
            if(!see_details.getText().contains("http://")) {
                see_details.click();
                System.out.println("location: ----***--");
                //LOCATION_SELECTORThread.sleep(3000);
                waitElement(driver, SEARCH_FILTERS_BAR_ALL_FILTERS);
                WebElement all_filters = driver.findElement(By.cssSelector(SEARCH_FILTERS_BAR_ALL_FILTERS));
                all_filters.click();
                WebDriverWait wait=new WebDriverWait(driver,30);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[3]/div/div/div[2]/div/div[1]/ul/li[3]/form/div/fieldset/ol/li[1]/div/div/input")));
                WebElement addLocation=driver.findElement(By.xpath("/html/body/div[3]/div/div/div[2]/div/div[1]/ul/li[3]/form/div/fieldset/ol/li[1]/div/div/input"));
                //waitElement(driver, SEARCH_BASIC_TYPEAHEAD_SEARCH_VERTICAL_TYPEAHEAD_EMBER_VIEW_INPUT);
                //WebElement addLocation = driver.findElement(By.cssSelector(SEARCH_BASIC_TYPEAHEAD_SEARCH_VERTICAL_TYPEAHEAD_EMBER_VIEW_INPUT));
                addLocation.sendKeys(location);
                //search-typeahead-v2__hit
                waitElement(driver, SEARCH_TYPEAHEAD_V2_HIT);
                List<WebElement> france = driver.findElements(By.cssSelector(SEARCH_TYPEAHEAD_V2_HIT));
                System.out.println("************* " + france.size());
                france.get(0).click();
                //search-advanced-facets__button--apply
                waitElement(driver, SEARCH_ADVANCED_FACETS_BUTTON_APPLY);
                WebElement apply = driver.findElement(By.cssSelector(SEARCH_ADVANCED_FACETS_BUTTON_APPLY));
                apply.click();
                Thread.sleep(3000);
                //search-results__total
                if (driver.findElements(By.className("t-20")).size() > 0) {
                    System.out.println("");
                    List<WebElement> not_found = driver.findElements(By.className("t-20"));
                    System.out.println(not_found.get(1).getText());
                } else {
                    waitElement(driver, SEARCH_RESULTS_TOTAL);
                    WebElement numberTotal = driver.findElement(By.cssSelector(SEARCH_RESULTS_TOTAL));
                    total_of_emplyees = numberTotal.getText();
                    System.out.println(numberTotal.getText());
                }
            }

        }catch (Exception ex){
            // close model
        }

        return total_of_emplyees;
    }

    private void searchForCompany(WebDriver driver, String companyname) throws InterruptedException {
        waitElement(driver,NAV_SEARCH_BAR_INPUT);
        WebElement searchInput = driver.findElement(By.cssSelector(NAV_SEARCH_BAR_INPUT));
        searchInput.clear();
        searchInput.sendKeys(companyname);
        searchInput.sendKeys(Keys.ENTER);
    }

    private boolean selectCompany(WebDriver driver)  {
        List<WebElement> filterButtons = null;
        try {
            waitElement(driver, SEARCH_VERTICAL_DROPDOWN);
            filterButtons = driver.findElements(By.cssSelector(SEARCH_VERTICAL_DROPDOWN));
            filterButtons.get(filterButtons.size() - 1).click();


            waitElement(driver, COMPANIES_SELECTOR);

            driver.findElement(By.cssSelector(COMPANIES_SELECTOR)).click();
        }catch (Exception ex){
            // close model
            if(filterButtons!=null && filterButtons.size()>0){
                filterButtons.get(filterButtons.size() - 1).click();
            }
            return false;
        }

        return true;
    }


    private void waitElement(WebDriver driver, String element) throws InterruptedException{
        if(element!=null) {
            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(element)));
        }
        randomWait();
    }

    public static void randomWait(){
        try {
            Thread.sleep((long) (1000+Math.random()*1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

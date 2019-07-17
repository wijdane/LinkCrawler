package com.leyton.link;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.Normalizer;
import java.util.List;

public class LinkedinNumberEmployees {

    public static final int DISTANCE_TOLERANCE = 5;

    public static String getNumberEmployees(WebDriver driver, String compagnyName) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 3);

        Thread.sleep(500);
        deleteFiltres(driver);
        waitingForInfo();

        String numberEmployees = null;
        numberEmployees = getNumberTotal(driver, wait, numberEmployees);
        return numberEmployees;

    }

    private static void deleteFiltres(WebDriver driver) throws InterruptedException {

        try {
            if (driver.findElement(By.cssSelector(".search-filters-bar__clear-filters")) != null) {
                // wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-control-name='all_filters_clear']")));
                driver.findElement(By.cssSelector(".search-filters-bar__clear-filters")).click();
                WebElement applyFilterElement = driver.findElement(By.className("search-advanced-facets__button--apply"));
                Thread.sleep(100);
                applyFilterElement.click();
            }
        } catch (Exception e) {
        }
        try {
            if (driver.findElement(By.cssSelector(".search-advanced-facets__button--clear")) != null) {
                // wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-control-name='all_filters_clear']")));
                driver.findElement(By.cssSelector(".search-advanced-facets__button--clear")).click();
                WebElement applyFilterElement = driver.findElement(By.className("search-advanced-facets__button--apply"));
                Thread.sleep(100);
                applyFilterElement.click();
            }
        } catch (Exception e) {
        }
    }

    public static String getNumberEmployeesFilter(WebDriver driver, String compagnyName, String filtername) throws InterruptedException {
        String numberEmployeesPhD = null;
        try {
            WebDriverWait wait = new WebDriverWait(driver, 3);
            waitingForInfo();
            deleteFiltres(driver);
            waitingForInfo();
            driver.findElement(By.xpath("//*[@data-control-name='all_filters']")).click();
            waitingForInfo();
            WebElement titleElement = driver.findElement(By.xpath("//*[@id=\"search-advanced-title\"]"));
            titleElement.clear();
            titleElement.sendKeys(filtername);
            waitingForInfo();
            driver.findElement(By.xpath("//*[@data-control-name='all_filters_apply']")).click();
            numberEmployeesPhD = getStringFilter(driver, compagnyName, wait, numberEmployeesPhD);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return numberEmployeesPhD;
    }

    private static String getStringFilter(WebDriver driver, String compagnyName, WebDriverWait wait, String numberEmployees) throws InterruptedException {
        try {
            Thread.sleep(100);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-filters-bar__all-filters")));
            WebElement formElement = driver.findElement(By.className("search-filters-bar__all-filters"));
            formElement.click();
            List<WebElement> compagnyFilterCheckbox = driver.findElements(By.className("search-s-facet__list"));
            List<WebElement> currentCompagnyList = compagnyFilterCheckbox.get(3).findElements(By.className("search-facet__value"));
            for (int i = 0; i < currentCompagnyList.size(); i++) {
                if (containsIgnoreCase(compagnyName, removeDiacriticalMarks(currentCompagnyList.get(i).getText()))) {
                    Thread.sleep(100);
                    System.out.println(currentCompagnyList.get(i).getText());
                    currentCompagnyList.get(i).click();
                    Thread.sleep(100);
                } else {
                    System.out.println("not found");
                }
            }

            driver.findElement(By.xpath("//*[@data-control-name='all_filters_apply']")).click();
            waitingForInfo();

            numberEmployees = getNumberTotal(driver, wait, numberEmployees);
        } catch (NoSuchElementException e) {
            WebElement introuvable = driver.findElement(By.className("search-no-results__container"));
            WebElement nosearch = introuvable.findElement(By.className("t-20"));
            if (!nosearch.isDisplayed()) {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("search-results__total")));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-filters-bar__all-filters")));
            } else {
                System.out.println(nosearch.getText());
            }
        } catch (ElementClickInterceptedException e) {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("search-filters-bar__all-filters")));
        }
        return numberEmployees;
    }

    public static boolean containsIgnoreCase(String s1, String s2) {
        return s1.toLowerCase().contains(s2.toLowerCase()) || s2.toLowerCase().contains(s1.toLowerCase()) ;
    }

    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static void waitingForInfo() throws InterruptedException {
        Thread.sleep((long) (Math.random() * 2000 + 1000));
    }

    private static String getNumberTotal(WebDriver driver, WebDriverWait wait, String numberEmployees) throws InterruptedException {
        Thread.sleep(500);
        try {

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-results__total")));
            Thread.sleep(500);
            numberEmployees = driver.findElement(By.className("search-results__total")).getText();
        }
        catch (Exception ex){
            return null;
        }

        return numberEmployees;
    }
}


package com.leyton.link;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.Normalizer;
import java.util.List;

public class LinkedinNumberEmployees {

    public static String getNumberEmployees(WebDriver driver, String compagnyName) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 50);
        driver.get("https://www.linkedin.com/feed/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"ember36\"]/li-icon/svg/g/g[3]/g[1]/g[1]/path[2]")));
        driver.findElement(By.xpath("//*[@id=\"ember36\"]/li-icon/svg/g/g[3]/g[1]/g[1]/path[2]")).click();
        WebElement searchInput = driver.findElement(By.xpath("//*[@id=\"ember32\"]/input"));
        searchInput.sendKeys(compagnyName);
        searchInput.submit();
        waitingForInfo();
        WebElement peopleLink = driver.findElement(By.xpath("//*[@id=\"ember1802\"]"));
        peopleLink.click();
        waitingForInfo();
        String numberEmployees = null;
        // driver.get("https://www.linkedin.com/search/results/people/?keywords=" + compagnyName + "&origin=SWITCH_SEARCH_VERTICAL");
        numberEmployees = getStringFilter(driver, compagnyName, wait, numberEmployees);
        return numberEmployees;
    }

    public static String getNumberEmployeesFilter(WebDriver driver, String compagnyName, String filtername) throws InterruptedException {
        String numberEmployeesPhD = null;
        try {
            WebDriverWait wait = new WebDriverWait(driver, 50);
            driver.get("https://www.linkedin.com/");
            WebElement searchInput = driver.findElement(By.xpath("//*[@id=\"ember33\"]/input"));
            searchInput.sendKeys(compagnyName);
            waitingForInfo();
            searchInput.sendKeys(Keys.ENTER);
            waitingForInfo();
            driver.findElement(By.xpath("//*[@data-control-name='all_filters']")).click();
            waitingForInfo();
            driver.findElement(By.xpath("//*[@id=\"search-advanced-title\"]")).sendKeys(filtername);
            waitingForInfo();
            driver.findElement(By.xpath("//*[@data-control-name='all_filters_apply']")).click();
            numberEmployeesPhD = getStringFilter(driver, compagnyName, wait, numberEmployeesPhD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numberEmployeesPhD;
    }

    private static String getStringFilter(WebDriver driver, String compagnyName, WebDriverWait wait, String numberEmployees) throws InterruptedException {
        try {
            waitingForInfo();
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
                } else {
                    System.out.println("not found");
                }
            }
            WebElement applyFilterElement = driver.findElement(By.className("search-advanced-facets__button--apply"));
            Thread.sleep(100);
            applyFilterElement.click();
            System.out.println(driver.findElement(By.className("search-results__total")).isDisplayed());
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-results__total")));
            Thread.sleep(1500);
            WebElement nombre_employee = driver.findElement(By.className("search-results__total"));
            numberEmployees = nombre_employee.getText();
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
        return s1.toLowerCase().contains(s2.toLowerCase());
    }

    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static void waitingForInfo() throws InterruptedException {
        Thread.sleep((long) (Math.random() * 2000 + 1000));
    }

}


package com.leyton.link;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.Normalizer;
import java.util.List;

public class LinkedinNumberEmployees {

    public static String getNumberEmployees(WebDriver driver, String compagnyName) throws InterruptedException {
        WebDriverWait wait=new WebDriverWait(driver,50);
        String numberEmployees=null;
            driver.get("https://www.linkedin.com/search/results/people/?keywords=" + compagnyName + "&origin=SWITCH_SEARCH_VERTICAL");
        numberEmployees = getStringFilter(driver, compagnyName, wait, numberEmployees);
        return numberEmployees;
    }

    public static String getNumberEmployeesFilter(WebDriver driver, String compagnyName, String filtername) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 50);
        String numberEmployeesPhD = null;
        driver.get(" https://www.linkedin.com/search/results/people/?keywords=+" + compagnyName + "&origin=FACETED_SEARCH&title="+filtername);
        numberEmployeesPhD = getStringFilter(driver, compagnyName, wait, numberEmployeesPhD);
        return numberEmployeesPhD;
    }

    private static String getStringFilter(WebDriver driver, String compagnyName, WebDriverWait wait, String numberEmployees) throws InterruptedException {
        try {
            Thread.sleep(200);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-filters-bar__all-filters")));
            WebElement formElement = driver.findElement(By.className("search-filters-bar__all-filters"));
            formElement.click();
            List<WebElement> compagnyFilterCheckbox = driver.findElements(By.className("search-s-facet__list"));
            List<WebElement> currentCompagnyList=compagnyFilterCheckbox.get(3).findElements(By.className("search-facet__value"));
            for(int i=0;i<currentCompagnyList.size();i++)
            {
                if(containsIgnoreCase(compagnyName,removeDiacriticalMarks(currentCompagnyList.get(i).getText())))
                {
                    Thread.sleep(100);
                    System.out.println(currentCompagnyList.get(i).getText());
                    currentCompagnyList.get(i).click();
                }else
                {
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
            numberEmployees=nombre_employee.getText();
        }catch (NoSuchElementException e) {
            WebElement introuvable=driver.findElement(By.className("search-no-results__container"));
            WebElement nosearch=introuvable.findElement(By.className("t-20"));
            if(!nosearch.isDisplayed())
            {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("search-results__total")));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-filters-bar__all-filters")));
            }else
            {
                System.out.println(nosearch.getText());
            }
        }
        catch (ElementClickInterceptedException e) {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("search-filters-bar__all-filters")));
        }
        return numberEmployees;
    }

    public static boolean containsIgnoreCase(String s1,String s2)
    {
        return s1.toLowerCase().contains(s2.toLowerCase());
    }
    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}


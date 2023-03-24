package org.search.engine.google;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.search.engine.BaseSearchResults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a page object that contains the specific locators and behaviours of the
 * Google search engine results page
 */
public class GoogleSearchResults extends GoogleSearchHome implements BaseSearchResults {
    /*
     * Notes for Locators:
     * 1. If we want to protect against other nodes appearing between root and end nodes we can replace "/" with "//"
     * 2. The results that have the same root site are nested and the children have different locators
     */
    private final String searchButtonLocator="//button[@aria-label='Search']";
    private final String searchFieldLocator="//div/input[@name='q']";
    private final String partialHomePage = " - Google Search";
    private final String nextPageButtonLocator = "pnnext";
    private final String resultsLocator="//*[@id=\"rso\"]//div/div/div[@data-sokoban-container]";
    private final String resultHeaderLocator =".//a/div/div/span";
    private final String resultUrlLocator=".//div/a";
    private final String resultTitleLocator=".//a/h3";
    private final String resultDescriptionLocator=".//div[@data-sokoban-feature='nke7rc']/div";
    private final String nestedResultUrlLocator=".//ul/li/div/div/div/div[1]/div/div/div[1]/div/cite";



    public GoogleSearchResults(){
        super();
        setExpectedHomePage(partialHomePage);
        setSearchField(searchFieldLocator);
        setSearchButton(searchButtonLocator);
    }

//    /**
//     * This is an explicit constructor that could be used for any exceptions to the standard use.
//     * I commented it out because I don't use it, but it could be useful in extended functionalities.
//     * @param homePage Page title
//     * @param searchField search field locator
//     * @param searchButton search button locator
//     */
//    public GoogleSearchResults(String homePage,String searchField, String searchButton){
//        super(homePage,searchField,searchButton);
//    }

    @Override
    public WebElement getSearchButton(WebDriver driver){
        return getElementWithXpath(driver,searchButtonLocator);
    }

    @Override
    public WebElement getSearchField(WebDriver driver){
        return getElementWithXpath(driver,searchFieldLocator);
    }

    @Override
    public Map<String, Map<String, String>> getResults(WebDriver driver) {
        Map<String,Map<String,String>> returnResults = new HashMap<>();
        return parseResults(driver,returnResults);
    }

    @Override
    public List<WebElement> getAllResults(WebDriver driver) {
        return driver.findElements(By.xpath(resultsLocator));

    }

    @Override
    public String getDescription(WebElement webElement){
        return webElement.findElement(By.xpath(resultDescriptionLocator)).getText();
    }

    @Override
    public String getTitle(WebElement webElement) {
        return webElement.findElement(By.xpath(resultTitleLocator)).getText();
    }

    @Override
    public String getHeader(WebElement webElement){
        try{
            return webElement.findElement(By.xpath(resultHeaderLocator)).getText();
        } catch (NoSuchElementException e){
            System.out.println("This result is nested and shares a Header with previous entry!");
            return "";
        }
    }

    @Override
    public String getUrl(WebElement webElement){
        try{
            return webElement.findElement(By.xpath(resultUrlLocator)).getAttribute("href");
        }catch (NoSuchElementException e){
            System.out.println("This result is nested and has a different Url locator!");
            return webElement.findElement(By.xpath(nestedResultUrlLocator)).getText();
        }
    }

    @Override
    public void nextPage(WebDriver driver) {
        driver.findElement(By.id(nextPageButtonLocator)).click();
    }
}

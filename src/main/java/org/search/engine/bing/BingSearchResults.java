package org.search.engine.bing;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.search.engine.BaseSearchResults;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a page object that contains the specific locators and behaviours of the
 * Bing search engine results page
 */
public class BingSearchResults extends BingSearchHome implements BaseSearchResults {
    /*
     * Notes for Locators:
     * 1. If we want to protect against other nodes appearing between root and end nodes we can replace "/" with "//"
     * 2. The top result has a different structure, so we need to handle it separately
     */

    private final String searchButtonLocator="sb_form_go";
    private final String searchFieldLocator="sb_form_q";
    private final String partialHomePage = " - Search";
    private final String nextPageButtonLocator = "//*[@id=\"b_results\"]//a[@title='Next page']";
    private final String resultsLocator="//*[@id=\"b_results\"]/li[@class='b_algo']";
    private final String resultHeaderLocator =".//h2/a";
    private final String resultUrlLocator=".//div[@class='b_attribution']/cite";
    private final String resultTitleLocator=".//h2/a";
    private final String resultDescriptionLocator=".//p";
    private final String topResultLocator="//*[@id=\"b_results\"]/li/div[@class='b_algo_group']";
    private final String topResultDescription=".//div[1]/div/div/div/p[@class='b_paractl']";

    public BingSearchResults(){
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
//    public BingSearchResults(String homePage,String searchField, String searchButton){
//        super(homePage,searchField,searchButton);
//    }

    @Override
    public Map<String, Map<String, String>> getResults(WebDriver driver) {
        Map<String,Map<String,String>> returnResults = new HashMap<>();
        return parseResults(driver,returnResults);
    }

    @Override
    public List<WebElement> getAllResults(WebDriver driver){
        List<WebElement> results = driver.findElements(By.xpath(topResultLocator));
        results.addAll(driver.findElements(By.xpath(resultsLocator)));
        return results;
    }

    @Override
    public String getDescription(WebElement webElement) {
        try {
            return webElement.findElement(By.xpath(resultDescriptionLocator)).getText();
        }catch (NoSuchElementException e){
            System.out.println("This is top result and has a different Description locator!");
            return webElement.findElement(By.xpath(topResultDescription)).getText();
        }    }

    @Override
    public String getTitle(WebElement webElement) {
        return webElement.findElement(By.xpath(resultTitleLocator)).getText();
    }

    @Override
    public String getHeader(WebElement webElement) {
        return webElement.findElement(By.xpath(resultHeaderLocator)).getText();
    }

    @Override
    public String getUrl(WebElement webElement) {
        return webElement.findElement(By.xpath(resultUrlLocator)).getText();
    }

    @Override
    public void nextPage(WebDriver driver) {
        getElementWithXpath(driver,nextPageButtonLocator).click();
    }
}

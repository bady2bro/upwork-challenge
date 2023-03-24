package org.search.engine;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface containing basic and common actions for the results page of a search engine
 */
public interface BaseSearchResults extends BaseSearch{
    /**
     * Handles the return of the desired results, after parsing, in the given structured object
     *
     * @param driver the driver to interact with
     * @return the structure storing the desired results <code>Map</code>
     */
    Map<String, Map<String, String>> getResults(WebDriver driver);

    /**
     * Gets all raw results as list of web elements.
     * It handles any exceptions with the Locators.
     * @param driver the driver to interact with
     * @return all the raw results as <code>List</code> of <code>WebElements</code>
     */
    List<WebElement> getAllResults(WebDriver driver);

    /**
     * Handles the extraction of the Description text out of given element
     * Should handle any exceptions in the Locators
     * @param webElement the element to query
     * @return the text in the description
     */
    String getDescription(WebElement webElement);
    /**
     * Handles the extraction of the Title text out of given element
     * Should handle any exceptions in the Locators
     * @param webElement the element to query
     * @return the text in the title
     */
    String getTitle(WebElement webElement);
    /**
     * Handles the extraction of the Header text out of given element
     * Should handle any exceptions in the Locators
     * @param webElement the element to query
     * @return the text in the header
     */
    String getHeader(WebElement webElement);
    /**
     * Handles the extraction of the URL text out of given element
     * Should handle any exceptions in the Locators
     * @param webElement the element to query
     * @return the text in the url
     */
    String getUrl(WebElement webElement);

    /**
     * Handles the navigation to the next page
     * Should handle any exceptions in the Locators
     * @param driver the element to query
     */
    void nextPage(WebDriver driver);

    /**
     * This is the common requirement for parsing the raw results:
     * 1. Store top 10 results
     * 2. Store results in a structured variable
     * 3. Makes sure the keys match between engines
     * @param driver the browser driver use in case we need next result page
     * @param returnResults the parameter that stores the return structure
     * @return returns <code>returnResults</code> in Map<String,Map<String,String>> structure
     */
    default Map<String,Map<String,String>> parseResults(WebDriver driver, Map<String, Map<String, String>> returnResults){
        //The for is for parsing each raw web element and store it in returnResults
        for (WebElement result: getAllResults(driver)) {
            System.out.println("Parsing element: "+getTitle(result));
            Map<String, String> elementMap = new HashMap<>();
            elementMap.put("header", getHeader(result));
            elementMap.put("url", getUrl(result));
            elementMap.put("title", getTitle(result));
            elementMap.put("description", getDescription(result));
            //the replaceAll is for making the keys(url) match between GOOGLE and BING
            //BING always removes trailing "/" while GOOGLE is inconsistent
            returnResults.put(getUrl(result).replaceAll("/$",""),elementMap);
            //if we have 10 stored results return "returnResults"
            if (returnResults.size()==10) {
                return returnResults;
            }
        }
        //Separated the recursion condition and call for 2 reasons:
            //1. clearer to read and implement
            //2. because I didn't want to risk moving to next page after every iteration
        if (returnResults.size()<10){
            nextPage(driver);
            parseResults(driver,returnResults);
        }
        return returnResults;
    }
}

package org.search.engine;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

public abstract class BaseSearchPage implements BaseSearch {
    /**
     * The absolutely common and minimal functionality fields
     */
    private String expectedHomePage;
    private String searchField;
    private String searchButton;
    private String cookieConsent;

    public BaseSearchPage(String expectedHomePage, String searchFieldLocator, String searchButtonLocator, String cookieConsent) {
        setExpectedHomePage(expectedHomePage);
        setSearchField(searchFieldLocator);
        setSearchButton(searchButtonLocator);
        setCookieConsent(cookieConsent);
    }

//    /**
//     * The default constructor.
//     * commented because not used, but might be used for expanded functionality
//     */
//    public BaseSearchPage() {}

//    /**
//     * This method gets the actual home page title
//     * NOTE:
//     * Haven't used it, but could be used to protect against wrong page being loaded
//     * @param driver to interact with
//     * @return actual page title
//     */
//    public String getHomePage(WebDriver driver) {
//        return driver.getTitle();
//    }

//    /**
//     * Get expected home page title for the search engine home page
//     * NOTE:
//     * Haven't used it, but could be used to protect against wrong page being loaded
//     * @return expected page title for engine homepage
//     */
//    public String getExpectedHomePage() {
//        return expectedHomePage;
//    }

//    /**
//     * Get expected home page title for the search results page
//     * NOTE:
//     * Haven't used it, but could be used to protect against wrong page being loaded
//     * @param keyword the keyword we searched for
//     * @return expected page title for engine results page
//     */
//    public String getExpectedHomePage(String keyword){
//        return keyword+getExpectedHomePage();
//    }

    public void setExpectedHomePage(String expectedHomePage) {
        this.expectedHomePage = expectedHomePage;
    }

    private void setCookieConsent(String cookieConsent) {
        this.cookieConsent=cookieConsent;
    }

    public WebElement getSearchField(WebDriver driver) {
        return driver.findElement(By.name(searchField));
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public WebElement getSearchButton(WebDriver driver) {
        return driver.findElement(By.name(searchButton));
    }

    public String getSearchButton() {
        return searchButton;
    }

    public void setSearchButton(String searchButton) {
        this.searchButton = searchButton;
    }

    /**
     * A more generic method for the times when we don't have id or name locators
     *
     * @param driver the driver to interact with
     * @param xpath the locator to query
     * @return found WebElement
     */
    public WebElement getElementWithXpath(WebDriver driver, String xpath) {
        return driver.findElement(By.xpath(xpath));
    }

    @Override
    public void search(WebDriver driver, String keywords) {
        System.out.println("Starting search for: " + keywords);
        System.out.println("\tTyping keywords in search field");
        //Storing the web element because we use it twice and don't want to search again
        WebElement field = getSearchField(driver);
        //making sure there are no default values in search field
        field.clear();
        field.sendKeys(keywords);
        System.out.println("\tClicking search button");
        //This block is a workaround for a Firefox bug that doesn't scroll to element for simple click
        if(driver instanceof FirefoxDriver){
            Actions actions=new Actions(driver);
            actions.sendKeys(Keys.ENTER).perform();
        }else {
            //Tried for a long time to do a workaround that still used the click function but gave up in the end.
            //it works for Chrome browser but not for Firefox
            getSearchButton(driver).click();
        }

    }

    /*
     * NOTE:
     * The commented part is an example of conditional wait, an alternative to bigger implicit wait.
     * It should wait until element is clickable.
     */
    @Override
    public void resolveCookies(WebDriver driver){
//        FluentWait<WebDriver> wait = new FluentWait<>(driver);
//        wait.until(ExpectedConditions.elementToBeClickable(By.id(cookieConsent))).click();
        driver.findElement(By.id(cookieConsent)).click();
    }
}

package org.search.engine.bing;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.search.engine.BaseSearchPage;

/**
 * This is a page object that contains the specific locators and behaviours of the
 * Bing search engine home page
 */
public class BingSearchHome extends BaseSearchPage {
    public BingSearchHome(){
        super("Bing","sb_form_q","search_icon","bnp_btn_reject");
    }

//    /**
//     * This is an explicit constructor that could be used for any exceptions to the standard use.
//     * I commented it out because I don't use it, but it could be useful in extended functionalities.
//     * @param homePage Page title
//     * @param searchField search field locator
//     * @param searchButton search button locator
//     */
//    public BingSearchHome(String homePage,String searchField, String searchButton){
//        super(homePage,searchField,searchButton);
//    }


    @Override
    public WebElement getSearchButton(WebDriver driver) {
        return driver.findElement(By.id(getSearchButton()));
    }

    @Override
    public WebElement getSearchField(WebDriver driver){
        return driver.findElement(By.id(getSearchField()));
    }
}

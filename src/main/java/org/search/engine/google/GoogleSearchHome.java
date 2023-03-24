package org.search.engine.google;

import org.search.engine.BaseSearchPage;

/**
 * This is a page object that contains the specific locators and behaviours of the
 * Google search engine home page
 */
public class GoogleSearchHome extends BaseSearchPage {

    /**
     * Default constructor
     */
    public GoogleSearchHome(){
        super("Google","q","btnK","W0wltc");
    }

//    /**
//     * This is an explicit constructor that could be used for any exceptions to the standard use.
//     * I commented it out because I don't use it, but it could be useful in extended functionalities.
//     * @param homePage Page title
//     * @param searchField search field locator
//     * @param searchButton search button locator
//     */
//    public GoogleSearchHome(String homePage,String searchField, String searchButton){
//        super(homePage,searchField,searchButton);
//    }
}

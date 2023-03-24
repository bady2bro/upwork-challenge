package org.search.engine;

import org.openqa.selenium.WebDriver;

/**
 * Interface containing the basic expected functionality for a search engine home page
 */
public interface BaseSearch {
    /**
     * Handles the search steps of the search engine
     *
     * @param driver the driver to interact with
     * @param keywords the keyword to search for
     */
    void search(WebDriver driver, String keywords);
    /**
     * This method tries to resolve the cookies prompt
     *
     * @param driver the driver to monitor and interact with
     */
    void resolveCookies(WebDriver driver);
}

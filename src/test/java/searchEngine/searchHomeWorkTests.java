package searchEngine;

import org.openqa.selenium.WebDriver;
import org.search.engine.BaseSearchPage;
import org.search.engine.BaseSearchResults;
import org.search.engine.bing.BingSearchHome;
import org.search.engine.bing.BingSearchResults;
import org.search.engine.google.GoogleSearchHome;
import org.search.engine.google.GoogleSearchResults;
import static browser.utils.DriverFactory.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.*;

/**
 * This class contains the test given as homework.
 * NOTE:
 * 1. Tested for keywords but results will vary depending on the localization:
 *  a. "boomerang" - passed
 *  b. "cat" - failed because one of the BING results is "Muzeum Sztuki i Techniki Japońskiej Manggha"
 *      that is missing "cat" from elements
 *  c. "chloroplast" - passed
 *  d. They still fail sometimes, so please execute at least twice until you see expected behavior
 *  e. The number of common links varies from execution to execution
 *      - For Firefox "chloroplast" has one common element
 *      - For Chrome "chloroplast" has two common elements
 *---------------------------------------------------------------
 * 2. Implemented implicit wait 10 seconds because it is fast fix for small test. Would implement conditional wait usually.
 * Reasons:
 *  a. when switching from google to bing and from chrome to firefox it took longer to load the page so I added and raised wait
 *  b. conditional wait is best, but it makes code more complicated and prone to duplication of wait code or major change to classes
 *---------------------------------------------------------------
 * 3. The test was implemented with driver executables in "resources" package
 * BUT it can be executed with external chromedriver.exe and firefoxdriver.exe:
 *  a. Supported by my Chrome(111.0.5563.111) and Firefox(111.0.1) versions.
 *  b. Please download and extract proper chromedriver for you at desired location:
 *      ex. C:\ChromeDriver\chromedriver_win32\chromedriver.exe
 *      AND
 *      change path in property webdriver.chrome.driver in DriverFactory class
 *  c. Please download and extract proper firefoxdriver for you at location:
 *      C:\FirefoxDriver\geckodriver.exe
 *      AND
 *      change path in property webdriver.gecko.driver in DriverFactory class
 *---------------------------------------------------------------
 * 4. Firefox has a bug that impacts the Google search button clickability: https://bugzilla.mozilla.org/show_bug.cgi?id=1374283
 *  a. So I found a workaround with sendKey(Keys.ENTER) that works for both browsers;
 *  b. but implemented the workaround to trigger only for Firefox;
 *  c. I believe there was another workaround by refreshing the page, but it is bad code
 *
 */
public class searchHomeWorkTests {
    private String searchEngine;
    private WebDriver driver;
    private BaseSearchPage searchHomePage;
    private BaseSearchResults searchResultsPage;

    //This is used in a commented test case
    private  Map<String,Map<String, Map<String,String>>> resultsBySearchEngine = new HashMap<>();

    /**
     * To switch between browsers activate the corresponding line of code
     *
     * for multiple scenarios we could pass parameters to this method @Parameters({browser}) and change the signature
     * BUT it would mean adding a testng.xml file and would complicate the structure more
     */
    @BeforeTest
    public void initialize(){
//        String browser="Chrome";
        String browser="Firefox";
        System.out.println("0.1. Instantiate driver based on browser under test");
        driver = getInstance(browser);
        //Modified implicit wait because when moving to BING there was lag and failure.
        //Ideally I would implement a conditional wait until the element is clickable.
        //See BaseSearchPage resolveCookies method for example.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    /**
     * Included all steps of the homework in this test, but it can be split in different ways
     *
     * If you want to ADD new keyword, add the value to the DataProvider method (keywords)
     *
     * If you want to run ONLY for a specific keyword you have to:
     *  1. Remove the "(dataProvider= Keywords)" from @Test annotation
     *  2. Remove the parameter "String keyword"
     *  3. Activate/Add "keyword" variable with wanted value
     */
    @Test(dataProvider = "Keywords")
    public void validateKeywordPresence(String keyword){
//        String keyword="chloroplast";
//        String keyword="cat";
//        String keyword="boomerang";
        Map<String, Map<String, String>> firstEngine;
        Map<String, Map<String, String>> secondEngine;
        System.out.println("1. Instantiating first search engine under test");
        setSearchEngine("Google");
        System.out.println("2. Opening browser on search engine: "+searchEngine);
        driver.navigate().to(searchEngine);
        System.out.println("3. Resolving cookies prompt");
        searchHomePage.resolveCookies(driver);
        System.out.println("4. Search for the keyword:"+keyword);
        searchHomePage.search(driver,keyword);
        System.out.println("5. Parse results");
        firstEngine=searchResultsPage.getResults(driver);
        resultsBySearchEngine.put(searchEngine,firstEngine);
        System.out.println("6. Validate all results contain keyword");
        for (String result: firstEngine.keySet()) {
//            I prefer using testng or a different test runner to the basic asserts
//            assert searchResultContains(keyword, firstEngine.get(result)) :"This result doesn't contain the keyword: "+result;
            Assert.assertTrue(searchResultContains(keyword, firstEngine.get(result)),
                    "This result doesn't contain the keyword: "+result);
        }
        System.out.println("7. Instantiating second search engine under test");
        setSearchEngine("bing");
        System.out.println("8. Opening browser on search engine: "+searchEngine);
        driver.navigate().to(searchEngine);
        System.out.println("9. Resolving cookies prompt");
        searchHomePage.resolveCookies(driver);
        System.out.println("10. Search for the keyword:"+keyword);
        searchHomePage.search(driver,keyword);
        System.out.println("11. Parse results");
        secondEngine = searchResultsPage.getResults(driver);
        resultsBySearchEngine.put(searchEngine,secondEngine);
        System.out.println("12. Validate all results contain keyword");
        for (String result: secondEngine.keySet()) {
            Assert.assertTrue(searchResultContains(keyword, secondEngine.get(result)),
                    "This result doesn't contain the keyword: "+result);
        }
        System.out.println("13. Check which are the common results between:");
        System.out.println("\tGoogle");
        printResults(firstEngine.keySet());
        System.out.println("\tBing");
        printResults(secondEngine.keySet());
        System.out.println("\tComparison");
        Set<String> comparison = firstEngine.keySet();
        comparison.retainAll(secondEngine.keySet());
        System.out.println("\t\tThe common results are:");
        printResults(comparison);
        //the homework didn't mention any assertion for this, but it feels like we should do this check
        System.out.println("\t\tAssert if there are common items");
        Assert.assertFalse(comparison.isEmpty(),"There are no common results between search engines!");
    }

    /**
     * In case we want to split above test into more, we need to store the 2 results for each engine
     * and control the order of execution. Options:
     * 1. Store results in variables above then control the order of execution with annotations:
     *  \@Test(priority = 1)
     * 2. Store the results a file and move test in a separate class then execute in order
     * 3. (Example commented below)The previous test can also be considered a precondition for following tests:
     *  \@Test(dependsOnMethods = {"validateKeywordPresence"})
     * 4. The bellow implementation is to make it easy to expand the number of search engines
     */
//    @Test(dependsOnMethods = {"validateKeywordPresence"})
//    public void compareTopResults(){
//        //Changed the structure because I needed to look back and forward during iteration
//        List<String> engines=resultsBySearchEngine.keySet().stream().toList();
//        List<Map<String, Map<String, String>>> results = resultsBySearchEngine.values().stream().toList();
//        //Initialized with the first engine because the retainAll needs to be inside the loop
//        Set<String> fullCompare = results.get(0).keySet();
//        for (int i=1;i<engines.size();i++) {
//            fullCompare.retainAll(results.get(i).keySet());
//            System.out.println("\tThe common results "+engines.get(i-1)+" Vs "+engines.get(i));
//            Set<String> currentVsPrevious = results.get(i-1).keySet();
//            currentVsPrevious.retainAll(results.get(i).keySet());
//            printResults(currentVsPrevious);
//        }
//        System.out.println("\tThe common results across all engines are:");
//        printResults(fullCompare);
//    }

    @AfterTest
    public void cleanup(){
        System.out.println("99. Cleanup");
        if (driver!=null){
            driver.quit();
        }
    }

    /**
     * Instantiating the engine related objects
     *
     * @param searchEngineName the name of the engine to be tested
     */
    private void setSearchEngine(String searchEngineName){
        if (searchEngineName.toLowerCase().contains("google")){
            System.out.println("\tTesting search engine: GOOGLE");
            searchEngine = "https://www.google.com/";
            searchHomePage=new GoogleSearchHome();
            searchResultsPage=new GoogleSearchResults();
        } else {
            System.out.println("\tTesting search engine: BING");
            searchEngine = "https://www.bing.com/";
            searchHomePage = new BingSearchHome();
            searchResultsPage= new BingSearchResults();
        }
    }

    /**
     * This checks if any element of the <code>result</code> contains the string in <code>keyword</code>.
     * It doesn't check for the word that the <code>keyword</code> represents.
     *
     * @param keyword the string to match
     * @param result the individual result to be tested
     * @return true if any element of <code>result</code> contains the string in <code>keyword</code>
     */
    private boolean searchResultContains(String keyword, Map<String,String> result){
        System.out.println("Start validating: "+result.get("title"));
        boolean present =false;
        for(String s:result.keySet()){
            System.out.println("\tResult contains "+keyword+" in "+s+": "+
                    result.get(s).toLowerCase().contains(keyword.toLowerCase()));
            if (!present && result.get(s).toLowerCase().contains(keyword.toLowerCase())){
                present=true;
            }
        }
        return present;
    }

    /**
     * To minimize code duplication and for standardization of output
     * @param results The set of keys top be printed. In our case the unique results from each engine or comparison
     */
    private void printResults(Set<String> results){
        results.forEach(System.out::println);
        System.out.println("------------------------------");
    }

    @DataProvider(name = "Keywords")
    public Object[][] keywords(){
        return new Object[][]{
                {"chloroplast"},
                {"cat"},
                {"boomerang"}
        };
    }
}

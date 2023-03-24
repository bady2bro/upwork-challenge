package searchEngine;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.search.engine.BaseSearchPage;
import org.search.engine.BaseSearchResults;
import org.search.engine.bing.BingSearchHome;
import org.search.engine.bing.BingSearchResults;
import org.search.engine.google.GoogleSearchHome;
import org.search.engine.google.GoogleSearchResults;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.*;

/**
 * This class contains the test given as homework.
 * NOTE:
 * 1. Tested for keywords:
 *  a. "boomerang" - passed
 *  b. "cat" - failed because one of the BING results is "Muzeum Sztuki i Techniki Japońskiej Manggha"
 *      that is missing "cat" from elements
 *  c. "chloroplast" - passed
 *  d. They still fail sometimes, so please execute at least twice until you see the above behavior
 *  e. The number of common links varies from execution to execution
 *      - For Firefox "chloroplast" has one common element
 *      - For Chrome "chloroplast" has two common elements
 * 2. Implemented implicit wait 10 seconds because it is fast fix for small test. Would implement conditional wait usually.
 * Reasons:
 *  a. when switching from chrome to bing it too longer to load page so made 2 second wait
 *  b. when searching for "chloroplast" on Bing it too longer than 2 seconds to load so made 3 seconds wait
 *  c. when run on firefox it take a lot longer to load so made it exaggerated 10 seconds wait
 *  d. conditional wait is best, but it makes code ugly and prone to duplication of wait code or major change to classes
 * 3. The test was implemented with external chromedriver.exe and firefoxdriver.exe.
 *  a. Please download and extract chromedriver at location: C:\ChromeDriver\chromedriver_win32\chromedriver.exe
 *      OR change path in variable
 *  b. Please download and extract firefoxdriver at location: C:\FirefoxDriver\geckodriver.exe
 *      OR change path in variable
 *  c. The executable can be moved inside project in multiple ways
 * 4. Firefox has a bug that impacts the Google search button clickability: https://bugzilla.mozilla.org/show_bug.cgi?id=1374283
 *  a. So I found a workaround with sendKey(Keys.ENTER) that works for both browsers;
 *  b. but implemented the workaround to trigger only for Firefox;
 *  c. I believe there was another workaround by refreshing the page, but it is not good code
 *
 */
public class searchHomeWorkTest {
    private String searchEngine;
    private WebDriver driver;
    private BaseSearchPage searchHomePage;
    private BaseSearchResults searchResultsPage;

    private  Map<String,Map<String, Map<String,String>>> resultsBySearchEngine = new HashMap<>();

    @BeforeTest
    public void initialize(){
        System.setProperty("webdriver.chrome.driver","C:\\ChromeDriver\\chromedriver_win32\\chromedriver.exe");
        System.setProperty("webdriver.gecko.driver","C:\\FirefoxDriver\\geckodriver.exe");
        String browser="Chrome";
//        String browser="Firefox";
        System.out.println("0.1. Instantiate driver based on browser under test");

        if (browser.equalsIgnoreCase("chrome")){
            System.out.println("\tTesting browser: CHROME");
            //setup of needed and common options for the browser
            ChromeOptions options = new ChromeOptions();
                //required to run non-local connection
            options.addArguments("--remote-allow-origins=*");
                //optional for better visuals
            options.addArguments("--start-maximized");
                //optional for sites that have tutorials or notifications
            options.addArguments("--disable-infobars");
            options.addArguments("--disable-notifications");
            driver = new ChromeDriver(options);
        } else {
            System.out.println("\tTesting browser: FIREFOX");
            FirefoxProfile profile = new FirefoxProfile();
            FirefoxOptions options = new FirefoxOptions();
            options.setProfile(profile);
            options.setAcceptInsecureCerts(true);
            driver = new FirefoxDriver(options);
            driver.manage().window().maximize();
        }
        //Modified implicit wait because when moving to BING there was lag and failure
        //I would implement a conditional wait until the element is clickable.
        //See BaseSearchPage resolveCookies method for example
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
    }

    /**
     * Included all steps of the homework in this test, but it can be split in different ways
     */
    @Test
    public void validateKeywordPresence(){
        String keyword="chloroplast";
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
            Assert.assertTrue(searchResultContains(keyword, firstEngine.get(result)));
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
            Assert.assertTrue(searchResultContains(keyword, secondEngine.get(result)));
        }
        System.out.println("13. Check which are the common results between:");
        System.out.println("------------------------------");
        firstEngine.keySet().forEach(System.out::println);
        System.out.println("------------------------------");
        secondEngine.keySet().forEach(System.out::println);
        System.out.println("------------------------------");
        Set<String> comparison = firstEngine.keySet();
        comparison.retainAll(secondEngine.keySet());
        System.out.println("\tThe common results are:");
        comparison.forEach(System.out::println);
    }

    /**
     * In case we want to split above test into more, we need to store the 2 results for each engine
     * and control the order of execution. Options:
     * 1. Store results in variables above then control the order of execution with annotations
     * 2. Store the results a file and move test in a separate class then execute in order
     * 3. The <code>validateKeywordPresence</code> test can also be considered a precondition for this.
     * 4. The bellow implementation is to make it easy to expand the number of search engines
     */
    @Test(priority = 1)
    public void compareTopResults(){
        //Changed the structure because I needed to look back and forward during iteration
        List<String> engines=resultsBySearchEngine.keySet().stream().toList();
        List<Map<String, Map<String, String>>> results = resultsBySearchEngine.values().stream().toList();
        //Initialized with the first engine because the retainAll needs to be inside the loop
        Set<String> fullCompare = results.get(0).keySet();
        for (int i=1;i<engines.size();i++) {
            fullCompare.retainAll(results.get(i).keySet());
            System.out.println("\tThe common results "+engines.get(i-1)+" Vs "+engines.get(i));
            Set<String> currentVsPrevious = results.get(i-1).keySet();
            currentVsPrevious.retainAll(results.get(i).keySet());
            currentVsPrevious.forEach(System.out::println);
            System.out.println("------------------------------------------------");
        }
        System.out.println("\tThe common results across all engines are:");
        fullCompare.forEach(System.out::println);
        System.out.println("-----------------------------------------------");
    }

    @AfterTest
    public void cleanup(){
        System.out.println("99. Cleanup");
        if (driver!=null){
            driver.quit();
        }
    }

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
}

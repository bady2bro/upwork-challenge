package browser.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

public class DriverFactory {
    public static WebDriver getInstance(String browser){
        if (browser.equalsIgnoreCase("chrome")){
            System.out.println("\tTesting browser: CHROME");
            //setup of needed and common options for the browser
            ChromeOptions options = new ChromeOptions();
            //required to run non-local connection
            options.addArguments("--remote-allow-origins=*");
            //optional for better visuals
            options.addArguments("--start-maximized");
            //optional for sites that have tutorials or notifications
//            options.addArguments("--disable-infobars");
//            options.addArguments("--disable-notifications");
            return new ChromeDriver(options);
        } else {
            System.out.println("\tTesting browser: FIREFOX");
            FirefoxProfile profile = new FirefoxProfile();
            FirefoxOptions options = new FirefoxOptions();
            options.setProfile(profile);
            options.setAcceptInsecureCerts(true);
            return new FirefoxDriver(options);
        }
    }
}

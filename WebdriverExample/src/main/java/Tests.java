import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created by Captain Osmant on 16.11.2016.
 */
public class Tests{

    public static WebDriver getBrowser(){

        System.setProperty("webdriver.gecko.driver","./src/main/resources/geckodriver.exe");
        WebDriver br = new FirefoxDriver();
        if(browser==null){
            browser = br;
        }
        return browser;
    }

    private static WebDriver browser;

    public static void auth(WebDriver browser){
        String path = "http://at.retarcorp.com/core";

        browser.get(path);

        try {
            browser.findElement(By.name("login")).sendKeys("tester");
            browser.findElement(By.name("password")).sendKeys("tester");
            browser.findElement(By.name("enter")).click();
        }catch(Exception e){
            sleep(1000);
            return;
        }
        sleep(2000);

        try{
            browser.findElement(By.name("login")).sendKeys("tester");
            browser.findElement(By.name("password")).sendKeys("tester");
            browser.findElement(By.name("enter")).click();
        }catch(Exception e){
            sleep(1000);
            return;
        }
        sleep(1000);
        return;
    }

    @Test
    public void testOneCanCreateNewArticle(){
        getBrowser();
        auth(browser);

        browser.findElement(By.className("i_blog")).click();
        sleep(2000);
        int count, resCount;
        count = browser.findElements(By.className("item")).size();
        browser.findElement(By.id("add")).click();

        sleep(2000);
        resCount = browser.findElements(By.className("item")).size();

        Assert.assertEquals(resCount - count, 1);


    }

    @Test
    public void testOneCanEditArticle(){
        getBrowser();
        auth(browser);

        String path="http://at.retarcorp.com/core/adm/blog/";
        browser.get(path);

        browser.findElements(By.className("item")).get(0).click();

        sleep(1500);

        String text = "Edited at timestamp "+System.currentTimeMillis();
        browser.findElement(By.id("title")).clear();
        browser.findElement(By.id("title")).sendKeys(text);


        browser.findElement(By.id("save")).click();
        sleep(1500);

        browser.findElements(By.className("item")).get(0).click();
        sleep(1500);
        String newText = browser.findElement(By.id("title")).getAttribute("value");
        Assert.assertEquals(newText, text);


    }
    private static void sleep(int millis){
        try{
            Thread.sleep(millis);
        }catch (InterruptedException ex){

        }
        return;
    }
}

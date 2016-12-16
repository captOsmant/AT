import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.SystemClock;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Captain Osmant on 16.11.2016.
 */
public class Tests{

    public static WebDriver getBrowser(){


        if(browser==null){
            System.setProperty("webdriver.gecko.driver","./src/main/resources/geckodriver.exe");
            WebDriver br = new FirefoxDriver();
            browser = br;

        }
        browser.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
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
        sleep(2000);
        return;
    }

    public static void rightClick(String cssSelector, WebDriver driver){
        sleep(200);
        String JS = "var event = new Event('contextmenu'); setTimeout(function(){ _.$(\""+cssSelector+"\")[0].dispatchEvent(event);}, 100)";
        System.out.println(JS);
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript(JS);
        sleep(200);
    }

    public static void rightClick(String cssSelector){
        rightClick(cssSelector, browser);
    }

    public static void removeIfExists(String filename){
        Integer size = browser.findElements(By.cssSelector("[data-addr='"+filename+"']")).size();
        if(size==0){
            return;
        }
        rightClick("[data-addr='"+filename+"']");
        browser.findElement(By.cssSelector(".contextmenu .delete")).click();
        sleep(1000);
    }

    public static WebElement getElement (final By by){
        return (new WebDriverWait(browser, 3)).until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver webDriver) {
                return browser.findElement(by);
            }
        });
    }
    @Test
    public void testOneCanCopyFile(){
        getBrowser();
        auth(browser);

        browser.get("http://at.retarcorp.com/core/file_manager/");
        getElement(By.cssSelector("[data-addr='/files']")).click();
        rightClick("[data-addr='/files/file-to-copy.txt']", browser);
        sleep(1000);

        getElement(By.cssSelector(".contextmenu .copy")).click();
        getElement(By.cssSelector("aside [data-addr='/test']")).click();
        sleep(1000);

        removeIfExists("/test/file-to-copy.txt");
        getElement(By.id("paste")).click();

        sleep(1000);
        int resultingSize= browser.findElements(By.cssSelector("[data-addr='/test/file-to-copy.txt']")).size();
        Assert.assertEquals(resultingSize, 1);

    }

    @Test
    public void testOneCanEditFile(){
        getBrowser();
        auth(browser);

        browser.get("http://at.retarcorp.com/core/file_manager/");


        getElement(By.cssSelector("[data-addr='/test']")).click();
        getElement(By.cssSelector("[data-addr='/test/file.txt']")).click();
        String textToUse = "HcYFDlwOXIfkSlfm";
        WebElement textarea = getElement(By.id("editor-content"));

        textarea.clear();
        textarea.sendKeys(textToUse);
        textarea.sendKeys(Keys.CONTROL, "S");

        // Delay to let AJAX save file contents
        sleep(1000);

        WebElement statusSpan = getElement(By.cssSelector("form.editor span.status"));
        String statusText = statusSpan.getText();

        Pattern pattern = Pattern.compile("^[\\D]*([\\d]+)\\s.*");
        Matcher m = pattern.matcher(statusText);

        Integer receivedSize=0, savedSize=0;

        while(m.find()){
            String value = m.group(1);
            savedSize = Integer.parseInt(value);
        }

        textarea.sendKeys(Keys.ESCAPE);
        rightClick("[data-addr='/test/file.txt']", browser);
        getElement(By.className("properties")).click();

        String propertyText = getElement(By.cssSelector(".popover h5:nth-child(3)")).getText();
        m = pattern.matcher(propertyText);

        while(m.find()){
            String value = m.group(1);
            receivedSize = Integer.parseInt(value);

        }
        Assert.assertEquals(receivedSize, savedSize);
    }


    @Test
    public void testOneCanNavigateFiles(){
        getBrowser();
        auth(browser);

        getElement(By.className("i_file_manager")).click();
        getElement(By.cssSelector("[data-addr='/core'].folder")).click();
        getElement(By.cssSelector("[data-addr='/core/file_manager'].folder")).click();
        getElement(By.cssSelector("[data-addr='/core/file_manager/test'].folder")).click();
        getElement(By.cssSelector("[data-addr='/core/file_manager/test/acx.txt']")).click();

        // Delaying to let AJAX load required file content;
        sleep(1000);
        String deFactoText = getElement(By.id("editor-content")).getText();
        String expectedText = "Hello, world!";

        Assert.assertEquals(deFactoText.equals(expectedText), true);
    }

    @Test
    public void testOneCanCreateNewArticle(){
        getBrowser();
        auth(browser);

        getElement(By.className("i_blog")).click();
        int count, resCount;
        count = browser.findElements(By.className("item")).size();
        getElement(By.id("add")).click();

        // AJAX delay
        sleep(1000);
        resCount = browser.findElements(By.className("item")).size();

        Assert.assertEquals(resCount - count, 1);


    }

    @Test
    public void testOneCanEditArticle(){
        getBrowser();
        auth(browser);

        String path="http://at.retarcorp.com/core/adm/blog/";
        browser.get(path);

        getElement(By.className("item")).click();

        String text = "Edited at timestamp "+System.currentTimeMillis();
        getElement(By.id("title")).clear();
        getElement(By.id("title")).sendKeys(text);


        getElement(By.id("save")).click();
        sleep(2000);

        browser.findElements(By.className("item")).get(0).click();



        String newText = getElement(By.id("title")).getAttribute("value");
        Assert.assertEquals(newText, text);


    }

    static String MYSQL_EXPECTED_RESULT = "15";
    @Test
    public void testOneCanExecMySQLRequest(){
        getBrowser();
        auth(browser);
        browser.get("http://at.retarcorp.com/core/mysql/");

        WebElement li = getElement(By.cssSelector(".query_list li:first-child"));
        li.click();

        WebElement listText = getElement(By.cssSelector(".query_list li:first-child xmp"));
        String queryExpected = listText.getText();

        String queryGot = getElement(By.id("query")).getAttribute("value");
        Assert.assertEquals(queryExpected.equals(queryGot), true);

        getElement(By.id("execute")).click();
        String receivedResult = getElement(By.cssSelector("section.content tbody tr:first-child td:first-child xmp")).getText();

        Assert.assertEquals(receivedResult.equals(MYSQL_EXPECTED_RESULT), true);

    }



    private static void sleep(int millis){
        try{
            Thread.sleep(millis);
        }catch (InterruptedException ex){

        }
        return;
    }
}

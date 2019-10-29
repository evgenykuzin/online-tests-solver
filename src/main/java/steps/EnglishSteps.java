package steps;

import main.Main;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class EnglishSteps extends WebDriverSteps {
    private static String shortAnswerType = "que shortanswer deferredfeedback notyetanswered";
    private static String multiChoiceType = "que multichoice deferredfeedback notyetanswered";
    private static String multiAnswerType = "que multianswer deferredfeedback notyetanswered";
    public EnglishSteps(WebDriver driver) {
        super(driver, Main.Subject.ENGLISH);
    }

    @Override
    public void getMainPage(String url, String userName, String password){
        driver.get(url);
        driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/nav/ul[2]/li[2]/div/span/a")).click();
        driver.findElement(By.xpath("//*[@id=\"region-main\"]/div/div[2]/div/div/div/div/div/div[1]/button")).click();
        driver.findElement(By.xpath("//*[@id=\"user\"]")).sendKeys(userName);
        driver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys(password);
        driver.findElement(By.xpath("//*[@id=\"doLogin\"]")).click();
    }

    public List<WebElement> getTasks() {
        return driver.findElements(By.className("activity quiz modtype_quiz "));
    }

    public void goToTask(WebElement task) {
        task.findElement(By.tagName("a")).click();
        driver.findElement(By.tagName("button")).click();
    }

    public boolean isMultiChoiceType() {
        try {
            driver.findElement(By.className(multiChoiceType));
        } catch (NoSuchElementException nsee) {
            return false;
        }
        return true;
    }

    public boolean isMultiAnswerType() {
        try {
            driver.findElement(By.className(multiAnswerType));
        } catch (NoSuchElementException nsee) {
            return false;
        }
        return true;
    }

    public boolean isShortAnswerType() {
        try {
            driver.findElement(By.className(shortAnswerType));
        } catch (NoSuchElementException nsee) {
            return false;
        }
        return true;
    }

    public void doTask() {
        List<WebElement> blocks = driver.findElements(By.className("qtext"));

        if (isMultiAnswerType()) {

        } else if (isMultiChoiceType()) {

        } else if (isShortAnswerType()) {
            for (WebElement block : blocks) {
                String question = block.findElement(By.tagName("p")).getText();
                block.findElement(By.tagName("input")).sendKeys("afafafaf");
            }
        }
    }

}

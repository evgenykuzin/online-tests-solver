package steps;

import info.debatty.java.stringsimilarity.Damerau;
import main.Main;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.Utils;

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
        try {
            driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/nav/ul[2]/li[2]/div/span/a")).click();
        } catch (NoSuchElementException nsee1){
            nsee1.printStackTrace();
        }
        try {
            driver.findElement(By.xpath("//*[@id=\"region-main\"]/div/div[2]/div/div/div/div/div/div[1]/button")).click();
        } catch (NoSuchElementException nsee1){
            nsee1.printStackTrace();
        }
        try {
            driver.findElement(By.xpath("//*[@id=\"user\"]")).sendKeys(userName);
            driver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys(password);
            driver.findElement(By.xpath("//*[@id=\"doLogin\"]")).click();
        } catch (NoSuchElementException nsee1){
            nsee1.printStackTrace();
        }
    }

    public void getPage(String url){
        driver.get(url);
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

    public void doTask(String answers) {
        List<WebElement> blocks = driver.findElements(By.className("qtext"));
        if (isMultiAnswerType()) {
            blocks = driver.findElements(By.className("formulation clearfix"));
            for (WebElement block : blocks) {
                String question = block.findElement(By.className("qtext")).getText();
                String answer = "";
                Damerau damerau = new Damerau();
                answer = Utils.findSimilar(question, answers.split("\n")).replaceAll(question, "");
                double dist = 10000;
                WebElement rightAnswer = null;
                for (WebElement a : block.findElements(By.className("input"))){
                    double dd = damerau.distance(a.getText(), answer);
                    if (dd < dist){
                        dist = dd;
                        rightAnswer = a;
                    }
                }
                if (rightAnswer == null) throw new IllegalArgumentException("answer not found");
               rightAnswer.click();
            }
        } else if (isMultiChoiceType()) {

        } else if (isShortAnswerType()) {
            for (WebElement block : blocks) {
                String question = block.findElement(By.tagName("p")).getText();
                block.findElement(By.tagName("input")).sendKeys("afafafaf");
            }
        }
    }

}

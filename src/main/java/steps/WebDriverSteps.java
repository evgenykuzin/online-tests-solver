package steps;

import main.Main;
import org.openqa.selenium.*;
import java.util.List;
public class WebDriverSteps {

    WebDriver driver;
    private Enum<Main.Subject> currSubject;

    public WebDriverSteps(WebDriver driver, Enum<Main.Subject> subjectEnum) {
        this.driver = driver;
        currSubject = subjectEnum;
    }

    public void getMainPage(String openEduPhisraUrl, String userName, String password) {
        driver.get(openEduPhisraUrl);
        driver.findElement(By.xpath("//*[@id=\"id_username\"]")).sendKeys(userName);
        driver.findElement(By.xpath("//*[@id=\"id_password\"]")).sendKeys(password);
        driver.findElement(By.xpath("//*[@id=\"auth_form_sub\"]")).click();
    }

    public List<WebElement> getQuestionBlocks() {
        List<WebElement> elements = driver.findElements(By.className("problem"));
        if (elements.size() <= 2) {
            elements = driver.findElements(By.className("wrapper-problem-response"));
            if (elements.size() <= 2) {
                elements = driver.findElements(By.tagName("fieldset"));

            }
        }
        return elements;
    }

    public String[] getAnswers(int i) {
       String[] arr = getQuestionBlocks().get(i).getText().split("\n");
       if (driver.findElements(By.className("problem")).size() == 1) {
           return arr;
       } else return new String[]{arr[1], arr[2], arr[3], arr[4]};
    }

    public WebElement getRadioButton(int i, int j) {
        List<WebElement> result = getQuestionBlocks().get(i).findElements(By.tagName("input"));
        try {
            return result.get(j);

        } catch (IndexOutOfBoundsException ioobe){
            return result.get(j-1);
        }
    }
}
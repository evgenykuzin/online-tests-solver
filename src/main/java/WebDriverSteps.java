import org.openqa.selenium.*;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.List;

public class WebDriverSteps {

    private WebDriver driver;
    private Enum<subject> currSubject;
    public WebDriverSteps(WebDriver driver, Enum<subject> subjectEnum) {
        this.driver = driver;
        currSubject = subjectEnum;
    }

    @Step
    public void openMainPage(String openEduPhisraUrl, String userName, String password) {
        driver.get(openEduPhisraUrl);
        driver.findElement(By.xpath("//*[@id=\"id_username\"]")).sendKeys(userName);
        driver.findElement(By.xpath("//*[@id=\"id_password\"]")).sendKeys(password);
        driver.findElement(By.xpath("//*[@id=\"auth_form_sub\"]")).click();
    }

    public enum subject {
        PHISRA,
        FILOSOFY
    }

    public enum typeXpath {
        QUESTION,
        ANSWER,
        SUBMIT_BUTTON,
        RADIO
    }

    public static String getNextXpath(String id, Enum<typeXpath> type) {
        String xpath = "";
        if (type.equals(typeXpath.QUESTION)) {
            xpath = Xpaths.QUESTION_XPATH;
        } else if (type.equals(typeXpath.SUBMIT_BUTTON)) {
            xpath = Xpaths.SUBMIT_ANSWER_BUTTON_XPATH;
        } else if (type.equals(typeXpath.ANSWER)) {
            xpath = Xpaths.ANSWER_TEXT_XPATH;
        } else if (type.equals(typeXpath.RADIO)){
            xpath = Xpaths.ANSWER_RADIO_XPATH;
        }
        String result = xpath.replace("ID", id);
        return result;
    }


    public WebElement getElement(String xpath) {
        System.out.println(xpath);
        return driver.findElement(By.xpath(xpath));
    }

    public String getId(int i){
        return getElement("//*[@id=\"seq_content\"]/div/div[2]/div[2]/div/div/div[" + i + "]").
                getAttribute("data-id").split("block@")[1];
    }

    public String getId(){
        return getElement("//*[@id=\"seq_content\"]/div/div[2]/div[1]").
                getAttribute("data-id").split("block@")[1];
    }

    @Step
    public WebElement getNext(int i, Enum<typeXpath> type) {
        String id = getId();
        if (currSubject.equals(subject.PHISRA)) {
            id = getId(i);
            return getElement(getNextXpath("problem_" + id, type));
        } else if (currSubject.equals(subject.FILOSOFY)){
            WebElement webElement;
            try {
                 webElement = getElement(Xpaths.FILOSOFY_QUESTION.replaceFirst("M", i+1+"").
                        replaceFirst("ID", id));
            } catch (NoSuchElementException nsee){
                id = getId(i);
                webElement = getElement(Xpaths.FILOSOFY_QUESTION_2.replaceFirst("ID", id));
            }
            return webElement;
        }
        return null;
    }

    @Step
    public List<WebElement> getQuestionBlocks(){
        return driver.findElements(By.className("problem"));
    }

    @Step
    public String[] getAnswers(int i){
        String[] arr = getQuestionBlocks().get(i).getText().split("\n");
        return new String[]{arr[1],arr[2],arr[3],arr[4]};
    }

    @Step
    public List<WebElement> getRadioButton(int i){
        return getQuestionBlocks().get(i).findElements(By.tagName("input"));
    }

    @Step
    public WebElement[] getNextAnswers(int i) {
        WebElement[] answers = new WebElement[4];
        String id = getId();
        for (int iter = 0; iter < 4; iter++) {
            if (currSubject.equals(subject.PHISRA)) {
                id = getId(i);
                answers[iter] = getElement(getNextXpath(id + "_2_1-choice_" + iter + "-label", typeXpath.ANSWER));
            } else if (currSubject.equals(subject.FILOSOFY)){
                try {
                    answers[iter] = getElement(Xpaths.FILOSOFY_ANSWER_TEXT.replaceFirst("M", iter+"").
                            replaceFirst("N", i+1+"").
                            replaceFirst("ID", id));
                } catch (NoSuchElementException nsee){
                    answers[iter] = getElement(Xpaths.FILOSOFY_ANSWER_TEXT.replaceFirst("M", iter+"").
                            replaceFirst("N", i+1+"").
                            replaceFirst("ID", id).replaceFirst("label", "legend"));
                }

            }
        }
        return answers;
    }

    @Step
    public WebElement getNextRadio(int i, int j){
        String id = getId();
        if (currSubject.equals(subject.PHISRA)) {
            id = getId(i);
            return getElement(getNextXpath("input_" + id + "_2_1_choice_" + j, typeXpath.RADIO));
        } else if (currSubject.equals(subject.FILOSOFY)){
            return getElement(Xpaths.FILOSOFY_RADIO.replaceAll("M", j+"").
                    replaceFirst("N", i+1+"")
                    .replaceFirst("ID", id));
        }
        return null;
    }


}
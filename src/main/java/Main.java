import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    private static WebDriver driver;
    private static WebDriverSteps steps;
    public static Map<String, String> answersMap;
    public static JPanel panel = new JPanel();
    public static TextArea textArea = new TextArea();
    public static JRadioButton radioButton = new JRadioButton("автонажатие 'оправить'");
    public static JComboBox comboBox = new JComboBox();
    public static File loginTempFile;
    public static Enum<WebDriverSteps.subject> subject;

    static {
        try {
            loginTempFile = new File(new File(".").
                    getCanonicalPath() + "\\loginTemp.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame();
            frame.setTitle("решатель онлайн-тестов");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(400, 600));
            frame.pack();
            frame.setResizable(true);
            frame.setLocationRelativeTo(null);
            panel.setPreferredSize(new Dimension(400, 600));
            comboBox = new JComboBox();
            comboBox.setEditable(true);
            comboBox.addItem("физкультура");
            comboBox.addItem("философия");
            comboBox.setSelectedItem("философия");
            String[] loginTemp = readFile(loginTempFile).split("specialspliter");
            System.out.println(loginTemp[0]);
            //System.out.println(loginTemp[1]);
            String login = "";
            String pass = "";
            if (loginTemp.length >= 2) {
                if (loginTemp[0] != null) login = loginTemp[0];
                if (loginTemp[1] != null) pass = loginTemp[1];
            }
            subject = WebDriverSteps.subject.FILOSOFY;
            final TextField userNameField = new TextField(login);
            final TextField passwordField = new TextField(pass);
            final TextField courseLinkField = new TextField();
            Label userLabel = new Label("логин");
            final Label passLabel = new Label("пароль");
            Label courseLabel = new Label("ссылка на страницу с нужным тестом");
            Dimension textDim = new Dimension(300, 50);
            Dimension labelDim = new Dimension(300, 20);
            userNameField.setPreferredSize(textDim);
            passwordField.setPreferredSize(textDim);
            courseLinkField.setPreferredSize(textDim);
            userLabel.setPreferredSize(labelDim);
            passLabel.setPreferredSize(labelDim);
            courseLabel.setPreferredSize(labelDim);
            JButton start = new JButton("запуск");
            start.setPreferredSize(new Dimension(300, 50));
            start.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (comboBox.getSelectedItem().toString().equals("физкультура")) {
                        subject = WebDriverSteps.subject.PHISRA;
                    } else if (comboBox.getSelectedItem().toString().equals("философия")) {
                        subject = WebDriverSteps.subject.FILOSOFY;
                    }
                    answersMap = getAnswersMap();
                    if (!userNameField.getText().equals("") && !passwordField.getText().equals("")) {
                        writeToFile(loginTempFile, userNameField.getText() + "specialspliter" + passwordField.getText());
                    }
                    log("запуск...");
                    try {
                        init();
                    } catch (Exception ex) {
                        log(ex.getMessage());
                        ex.printStackTrace();
                    }

                    try {
                        go(100, courseLinkField.getText(), userNameField.getText(), passwordField.getText());
                    } catch (NoSuchElementException nsee) {
                        System.out.println("complite");
                        log("миссия окончена.");
                    }
                }
            });
            panel.add(courseLabel);
            panel.add(courseLinkField, BorderLayout.CENTER);
            panel.add(userLabel);
            panel.add(userNameField, BorderLayout.CENTER);
            panel.add(passLabel);
            panel.add(passwordField, BorderLayout.CENTER);
            radioButton.setSelected(true);
            panel.add(radioButton);
            panel.add(comboBox);
            panel.add(start, BorderLayout.CENTER);
            textArea.setPreferredSize(new Dimension(300, 250));
            panel.add(textArea);
            panel.setVisible(true);
            frame.add(panel);
            frame.setVisible(true);
            answersMap = getAnswersMap();
        } catch (Exception e) {
            e.printStackTrace();
            log(e.getMessage());
        }

    }

    public static void log(String msg) {
        textArea.append(msg + "\n\n");
    }

    public static void init() throws Exception {
        String chromeDriverURL;
        chromeDriverURL = new File(".").getCanonicalPath() + "\\chromedriver";
        System.out.println("path - " + chromeDriverURL);
        try {
            System.setProperty("webdriver.chrome.driver", chromeDriverURL + ".exe");
            driver = new ChromeDriver();
        } catch (IllegalStateException ise) {
            try {
                System.setProperty("webdriver.chrome.driver", chromeDriverURL + "1.exe");
                driver = new ChromeDriver();
            } catch (IllegalStateException ise2) {
                System.setProperty("webdriver.chrome.driver", chromeDriverURL + "2.exe");
                driver = new ChromeDriver();
            }
        }

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        steps = new WebDriverSteps(driver, subject);
    }

    public static void go(int size, String url, String userName, String password) {
        steps.openMainPage(url, userName, password);
        if (subject.equals(WebDriverSteps.subject.FILOSOFY)) {
            driver.findElement(By.xpath(Xpaths.FILOSOFY_TEST_BTN)).click();
        }
        for (int i = 1; i < size; i++) {
            String question;
            question = steps.getQuestionBlocks().get(i).getText().split("\n")[0];
            if (question != "" && question != null) {
                String answer = null;
                for (String key : answersMap.keySet()) {
                    if (key.trim().toLowerCase().contains(question.trim().toLowerCase()) ||
                            question.trim().toLowerCase().contains(key.trim().toLowerCase())) {
                        answer = answersMap.get(key);
                    }
                }
                System.out.println(question);
                if (answer != null) {
                    System.out.println(answer);
                    WebElement radioAnswer = null;
                    int iter = 0;
                    for (String element : steps.getAnswers(i)) {
                        if (answer.trim().toLowerCase().contains(element.trim().toLowerCase()) ||
                                element.trim().toLowerCase().contains(answer.trim().toLowerCase())) {
                            radioAnswer = steps.getRadioButton(i).get(iter);
                            break;
                        }
                        iter++;
                    }
                    if (radioAnswer != null) {
                        radioAnswer.click();
                        if (radioButton.isSelected()) {
                            try {
                                steps.getQuestionBlocks().get(i).findElement(By.className("submit")).click();
                            } catch (ElementNotInteractableException enie){
                                enie.printStackTrace();
                                log(enie.getMessage());
                            }
                            try {
                            } catch (Exception e) {

                            }
                        }
                    }
                } else {
                    System.out.println("Вопрос №" + i + " не найден, нужно ввести вручную!");
                    log("Вопрос №" + i + " не найден, нужно ввести вручную! {\n" + question +"\n"+answersMap.values().toString());
                }

            } else {
                System.out.println("Проблема с вопросом №" + i);
                log("Проблема с вопросом №" + i);
            }
        }
        System.out.println("complite");
        log("миссия окончена.");
    }

    public static Map<String, String> getAnswersMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        String fileName = "\\phisraVSP.txt";
        if (subject.equals(WebDriverSteps.subject.PHISRA)) {
            fileName = "\\phisraVSP.txt";
        } else if (subject.equals(WebDriverSteps.subject.FILOSOFY)) {
            fileName = "\\filosofiaVSP.txt";
        }
        File file = null;
        try {
            file = new File(new File(".").getCanonicalPath() + fileName);
            //file = new File(System.getProperty("user.dir") + fileName);
        } catch (IOException e) {
           log(e.getMessage());
        }
        String[] sourse = read(file).
                split("<td class=\"column-1\">");
        for (int i = 0; i < 1000000; i++) {
            try {
                String string = sourse[i].
                        replaceAll("<td class=\"column-1\">", "").
                        replaceAll("<td class=\"column-2\">", "").
                        replaceAll("</td></tr><tr class=\"row-\\d even\">", "").
                        replaceAll("</td></tr><tr class=\"row-\\d odd\">", "").
                        replaceAll("\t", "").
                        replaceAll("<tr class=\"row-1 odd\"><th class=\"column-1\">", "");
                if (string.contains("</td>") && !string.contains("МОДУЛЬ")) {
                    String[] pair = string.split("</td>");
                    String question = pair[0];
                    String answer = pair[1];
                    map.put(question, answer);
                }
            } catch (Exception e) {
                //log(e.getMessage());
                e.printStackTrace();
                break;
            }
        }
        return map;
    }

    public static String readFile(File file) {
        StringBuilder res = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            if (line != null) {
                while (line != null) {
                    res.append(line);
                    line = bufferedReader.readLine();
                }
            }
        } catch (IOException e) {
            log(e.getMessage());
            e.printStackTrace();
        }
        return res.toString();
    }

    public static String read(File file){
        BufferedReader bReader = null;
        InputStreamReader iReader = null;
        FileInputStream fStream = null;
        StringBuilder sb = new StringBuilder();
        try {
            fStream = new FileInputStream(file);
            iReader = new InputStreamReader(fStream, "UTF-8");
            bReader = new BufferedReader(iReader);

            String fileLine = bReader.readLine();
            sb.append(fileLine).append("\n");
            while (fileLine != null) {
                //....................................
                fileLine = bReader.readLine();
                sb.append(fileLine).append("\n");
            }
        }catch (IOException ioe){
            log(ioe.getMessage());
        }
        return sb.toString();
    }

    public static void writeToFile(File file, String string) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            StringBuilder sb = new StringBuilder();
            sb.append(string).append("\n");
            bufferedWriter.write(sb.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

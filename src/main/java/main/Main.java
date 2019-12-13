package main;

import info.debatty.java.stringsimilarity.Damerau;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import steps.EnglishSteps;
import steps.WebDriverSteps;
import utils.TextRecognizer;
import utils.Utils;

public class Main {
    private static WebDriver driver;
    private static WebDriverSteps webDriverSteps;
    private static EnglishSteps englishSteps;
    private static Map<String, String> answersMap;
    private static JPanel panel = new JPanel();
    private static TextArea textArea = new TextArea();
    private static JRadioButton authoSubmit = new JRadioButton("автонажатие 'оправить' (для физры)");
    private static JComboBox comboBox = new JComboBox();
    private static File loginTempFile;
    private static TextField userNameField;
    private static TextField passwordField;
    private static TextField courseLinkField;
    private static Enum<Subject> subject;
    private static final String english = "английский";
    private static final String filosofy = "философия";
    private static final String phisra = "физкультура";
    private String moduleUrl = "";
    private static String assetsPath = "\\src\\main\\assets\\";
    private static Main m = new Main();

    static {
        try {
            loginTempFile = new File(new File(".").
                    getCanonicalPath() + assetsPath + "loginTemp.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum Subject {
        PHISRA,
        FILOSOFY,
        ENGLISH
    }

    public static void main(String[] args) {
        //final main.Main m = new main.Main();
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
            comboBox.addItem(phisra);
            comboBox.addItem(filosofy);
            comboBox.addItem(english);
            comboBox.setSelectedItem(filosofy);
            String[] loginTemp = m.readFile(loginTempFile).split("specialspliter");
            System.out.println(loginTemp[0]);
            String login = "";
            String pass = "";
            if (loginTemp.length >= 2) {
                if (loginTemp[0] != null) login = loginTemp[0];
                if (loginTemp[1] != null) pass = loginTemp[1];
            }
            subject = Subject.FILOSOFY;
            userNameField = new TextField(login);
            passwordField = new TextField(pass);
            courseLinkField = new TextField();
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
            start.addActionListener(new MainButtonActionListener());
            panel.add(courseLabel);
            panel.add(courseLinkField, BorderLayout.CENTER);
            panel.add(userLabel);
            panel.add(userNameField, BorderLayout.CENTER);
            panel.add(passLabel);
            panel.add(passwordField, BorderLayout.CENTER);
            authoSubmit.setSelected(false);
            panel.add(authoSubmit);
            panel.add(comboBox);
            panel.add(start, BorderLayout.CENTER);
            textArea.setPreferredSize(new Dimension(300, 250));
            panel.add(textArea);
            panel.setVisible(true);
            frame.add(panel);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            m.log(e.getMessage());
        }

    }

    static class MainButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (comboBox.getSelectedItem().toString().equals(phisra)) {
                subject = Subject.PHISRA;
            } else if (comboBox.getSelectedItem().toString().equals(filosofy)) {
                subject = Subject.FILOSOFY;
            } else if (comboBox.getSelectedItem().toString().equals(english)) {
                subject = Subject.ENGLISH;
                //answersMap = getEnglishAnswers();
            }
            if (!userNameField.getText().equals("") && !passwordField.getText().equals("")) {
                Utils.writeToFile(loginTempFile, userNameField.getText() + "specialspliter" + passwordField.getText());
            }
            m.log("запуск...");
            try {
                m.init();
            } catch (Exception ex) {
                m.log(ex.getMessage());
                ex.printStackTrace();
            }
            if (subject.equals(Subject.ENGLISH)) {
                m.goEnglish(courseLinkField.getText());
            } else {
                m.go(100, courseLinkField.getText(), userNameField.getText(), passwordField.getText());
            }
            m.log("миссия окончена.");
        }
    }

    public void log(String msg) {
        textArea.append(msg + "\n\n");
    }

    public void init() throws Exception {
        String chromeDriverURL;
        chromeDriverURL = new File(".").getCanonicalPath() + assetsPath + "drivers\\chromedriver";
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
        if (subject.equals(Subject.ENGLISH)) {
            englishSteps = new EnglishSteps(driver);
        } else {
            webDriverSteps = new WebDriverSteps(driver, subject);
            answersMap = m.getAnswersMap();
        }
    }

    private void returnToModule() {
        driver.get(moduleUrl);
    }

    public void goEnglish(String moduleUrl) {
        englishSteps.getMainPage(moduleUrl, userNameField.getText(), passwordField.getText());
        this.moduleUrl = moduleUrl;
        returnToModule();
        List<WebElement> module = englishSteps.getTasks();
        for (WebElement taskSrc : module) {
            returnToModule();
            englishSteps.goToTask(taskSrc);
            //*get task name from page*
            String taskName = "englishSteps.getTaskName()";
            taskName = "\\testing\\qutothleve\\";
            String answers = getAnswersForEnglish(taskName);
            englishSteps.doTask(answers);
        }
    }

    private String getAnswersForEnglish(String taskName) {
        StringBuilder answers = new StringBuilder();
        TextRecognizer tr = new TextRecognizer();
        tr.begin();
        File answerDir = new File(taskName);
        if (answerDir.isDirectory()) {
            for (File img : answerDir.listFiles()) {
                answers.append(tr.recognize(img.getPath())).append("\n");
            }
        }
        return answers.toString();
    }

    public void go(int size, String url, String userName, String password) {
        webDriverSteps.getMainPage(url, userName, password);
        if (subject.equals(Subject.FILOSOFY)) {
            List<WebElement> buttons = driver.findElements(By.tagName("button"));
            boolean onRightPage = false;
            while (!onRightPage) {
                try {
                    driver.findElement(By.className("problem"));
                    onRightPage = true;
                    //break;
                } catch (NoSuchElementException nsee) {
                    try {
                        driver.findElement(By.className("wrapper-problem-response"));
                        onRightPage = true;
                    } catch (NoSuchElementException nsee2) {
                        for (WebElement btn : buttons) {
                            if (btn.getText().equals("Вперёд")) {
                                btn.click();
                                break;
                            }

                        }
                    }

                }
            }
        }
        for (int i = 0; i < size; i++) {
            String question = null;
            String[] block = webDriverSteps.getQuestionBlocks().get(i).getText().split("\n");
            try {
                if (webDriverSteps.getAnswers(i).length < 5) {
                    question = block[0];
                } else {
                    WebElement problem = driver.findElement(By.className("problem"));
                    question = problem.findElements(By.tagName("p")).get(i).getText();
                }
            } catch (NoSuchElementException | IndexOutOfBoundsException nsee_ioobe) {
                try {
                    question = webDriverSteps.getQuestionBlocks().get(i).findElement(By.tagName("legend")).getText();
                } catch (NoSuchElementException nsee2) {
                    question = driver.findElements(By.tagName("fieldset")).get(i).findElement(By.tagName("legend")).getText();
                }
            }

            if (!question.equals("")) {
                String answer = null;
                try {
                    Damerau d = new Damerau();
                    double dist = 100;
                    for (String key : answersMap.keySet()) {
                        String k = key.trim().toLowerCase().replaceAll("[\\d$&+:;=?@#|'<>.^*()%!-]", "");
                        String q = question.trim().toLowerCase().replaceAll("[\\d$&+:;=?@#|'<>.^*()%!-]", "");
                        if (d.distance(k, q) < dist) {
                            if (!key.equals("")) {
                                answer = answersMap.get(key);
                                dist = d.distance(k, q);
                            }
                        }
                    }
                } catch (Exception e) {
                    log(e.getMessage());
                }
                System.out.println(question + " {");
                log(question + " {");
                if (answer != null) {
                    System.out.println(answer + "\n}\n");
                    log(answer + "\n\n}\n");
                    WebElement radioAnswer = null;
                    int radioJ = findRadioAnswerID(answer, i);
                    radioAnswer = webDriverSteps.getRadioButton(i, radioJ);
                    if (radioAnswer != null) {
                        try {
                            radioAnswer.click();
                        } catch (ElementNotInteractableException enie) {
                            radioAnswer = webDriverSteps.getQuestionBlocks().get(i).findElements(By.className("field")).get(radioJ);
                            radioAnswer.click();
                        }
                        if (authoSubmit.isSelected()) {
                            try {
                                webDriverSteps.getQuestionBlocks().get(i).findElement(By.className("submit")).click();
                            } catch (ElementNotInteractableException enie) {
                                enie.printStackTrace();
                                log(enie.getMessage());
                            }
                            try {
                            } catch (Exception e) {

                            }
                        }
                    }
                } else {
                    System.out.println("Вопрос №" + i + 1 + " не найден, нужно ввести вручную!");
                    log("Вопрос №" + i + 1 + " не найден, нужно ввести вручную! {\n" + question + "\n" + answersMap.values().toString());
                }
            } else {
                System.out.println("Проблема с вопросом №" + i);
                log("Проблема с вопросом №" + i);
            }
        }
        System.out.println("complite");
        log("миссия окончена.");
    }

    private int findRadioAnswerID(String answer, int i) {
        Damerau d = new Damerau();
        WebElement radioAnswer = null;
        int iter = 0;
        double dist = 100;
        int result = 0;
        for (String element : webDriverSteps.getAnswers(i)) {
            String a = answer.trim().toLowerCase().replaceAll("[\\d$&+:;=?@#|'<>.^*()%!]", "");
            String e = element.trim().toLowerCase().replaceAll("[\\d$&+:;=?@#|'<>.^*()%!]", "");
            if (d.distance(e, a) < dist) {
                result = iter;
                dist = d.distance(e, a);
            }

            iter++;
        }
        return result;
    }

    public Map<String, String> getAnswersMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        String fileName = "";
        if (subject.equals(Subject.PHISRA)) {
            fileName = assetsPath + "answers\\phisraVSP2.txt";
        } else if (subject.equals(Subject.FILOSOFY)) {
            fileName = assetsPath + "answers\\filosofia2VSP2.txt";
        }
        File file = null;
        try {
            file = new File(new File(".").getCanonicalPath() + fileName);
        } catch (IOException e) {
            log(e.getMessage());
        }
        String[] sourse = Utils.read(file).
                split("\n");
        for (int i = 0; i < 1000000; i++) {
            try {
                String string = sourse[i];
                String[] pair = string.split("ОТВЕТ");
                if (pair.length == 2 && pair[0] != "" && pair[1] != "") {
                    String question = pair[0];
                    String answer = pair[1];
                    map.put(question, answer);
                }
            } catch (IndexOutOfBoundsException iobe) {
                break;
            }
        }
        return map;
    }

    public String readFile(File file) {
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

//    public String read(File file) {
//        BufferedReader bReader = null;
//        InputStreamReader iReader = null;
//        FileInputStream fStream = null;
//        StringBuilder sb = new StringBuilder();
//        try {
//            fStream = new FileInputStream(file);
//            iReader = new InputStreamReader(fStream, "UTF-8");
//            bReader = new BufferedReader(iReader);
//
////            String fileLine = bReader.readLine().replaceAll("[\\w]", "");
////            sb.append(fileLine).append("\n");
//            String fileLine = null;
//            do {
//                fileLine = bReader.readLine();
//                if (fileLine != null) {
//                    //НЕ УБИРАТЬ ЭТИ КОММЕНТАРИИ НИЖЕ!!!!!!!!!!
////                    fileLine = fileLine.replaceAll("</td><td class=\"column-2\">", "ОТВЕТ");
////                    fileLine = fileLine.replaceAll("[\\w$&+:;=?@#|'<>.,^*()%!-\\\\/]", "");
//                    if (!fileLine.equals("  ") && !fileLine.equals("")) {
//                        sb.append(fileLine).append("\n");
//                    }
//                }
//            } while (fileLine != null);
//
//        } catch (IOException ioe) {
//            log(ioe.getMessage());
//        }
//        return sb.toString();
//    }
//
//
//    public void writeToFile(File file, String string) {
//        try {
//            FileWriter fileWriter = new FileWriter(file);
//            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//            StringBuilder sb = new StringBuilder();
//            sb.append(string).append("\n");
//            bufferedWriter.write(sb.toString());
//            bufferedWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}

package utils;

import java.io.*;

public class VSPFormater {
    public static void main(String[] args) {
        String path = "src/main/assets/answers/filosofia2VSP.txt";
        format(path, "filosofia2VSP.txt", "filosofia2VSP2.txt");
    }

    private static void format(String path, String oldFileName, String newFileName){
        File file = new File(path);
        Utils.writeToFile(new File(path.replace(oldFileName, newFileName)), read(file));
    }

    public static String read(File file) {
        BufferedReader bReader = null;
        InputStreamReader iReader = null;
        FileInputStream fStream = null;
        StringBuilder sb = new StringBuilder();
        try {
            fStream = new FileInputStream(file);
            iReader = new InputStreamReader(fStream, "UTF-8");
            bReader = new BufferedReader(iReader);

//            String fileLine = bReader.readLine().replaceAll("[\\w]", "");
//            sb.append(fileLine).append("\n");
            String fileLine = null;
            do {
                fileLine = bReader.readLine();
                if (fileLine != null) {
                    //НЕ УБИРАТЬ ЭТИ КОММЕНТАРИИ НИЖЕ!!!!!!!!!!
                    fileLine = fileLine.replaceAll("</td><td class=\"column-2\">", "ОТВЕТ");
                    fileLine = fileLine.replaceAll("[\\w$&+:;=?@#|'<>.,^*()%!-\\\\/]", "");
                    if (!fileLine.equals("  ") && !fileLine.equals("")) {
                        sb.append(fileLine).append("\n");
                    }
                }
            } while (fileLine != null);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return sb.toString();
    }
}

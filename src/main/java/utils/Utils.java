package utils;

import info.debatty.java.stringsimilarity.Damerau;

import java.io.*;

public class Utils {
    public static String findSimilar(String s1, String[] ss){
        String similar = null;
        Damerau damerau = new Damerau();
        double dist = 10000;
        for (String s2 : ss) {
            double dd = damerau.distance(s1, s2);
            if (dd < dist){
                dist = dd;
                similar = s2;
            }
        }
        return similar;
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
//                    fileLine = fileLine.replaceAll("</td><td class=\"column-2\">", "ОТВЕТ");
//                    fileLine = fileLine.replaceAll("[\\w$&+:;=?@#|'<>.,^*()%!-\\\\/]", "");
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

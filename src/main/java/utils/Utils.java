package utils;

import info.debatty.java.stringsimilarity.Damerau;

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
}

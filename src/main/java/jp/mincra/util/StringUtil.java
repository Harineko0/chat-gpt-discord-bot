package jp.mincra.util;

public class StringUtil {
    /**
     * \n -> _n
     * \" -> _'
     * @return
     */
    public static String encodeEscape(String str) {
        return str.replaceAll("\n", "").replaceAll("\"", "'");
    }


    public static String decodeEscape(String str) {
        return str.replaceAll("'", "\"");
    }
}

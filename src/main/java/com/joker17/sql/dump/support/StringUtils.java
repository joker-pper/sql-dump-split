package com.joker17.sql.dump.support;

public class StringUtils {

    public static boolean equals(CharSequence left, CharSequence right) {
        if (left != null && left != null) {
            return left.toString().equals(right.toString());
        } else {
            return false;
        }
    }

    public static boolean contains(CharSequence seq, CharSequence searchSeq) {
        if (seq != null && searchSeq != null) {
            return seq.toString().indexOf(searchSeq.toString(), 0) >= 0;
        } else {
            return false;
        }
    }

    public static boolean startWith(CharSequence seq, CharSequence searchSeq) {
        if (seq != null && searchSeq != null) {
            return seq.toString().startsWith(searchSeq.toString());
        } else {
            return false;
        }
    }

    public static String defaultIfEmpty(String str, String defaultStr) {
        return str == null || str.length() == 0 ? defaultStr : str;
    }

    public static String[] split(String str, String separator) {
        if (str != null && separator != null) {
            return str.split(separator, -1);
        }
        return null;
    }

    public static String remove(CharSequence text, CharSequence replacement) {
        if (text != null && replacement != null) {
            return text.toString().replace(replacement, "");
        } else {
            if (replacement == null) {
                return text.toString();
            }
            return null;
        }
    }

}

package com.nuamx.util;

import java.util.List;

public class CommonUtil {

    public static boolean isNullOrEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static String customSubstring(String s, int n) {
        if (s != null) {
            return s.substring(0, Math.min(n, s.length()));
        } else {
            return "Unknown";
        }
    }

}

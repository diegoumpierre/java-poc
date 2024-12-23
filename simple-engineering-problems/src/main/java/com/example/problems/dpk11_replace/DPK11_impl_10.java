package com.example.problems.dpk11_replace;

public class DPK11_impl_10 {
    public static String replace(String input, String token, String newToken) {
        String result = "";
        boolean replace = false;
        for (int i = 0; i < input.length(); i++) {
            for (int j = 0; j < token.length(); j++) {
                replace = true;
                if (i + j < input.length() && input.charAt(i + j) != token.charAt(j)) {
                    replace = false;
                    break;
                }
            }
            if (replace) {
                result += newToken;
                i += token.length() - 1;
            } else {
                result += input.charAt(i);
            }
        }
        return result;
    }
}

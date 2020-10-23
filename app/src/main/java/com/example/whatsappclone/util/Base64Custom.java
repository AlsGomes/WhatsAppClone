package com.example.whatsappclone.util;

import android.util.Base64;

public class Base64Custom {

    public static String encode(String toEncode) {
        String encoded = Base64.encodeToString(toEncode.getBytes(), Base64.DEFAULT).replaceAll("\\n|\\r", "");
        return encoded;
    }
}

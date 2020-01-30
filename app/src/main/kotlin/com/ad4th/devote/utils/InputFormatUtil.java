package com.ad4th.devote.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

public  class InputFormatUtil {

    /**
     * 공백 입력 제한 .
     */
    public static class BlankFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start,
                                   int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[-_a-zA-Z0-9\\w\\s]+$");

            if(source.equals("") || ps.matcher(source).matches()){
                return source;
            }


            return "";
        }
    }
}

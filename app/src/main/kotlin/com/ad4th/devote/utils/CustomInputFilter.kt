package com.ad4th.devote.utils

import android.text.InputFilter

object CustomInputFilter {


    val noSpace: InputFilter = InputFilter { source, start, end, dest, dstart, dend ->
        for (i in start until end) {
            if (Character.isWhitespace(source[i])) {
                return@InputFilter ""
            }
        }
        null
    }
}

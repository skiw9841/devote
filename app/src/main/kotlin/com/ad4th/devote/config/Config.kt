package com.ad4th.devote.config

interface Config {
    val server: Int

    companion object {
        val DEV = 0
        val REAL = 1
    }
}
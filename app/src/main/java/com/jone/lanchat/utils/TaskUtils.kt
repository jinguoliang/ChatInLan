package com.jone.lanchat.utils

fun runUntil(timeout: Int, function: () -> Unit) {
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < timeout) {
        function.invoke()
    }
}
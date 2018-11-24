package com.jone.lanchat.utils

import com.empty.jinux.baselibaray.log.loge

fun runUntil(timeout: Int, function: () -> Unit) {
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < timeout) {
        function.invoke()
        loge("looping", "runUntil")
    }
}
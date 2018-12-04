package com.jone.lanchat.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.getSelectFileIntent(root: Uri? = null): Intent {
    return Intent(Intent.ACTION_GET_CONTENT).apply {
        if (root == null) {
            type = "*/*"
        } else {
            setDataAndType(root, "*/*")
        }
        addCategory(Intent.CATEGORY_OPENABLE)
    }
}
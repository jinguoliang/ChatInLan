package com.jone.lanchat.utils

import android.app.Activity
import android.widget.Toast

fun Activity.showToast(text: String): Unit {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
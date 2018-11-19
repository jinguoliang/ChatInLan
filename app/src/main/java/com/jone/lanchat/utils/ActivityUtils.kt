package com.jone.lanchat.utils

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes

fun Activity.showToast(@StringRes res: Int) {
    Toast.makeText(this, res, Toast.LENGTH_LONG).show()
}
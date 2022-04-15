package com.example.httpsdemo.utils

import android.app.AlertDialog
import android.content.Context

/**
 * 简单的创建一个dialog的工具
 * @author liuhaichao
 */
object DialogUtil {

    fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        buttonText: String,
        callback: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                buttonText
            ) { _, _ ->
                callback()
            }
        builder.create().show()
    }
}
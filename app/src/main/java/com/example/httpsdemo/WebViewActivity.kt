package com.example.httpsdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import com.example.httpsdemo.utils.CheckCertificateWebViewClient
import com.example.httpsdemo.utils.DialogUtil

/**
 * 显示一个网页的activity
 * @author liuhaichao
 */
class WebViewActivity : AppCompatActivity() {

    companion object {
        private const val URL = "url"
        private const val CERTIFICATES = "certificates"
        fun show(context: Context, url: String, @RawRes vararg certificates: Int) {
            Intent(context, WebViewActivity::class.java).apply {
                putExtra(URL, url)
                putExtra(CERTIFICATES, certificates)
                context.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val url: String? = intent.getStringExtra(URL)
        val certificates: IntArray? = intent.getIntArrayExtra(CERTIFICATES)
        if (url.isNullOrEmpty()) {
            DialogUtil.showAlertDialog(this, "警告", "没有传递任何url", "关闭页面") {
                finish()
            }
            return
        }
        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = CheckCertificateWebViewClient(certificates ?: IntArray(0))
        webView.loadUrl(url)
    }

}
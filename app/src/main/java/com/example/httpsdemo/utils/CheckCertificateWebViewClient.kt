package com.example.httpsdemo.utils

import android.app.Activity
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * 检查证书的webViewClient
 * @author liuhaichao
 */
class CheckCertificateWebViewClient(private val certificates: IntArray) : WebViewClient() {
    override fun onReceivedSslError(
        view: WebView,
        handler: SslErrorHandler,
        error: SslError
    ) {
        if (error.primaryError == SslError.SSL_DATE_INVALID // 证书的日期无效
            || error.primaryError == SslError.SSL_EXPIRED // 证书已过期
            || error.primaryError == SslError.SSL_INVALID // 一般错误
            || error.primaryError == SslError.SSL_UNTRUSTED // 证书颁发机构不受信任
        ) {
            when {
                //发生以上4种错误的时候，判断error.certificate的Sha256在本地是否有匹配的证书，如果匹配就验证通过
                !checkMySslCert(view, error, certificates) -> {
                    DialogUtil.showAlertDialog(view.context, "警告", "证书校验失败：没有任何匹配的证书", "关闭页面") {
                        (view.context as Activity).finish()
                    }
                    //handler.cancel 就是让加载的页面白屏，所有导致了如果webView校验证书存在异常，android在默认情况下会显示白屏
                    handler.cancel()
                }
                else -> {
                    //如果证书一致，忽略错误
                    handler.proceed()
                }
            }
        }
    }

    /**
     * 只要有error.certificate的Sha256匹配的证书，就返回true
     */
    private fun checkMySslCert(
        view: WebView,
        error: SslError,
        certificates: IntArray
    ): Boolean {
        for (certificate in certificates) {
            val certSha256: String = SSLSocketCert.getSSLCertSHA256FromCert(
                view.resources.openRawResource(certificate)
            )
            val serverSha256: String =
                SSLSocketCert.getSSLCertFromServer(error.certificate)
            if (certSha256.equals(serverSha256, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

}
package com.example.httpsdemo.trust

import android.annotation.SuppressLint
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * 信任所有证书，只能在debug下使用
 * @author liuhaichao
 */
object TrustAll {

    fun trust(okHttpClientBuilder: OkHttpClient.Builder): OkHttpClient.Builder {
        return okHttpClientBuilder
            .sslSocketFactory(
                createSSLSocketFactory(),
                HttpsTrustAllCerts()
            ).hostnameVerifier(
                TrustAllHostnameVerifier()
            )
    }

    // 信任所有域名的验证器
    private class TrustAllHostnameVerifier : HostnameVerifier {
        override fun verify(s: String, sslSession: SSLSession): Boolean {
            return true
        }
    }

    // SSLSocketFactory 创建器
    private fun createSSLSocketFactory(): SSLSocketFactory {
        val sc = SSLContext.getInstance("TLS")
        sc.init(null, arrayOf<TrustManager>(HttpsTrustAllCerts()), SecureRandom())
        return sc.socketFactory
    }

    /**
     * Implementing a custom X509TrustManager is error-prone and likely to be insecure.
     * It is likely to disable certificate validation altogether,
     * and is non-trivial to implement correctly without calling Android's default implementation
     * 实现自定义 X509TrustManager 容易出错，并且可能不安全。它可能会完全禁用证书验证，并且在不调用Android的默认实现的情况下正确实现并非易事。
     */
    private class HttpsTrustAllCerts : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
        }

        @SuppressLint("TrustAllX509TrustManager")
        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            // 验证服务端证书需要重写该函数
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            //返回长度为0的数组，相当于return null
            return arrayOfNulls(0)
        }
    }
}
package com.example.httpsdemo.trust

import android.content.Context
import androidx.annotation.RawRes
import okhttp3.OkHttpClient
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * 只信任指定证书
 * @author liuhaichao
 */
object TrustCertificate {
    fun trust(
        okHttpClientBuilder: OkHttpClient.Builder,
        context: Context,
        @RawRes vararg rawResIds: Int,
    ): OkHttpClient.Builder {
        // 创建 Keystore 包含我们的证书
        val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        for ((index, rawResId) in rawResIds.withIndex()) {
            // 获取证书输入流
            val openRawResource: InputStream =
                context.applicationContext.resources.openRawResource(rawResId)

            val ca: Certificate =
                CertificateFactory.getInstance("X.509").generateCertificate(openRawResource)
            keyStore.setCertificateEntry(index.toString(), ca)
        }
        // 创建一个 TrustManager 仅把 Keystore 中的证书 作为信任的锚点
        // 建议不要使用自己实现的X509TrustManager，而是使用默认的X509TrustManager
        val trustManagerFactory: TrustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

        trustManagerFactory.init(keyStore)
        // 用 TrustManager 初始化一个 SSLContext
        val sslContext: SSLContext =
            SSLContext.getInstance("TLS")

        sslContext.init(null, trustManagerFactory.trustManagers, SecureRandom())
        return okHttpClientBuilder
            .sslSocketFactory(
                sslContext.socketFactory,
                trustManagerFactory.trustManagers[0] as X509TrustManager
            )
    }

}
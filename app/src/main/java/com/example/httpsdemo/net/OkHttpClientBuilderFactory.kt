package com.example.httpsdemo.net

import android.content.Context
import com.example.httpsdemo.R
import com.example.httpsdemo.trust.TrustAll
import com.example.httpsdemo.trust.TrustCertificate
import okhttp3.OkHttpClient
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * 获取OkHttpClientBuilder示例，可在此基础上继续添加参数
 * @author liuhaichao
 */
object OkHttpClientBuilderFactory {
    fun createOkHttpClientBuilder(type: TYPE, context: Context): OkHttpClient.Builder {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
        when (type) {
            TYPE.TYPE_DEFAULT -> {
                return okHttpClientBuilder.proxy(proxy)
            }
            TYPE.TYPE_TRUST_ALL -> {
                return TrustAll
                    .trust(okHttpClientBuilder)
                    .proxy(proxy)
            }
            TYPE.TYPE_ONLY -> {
                return TrustCertificate
                    .trust(
                        okHttpClientBuilder,
                        context,
                        R.raw.baidu,
                        R.raw.wanandroid
                    )
//                    .proxy(proxy)
            }
        }
    }

    enum class TYPE {
        //默认
        TYPE_DEFAULT,

        //DEBUG模式，信任所有证书
        TYPE_TRUST_ALL,

        //只信任指定证书
        TYPE_ONLY,
    }

    private val proxy = Proxy(
        Proxy.Type.HTTP,
        InetSocketAddress("10.53.180.127", 8888)
    )
}
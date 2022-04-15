package com.example.httpsdemo.net

import com.example.httpsdemo.AppApplication
import okhttp3.OkHttpClient

/**
 * 联网工具类
 * @author liuhaichao
 */
object NetService {

    private lateinit var okHttpClient: OkHttpClient

    fun getCertificateOkHttpClient(type: OkHttpClientBuilderFactory.TYPE): OkHttpClient {
        if (!this::okHttpClient.isInitialized) {
            okHttpClient = OkHttpClientBuilderFactory.createOkHttpClientBuilder(
                type,
                AppApplication.instance
            ).build()
        }
        return okHttpClient
    }

}
package com.example.httpsdemo.net

import android.content.Context
import com.example.httpsdemo.AppApplication
import com.example.httpsdemo.R
import com.example.httpsdemo.trust.TrustAll
import com.example.httpsdemo.trust.TrustCertificate
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * 联网工具类
 * @author liuhaichao
 */
object NetService {

    private lateinit var okHttpClient: OkHttpClient

    fun getCertificateOkHttpClient(type: TYPE): OkHttpClient {
        if (!this::okHttpClient.isInitialized) {
            val okHttpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
            okHttpClient = createOkHttpClientBuilder(
                okHttpClientBuilder,
                type,
                AppApplication.instance
            ).build()
//            okHttpClient = createOkHttpClientBuilderByPublicKey(
//                okHttpClientBuilder,
//            ).build()
        }
        return okHttpClient
    }

    private fun createOkHttpClientBuilder(
        okHttpClientBuilder: OkHttpClient.Builder,
        type: TYPE,
        context: Context
    ): OkHttpClient.Builder {
        when (type) {
            TYPE.TYPE_DEFAULT -> {
                //默认Android 7 或者更高的版本安装代理软件的证书后也不能被抓包，
                //但是Android 6 或者之前的版本安装代理软件的证书后是可以被抓包的
                return okHttpClientBuilder.proxy(proxy)
            }
            TYPE.TYPE_TRUST_ALL -> {
                return TrustAll
                    .trust(okHttpClientBuilder)
                    .proxy(proxy)
                //设置不使用代理后，虽然app所在设备之外的设备无法抓包，但是app所在设备可以安装抓包工具（如Packet Capture）对数据抓包
//                    .proxy(Proxy.NO_PROXY)
            }
            TYPE.TYPE_ONLY -> {
                return TrustCertificate
                    .trust(
                        okHttpClientBuilder,
                        context,
                        R.raw.baidu,
                        R.raw.wanandroid
                    )
                //证书绑定后再设置代理会报错
//                    .proxy(proxy)
            }
        }
    }

    /**
     * caPublicKey随便写，报错的时候异常里有正确的值，然后拷贝过来将caPublicKey替换成正确的值即可
     */
    private fun createOkHttpClientBuilderByPublicKey(
        okHttpClientBuilder: OkHttpClient.Builder
    ): OkHttpClient.Builder {
        val caDomain = "www.baidu.com"
        val caPublicKey = "sha256//Zym5rS1SySuo+9dunLEq90wqQ+PnoI9HWGwyZFK/BM="
        val pinner: CertificatePinner = CertificatePinner.Builder()
            .add(caDomain, caPublicKey)
            .build()
        //android 6 或者之前的设备用代理时，调用baidu链接会报异常，因为charles的公钥和baidu公钥不匹配，
        //但是 6 或者之前的设备用代理时调用wanandroid不会报异常，因为wanandroid没有绑定公钥，并且 6 或者之前的版本默认信任用户证书，也就是charles证书
        //在 7 或者之后的设备用代理时都会报异常
        return okHttpClientBuilder.certificatePinner(pinner)//.proxy(proxy)
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
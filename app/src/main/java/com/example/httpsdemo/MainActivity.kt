package com.example.httpsdemo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.httpsdemo.net.NetService
import com.example.httpsdemo.net.OkHttpClientBuilderFactory
import com.example.httpsdemo.utils.DefaultThreadPool
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class MainActivity : AppCompatActivity() {

    private val handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var okHttpClient: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("API", "${Build.VERSION.SDK_INT}")
        findViewById<Button>(R.id.buttonBaidu).setOnClickListener {
            DefaultThreadPool.execute {
                requestNet("https://www.baidu.com")
            }
        }
        findViewById<Button>(R.id.buttonWanandroid).setOnClickListener {
            DefaultThreadPool.execute {
                requestNet("https://www.wanandroid.com")
            }
        }
        findViewById<Button>(R.id.buttonWebView).setOnClickListener {
            WebViewActivity.show(
                this, "https://www.baidu.com",
//                R.raw.baidu,
//                R.raw.wanandroid
            )
        }
        //创建OkHttpClient对象
        okHttpClient =
            NetService.getCertificateOkHttpClient(OkHttpClientBuilderFactory.TYPE.TYPE_ONLY)
    }

    override fun onDestroy() {
        DefaultThreadPool.shutdown()
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    private fun requestNet(url: String) {
        try {
            val requestBuilder: Request.Builder = Request.Builder()
                .url(url)
            val request: Request = requestBuilder.build()
            val response: Response?
            //得到Response 对象
            response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val code = response.code
                val message = response.message
                val body = response.body!!.string()
                handler.post {
                    findViewById<TextView>(R.id.tvResult).text =
                        """
response.code=$code
message=$message
body=$body
"""
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            handler.post {
                findViewById<TextView>(R.id.tvResult).text = e.message
            }
        }
    }
}
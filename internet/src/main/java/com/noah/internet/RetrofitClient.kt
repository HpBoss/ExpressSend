package com.noah.internet

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @Auther: 何飘
 * @Date: 2/6/21 15:21
 * @Description:
 */
class RetrofitClient {
    private val mRetrofit: Retrofit
    val service: RequestApi
        get() = mRetrofit.create(RequestApi::class.java)

    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)

        mRetrofit = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .client(builder.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        //双重检查模式
        @Volatile
        private var ourInstance: RetrofitClient? = null
        @JvmStatic
        val instance: RetrofitClient?
            get() {
                if (ourInstance == null) {
                    synchronized(RetrofitClient::class.java) {
                        if (ourInstance == null) {
                            ourInstance = RetrofitClient()
                        }
                    }
                }
                return ourInstance
            }

        @JvmStatic
        fun <T> applySchedulers(observer: Observer<T>): ObservableTransformer<T, T> {
            return ObservableTransformer { upstream: Observable<T> ->
                val observable = upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                observable.subscribe(observer)
                observable
            }
        }
    }

}
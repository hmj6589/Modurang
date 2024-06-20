package com.example.projectapplication

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://apis.data.go.kr/B551011/KorWithService1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    private val parser = TikXml.Builder()
        .exceptionOnUnreadXml(false)
        .build()

    private val xmlRetrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(TikXmlConverterFactory.create(parser))
            .build()

    val xmlNetworkService: ApiService by lazy {
        xmlRetrofit.create(ApiService::class.java)
    }
}










// commonInforFragememt에 데이터 안떠서 수정할 예정 잘못하면 여기로 돌아오렴
//object RetrofitClient {
//    private const val BASE_URL = "https://apis.data.go.kr/B551011/KorWithService1/"
//
//    private val loggingInterceptor = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY
//    }
//
//    private val httpClient = OkHttpClient.Builder()
//        .addInterceptor(loggingInterceptor)
//        .build()
//
//    private val gson = GsonBuilder()
//        .setLenient()
//        .create()
//
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .client(httpClient)
//        .addConverterFactory(ScalarsConverterFactory.create()) // Add ScalarsConverterFactory to handle raw string responses
//        .addConverterFactory(SimpleXmlConverterFactory.createNonStrict()) // Add XML converter
//        .addConverterFactory(GsonConverterFactory.create(gson)) // Add JSON converter
//        .build()
//
//    val apiService: TourApiService by lazy {
//        retrofit.create(TourApiService::class.java)
//    }
//}



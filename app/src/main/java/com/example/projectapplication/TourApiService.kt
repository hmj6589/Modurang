package com.example.projectapplication

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("searchKeyword1")
    fun getTourInfo(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("serviceKey") serviceKey: String,
        @Query("_type") type: String,
        @Query("listYN") listYN: String,
        @Query("keyword") keyword: String
    ): Call<ApiResponse>
}
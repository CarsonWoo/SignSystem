package com.carson.signsystem.home

import com.carson.signsystem.home.model.StaffData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeService  {
    @GET("/employee_info")
    fun getInfo(@Query("job_number") job_number: String?) : Call<StaffData>
}
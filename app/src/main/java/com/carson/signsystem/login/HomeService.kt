package com.carson.signsystem.login

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface HomeService {
    // 管理员或员工登录
    @FormUrlEncoded
    @POST("/{url}")
    fun employerLogin(@Path("url") url: String,
                      @Field("job_number") username: String,
                      @Field("password") password: String)


}
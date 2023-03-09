package com.example.digiboxxdemo.di

import com.example.digiboxxdemo.Constant.BASE_URL
import com.example.digiboxxdemo.retrofit.ApiCall
import com.example.digiboxxdemo.retrofit.Client
import com.example.digiboxxdemo.retrofit.MyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getRetroInstance(apiCall: ApiCall): MyApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(apiCall.httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyApi::class.java)
    }

    @Singleton
    @Provides
    fun getRetroInstance1(apiCall: ApiCall): Client {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()
            .create(Client::class.java)
    }
}
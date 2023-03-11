package com.example.onlineshop.di

import android.content.Context
import com.example.onlineshop.data.network.ProductsApiService
import com.example.onlineshop.data.user.repository.UserRepositoryImpl
import com.example.onlineshop.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserRepository():UserRepository = UserRepositoryImpl()
}
package com.example.onlineshop.di

import com.example.onlineshop.data.di.DataModule
import com.example.onlineshop.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideUserRepository():UserRepository = DataModule.provideUserRepository()
}
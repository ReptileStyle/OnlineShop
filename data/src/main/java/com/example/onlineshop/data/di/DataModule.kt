package com.example.onlineshop.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        Firebase.firestore.firestoreSettings = firestoreSettings { isPersistenceEnabled = false }
        return Firebase.firestore
    }

    @Provides
    fun provideFirebaseStorage() = Firebase.storage

    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Singleton
    @Provides
    fun provideUserRepository(): com.example.onlineshop.domain.repository.UserRepository = com.example.onlineshop.data.user.repository.UserRepositoryImpl(
        firebaseAuth = provideFirebaseAuth(),
        firebaseStorage = provideFirebaseStorage(),
        firestore = provideFirebaseFirestore()
    )

}
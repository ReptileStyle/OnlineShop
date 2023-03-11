package com.example.onlineshop.domain.repository

import android.app.Instrumentation
import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResult
import com.example.onlineshop.domain.model.ProfileData
import com.example.onlineshop.domain.model.Response
import com.example.onlineshop.domain.model.User
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createUser(email: String, password: String, firstname:String, lastname:String): Flow<Response<FirebaseUser>>

    suspend fun loginWithEmailAndPassword(email: String, password: String): Flow<Response<FirebaseUser>>

    suspend fun isUsernameExist(username:String): Boolean

    suspend fun getEmailByUsername(username:String): String

    suspend fun loginWithFirstname(firstname: String): Flow<Response<FirebaseUser>>

   // suspend fun addUserToFirestore(uid: String?, user: User): Response<Boolean>

    fun logout()

    fun uploadUserProfilePhoto(uri: Uri?,): Flow<Response<Boolean>>

    fun <T> updateUserData(field: String, data: T): Response<Boolean>

    suspend fun getProfileData(): Response<ProfileData>

    suspend fun signInWithGoogle(context: Context):Flow<Response<BeginSignInResult>>
    suspend fun processGoogleSignInResult(result: ActivityResult, context: Context):Flow<Response<Boolean>>

    fun isLoggedIn():Boolean
}
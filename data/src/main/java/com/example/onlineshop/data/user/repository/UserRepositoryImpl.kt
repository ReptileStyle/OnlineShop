package com.example.onlineshop.data.user.repository

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.data.R
import com.example.onlineshop.core.Constants
import com.example.onlineshop.domain.model.InternetNotAvailableException
import com.example.onlineshop.domain.model.ProfileData
import com.example.onlineshop.domain.model.Response
import com.example.onlineshop.domain.model.User
import com.example.onlineshop.domain.repository.UserRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserRepositoryImpl @Inject constructor() : com.example.onlineshop.domain.repository.UserRepository {


    private val firebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore
    private val firebaseStorage = Firebase.storage
    init {
        Firebase.firestore.firestoreSettings = firestoreSettings { isPersistenceEnabled = false }
    }

    private var profileData: com.example.onlineshop.domain.model.ProfileData? = null


    override suspend fun createUser(
        email: String,
        password: String,
        firstname: String,
        lastname: String
    ): Flow<com.example.onlineshop.domain.model.Response<FirebaseUser>> {
        return flow {
            emit(com.example.onlineshop.domain.model.Response.Loading)
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                addUserToFirestore(
                    result.user?.uid,
                    com.example.onlineshop.domain.model.User(
                        email = email,
                        firstname = firstname,
                        lastname = lastname
                    )
                )
                emit(com.example.onlineshop.domain.model.Response.Success(result.user))
            } catch (e: FirebaseException) {
                e.printStackTrace()
                emit(com.example.onlineshop.domain.model.Response.Failure(e))
            }
        }
    }

    override suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<com.example.onlineshop.domain.model.Response<FirebaseUser>> {
        return flow {
            emit(com.example.onlineshop.domain.model.Response.Loading)
            try {
                val result =
                    firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
                emit(com.example.onlineshop.domain.model.Response.Success(result.user))
            } catch (e: Exception) {
                emit(com.example.onlineshop.domain.model.Response.Failure(e))
            }
        }
    }

    override suspend fun isUsernameExist(username: String): Boolean {
        val result = firestore
            .collection("users")
            .whereEqualTo("firstname", username)
            .get()
            .await()
        if (result.metadata.isFromCache) throw com.example.onlineshop.domain.model.InternetNotAvailableException(
            "internet is not available"
        )
        return !result.isEmpty
    }

    override suspend fun getEmailByUsername(username: String): String {
        val result = firestore
            .collection("users")
            .whereEqualTo("firstname", username)
            .get()
            .await()
        if (result.metadata.isFromCache) throw Exception("internet is not available")
        if (result.isEmpty) return ""
        return result.documents.first().get("email").toString()
    }

    override suspend fun loginWithFirstname(firstname: String): Flow<com.example.onlineshop.domain.model.Response<FirebaseUser>> {
        return flow {
            emit(com.example.onlineshop.domain.model.Response.Loading)
            if (!isUsernameExist(firstname)) {
                emit(com.example.onlineshop.domain.model.Response.Failure(Exception("First name does not exist")))
                return@flow
            }
            try {
                val result =
                    firebaseAuth.signInWithEmailAndPassword(getEmailByUsername(firstname), "111111")
                        .await()
                emit(com.example.onlineshop.domain.model.Response.Success(result.user))
            } catch (e: Exception) {
                emit(com.example.onlineshop.domain.model.Response.Failure(e))
            }
        }
    }

    suspend fun addUserToFirestore(uid: String?, user: com.example.onlineshop.domain.model.User): com.example.onlineshop.domain.model.Response<Boolean> {
        return try {
            uid?.let { uid ->
                firestore.collection("users").document(uid).set(
                    mapOf(
                        "firstname" to user.firstname,
                        "lastname" to user.lastname,
                        "email" to user.email,
                    ),
                    SetOptions.merge()
                ).addOnSuccessListener { result ->
                    Log.d("AddingUserToFirebase", "User successfully added to Firebase: $result")
                }.addOnFailureListener { e ->
                    com.example.onlineshop.domain.model.Response.Failure(e)
                    Log.d("AddingUserToFirebase", "User addition failed: ${e.message}")
                    return@addOnFailureListener
                }.await()
            }
            com.example.onlineshop.domain.model.Response.Success(true)
        } catch (e: FirebaseFirestoreException) {
            com.example.onlineshop.domain.model.Response.Failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
        profileData = null
    }

    override fun uploadUserProfilePhoto(uri: Uri?): Flow<com.example.onlineshop.domain.model.Response<Boolean>> {
        return flow {
            emit(com.example.onlineshop.domain.model.Response.Loading)
            if (firebaseAuth.uid == null) {
                emit(com.example.onlineshop.domain.model.Response.Failure(NullPointerException("uid is null")))
                return@flow
            }
            if (uri == null) {
                emit(com.example.onlineshop.domain.model.Response.Failure(NullPointerException("URI on a null reference")))
                return@flow
            }
            try {
                val profilePicsRef =
                    firebaseStorage.getReference("profile_pics/${firebaseAuth.uid}")
                profilePicsRef.putFile(uri).await()
                val downloadUri = profilePicsRef.downloadUrl.await()
                updateUserData("profilePhotoUrl", downloadUri)
                emit(com.example.onlineshop.domain.model.Response.Success(true))
            } catch (e: Exception) {
                emit(com.example.onlineshop.domain.model.Response.Failure(e))
            }
        }
    }

    override fun <T> updateUserData(field: String, data: T): com.example.onlineshop.domain.model.Response<Boolean> {
        return try {
            val uid = firebaseAuth.uid
            uid?.let {
                firestore.collection("users").document(uid).update(field, data)
            }
            com.example.onlineshop.domain.model.Response.Success(true)
        } catch (e: Exception) {
            Log.d("Updating users $field data", e.message.toString())
            com.example.onlineshop.domain.model.Response.Failure(e)
        }
    }

    override suspend fun getProfileData(): com.example.onlineshop.domain.model.Response<com.example.onlineshop.domain.model.ProfileData> {
        if (profileData != null) return com.example.onlineshop.domain.model.Response.Success(profileData)
        else {
            try {
                var profileImage: Bitmap? = null
                var username = ""
                val profileImageJob =
                    CoroutineScope(Dispatchers.IO).launch { profileImage = getProfileImage() }
                val userJob = CoroutineScope(Dispatchers.IO).launch {
                    val user =
                        firestore.collection("users").document("${firebaseAuth.uid}").get().await()
                    username = "${user.get("firstname")} ${user.get("lastname")}"
                }
                profileImageJob.join()
                userJob.join()
                profileData = com.example.onlineshop.domain.model.ProfileData(
                    imageBitmap = profileImage,
                    username = username
                )
                return com.example.onlineshop.domain.model.Response.Success(
                    profileData
                )
            } catch (e: Exception) {
                return com.example.onlineshop.domain.model.Response.Failure(e)
            }
        }
    }

    suspend fun getProfileImage(): Bitmap? {
        try {
            val imageBytes =
                firebaseStorage.getReference("profile_pics/${firebaseAuth.uid}")
                    .getBytes(1024 * 1024*1024).await()
            Log.d("ChatViewModel", "try block")
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            Log.d("ChatViewModel", "${e.message}")
            return null
        }
    }

    override suspend fun signInWithGoogle(context: Context):Flow<Response<BeginSignInResult>>{
        return flow{
            emit(Response.Loading)
            try {
                val oneTapClient = Identity.getSignInClient(context)
                val signInRequest = BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setServerClientId(Constants.WEB_CLIENT_ID)
                            .setFilterByAuthorizedAccounts(false)
                            .build())
                    .build()

                emit(Response.Success(oneTapClient.beginSignIn(signInRequest).await()))
            }catch (e:Exception){
                emit(Response.Failure(e))
            }
        }
    }

    override suspend fun processGoogleSignInResult(result: ActivityResult, context: Context):Flow<Response<Boolean>>{
        return flow {
            try {
                emit(Response.Loading)
                if (result.resultCode == Activity.RESULT_OK) {
                    val credentials =
                        Identity.getSignInClient(context).getSignInCredentialFromIntent(result.data)
                    val googleId = credentials.googleIdToken
                    val googleCredentials = GoogleAuthProvider.getCredential(googleId, null)
                    val authResult = firebaseAuth.signInWithCredential(googleCredentials).await()
                    val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
                    if (isNewUser) {
                        addUserToFirestore()
                    }
                    emit(Response.Success(true))
                }
            }catch (e:Exception){
                emit(Response.Failure(e))
            }
        }
    }

    private suspend fun addUserToFirestore() {
        firebaseAuth.currentUser?.apply {
            val user = toUser()
            firestore.collection("users").document(uid).set(user).await()
        }
    }

    override fun isLoggedIn(): Boolean {
        return firebaseAuth.uid!=null
    }
}
fun FirebaseUser.toUser() = mapOf(
    "firstname" to (displayName?.split(' ')?.get(0) ?: "empty"),
    "lastname" to (displayName?.split(' ')?.get(1) ?: "empty"),
    "email" to email,
    "profilePhotoUrl" to photoUrl?.toString(),
    "created_at" to FieldValue.serverTimestamp()
)
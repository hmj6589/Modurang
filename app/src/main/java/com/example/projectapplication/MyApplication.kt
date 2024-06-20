package com.example.projectapplication

import androidx.multidex.MultiDexApplication
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class MyApplication : MultiDexApplication() {

    companion object{
        lateinit var auth: FirebaseAuth

        // 이 auth를 통해서 현재 입력된 로그인 (이메일) 에 대한 string 을 하나 가지고 있을 필요가 있음
        var email: String? = null

        var name: String? = null

        lateinit var db: FirebaseFirestore
        lateinit var storage: FirebaseStorage

        fun checkAuth(): Boolean {
            if (!::auth.isInitialized) {
                initializeAuth()
            }
            if (!::db.isInitialized) {
                db = FirebaseFirestore.getInstance()
            }
            val currentUser = auth.currentUser

            return if (currentUser != null) {// 유효한 user라면
                // 그 유저의 이메일을 내가 로그인한 이메일의 유저로 받아들이겠다
                email = currentUser.email

                fetchUserName(currentUser.uid)

                currentUser.isEmailVerified
                // 그리고 그 유저에 대한 verified 되어져 있다라는 상태로 반환하겠다
            } else {
                false
            }
        }

        private fun initializeAuth() {
            auth = Firebase.auth
        }

        private fun fetchUserName(uid: String){
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if(document != null){
                        name=document.getString("name")
                    }
                }
                .addOnFailureListener{ exception ->
                    // 실패 시 처리
                }
        }
    }

    override fun onCreate() {
        super.onCreate()
        initializeAuth()
        db = FirebaseFirestore.getInstance() // 초기화
        storage = Firebase.storage // 초기화
    }
}
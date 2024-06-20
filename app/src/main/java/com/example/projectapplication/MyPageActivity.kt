package com.example.projectapplication

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectapplication.databinding.ActivityMyPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import android.util.Log
import androidx.preference.PreferenceManager

class MyPageActivity : AppCompatActivity() {
    // 뷰 바인딩 전역 변수로 설정
    private lateinit var binding: ActivityMyPageBinding
    private lateinit var currentUserEmail: String


    private lateinit var sharedPreference: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 사용자 이름을 표시하는 TextView 설정
        val name = intent.getStringExtra("name")
        binding.mypageName.text = name ?: "사용자 이름이 없습니다."

        // FirebaseAuth에서 현재 로그인한 사용자 이메일 가져오기
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUserEmail = currentUser?.email ?: ""


        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)

        // 설정된 이미지 가져오기
        val selectedImageName = sharedPreference.getString("user_image", "pororo") ?: "pororo"
        val resourceId = getResourceIdByName(selectedImageName)
        if (resourceId != 0) {
            binding.mypageImage.setImageResource(resourceId)
        } else {
            Log.e("MyPageActivity", "Resource ID not found for $selectedImageName")
        }
    }
    private fun getResourceIdByName(resourceName: String): Int {
        return resources.getIdentifier(resourceName, "drawable", packageName)
    }

    override fun onStart() {
        super.onStart()

        try {
            if (MyApplication.checkAuth()) {
                Log.d("MyPageActivity", "FirebaseAuth 인증 성공")
                MyApplication.db.collection("comments")
                    .whereEqualTo("email", currentUserEmail)
                    .orderBy("date_time", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { result ->
                        Log.d("MyPageActivity", "Firestore 쿼리 성공: ${result.size()}개의 문서")
                        val itemList = mutableListOf<ItemData>()
                        for (document in result) {
                            val item = document.toObject(ItemData::class.java)
                            item.docId = document.id
                            itemList.add(item)
                        }
                        binding.mypageRecyclerView.layoutManager = LinearLayoutManager(this)
                        binding.mypageRecyclerView.adapter = BoardAdapter(this, itemList)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MyPageActivity", "서버 데이터 획득 실패", exception)
                        Toast.makeText(this, "서버 데이터 획득 실패", Toast.LENGTH_LONG).show()
                    }
            }
        } catch (e: Exception) {
            Log.e("MyPageActivity", "에러 발생", e)
            Toast.makeText(this, "에러 발생: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}

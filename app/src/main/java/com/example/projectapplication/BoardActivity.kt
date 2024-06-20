package com.example.projectapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectapplication.databinding.ActivityBoardBinding
import com.google.firebase.firestore.Query

class BoardActivity : AppCompatActivity() {
    // 뷰 바인딩 먼저 해주기 (이번엔 전역변수로 해줄게)
    lateinit var binding : ActivityBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // activity_board.xml에서 추가하는 버튼 그거 누르면
        binding.mainFab.setOnClickListener {
            try {
                if(MyApplication.checkAuth()){
                    startActivity(Intent(this, AddActivity::class.java))
                } else {
                    Toast.makeText(this, "인증을 먼저 진행해주세요.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "에러 발생: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        try {
            if(MyApplication.checkAuth()){
                MyApplication.db.collection("comments") // firestore에서 컬렉션 이름 동일하게 작성해야함
                    .orderBy("date_time", Query.Direction.DESCENDING) // date_time(최신순)으로 정렬
                    .get()
                    .addOnSuccessListener { result ->
                        val itemList = mutableListOf<ItemData>()
                        for (document in result) {
                            // 하나씩 itemList에 넣을 거야
                            val item = document.toObject(ItemData::class.java)

                            // id 저장은 별도로 해야힘
                            item.docId = document.id
                            itemList.add(item)
                        }
                        binding.recyclerView.layoutManager = LinearLayoutManager(this)
                        binding.recyclerView.adapter = BoardAdapter(this, itemList)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "서버 데이터 획득 실패", Toast.LENGTH_LONG).show()
                    }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "에러 발생: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}
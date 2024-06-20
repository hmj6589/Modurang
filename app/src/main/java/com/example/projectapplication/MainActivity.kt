package com.example.projectapplication

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat.startActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectapplication.databinding.ActivityMainBinding
import com.example.projectapplication.databinding.ActivityMyPageBinding
import com.example.projectapplication.databinding.ActivityVideoBinding
import com.google.android.material.navigation.NavigationView

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var headerView: View
    lateinit var sharedPreference: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TourInfoAdapter
    private val tourInfoList: MutableList<XmlItem> = mutableListOf()
    lateinit var myPageBinding: ActivityMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)

        val color = sharedPreference.getString("color", "#00ff00") // 배경 색깔 -> 디폴트
        binding.btnSearch.setBackgroundColor(Color.parseColor(color)) // 검색 버튼 배경 색상 설정

        val size = sharedPreference.getString("size", "16.0f") // 글씨 크기 -> 디폴트
        binding.edtName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size!!.toFloat()) // 검색창 글씨 크기 사이즈 설정



        toggle = ActionBarDrawerToggle(
            this,
            binding.drawer,
            R.string.drawer_opened,
            R.string.drawer_closed
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        binding.mainDrawerView.setNavigationItemSelectedListener(this)

        headerView = binding.mainDrawerView.getHeaderView(0)
        val button = headerView.findViewById<Button>(R.id.btnAuth)
        button.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            if (button.text == "로그인") {
                intent.putExtra("status", "logout")
            } else if (button.text == "로그아웃") {
                intent.putExtra("status", "login")
            }
            startActivity(intent)
            binding.drawer.closeDrawers()
        }

        recyclerView = findViewById(R.id.xmlRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TourInfoAdapter(tourInfoList)
        recyclerView.adapter = adapter

        val edtName = findViewById<EditText>(R.id.edtName)
        val btnSearch = findViewById<Button>(R.id.btnSearch)

        btnSearch.setOnClickListener {
            val keyword = edtName.text.toString()
            if (keyword.isNotEmpty()) {
                searchTourInfo(keyword)
            } else {
                Toast.makeText(this, "여행지를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchTourInfo(keyword: String) {
        val encodedKeyword = binding.edtName.text.toString()
        Log.d("MainActivity", "Encoded keyword: $encodedKeyword")

        RetrofitClient.xmlNetworkService.getTourInfo(
            numOfRows = 10,
            pageNo = 1,
            mobileOS = "AND",
            mobileApp = "ProjectApp",
            serviceKey = "O3hl/g6Dtviy+FkpasNjvSNu29/InnSiaEpLCXwtw8pSxQdlIiCoVyblcsV3zeckJK3lECAqcJrCE0YO7imieg==",  // 디코딩된 서비스 키 사용
            type = "xml",
            listYN = "Y",
            keyword = encodedKeyword
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        Log.d("MainActivity", "Response : $apiResponse")
                        tourInfoList.clear()
                        apiResponse.body?.items?.item?.let { tourInfoList.addAll(it) }
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.e("MainActivity", "Response body is null")
                        Toast.makeText(this@MainActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("MainActivity", "API 호출 실패: $errorBody")
                    Toast.makeText(this@MainActivity, "API 호출 실패: $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("MainActivity", "네트워크 에러", t)
                Toast.makeText(this@MainActivity, "네트워크 에러: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_page -> {
                val intent = Intent(this, MyPageActivity::class.java)
                MyApplication.auth.currentUser?.let { user ->
                    val email = user.email
                    MyApplication.db.collection("users").document(user.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            val name = document.getString("name") ?: email
                            intent.putExtra("name", name)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Log.e("MainActivity", "Firestore에서 데이터 가져오기 실패", e)
                            intent.putExtra("name", email)
                            startActivity(intent)
                        }
                }
                true
            }
            R.id.item_board -> {
                val intent = Intent(this, BoardActivity::class.java)
                startActivity(intent)
                binding.drawer.closeDrawers()
                true
            }
            R.id.item_video -> {
                val intent = Intent(this, VideoActivity::class.java)
                startActivity(intent)
                binding.drawer.closeDrawers()
                true
            }
            R.id.map -> {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
                binding.drawer.closeDrawers()
                true
            }
            R.id.todo -> {
                val intent = Intent(this, TodoActivity::class.java)
                startActivity(intent)
                binding.drawer.closeDrawers()
                true
            }
            R.id.item_setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                binding.drawer.closeDrawers()
                true
            }
        }
        return false
    }

    override fun onStart(){
        super.onStart()

        // 로그인 하고 돌아왔을 때 버튼 자체가 로그아웃으로 바뀌어있으면 좋겠을 때 !
        val button = headerView.findViewById<Button>(R.id.btnAuth)
        val tv = headerView.findViewById<TextView>(R.id.tvID)

        //val mypage_tv = headerView.findViewById<TextView>(R.id.mypage_name) // 마이페이지에서 이름 보이게

        if (MyApplication.checkAuth()) {
            MyApplication.auth.currentUser?.let { user ->
                // 사용자가 로그인한 경우
                val email = user.email

                // Firestore에서 사용자 데이터 문서 가져오기
                MyApplication.db.collection("users").document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Firestore에 사용자 데이터가 있는 경우
                            val name = document.getString("name")
                            if (name != null) {
                                // 이름이 있을 경우 처리
                                button.text = "로그아웃"
                                tv.text = "$name 님\n반갑습니다"


                            } else {
                                // 이름이 Firestore에 저장되지 않은 경우
                                button.text = "로그아웃"
                                tv.text = "사용자 이름이 없습니다."

                            }
                        } else {
                            // Firestore에 사용자 데이터가 없는 경우
                            button.text = "로그아웃"
                            if (email != null) {
                                tv.text = "$email 님\n반갑습니다"


                            } else {
                                tv.text = "이메일이 없습니다."
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        // Firestore에서 데이터 가져오기 실패 처리
                        Log.e("AuthActivity", "Firestore에서 데이터 가져오기 실패", e)
                        tv.text = "사용자 데이터를 가져오는 중 오류가 발생했습니다."
                    }
            }
        } else {
            // 사용자가 로그인하지 않은 경우
            button.text = "로그인"
            tv.text = "로그인되지 않았습니다."
        }
    }

    override fun onResume() {
        super.onResume()
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)

        // 검색 버튼 -> 배경 바꾸기
        val color = sharedPreference.getString("color", "#00ff00") // 배경 색깔 -> 디폴트
        binding.btnSearch.setBackgroundColor(Color.parseColor(color)) // 검색 버튼 배경 색상 설정

        // 검색창 글씨 -> 크기 바꾸기
        val size = sharedPreference.getString("size", "16.0f") // 글씨 크기 -> 디폴트
        binding.edtName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size!!.toFloat()) // 검색창 글씨 크기 사이즈 설정

        // 검색창 글씨 -> 스타일 바꾸기
        val textStyle = sharedPreference.getString("text_style", "normal")
        val edtName = findViewById<EditText>(R.id.edtName)
        when (textStyle) {
            "bold" -> edtName.setTypeface(null, Typeface.BOLD)
            "italic" -> edtName.setTypeface(null, Typeface.ITALIC)
            else -> edtName.setTypeface(null, Typeface.NORMAL)
        }


        // 사용자 이미지 -> 바꾸기
        val selectedImageName = sharedPreference.getString("user_image", "pororo") ?: "pororo"
        val resourceId = getResourceIdByName(selectedImageName)
        if (resourceId != 0) {
            // 네비게이션 헤더 뷰에서 이미지 뷰 찾기
            val headerView = binding.mainDrawerView.getHeaderView(0)
            val userImageView = headerView.findViewById<ImageView>(R.id.user_image)
            userImageView.setImageResource(resourceId)
            Log.d("MainActivity", "Set user image resource: $selectedImageName")
        } else {
            Log.e("MainActivity", "Resource ID not found for $selectedImageName")
        }
    }

    private fun getResourceIdByName(resourceName: String): Int {
        return resources.getIdentifier(resourceName, "drawable", packageName)
    }
}
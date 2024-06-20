package com.example.projectapplication

import android.content.Intent
import android.health.connect.datatypes.units.Length
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.projectapplication.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 버튼 클릭 시 웹사이트로 이동
        binding.videoWhere1.setOnClickListener {
            Toast.makeText(this, "한국관광공사TV 유튜브 페이지로 이동", Toast.LENGTH_LONG).show()

            val url = "https://www.youtube.com/@KTOkorea" // 한국관광공사TV 유튜브 URL로 변경
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        binding.videoWhere2.setOnClickListener {
            Toast.makeText(this, "문화체육관광부 유튜브 페이지로 이동", Toast.LENGTH_LONG).show()

            val url = "https://www.youtube.com/@%EB%AC%B8%ED%99%94%EC%B2%B4%EC%9C%A1%EA%B4%80%EA%B4%91%EB%B6%80" // 문화체육관광부 유튜브 URL로 변경
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        binding.videoWhere3.setOnClickListener {
            Toast.makeText(this, "문화산책 유튜브 페이지로 이동", Toast.LENGTH_LONG).show()

            val url = "https://www.youtube.com/@SPACESANCHECK" // 문화산책 유튜브 URL로 변경
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
}

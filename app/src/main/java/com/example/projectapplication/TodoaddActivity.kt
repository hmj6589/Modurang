package com.example.projectapplication

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectapplication.databinding.ActivityTodoaddBinding
import java.io.File
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat

class TodoaddActivity : AppCompatActivity() {
    lateinit var binding: ActivityTodoaddBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTodoaddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var date = intent.getStringExtra("today")
        binding.date.text = date

        binding.btnSave.setOnClickListener {
            // 저장하기 위한 변수 선언
            val edt_srt = binding.addEditView.text.toString()


            val intent = intent
            intent.putExtra("result", edt_srt)
            setResult(Activity.RESULT_OK, intent)


            // db에 저장하기

            // 1. db 불러오기
            val db = DBHelper(this).writableDatabase

            // 2. edt_srt값을 table에 넣기
            db.execSQL("insert into todo_tb (todo) values (?) ", arrayOf<String>(edt_srt))

            // 3. 테이블 닫기
            db.close()



            // 파일 저장하기
            val dataFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss") // 년 월 일 시 분 초

            // 파일을 저장하기 위해서는 포인터가 필요
            // filesDir -> 내장 공간에 대한 폴더 제공
            val file = File(filesDir, "test.txt")

            // 파일을 쓰기 위해서는 write
            val writestream: OutputStreamWriter = file.writer()

            // 파일에 대한 writestream 가져와서 글 적겠다 현재 시간
            writestream.write(dataFormat.format(System.currentTimeMillis()))

            // 최종적으로 flush 해줘야 적힘
            writestream.flush()



            finish()
            true
        }
    } // onCreate()

}
package com.example.projectapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectapplication.databinding.ActivityTodoBinding
import java.io.BufferedReader
import java.io.File
import java.text.SimpleDateFormat

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import java.util.Locale

class TodoActivity : AppCompatActivity() {
    lateinit var binding: ActivityTodoBinding
    lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val datas = mutableListOf<String>()
        val db = DBHelper(this).readableDatabase
        val cursor = db.rawQuery("select * from todo_tb", null)

        while (cursor.moveToNext()) {
            datas.add(cursor.getString(1))
        }

        cursor.close()
        db.close()

        adapter = MyAdapter(datas, object : MyAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                showDeleteDialog(position)
            }
        })

        binding.recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        // 앱을 시작할 때 마지막 저장 시간을 읽어와 표시
        val file = File(filesDir, "test.txt")
        if (file.exists()) {
            val readStream: BufferedReader = file.reader().buffered()
            val lastSavedTime = readStream.readLine()
            binding.lastsaved.text = "마지막 저장 시간 : $lastSavedTime"
            readStream.close()
        }

        val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.getStringExtra("result")?.let { result ->
                if (result.isNotEmpty()) {
                    datas.add(result)
                    adapter.notifyDataSetChanged()

                    // 현재 시간 저장
                    val currentTime = System.currentTimeMillis()
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val formattedTime = dateFormat.format(currentTime)

                    // 파일에 시간 저장
                    try {
                        val writer = BufferedWriter(FileWriter(file))
                        writer.write(formattedTime)
                        writer.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    // 저장된 시간 읽기
                    val readStream: BufferedReader = file.reader().buffered()
                    binding.lastsaved.text = "마지막 저장 시간 : " + readStream.readLine()
                    readStream.close()
                }
            }
        }

        binding.mainFab.setOnClickListener {
            val intent = Intent(this, TodoaddActivity::class.java)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            intent.putExtra("today", dateFormat.format(System.currentTimeMillis()))
            requestLauncher.launch(intent)
        }
    }

    private fun showDeleteDialog(position: Int) {
        val item = adapter.datas?.get(position)
        AlertDialog.Builder(this)
            .setTitle("삭제 확인")
            .setMessage("$item 항목을 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                deleteItem(position)
            }
            .setNegativeButton("아니요", null)
            .show()
    }

    private fun deleteItem(position: Int) {
        val item = adapter.datas?.get(position)

        // DB에서 삭제
        val db = DBHelper(this).writableDatabase
        db.execSQL("DELETE FROM todo_tb WHERE todo = ?", arrayOf(item))
        db.close()

        // 리스트에서 삭제하고 어댑터 갱신
        adapter.datas?.removeAt(position)
        adapter.notifyItemRemoved(position)
    }
}
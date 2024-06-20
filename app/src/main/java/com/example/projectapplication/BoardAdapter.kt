package com.example.projectapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
//import com.example.projectapplication.databinding.ItemCommentBinding
import com.example.projectapplication.ItemData
import com.example.projectapplication.databinding.ItemCommentBinding

class BoardViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

class BoardAdapter (val context: Context, val itemList: MutableList<ItemData>) : RecyclerView.Adapter<BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BoardViewHolder(ItemCommentBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val data = itemList[position]

        holder.binding.run {
            idTextView.text = data.email
            dateTextView.text = data.date_time
            contentsTextView.text = data.comments
            ratingBar.rating = data.stars.toFloat()
        }

        try {
            val imageRef = MyApplication.storage.reference.child("images/${data.docId}.jpg")
            Log.d("이미지 파일 접근", "이미지 파일 접근")

            imageRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    holder.binding.itemImageView.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(task.result)
                        .into(holder.binding.itemImageView)
                } else {
                    holder.binding.itemImageView.visibility = View.GONE
                }
            }.addOnFailureListener {
                holder.binding.itemImageView.visibility = View.GONE
                Log.e("이미지 로드 실패", "이미지 로드 실패", it)
            }
        } catch (e: Exception) {
            holder.binding.itemImageView.visibility = View.GONE
            Log.e("이미지 로드 예외", "이미지 로드 예외", e)
        }
    }
}
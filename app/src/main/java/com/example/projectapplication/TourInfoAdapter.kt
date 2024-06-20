package com.example.projectapplication

import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectapplication.databinding.ItemTourInfoBinding

class TourInfoAdapter(private val tourInfoList: List<XmlItem>) :
    RecyclerView.Adapter<TourInfoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.item_title)
        val address: TextView = view.findViewById(R.id.item_address)
        val image: ImageView = view.findViewById(R.id.item_image)
        val phone: TextView = view.findViewById(R.id.item_phone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tour_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val imageUrl = "http://tong.visitkorea.or.kr/cms/resource/18/3077518_image2_1.JPG"
        val item = tourInfoList[position]
        holder.title.text = item.title
        holder.address.text = item.addr1
        holder.phone.text = item.tel
        Log.d("image",item.firstimage.toString())

        if (item.firstimage != null) {
            Glide.with(holder.itemView.context).load(item.firstimage).into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_background)  // 기본 이미지 리소스
        }
    }

    override fun getItemCount(): Int {
        return tourInfoList.size
    }
}
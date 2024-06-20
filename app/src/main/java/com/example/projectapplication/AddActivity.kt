package com.example.projectapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectapplication.databinding.ActivityAddBinding
import java.text.SimpleDateFormat

class AddActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddBinding

    lateinit var uri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.tvId.text = MyApplication.email // 현재 이메일 출력

        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode === android.app.Activity.RESULT_OK){
                binding.addImageView.visibility = View.VISIBLE
                Glide
                    .with(applicationContext)
                    .load(it.data?.data)
                    .override(200,150)
                    .into(binding.addImageView)

                uri = it.data?.data!!
            }
        }

        binding.uploadButton.setOnClickListener {
            // 이미지 올린 것 처리
            val intent = Intent(Intent.ACTION_PICK)

            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")

            // 이미지를 픽 해서 왔을 때 어떤 처ㄹ리를 할 것인가
            requestLauncher.launch(intent)
        }

        // 리턴 값이 있는 intent 처리해주는 역할을 함
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions() ) {

            if (it.all { permission -> permission.value == true }) {
                // 사용자가 퍼미션 줬다고 하면 알림 띄우는 것
                noti()
            }
            else {
                // 사용자가 퍼미션 안줬다고(사용자가 허용 X) 하면 알림 못띄운다고 하는 것
                Toast.makeText(this, "permission denied...", Toast.LENGTH_SHORT).show()
            }
        }


        binding.saveButton.setOnClickListener {  // 저장하기 버튼 누르면
            if(binding.input.text.isNotEmpty()){ // 만약 사용자가 edit text에 입력했다면
                // firesotre에 저장하기 전에 데이터(document) 만들어줘야함

                // 로그인 이메일(아이디), 스타(별표), 한줄평, 입력시간에 대한 내용 저장할꺼야
                val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

                val data = mapOf(
                    // key value
                    "email" to MyApplication.email,
                    "stars" to binding.ratingBar.rating.toFloat(), // 별표 갯수 가져와  (float 형태로 가져와)
                    "comments" to binding.input.text.toString(), // 한줄평 -> String 형태로 가져와
                    "date_time" to dateFormat.format(System.currentTimeMillis())  // 버튼이 클릭된 현재 시간 가져와
                )

                MyApplication.db.collection("comments") // comments로 불리는 collection에 데이터 추가
                    .add(data)
                    .addOnSuccessListener { // 데이터 저장 성공했다면
                        Toast.makeText(this, "데이터 저장 성공.", Toast.LENGTH_LONG).show()
                        uploadImage(it.id)
                        finish()
                    }
                    .addOnFailureListener{ // 데이터 저장 실패했다면
                        Toast.makeText(this, "데이터 저장 실패 !.", Toast.LENGTH_LONG).show()
                    }
            }

            else{ // 만약 사용자가 edit text에 입력 안했다면
                Toast.makeText(this, "한줄평을 먼저 입력해주세요.", Toast.LENGTH_LONG).show()
            }




            // 버전이 낮으면 걍 하면 되는데
            // 버전이 높으면 확인하고 처리해야함

            // 티라미수 버전보다 높으면 이 퍼미션이 정말로 잘 주어졌는지 확인하는 작업
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this,"android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED) {
                    noti()
                }
                else {
                    // 사용자한테 퍼미션 요청할고야 하는 것
                    permissionLauncher.launch( arrayOf( "android.permission.POST_NOTIFICATIONS"  ) )
                }
            }
            else {
                noti()
            }
        } // binding.notificationButton
    }

    fun uploadImage(docId : String){
        val imageRef = MyApplication.storage.reference.child("images/${docId}.jpg")

        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "사진 업로드 성공", Toast.LENGTH_LONG).show()
        }
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_LONG).show()
        }
    }


    fun noti(){ // 알림창을 띄우는 함수
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 알림을 만들 수 있는 빌더 만들기
        val builder: NotificationCompat.Builder

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){     // 26 버전 이상에서는 빌더를 만드는 방법이 달라짐
            // 알림을 줄 때 채널이라는 개념이 생김
            // 채널을 다양하게 가짐
            // 각 채널 별로 알림을 보낼 수 있음 (한 번에 여러 개의 채널에서 알림을 보낼 수 있음)
            val channelId="one-channel"
            val channelName="My Channel One"

            val channel = NotificationChannel( // 채널 만들기
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {   // 채널에 다양한 정보 설정
                description = "My Channel One Description"

                setShowBadge(true)  // 앱 런처 아이콘 상단에 숫자 배지를 표시할지 여부를 지정 (상태바에 표시할지)

                // 에뮬레이터에서 소리를 제공하지 않아서 오디오 설정 안할거면 지우기
                val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val audioAttributes = AudioAttributes.Builder() // 오디오가 어떠한 특성을 가지고 있는 애를 사용할 것인가
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // 오디오에 대한 내용
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                setSound(uri, audioAttributes) // 소리를 넣을지
                enableVibration(true) // 진동
                // 여기까지 지우기
            }
            // 채널을 NotificationManager에 등록
            manager.createNotificationChannel(channel)

            // 채널을 이용하여 builder 생성
            builder = NotificationCompat.Builder(this, channelId)
        }
        else {  // 26 버전 미만
            // 바로 (간단하게) 빌더를 만들 수 있음
            builder = NotificationCompat.Builder(this)
        }

        // 알림의 기본 정보
        builder.run {
            setSmallIcon(R.drawable.modurang_small) // 상태바에 뜨는 작은 아이콘
            setWhen(System.currentTimeMillis()) // 알림이 뜨는 시간 - 현재 시간
            setContentTitle("모두랑 게시글 업로드 알림")
            setContentText("게시글이 업로드 되었습니다. 확인해보세요!")
        }

        manager.notify(11, builder.build()) // 지금까지 만들어둔 것 실행해라
    }
}
package com.example.projectapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.projectapplication.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_auth)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeVisibility(intent.getStringExtra("status").toString())


        binding.goSignInBtn.setOnClickListener {    // 회원 가입 Button
            changeVisibility("signin")
        }


        binding.signBtn.setOnClickListener {    // 가입 Button
            // 먼저!! edit text에 입력된 내용 먼저 불러오기
            val email = binding.authEmailEditView.text.toString() // 이메일 변수
            val password = binding.authPasswordEditView.text.toString() // 비밀번호 변수
            val name = binding.authNameEditView.text.toString().trim() // 이름 변수
            // 이거 가지고서 우리가 회원가입 하려고 함

            // Firebase에서 회원가입하기 위한 모든 처리는 auth라는 객체를 통해서 함
            // auth는 MyApplication에 만들어둠

            if(email.isEmpty() || password.isEmpty() || name.isEmpty()){
                Toast.makeText(this, "모든 필드를 채워주시길 바랍니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }




            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    binding.authNameEditView.text.clear()
                    if (task.isSuccessful) {
                        val user = MyApplication.auth.currentUser
                        user?.let {
                            user.sendEmailVerification()
                                .addOnCompleteListener { sendTask ->
                                    if (sendTask.isSuccessful) {
                                        val userInfo = hashMapOf(
                                            "name" to name,
                                            "emailVerified" to false  // 이메일 인증 여부를 추가
                                        )
                                        MyApplication.db.collection("users").document(user.uid)
                                            .set(userInfo)
                                            .addOnSuccessListener {
                                                Toast.makeText(baseContext, "회원가입 성공! 메일을 확인해주세요", Toast.LENGTH_SHORT).show()
                                                Log.d("mobileapp", "회원가입 성공!")
                                                changeVisibility("logout")
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(baseContext, "추가 정보 저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                                                Log.d("mobileapp", "추가 정보 저장 실패: ${e.message}")
                                            }
                                    } else {
                                        Toast.makeText(baseContext, "메일 발송 실패", Toast.LENGTH_SHORT).show()
                                        Log.d("mobileapp", "메일 발송 실패")
                                        changeVisibility("logout")
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(baseContext, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.d("mobileapp", "회원가입 실패: ${task.exception?.message}")
                        changeVisibility("logout")
                    }
                }
        }

        binding.loginBtn.setOnClickListener {   // 로그인 Button
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()

            MyApplication.auth.signInWithEmailAndPassword(email,password) // 모든 객체는 MyApplication의 auth 거쳐간다
                .addOnCompleteListener(this){task ->

                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()

                    if(task.isSuccessful){

                        if(MyApplication.checkAuth()){ // 이메일 인증했?

                            // 올바르게 로그인이 되었다
                            MyApplication.email = email
                            // 올바르게 로그인이 되었다는 건
                            // 가입할 때 적은 메일과 비밀번호가 확인이 되었다는 것
                            // 성공이 되었다는 건 그 이메일을 내가 로그인한 이메일로 바꿈

                            Log.d("mobileapp", "로그인 성공")
                            Toast.makeText(this, "로그인 성공 !", Toast.LENGTH_LONG).show()

                            // 로그인 다 됐으니까 피니시!
                            finish()
                        }
                        else{ // 이메일이 등록은 됐는데 성공은 못한 경우
                            Toast.makeText(baseContext,"이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("mobileapp", "이메일 인증 안됨")
                        }
                    }
                    else{ // 로그인 자체가 실패
                        Toast.makeText(baseContext,"로그인 실패", Toast.LENGTH_SHORT).show()
                        Log.d("mobileapp", "로그인 실패")
                    }
                }
        }

        binding.logoutBtn.setOnClickListener {  // 로그아웃 Button
            MyApplication.auth.signOut() // auth 객체에 대해서 singout(로그아웃)

            // 이전에 적어둔 이메일 -> null로 (지우기)
            MyApplication.email = null

            Log.d("mobileapp", "로그 아웃")
            Toast.makeText(this, "로그아웃 성공 !", Toast.LENGTH_LONG).show()

            finish()
        }


        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data) // 로그인할려는 이메일을 변수로 가져

            Log.d("mobileapp","account1 : ${task.toString()}")
            //Log.d("mobileapp","account2 : ${task.result}")

            try{
                val account = task.getResult(ApiException::class.java)
                val crendential = GoogleAuthProvider.getCredential(account.idToken, null) // Credential을 통해서 확보

                MyApplication.auth.signInWithCredential(crendential)
                    .addOnCompleteListener(this){task ->
                        if(task.isSuccessful){ // 성공적으로 로그인
                            MyApplication.email = account.email // 로그인한 이메일에 대한 정보 확보 (구글 이메일로 바꿔)

                            Toast.makeText(baseContext,"구글 로그인 성공", Toast.LENGTH_SHORT).show()
                            Log.d("mobileapp", "구글 로그인 성공")

                            finish()
                        }
                        else{ // 로그인 실패
                            changeVisibility("logout")
                            Toast.makeText(baseContext,"구글 로그인 실패", Toast.LENGTH_SHORT).show()
                            Log.d("mobileapp", "구글 로그인 실패")
                        }
                    }
            }catch (e: ApiException){ // APIException은 이미 지정된 exception말고 custom한 exception을 만들어서 쓰고 싶을때 사용
                changeVisibility("logout")
                Toast.makeText(baseContext,"구글 로그인 Exception : ${e.printStackTrace()},${e.statusCode}",
                    Toast.LENGTH_SHORT).show()
                Log.d("mobileapp", "구글 로그인 Exception : ${e.message}, ${e.statusCode}")
            }
        }

        binding.googleLoginBtn.setOnClickListener { // 구글인증 Button
            // 버튼 클릭하면 인텐트를 통해서 처리

            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // 구글 로그인 작업을 디폴트로 두겠다
                .requestIdToken(getString(R.string.default_client_id)) // id 토큰 -> client 아이디를 통해서 접근하겠다
                .requestEmail() // 구글 이메일로
                .build()

            // 이렇게 만들어진 옵션을 가지고 구글 로그인을 하는데 필요한 처리

            val signInIntent = GoogleSignIn.getClient(this,gso).signInIntent // 구글 로그인 창도 하나의 인텐트임

            requestLauncher.launch(signInIntent) // 로그인 처리 -> requestLancher에 넘겨
        }

    }

    fun changeVisibility(mode:String){
        if(mode.equals("login")){       // 현재 로그인 상태
            binding.run{
                authMainTextView.text = "정말 로그아웃하시겠습니까?"
                authMainTextView.visibility = View.VISIBLE
                logoutBtn.visibility = View.VISIBLE

                goSignInBtn.visibility = View.GONE // 회원 가입을 위해 가기 위해 누르는 버튼
                authEmailEditView.visibility = View.GONE
                authPasswordEditView.visibility = View.GONE
                signBtn.visibility = View.GONE // 진짜로 회원 가입 하는 버튼 (아이디, 비밀번호 누르고 회원가입 ㄱㄱ 하는 버튼)
                loginBtn.visibility= View.GONE
                googleLoginBtn.visibility = View.GONE

                authNameEditView.visibility = View.GONE




            }
        }
        else if(mode.equals("logout")){ // 현재 로그아웃 상태
            binding.run{
                authMainTextView.text = "로그인 하거나 회원가입 해주세요."

                authMainTextView.visibility = View.VISIBLE

                logoutBtn.visibility = View.GONE // 로그아웃 버튼 없애고

                goSignInBtn.visibility = View.VISIBLE // 회원가입 버튼 생겨
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.GONE
                loginBtn.visibility= View.VISIBLE // 로그인 버튼 생겨
                googleLoginBtn.visibility = View.VISIBLE

                authNameEditView.visibility = View.GONE
            }
        }
        else if(mode.equals("signin")){    // 회원가입 버튼 클릭 : 회원가입 진행 상태
            binding.run{
                authMainTextView.visibility = View.GONE
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE

                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.VISIBLE

                loginBtn.visibility= View.GONE
                googleLoginBtn.visibility = View.GONE

                authNameEditView.visibility = View.VISIBLE
            }
        }
    }
}
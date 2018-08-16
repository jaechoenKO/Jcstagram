package com.study.jcstagram

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class LoginActivity : AppCompatActivity() {

    // firebase Authentication 관리 클래스 ?는 코틀린이 가지고 있는 null-safety
    var auth : FirebaseAuth? = null

    // GoogleLogin 관리 클래스
    var googleSignInClient : GoogleSignInClient? = null

    // onActivityResult에서 사용할 Google Login Request 코드
    var GOOGLE_LOGIN_CODE = 9001

    // FaceBook 로그인 처리 결과 관리 클래스
    var callbackManager : CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Firebase 로그인 통합 관리하는 Object 만들기
        auth = FirebaseAuth.getInstance()

        // 이메일 로그인 버튼 리스너
        email_login_button.setOnClickListener {
            createAndLoginEmail()
        }

        // 구글 로그인 버튼 리스너
        google_sign_in_button.setOnClickListener {
            googleLogin()
        }

        // 페이스북 로그인 버튼 리스너
        facebook_login_button.setOnClickListener {
            facebookLogin()
        }

        // 구글 로그인 옵션, 구글 API키 세팅 및 권한 요청 설정. 현재 이메일 주소만 요청하는 코드
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build() // build 코드를 마치겠다. 조립 완성.

        // 구글 로그인 클래스
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        printHashKey(this)

        // 초기화
        callbackManager = CallbackManager.Factory.create()

    }

    // 해시 키 구하는 코드 https://stackoverflow.com/questions/7506392/how-to-create-android-facebook-key-hash
    fun printHashKey(pContext: Context) {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("hash", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("hash", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("hash", "printHashKey()", e)
        }

    }

    // 이메일 회원 가입 및 로그인 함수
    fun createAndLoginEmail(){

        // 사용자의 이메일과 패스워드 입력 값 받아오기
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
                ?.addOnCompleteListener { task ->
                    // 아이디 생성 성공 시
                    if(task.isSuccessful){
                        moveMainPage(auth?.currentUser) // 로그인 성공하게 되면 auth는 유저의 대한 정보를 가지고 있다. 세션을 가져온다.
                    }else if(task.exception?.message.isNullOrEmpty()){ // 또는 task.exception?.message == null, 에러 메세지가 있을 시에
                        // 회원 가입 에러시 토스트 메세지
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }else{
                        // 이메일 로그인 함수 호출
                        signinEmail()
                    }
        }
    }

    fun signinEmail(){
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
                ?.addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        moveMainPage(auth?.currentUser) // 로그인 성공하게 되면 auth는 유저의 대한 정보를 가지고 있다. 세션을 가져온다.
                    }else{
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }

        }
    }

    // 페이지 이동 함수
    fun moveMainPage(user : FirebaseUser?){

        // 유저가 있을 경우
        if(user != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // 구글 로그인 함수
    fun googleLogin(){

        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE) // 구글 dialog 창 띄우기
    }

    // 구글 로그인 토큰 값을 Credentail로 변환하는 함수
    fun firebaseAuthWithGoogle(account : GoogleSignInAccount){

        // 구글과 파이어 베이스는 전혀 다른 플랫폼이기 때문에 넘겨주는 코드를 넣어야 한다.
        var credential = GoogleAuthProvider.getCredential(account.idToken, null) // 구글 로그인 토큰 값을 Credentail로 변환
        auth?.signInWithCredential(credential)

    }

    fun facebookLogin(){
        LoginManager
                .getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result?.accessToken) // 로그인이 성공했을 경우 토큰을 handleFacebookAccessToken에 넘겨줌
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {

            }

        })
    }

    // 페이스북 토큰을 Firebase로 넘겨주는 함수
    fun handleFacebookAccessToken(token : AccessToken?){
        var credential = FacebookAuthProvider.getCredential(token?.token!!) // !!는 null-safety 풀어줌.
        auth?.signInWithCredential(credential)?.addOnCompleteListener { // callback
            task ->
            if(task.isSuccessful){
                moveMainPage(auth?.currentUser)
            }

        }
    }

    // 자동 로그인 함수
    override fun onResume() {
        super.onResume()
        moveMainPage(auth?.currentUser)
    }

    // googleLogin 함수의 결과 값이 넘어와서 받는 함수.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 페이스북 SDK로 값 넘겨주기
        // 페이스북 로그인 성공 결과값을 중간에 캐치해서 registerCallback의 FacebookCallback<LoginResult> 인터페이스로 넘겨주는 부분
        callbackManager?.onActivityResult(requestCode, resultCode, data)

        // 구글에서 승인된 정보 가져오기
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            // 로그인 성공 했을 경우
            if(result.isSuccess){
                var account = result.signInAccount
                // 구글 로그인 결과 값을 account에 담아 firebase에 계정을 생성하는 function인 firebaseAuthWithGoogle에 넣는다.
                firebaseAuthWithGoogle(account!!)
            }
        }

    }

}

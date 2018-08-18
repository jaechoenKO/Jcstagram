package com.study.jcstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // 소통하는 결과 값이 들어옴.
        when(item.itemId){
            R.id.action_home -> {
                var detailViewFragment = DetailViewFragment()
                // 트렌젝션 처리. action_home 클릭시 DetailViewFragment로 이동
                supportFragmentManager.beginTransaction().replace(R.id.main_content, detailViewFragment).commit() // commit 꼭 넣어야함.
                return true // 작동
            }
            R.id.action_search -> {
                var gridFragment = GridFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, gridFragment).commit() // commit 꼭 넣어야함.
                return true
            }
            R.id.action_add_photo -> {
                /*
                 ContextCompat.checkSelfPermission을 통해서 READ_EXTERNAL_STORAGE 권한이 있을 경우 AddPhotoActivity::Class.java를 실행한다.
                 퍼미션 받았는지 확인, 스토리지 권한을 못받았으면 앨범 접근 안됨.
                */
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    startActivity(Intent(this,AddPhotoActivity::class.java))
                }else{
                    Toast.makeText(this, "스토리지 읽기 권한이 없습니다.", Toast.LENGTH_LONG).show()
                }
                return true
                return true
            }
            R.id.action_favorite_alarm -> {
                var alertFragment = AlertFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, alertFragment).commit() // commit 꼭 넣어야함.
                return true
            }
            R.id.action_account -> {
                var userFragment = UserFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, userFragment).commit() // commit 꼭 넣어야함.
                return true
            }
        }
        return false // 작동 안함
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener(this) // this : class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener 을 의미

        // 자동으로 선택.
        bottom_navigation.selectedItemId = R.id.action_home

        // 앨범 접근 권한을 위한 코드
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

    }
}

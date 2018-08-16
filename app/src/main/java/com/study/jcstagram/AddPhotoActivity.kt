package com.study.jcstagram

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    val PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        storage = FirebaseStorage.getInstance()

        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        // AddPhotoAcitivty를 실행했을 때 자동적으로 앨범이 열리도록 코드를 입력
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        addphoto_image.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            // AddPhotoAcitivty를 실행했을 때 자동적으로 앨범이 열리도록 코드를 입력
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }

        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }

    }

    // 모든 결과값이 집중.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 앨범이 닫히면서 결과값이 넘어오는 부분
        // 필터링 해주어야함. PICK_IMAGE_FROM_ALBUM이 결과만 받겠다.
        if(requestCode == PICK_IMAGE_FROM_ALBUM){

            // 사진을 선택 했을 때, 취소 했을 때. 둘다 프로세스를 필터링 해주어야 한다.
            // 선택 했을 시 와 취소를 눌렀을시 필터링 해야한다.
            if(resultCode == Activity.RESULT_OK){ // 사진 선택
                photoUri = data?.data
                addphoto_image.setImageURI(data?.data)
            }
            // 취소를 눌렀을 시
//            if(resultCode == Activity.RESULT_CANCELED){
//
//            }

        }else{
            finish()
        }

    }

    // 파이어 베이스 스토리지에 이미지 올리는 함수
    fun contentUpload(){
        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_"+timeStamp + "_.png"

        // images라는 폴더에 파일이 넣는다.
        val storageRef = storage?.reference?.child("images")?.child(imageFileName)

        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG).show()
        }
    }

}

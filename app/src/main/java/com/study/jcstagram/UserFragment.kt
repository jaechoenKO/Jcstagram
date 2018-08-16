package com.study.jcstagram

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// 유저에 대한 상세 페이지로 팔로잉 또는 로그아웃과 그리고 프로필 사진을 올리는 Fragment
class UserFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(inflater.context).inflate(R.layout.fragment_user, container, false)
    }

}
package com.study.jcstagram

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.study.jcstagram.model.ContentDTO
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

// 사진의 상세 정보를 볼 수 있는 리스트 Fragment
class DetailViewFragment : Fragment(){

    var firestore : FirebaseFirestore? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // 초기화
        firestore = FirebaseFirestore.getInstance()

        var view = LayoutInflater.from(inflater.context).inflate(R.layout.fragment_detail, container, false)
        view.detailviewfragment_recyclerview.adapter = DetailRecyclerviewAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view
    }
    // 여러가지 방법이 있지만 inner class를 만드는게 편하다.
    inner class DetailRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val contentDTOs : ArrayList<ContentDTO>
        val contentUidList : ArrayList<String>

        // 데이터 접근 소스.
        init { // 컨스트럭터 생성자, timestamp 즉 올린 시간 대로 정렬

            contentDTOs = ArrayList()
            contentUidList = ArrayList()

            // 현재 로그인 된 유저의 UID(예: 주민등록번호)
            var uid = FirebaseAuth.getInstance().currentUser?.uid

            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                // 데이터가 쌓이지 않게 하기 위해 클리어를 한다.
                contentDTOs.clear()
                contentUidList.clear()

                // 첫번 째 부터 끝까지 들어가겠다., 스냅샷은 필드에 있는 데이터들.
                for(snapshot in querySnapshot!!.documents){
                    // ContentDTO에 맵핑.
                    var item = snapshot.toObject(ContentDTO::class.java) // item에 할당. 스냅샷이 ContentDTO로 캐스팅. 물론 안에 있는 데이터가 모양이 같아야함.
                    contentDTOs.add(item)
                    contentUidList.add(snapshot.id)
                }
                // 새로고침. firebase 안에 있어야 한다. 데이터 베이스 호출 될때마다 for문이 호출 되기 때문에. 밖에 있으면 for문과 따로 놀기 때문에 밖에 까지 도달 하지 못함.
                notifyDataSetChanged()
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            //var imageview = ImageView(parent.context)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view!!)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val viewHolder = (holder as CustomViewHolder).itemView

            // 유저 아이디 담음.
            viewHolder.detailviewitem_profile_textview.text = contentDTOs!![position].userId
            // 이미지 담음. 쓰레드 방식. 콜백 방식이기 때문에 들어가는 애가 마지막.
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewHolder.detailviewitem_imageview_content)
            // 설명 텍스트
            viewHolder.detailviewitem_explain_textview.text = contentDTOs!![position].explain

            // 좋아요 카운터 설정
            viewHolder.detailviewitem_favoritecounter_textview.text = "좋아요" + contentDTOs!![position].favoriteCount + "개"

        }

    }

}
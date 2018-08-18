package com.study.jcstagram.model

/*
/ContentDTO 규격이 있다. 첫번째로 explain 사진을 올릴 때 설명글, 이미지, 식별, 아이디, 업로드 시간, 좋아요, favorites 클릭 체크
 */
data class ContentDTO(var explain: String? = null,
                      var imageUrl : String? = null,
                      var uid : String? = null,
                      var userId : String? = null,
                      var timestamp : Long? = null,
                      var favoriteCount : Int = 0,
                      var favorites : Map<String, Boolean> = HashMap()){

    data class Comment(var uid : String? = null,
                       var userId: String? = null,
                       var comment : String? = null,
                       var timestamp : Long? = null)
}
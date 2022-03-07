package sogong.sogongSpring.dto

import java.io.Serializable
import java.util.*

data class EntirePostDto(
    val postid: Long? = null,
    val userid: Long,
    var subject: String,
    var content: String,
    var date: Date ?= null, //null빼야함 수정바람.
    var picture: String? = null,
    var hashtag: String,
    var countcomment: Int = 0,
    var countlike: Int = 0
) : Serializable

package sogong.sogongSpring.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sogong.sogongSpring.dto.hashtag.PostHashtagDto
import sogong.sogongSpring.dto.hashtag.UserHashTagDto
import sogong.sogongSpring.entity.EntirePostEntity
import sogong.sogongSpring.entity.PostHashtagEntity
import sogong.sogongSpring.entity.UserHashtagEntity
import sogong.sogongSpring.repository.*
import java.lang.Math.abs
import javax.transaction.Transactional

@Service
class HashtagService {
    @Autowired
    private lateinit var postHashtagRepository: PostHashtagRepository
    @Autowired
    private lateinit var hashtagDbRepository: HashtagDbRepository
    @Autowired
    private lateinit var entirePostRepository : EntirePostRepository
    @Autowired
    private lateinit var userHashtagRepository: UserHashtagRepository
    @Autowired
    private lateinit var userLoginRepository: UserLoginRepository

    @Transactional
    fun savePostHashtag(postHashtagDto: PostHashtagDto){
        val hashIds = hashtagDbRepository.findByHashNames(postHashtagDto.hashName)
        if(hashIds.isNotEmpty()) {
            runCatching{
                entirePostRepository.findById(postHashtagDto.postId).get()
            }.onSuccess { epe ->
                hashIds.forEach { i ->
                    val postHashtagEntity = PostHashtagEntity(hashId = i, postId = epe)
                    postHashtagRepository.save(postHashtagEntity)
                }
            }.onFailure {throw PostIdException(postHashtagDto.postId)}
        } else{throw HashNameException(postHashtagDto.hashName)}
    }

    @Transactional
    fun saveUserHashtag(userHashTagDto: UserHashTagDto){
        runCatching{
            userLoginRepository.findById(userHashTagDto.userId).get()
        }.onSuccess {
            if(hashtagDbRepository.findByHashNames(userHashTagDto.hashName).isEmpty())
                throw HashNameException(userHashTagDto.hashName)
            else{
                for(hash in userHashTagDto.hashName)
                    userHashtagRepository.save(
                        UserHashtagEntity(userId = it, hashName = hash, groupId = userHashTagDto.groupId)
                    )
            }
        }.onFailure { throw UserIdException(userHashTagDto.userId)}
    }

    @Transactional
    fun editUserHashtag(userHashTagDto: UserHashTagDto){
        runCatching{
            userLoginRepository.findById(userHashTagDto.userId).get()
        }.onSuccess {
            val uhrs = userHashtagRepository.findByUserIdAndGroupId(it, userHashTagDto.groupId)
            val hash = userHashTagDto.hashName
            var difAndSize = uhrs.size-hash.size

            if(difAndSize>0){ //ex)해시태그 4->2개로
                for(i:Int in 1..difAndSize)
                    userHashtagRepository.delete(uhrs[uhrs.size-i])
                difAndSize = hash.size-1
            }
            else if (difAndSize<0){ //ex)해시태그 2->5개로
                for(i:Int in 1..abs(difAndSize)){
                    userHashtagRepository.save(
                        UserHashtagEntity(
                            userId = it, groupId = userHashTagDto.groupId,
                            hashName = userHashTagDto.hashName[hash.size-i]))
                }
                difAndSize = uhrs.size-1
            }
            else difAndSize=uhrs.size-1 //ex)해시태그 2->2개로

            for (i:Int in 0..difAndSize){
                uhrs.get(i).hashName = hash.get(i)
                userHashtagRepository.save(uhrs.get(i))
            }
        }.onFailure {throw UserIdException(userHashTagDto.userId)}
    }

    @Transactional
    fun editPostHashtag(postHashtagDto: PostHashtagDto){
        val original = postHashtagRepository.findAllByPost(postHashtagDto.postId)
        if(original.isNotEmpty()) {
            val hashes = hashtagDbRepository.findByHashNames(postHashtagDto.hashName)
            if(hashes.isNotEmpty()) {
                for (i: Int in 0 until original.size)
                    postHashtagRepository.save(
                        PostHashtagEntity(
                            postHashId = original[i].postHashId,
                            postId = original[i].postId,
                            hashId = hashes[i]
                        )
                    )
            } else throw HashNameException(postHashtagDto.hashName)
        } else throw PostIdException(postHashtagDto.postId)
    }

    @Transactional
    fun searchOrPost(hashtags: List<String>) : List<EntirePostEntity> {
        val hashIds = hashtagDbRepository.findByHashNames(hashtags)
        if(hashIds.isNotEmpty())
            return postHashtagRepository.findByHashIds(hashIds) //Dto로 변경?
        else throw HashNameException(hashtags)
    }

    @Transactional
    fun printPostHashtag(postId: Long) : List<String>{
        val hashes = hashtagDbRepository.findAllById(postHashtagRepository.findHashByPost(postId))
        if(hashes.isEmpty()) throw java.lang.IllegalArgumentException("postId Error!!")
        else{ return hashes.map { hash -> hash.hashName } }
    }

    @Transactional
    fun printUserHashtag(userId: Long, groupId: Long) : List<UserHashtagEntity>{
        runCatching{
            userLoginRepository.findById(userId).get()
        }.onSuccess {
            return userHashtagRepository.findByUserIdAndGroupId(it, groupId)
        }.onFailure { throw UserIdException(userId)}
        throw IllegalStateException("Server Error!!")
    }

}
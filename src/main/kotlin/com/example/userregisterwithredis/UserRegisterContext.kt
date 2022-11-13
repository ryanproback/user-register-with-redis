package com.example.userregisterwithredis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.security.SecureRandom
import java.time.LocalDate

@RedisHash(timeToLive = 30 * 60, value = "register_context") // 30분
class UserRegisterContext(
    var username: String? = null,
    var birth: LocalDate? = null,
    var email: String? = null,
    var authncode: String? = null,
    var isVerified: Boolean = false
) {
    @Id
    lateinit var contextId: String

    fun resetEmailAuthncode(email: String) {
        this.email = email
        this.authncode = createAuthncode()
        this.isVerified = false
    }

    fun verify(authncode: String) {
        this.isVerified = !this.isVerified && authncode == this.authncode
    }

    fun isDone(): Boolean = !this.username.isNullOrBlank()
            && this.birth != null
            && !this.email.isNullOrBlank()
            && isVerified

    fun update(request: RequiredInfoPutRequest) {
        this.username = request.username
    }
}

private fun createAuthncode(): String {
    val random = SecureRandom()
    random.setSeed(20)
    return random.nextInt(999999).toString().padStart(6, '0')
}
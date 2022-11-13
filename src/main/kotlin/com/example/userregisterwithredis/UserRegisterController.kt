package com.example.userregisterwithredis

import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestControllerAdvice
class UserRegisterController(private val contextRepository: UserRegisterContextRepository) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/user-registers")
    fun createContext(): ApiResult<String> {
        val context = contextRepository.save(UserRegisterContext())
        logger.info("user register context created. contextId: $context")
        return ApiResult.success(context.contextId)
    }

    @GetMapping("/user-registers/username/available")
    fun checkUsernameAvailable(
        @RequestParam contextId: String,
        @RequestParam username: String
    ): ResponseEntity<ApiResult<Boolean>> {
        logger.info { "check username avaliable. contextId: $contextId, username: $username" }
        val context = getContextById(contextId)

        return ResponseEntity.ok(ApiResult.success(isUsernameAvailable(username)))
    }

    fun isUsernameAvailable(username: String): Boolean {
        return username != "already"
    }

    @PostMapping("/user-registers/email-authn/send")
    fun sendEmailAuthnCode(
        @RequestParam contextId: String,
        @RequestParam email: String
    ): ApiResult<Unit> {
        logger.info { "send email authentication code. contextId: $contextId, email: $email" }
        val context = contextRepository.findByIdOrNull(contextId)
        check(context != null) { "INVALID_REGISTER_CONTEXT" }

        context.resetEmailAuthncode(email)
        contextRepository.save(context)
        logger.info("email authncode reset. code: ${context.authncode}")

        return ApiResult.success()
    }

    @PostMapping("/user-registers/email-authn/verify")
    fun verifyEmailAutnCode(
        @RequestParam contextId: String,
        @RequestParam authncode: String
    ): ApiResult<Unit> {
        logger.info { "verify email authentication code. contextId: $contextId, code: $authncode"}
        val context = getContextById(contextId)

        context.verify(authncode)
        contextRepository.save(context)
        require(context.isVerified) { "INVALID_EMAIL_AUTHNCODE" }

        return ApiResult.success()
    }

    @PutMapping("/user-registers/required-info")
    fun putRequiredInfo(@RequestParam contextId: String,
                        @RequestBody request: RequiredInfoPutRequest): ApiResult<Unit> {
        logger.info { "put required info. contextId: $contextId, request: $request"}
        val context = getContextById(contextId)

        context.update(request)
        contextRepository.save(context)

        return ApiResult.success()
    }

    @PostMapping("/user-registers/done")
    fun doneUserReigsterProcess(@RequestParam contextId: String): ApiResult<Unit> {
        logger.info { "done user register process. contextId: $contextId"}
        val context = getContextById(contextId)
        check(context.isDone())

        // create user.

        contextRepository.delete(context)

        return ApiResult.success()
    }

    fun getContextById(contextId: String): UserRegisterContext {
        val context = contextRepository.findByIdOrNull(contextId)
        check(context != null) { "INVALID_REGISTER_CONTEXT" }
        return context
    }
}
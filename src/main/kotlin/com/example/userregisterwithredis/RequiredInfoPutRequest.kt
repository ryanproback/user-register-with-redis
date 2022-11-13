package com.example.userregisterwithredis

import java.time.LocalDate

data class RequiredInfoPutRequest(val username: String, val birth: LocalDate)
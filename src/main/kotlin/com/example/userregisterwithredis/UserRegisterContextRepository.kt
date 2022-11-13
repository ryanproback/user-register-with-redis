package com.example.userregisterwithredis

import org.springframework.data.repository.CrudRepository

interface UserRegisterContextRepository: CrudRepository<UserRegisterContext, String> {

}

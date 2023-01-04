package com.inovex.inoventory.user
import com.inovex.inoventory.user.entity.UserEntity
import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("/api/v1/users")
class UserController(private val repository: UserRepository) {
    @GetMapping
    fun getAll(): List<UserEntity> = repository.findAll()
}
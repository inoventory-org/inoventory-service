package com.inovex.inoventory.user
import com.inovex.inoventory.user.domain.User
import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("/api/v1/users")
class UserController(private val repository: UserRepository) {
    @GetMapping
    fun getAll(): List<User> = repository.findAll()
}
package com.spring.spring_init.user.repository

import com.spring.spring_init.common.persistence.config.JpaConfig
import com.spring.spring_init.user.entity.User
import com.spring.spring_init.verify.repository.EmailVerifyJpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
    private val emailVerifyJpaRepository: EmailVerifyJpaRepository,
    private val jpaConfig: JpaConfig
) : UserRepository {

    override fun save(user: User): User {
        return userJpaRepository.save(user)
    }

    override fun findByEmail(email: String): Optional<User> {
        return userJpaRepository.findByEmail(email)
    }

    override fun findAll(): List<User> {
        return userJpaRepository.findAll()
    }

    override fun findById(userId: Long): Optional<User> {
        return userJpaRepository.findById(userId)
    }

    override fun findByOauthId(id: String): Optional<User> {
        return userJpaRepository.findByProviderId(id)
    }
}

package ru.kyamshanov.mission.authorization.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.authorization.entities.UserEntity
import java.util.Optional


@Repository
interface UserRepository : JpaRepository<UserEntity, String> {

    fun findByUsername(username: String): Optional<UserEntity>
}
package ru.kyamshanov.mission.authorization.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID


@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @Column(name = "id")
    val id: String = UUID.randomUUID().toString().replace("-", ""),
    @Column(name = "username")
    val username: String = "",
    @Column(name = "password")
    val password: String = "",
    @Column(name = "enabled")
    val enabled: Boolean = false,
)
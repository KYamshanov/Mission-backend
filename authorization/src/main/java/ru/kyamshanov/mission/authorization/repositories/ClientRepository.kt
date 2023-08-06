package ru.kyamshanov.mission.authorization.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.authorization.entities.Client
import java.util.Optional


@Repository
interface ClientRepository : JpaRepository<Client, String> {
    fun findByClientId(clientId: String): Optional<Client>
}
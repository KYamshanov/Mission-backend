package ru.kyamshanov.mission.client

interface ClientStorage {

    fun getAllClients(): List<Client>
}
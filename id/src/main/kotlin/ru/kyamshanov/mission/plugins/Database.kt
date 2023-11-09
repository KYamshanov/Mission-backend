package ru.kyamshanov.mission.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.tables.*

fun Application.dbConnect() {
    val databaseUrl = environment.config.property("database.postgresql.url").getString()
    val databaseUser = environment.config.property("database.postgresql.user").getString()
    val databasePassword = environment.config.property("database.postgresql.password").getString()

    Database.connect(
        driver = "org.postgresql.Driver",
        url = databaseUrl,
        user = databaseUser,
        password = databasePassword
    )

    transaction {
        addLogger(StdOutSqlLogger) // print sql to std-out
        SchemaUtils.create(Authorities)
        SchemaUtils.create(AuthorizationTable)
        SchemaUtils.create(ClientServiceTable)
        SchemaUtils.create(ClientsTable)
        SchemaUtils.create(ExAuthTable)
        SchemaUtils.create(UserAuthorities)
        SchemaUtils.create(UsersTable)
    }
}

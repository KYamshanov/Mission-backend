package ru.kyamshanov.mission.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kyamshanov.mission.tables.*
import ru.kyamshanov.mission.utils.secret

fun Application.dbConnect() {
    val databaseUrl = environment.config.secret("database.postgresql.url")
    val databaseUser = environment.config.secret("database.postgresql.user")
    val databasePassword = environment.config.secret("database.postgresql.password")

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

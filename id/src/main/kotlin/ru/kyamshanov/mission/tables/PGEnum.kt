package ru.kyamshanov.mission.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.postgresql.util.PGobject
import ru.kyamshanov.mission.client.models.SocialService

class PGEnum<T : Enum<T>>(enumTypeName: String, enumValue: T?) : PGobject() {
    init {
        value = enumValue?.name
        type = enumTypeName
    }
}


fun Table.socialServiceEnumeration(name: String): Column<SocialService> = customEnumeration(
    name,
    "social_service",
    { value -> SocialService.valueOf(value as String) },
    { PGEnum("social_service", it) })
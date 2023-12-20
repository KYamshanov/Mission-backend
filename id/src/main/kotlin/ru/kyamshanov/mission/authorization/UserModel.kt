package ru.kyamshanov.mission.authorization

import java.util.*

data class UserModel(
    val userId: UUID,
    val enabled: Boolean,
)

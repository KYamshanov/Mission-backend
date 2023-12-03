package ru.kyamshanov.mission.point.network.dtos

data class SearchRqDto(
    val searchPhrase: String?,
    val labels: List<String>
)
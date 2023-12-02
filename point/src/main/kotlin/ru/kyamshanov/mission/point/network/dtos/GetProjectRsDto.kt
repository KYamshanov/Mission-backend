package ru.kyamshanov.mission.point.network.dtos

data class GetProjectRsDto(
    val id: String,
    val title: String,
    val description: String?,
    val editingRules: EditingProjectRulesDto?,
    val labels: List<LabelDto>
)

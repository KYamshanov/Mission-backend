package ru.kyamshanov.mission.point.database.configuration

import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.convert.EnumWriteSupport
import ru.kyamshanov.mission.point.domain.models.TaskStatus
import ru.kyamshanov.mission.point.domain.models.TaskType

@WritingConverter
@ReadingConverter
class TaskTypeConverter : EnumWriteSupport<TaskType>()
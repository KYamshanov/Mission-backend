package ru.kyamshanov.mission.point.database.configuration

import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.convert.EnumWriteSupport
import ru.kyamshanov.mission.point.domain.models.TaskStatus

@WritingConverter
@ReadingConverter
class TaskStatusConverter : EnumWriteSupport<TaskStatus>()
package ru.kyamshanov.mission.point.database.configuration

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.postgresql.codec.EnumCodec
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import ru.kyamshanov.mission.point.database.entities.TaskPriority
import ru.kyamshanov.mission.point.domain.models.TaskStatus
import ru.kyamshanov.mission.point.domain.models.TaskType
import java.time.Duration

@Configuration
class EnumsConfig(
    private val r2dbcProperties: R2dbcProperties,
) : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {


        val parse = ConnectionFactoryOptions.parse(r2dbcProperties.url)

        return PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(parse.getValue(ConnectionFactoryOptions.HOST).toString())
                .apply {
                    parse.getValue(ConnectionFactoryOptions.PORT)?.toString()?.toIntOrNull()
                        ?.let { port(it) }
                }
                .database(r2dbcProperties.name)
                .username(r2dbcProperties.username)
                .password(r2dbcProperties.password)
                .codecRegistrar(
                    EnumCodec.builder()
                        .withEnum("task_priority", TaskPriority::class.java)
                        .withEnum("task_status", TaskStatus::class.java)
                        .withEnum("task_type", TaskType::class.java)
                        .build()
                )
                .build()
        ).let {
            ConnectionPoolConfiguration.builder(it)
                .initialSize(5)
                .maxSize(10)
                .maxIdleTime(Duration.ofMinutes(5))
                .build()
        }.let { ConnectionPool(it) }
    }

    override fun getCustomConverters(): MutableList<Any> = mutableListOf(
        TaskPriorityConverter(),
        TaskStatusConverter(),
        TaskTypeConverter(),
    )

}
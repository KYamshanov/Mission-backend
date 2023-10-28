package ru.kyamshanov.mission.plugins

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.freemarker.*

fun Application.freeMarker() = install(FreeMarker) {
    templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
}
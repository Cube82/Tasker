package pl.cube

import io.ktor.server.application.Application
import pl.cube.config.configureRouting
import pl.cube.config.configureSerialization
import pl.cube.config.configureSockets

val repository = TaskRepositoryImpl()

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSockets(repository)
    configureSerialization()
    configureRouting()
}

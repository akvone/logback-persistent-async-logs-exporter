package com.akvone.logs

import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.io.File

open class LogsExportingHttpClient(
    private val properties: LoggingProperties
) {
    private val factory = DefaultDataBufferFactory()

    private val webClient = WebClient
        .builder()
        .apply {
            val authProps = properties.exporting.basicAuth
            if (authProps != null) {
                it.defaultHeaders { header -> header.setBasicAuth(authProps.username, authProps.password) }
            }
        }
        .build()


    fun push(path: File, position: Long): Long {
        return webClient.post()
            .uri(properties.exporting.url)
            .body(
                DataBufferUtils
                    .read(FileSystemResource(path), position, factory, 1000)
                    .let { BodyInserters.fromDataBuffers(it) }
            )
            .retrieve()
            .bodyToMono<Long>()
            .block()!!
    }
}
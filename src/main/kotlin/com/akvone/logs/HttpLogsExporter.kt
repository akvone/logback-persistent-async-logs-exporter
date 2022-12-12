package com.akvone.logs

import kotlinx.coroutines.runBlocking
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity

open class HttpLogsExporter(
    private val properties: CustomLoggingProperties
) {

    private val webClient = WebClient.create()

    fun push(logLines: List<String>) {
        runBlocking {
            webClient.post()
                .uri(properties.exporter.url)
                .bodyValue(logLines)
                .retrieve()
                .awaitBodilessEntity()
        }
    }
}
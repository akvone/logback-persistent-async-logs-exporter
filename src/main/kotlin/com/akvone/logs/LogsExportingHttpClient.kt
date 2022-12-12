package com.akvone.logs

import org.springframework.web.reactive.function.client.WebClient

open class LogsExportingHttpClient(
    private val properties: LoggingProperties
) {

    private val webClient = WebClient.create()

    fun push(logLines: List<String>) {
        webClient.post()
            .uri(properties.exporting.url)
            .bodyValue(logLines)
            .retrieve()
            .toBodilessEntity()
            .block()
    }
}
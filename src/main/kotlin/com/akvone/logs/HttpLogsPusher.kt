package com.akvone.logs

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity

@Service
open class HttpLogsPusher {

    private val webClient = WebClient.builder()
        .baseUrl("http://localhost:8081")
        .build()

    fun push(logLines: List<String>) {
        runBlocking {
            webClient.post()
                .uri("/logs")
                .bodyValue(logLines)
                .retrieve()
                .awaitBodilessEntity()
        }
    }
}
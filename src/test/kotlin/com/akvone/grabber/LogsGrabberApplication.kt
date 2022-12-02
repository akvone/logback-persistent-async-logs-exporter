package com.akvone.grabber

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong


@SpringBootApplication
open class LogsGrabberApplication

fun main() {
    SpringApplicationBuilder(LogsGrabberApplication::class.java)
        .properties("server.port=8081")
        .build()
        .run()
}

@RestController
@RequestMapping("/")
open class LogsGrabber {

    private val log: Logger = LoggerFactory.getLogger(LogsGrabber::class.java)

    private var lastLoggedLineNumberA = AtomicLong(-1)

    @PostMapping("logs")
    fun logs(@RequestBody(required = false) lines: List<String>) {
        lines
            .mapNotNull { it.toLongOrNull() }
            .forEach { nextLineNumber ->
                if (nextLineNumber == lastLoggedLineNumberA.get() + 1) {
                    lastLoggedLineNumberA.incrementAndGet()
                } else {
                    log.error("Got something strange $nextLineNumber")
                    lastLoggedLineNumberA.set(nextLineNumber)
                }
            }

        log.info(lastLoggedLineNumberA.toString())
    }

}
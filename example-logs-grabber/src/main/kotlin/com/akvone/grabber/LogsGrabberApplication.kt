package com.akvone.grabber

import org.apache.commons.io.input.CountingInputStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import javax.servlet.http.HttpServletRequest
import kotlin.streams.asSequence


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

    @PostMapping("logs", consumes = [APPLICATION_OCTET_STREAM_VALUE])
    fun logs(request: HttpServletRequest): Long {
        val countingInputStream = CountingInputStream(request.inputStream)
        countingInputStream.bufferedReader()
            .lineSequence()
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
        log.info("Count ${countingInputStream.byteCount}")

        return countingInputStream.byteCount
    }

}
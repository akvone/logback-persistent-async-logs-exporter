package com.akvone.logs

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@SpringBootApplication
@EnableScheduling
open class LogsPusherApplication

fun main() {
    SpringApplicationBuilder(LogsPusherApplication::class.java)
        .properties("logging.config=classpath:logs-pusher-logback-spring.xml")
        .build()
        .run()
}

@Service
open class LogsGenerator {

    private val log: Logger = LoggerFactory.getLogger(LogReaderAndPusher::class.java)

    private var currentLogLine = 0

    @Scheduled(fixedDelay = 2)
    fun generateLogLine() {
        log.info("${currentLogLine++}")
    }
}

@Service
open class LogReaderAndPusher(
    private val logsService: LogsService
) {

    @Scheduled(fixedDelay = 3000)
    fun readNewLogsAndPushThem() {
        logsService.readNewLogsAndPushThem()
    }
}
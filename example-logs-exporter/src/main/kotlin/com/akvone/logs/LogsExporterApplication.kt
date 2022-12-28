package com.akvone.logs

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@SpringBootApplication
@EnableScheduling
open class LogsExporterApplication

fun main() {
    SpringApplicationBuilder(LogsExporterApplication::class.java)
        .properties("logging.config=classpath:test-logs-exporter-logback-spring.xml")
        .build()
        .run()
}

@Service
open class LogsGenerator {

    private val log: Logger = LoggerFactory.getLogger(LogsExportingJob::class.java)

    private var currentLogLine = 0

    @Scheduled(fixedDelay = 2)
    fun generateLogLine() {
        log.info("${currentLogLine++}")
    }
}


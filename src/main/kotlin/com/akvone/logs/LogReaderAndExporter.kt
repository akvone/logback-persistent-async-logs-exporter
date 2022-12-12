package com.akvone.logs

import org.springframework.scheduling.annotation.Scheduled

open class LogReaderAndExporter(
    private val logsService: LogsService
) {

    @Scheduled(fixedDelay = 3000)
    fun readNewLogsAndPushThem() {
        logsService.readNewLogsAndPushThem()
    }
}
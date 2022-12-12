package com.akvone.logs

import org.springframework.scheduling.annotation.Scheduled

open class LogsExportingJob(
    private val logsService: LogsService
) {

    @Scheduled(fixedDelayString = "\${logging.custom.exporting.job.fixedDelay}")
    fun readNewLogsAndPushThem() {
        logsService.readNewLogsAndPushThem()
    }
}
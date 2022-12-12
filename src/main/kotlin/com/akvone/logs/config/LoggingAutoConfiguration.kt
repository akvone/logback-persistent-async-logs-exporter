package com.akvone.logs.config

import ch.qos.logback.classic.LoggerContext
import com.akvone.logs.LoggingProperties
import com.akvone.logs.LogsExportingHttpClient
import com.akvone.logs.LogsExportingJob
import com.akvone.logs.LogsService
import com.akvone.logs.config.LoggingAutoConfiguration.Companion.CUSTOM_APPENDER_NAME
import org.slf4j.LoggerFactory
import org.slf4j.impl.StaticLoggerBinder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.type.AnnotatedTypeMetadata

@Configuration
@Conditional(CustomLoggingCondition::class)
@EnableConfigurationProperties(LoggingProperties::class)
@Import(
    LogsExportingHttpClient::class,
    LogsExportingJob::class,
    LogsService::class
)
open class LoggingAutoConfiguration {

    private val log = LoggerFactory.getLogger(LoggingAutoConfiguration::class.java)

    init {
        log.info("Custom logging is enabled")
    }

    companion object {
        const val CUSTOM_APPENDER_NAME = "CUSTOM_APPENDER"
    }
}

class CustomLoggingCondition : Condition {

    override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
        return StaticLoggerBinder.getSingleton().loggerFactory
            .let { it as LoggerContext }
            .loggerList
            .map { it.iteratorForAppenders().asSequence().any { appender -> appender.name == CUSTOM_APPENDER_NAME } }
            .any()
    }

}


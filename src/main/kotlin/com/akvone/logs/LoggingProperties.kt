package com.akvone.logs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("logging.custom")
data class LoggingProperties(
    val persistentAppender: PersistentAppenderProperties,
    val exporting: ExportingProperties
)

data class PersistentAppenderProperties(
    val fileNamePartsSeparator: String,
    val filesPrefix: String,
    val logsParentFolder: String
)

data class ExportingProperties(
    val url: String,
    val basicAuth: BasicAuthProperties? = null,
    val job: JobProperties
)

data class BasicAuthProperties(
    val username: String,
    val password: String
)

data class JobProperties(
    val fixedDelay: Int
)

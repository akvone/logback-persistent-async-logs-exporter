package com.akvone.logs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("logging.custom")
data class CustomLoggingProperties (
    val persistentAppender: PersistentAppenderProperties,
    val exporter: ExporterProperties
)

data class PersistentAppenderProperties(
    val fileNamePartsSeparator: String,
    val filesPrefix: String,
    val logsParentFolder: String
)



data class ExporterProperties(
    val url: String,
)

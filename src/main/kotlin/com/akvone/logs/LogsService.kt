package com.akvone.logs

import java.io.File

open class LogsService(
    private val logsExportingHttpClient: LogsExportingHttpClient,
    properties: LoggingProperties
) {

    private var lastReadFileName = "" // Any name which will never exist in log folder
    private var lastReadByte = 0L

    private val fileNamePartsSeparator = properties.persistentAppender.fileNamePartsSeparator
    private val filesPrefix = properties.persistentAppender.filesPrefix
    private val logsParentFolder = properties.persistentAppender.logsParentFolder

    fun readNewLogsAndPushThem() {
        getSortedLogFiles().forEach {
            if (it.name == lastReadFileName) {
                val committedBytes = logsExportingHttpClient.push(it, lastReadByte)
                lastReadByte += committedBytes
            } else {
                val committedBytes = logsExportingHttpClient.push(it, 0)
                lastReadByte = committedBytes
                lastReadFileName = it.name
            }
        }
    }



    private fun getSortedLogFiles(): List<File> {
        val comparator = Comparator
            .comparing<FileWithNameParts, String> { it.dateAndTime }
            .thenBy { it.rotationCounter }

        return File(logsParentFolder)
            .listFiles()!!
            .filter { it.name.startsWith(filesPrefix) }
            .map { FileWithNameParts(it, fileNamePartsSeparator) }
            .sortedWith(comparator)
            .let { sortedLogFiles ->
                if (sortedLogFiles.map { it.name }.contains(lastReadFileName)) {
                    sortedLogFiles.dropWhile { it.name != lastReadFileName }
                } else {
                    sortedLogFiles
                }
            }.map { it.file }
    }

    private data class FileWithNameParts(
        val file: File,
        val fileNamePartsSeparator: String
    ) {
        val name: String = file.name
        val dateAndTime: String

        val rotationCounter: Int

        init {
            val fileNameParts = name.split(fileNamePartsSeparator)
            dateAndTime = fileNameParts[1]
            rotationCounter = fileNameParts[2].toInt()
        }
    }

}
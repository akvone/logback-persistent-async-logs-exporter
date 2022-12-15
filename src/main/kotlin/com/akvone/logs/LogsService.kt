package com.akvone.logs

import java.io.File
import java.nio.file.Files
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList

open class LogsService(
    private val logsExportingHttpClient: LogsExportingHttpClient,
    properties: LoggingProperties
) {

    private var lastReadFileName = "" // Any name which will never exist in log folder
    private var lastReadLineNumber = 0

    private val fileNamePartsSeparator = properties.persistentAppender.fileNamePartsSeparator
    private val filesPrefix = properties.persistentAppender.filesPrefix
    private val logsParentFolder = properties.persistentAppender.logsParentFolder

    fun readNewLogsAndPushThem() {
        getSortedLogFiles().forEach {
            if (it.name == lastReadFileName) {
                val list = getLogLinesSince(it, lastReadLineNumber)
                logsExportingHttpClient.push(list)
                lastReadLineNumber += list.size
            } else {
                val list = getLogLinesSince(it, 0)
                logsExportingHttpClient.push(list)
                lastReadLineNumber = list.size
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

    private fun getLogLinesSince(logFile: File, lastReadLineNumber: Int): List<String> {
        // This is the alternative to Files#lines.
        // The latter explicitly forbids file modification on terminal operation, which we have with log files
        return Files.newBufferedReader(logFile.toPath()).lines().use{
            it.skip(lastReadLineNumber.toLong()).toList()
        }
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
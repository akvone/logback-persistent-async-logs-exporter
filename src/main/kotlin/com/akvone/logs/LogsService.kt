package com.akvone.logs

import org.springframework.stereotype.Service
import java.io.File
import java.util.*

@Service
open class LogsService(
    private val httpLogsExporter: HttpLogsExporter,
    properties: CustomLoggingProperties
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
                httpLogsExporter.push(list)
                lastReadLineNumber += list.size
            } else {
                val list = getLogLinesSince(it, 0)
                httpLogsExporter.push(list)
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

    private fun getLogLinesSince(logFile: File, lastReadLineNumber: Int): List<String> { // TODO: Improve this code
        val scanner = Scanner(logFile)
        val list = mutableListOf<String>()
        while (scanner.hasNextLine()) {
            list.add(scanner.nextLine())
        }
        return list.asSequence().drop(lastReadLineNumber).toList()
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
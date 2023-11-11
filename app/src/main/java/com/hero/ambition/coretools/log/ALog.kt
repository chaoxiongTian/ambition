package com.hero.ambition.coretools.log

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.hero.ambition.coretools.log.WriteHandler.Companion.generateWriteHandler
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.CsvFormatStrategy
import com.orhanobut.logger.DiskLogAdapter
import com.orhanobut.logger.DiskLogStrategy
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.LogcatLogStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import java.io.File
import java.io.FileWriter
import java.io.IOException


/**
 * logger warp, to hide true implementation.
 */
object ALog {

    private const val GLOBAL_TAG = "a-log"
    fun init(context: Context) {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true) // (Optional) Whether to show thread info or not. Default true
            .methodCount(0) // (Optional) How many method line to show. Default 2
            .methodOffset(7) // (Optional) Hides internal method calls up to offset. Default 5
            .logStrategy(LogcatLogStrategy()) // (Optional) Changes the log strategy to print out. Default LogCat
            .tag(GLOBAL_TAG) // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        val build = CsvFormatStrategy.newBuilder().logStrategy(DiskLogStrategy(generateWriteHandler(context)))
        Logger.addLogAdapter(DiskLogAdapter(build.build()))
    }

    fun d(message: String, vararg args: Any) {
        Logger.d(message, args)
    }

    fun d(tag: String, message: String, vararg args: Any) {
        Logger.t(tag).d(tag, message, args)
    }

    fun d(message: Any) {
        Logger.d(message)
    }

    fun d(tag: String, message: Any) {
        Logger.t(tag).d(message)
    }

    fun e(message: String, vararg args: Any) {
        Logger.e(message, args)
    }

    fun e(tag: String, message: String, vararg args: Any) {
        Logger.t(tag).e(message, args)
    }

    fun e(throwable: Throwable, message: String, vararg args: Any) {
        Logger.e(throwable, message, args)
    }

    fun e(tag: String, throwable: Throwable, message: String, vararg args: Any) {
        Logger.t(tag).e(throwable, message, args)
    }

    fun w(message: String, vararg args: Any) {
        Logger.w(message, args)
    }

    fun w(tag: String, message: String, vararg args: Any) {
        Logger.t(tag).w(message, args)
    }

    fun i(message: String, vararg args: Any) {
        Logger.i(message, args)
    }

    fun i(tag: String, message: String, vararg args: Any) {
        Logger.t(tag).t(tag).i(message, args)
    }

    fun v(message: String, vararg args: Any) {
        Logger.v(message, args)
    }

    fun v(tag: String, message: String, vararg args: Any) {
        Logger.t(tag).v(message, args)
    }

    fun wtf(message: String, vararg args: Any) {
        Logger.wtf(message, args)
    }

    fun wtf(tag: String, message: String, vararg args: Any) {
        Logger.t(tag).wtf(message, args)
    }

    fun json(json: String) {
        Logger.json(json)
    }

    fun json(tag: String, json: String) {
        Logger.t(tag).json(json)
    }

    fun xml(xml: String) {
        Logger.xml(xml)
    }

    fun xml(tag: String, xml: String) {
        Logger.t(tag).xml(xml)
    }
}

class WriteHandler private constructor(looper: Looper, private val folder: String, private val maxFileSize: Int) :
    Handler() {
    companion object {

        private const val MAX_BYTES = 500 * 1024
        fun generateWriteHandler(context: Context): WriteHandler {
            val diskPath = context.cacheDir.absolutePath
            val folder = diskPath + File.separatorChar + "logger"
            val ht = HandlerThread("AndroidFileLogger.$folder")
            ht.start()
            return WriteHandler(ht.looper, folder, MAX_BYTES)
        }
    }

    override fun handleMessage(msg: Message) {
        val content = msg.obj as String
        var fileWriter: FileWriter? = null
        val logFile = getLogFile(folder, "logs")
        try {
            fileWriter = FileWriter(logFile, true)
            writeLog(fileWriter, content)
            fileWriter.flush()
            fileWriter.close()
        } catch (e: IOException) {
            if (fileWriter != null) {
                try {
                    fileWriter.flush()
                    fileWriter.close()
                } catch (e1: IOException) { /* fail silently */
                }
            }
        }
    }

    /**
     * This is always called on a single background thread.
     * Implementing classes must ONLY write to the fileWriter and nothing more.
     * The abstract class takes care of everything else including close the stream and catching IOException
     *
     * @param fileWriter an instance of FileWriter already initialised to the correct file
     */
    @Throws(IOException::class)
    private fun writeLog(fileWriter: FileWriter, content: String) {
        fileWriter.append(content)
    }

    private fun getLogFile(folderName: String, fileName: String): File {
        val folder = File(folderName)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        var newFileCount = 0
        var newFile: File
        var existingFile: File? = null
        newFile = File(folder, String.format("%s_%s.csv", fileName, newFileCount))
        while (newFile.exists()) {
            existingFile = newFile
            newFileCount++
            newFile = File(folder, String.format("%s_%s.csv", fileName, newFileCount))
        }
        return if (existingFile != null) {
            if (existingFile.length() >= maxFileSize) {
                newFile
            } else existingFile
        } else newFile
    }
}
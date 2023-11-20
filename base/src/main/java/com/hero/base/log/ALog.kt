package com.hero.base.log

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.hero.base.log.WriteHandler.Companion.generateWriteHandler
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
    private const val NULL_LOG_MES = "null_log_mes"
    private const val NULL_LOG_OBJ = "null_log_Obj"
    fun init(context: Context) {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false) // (Optional) Whether to show thread info or not. Default true
            .methodCount(0) // (Optional) How many method line to show. Default 2
            .methodOffset(7) // (Optional) Hides internal method calls up to offset. Default 5
            .logStrategy(LogcatLogStrategy()) // (Optional) Changes the log strategy to print out. Default LogCat
            .tag(GLOBAL_TAG) // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        val build = CsvFormatStrategy.newBuilder().logStrategy(DiskLogStrategy(generateWriteHandler(context)))
        Logger.addLogAdapter(DiskLogAdapter(build.build()))
    }


    private fun adjustMessage(message: String?): String = message ?: NULL_LOG_MES
    private fun adjustObjMessage(message: Any?): Any = message ?: NULL_LOG_OBJ

    fun d(message: Any?) = Logger.d(adjustObjMessage(message))

    fun d(tag: String, message: Any?) = Logger.t(tag).d(adjustObjMessage(message))

    fun e(message: String?) {
        Logger.e(adjustMessage(message))
    }

    fun e(tag: String, message: String?) {
        Logger.t(tag).e(adjustMessage(message))
    }

    fun e(throwable: Throwable, message: String?) {
        Logger.e(throwable, adjustMessage(message))
    }

    fun e(tag: String, throwable: Throwable, message: String?) {
        Logger.t(tag).e(throwable, adjustMessage(message))
    }

    fun w(message: String?) {
        Logger.w(adjustMessage(message))
    }

    fun w(tag: String, message: String?) {
        Logger.t(tag).w(adjustMessage(message))
    }

    fun i(message: String?) {
        Logger.i(adjustMessage(message))
    }

    fun i(tag: String, message: String?) {
        Logger.t(tag).i(adjustMessage(message))
    }

    fun v(message: String?) {
        Logger.v(adjustMessage(message))
    }

    fun v(tag: String, message: String?) {
        Logger.t(tag).v(adjustMessage(message))
    }

    fun wtf(message: String?) {
        Logger.wtf(adjustMessage(message))
    }

    fun wtf(tag: String, message: String?) {
        Logger.t(tag).wtf(adjustMessage(message))
    }

    fun json(json: String?) {
        Logger.json(adjustMessage(json))
    }

    fun json(tag: String, json: String?) {
        Logger.t(tag).json(adjustMessage(json))
    }

    fun xml(xml: String?) {
        Logger.xml(adjustMessage(xml))
    }

    fun xml(tag: String, xml: String?) {
        Logger.t(tag).xml(adjustMessage(xml))
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
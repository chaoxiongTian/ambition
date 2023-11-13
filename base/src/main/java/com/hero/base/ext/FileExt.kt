package com.hero.base.ext

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.text.DecimalFormat


val File.canListFiles: Boolean
    get() = canRead() and isDirectory

/**
 * Total size (include all subFile)
 */
val File.totalSize: Long
    get() = if (isFile) length() else getFolderSize(this)

/**
 * Formatted total size (include all subFile)
 */
val File.formatSize: String
    get() = getFormatFileSize(totalSize)

/**
 * Return file's mimeType, such as "png"
 */
val File.mimeType: String
    get() = getMimeType(extension, isDirectory)

/**
 * List sub files
 * @param isRecursive whether to list recursively
 * @param filter exclude some files
 */
fun File.listFiles(
    isRecursive: Boolean = false,
    filter: ((file: File) -> Boolean)? = null
): Array<out File> {
    val fileList = if (!isRecursive) listFiles() else getAllSubFile(this)
    var result: Array<File> = arrayOf()
    return if (filter == null) fileList
    else {
        for (file in fileList) {
            if (filter(file)) result = result.plus(file)
        }
        result
    }
}

/**
 * write some text to file
 * @param append whether to append or overwrite
 * @param charset default charset is utf-8
 */
fun File.writeText(append: Boolean = false, text: String, charset: Charset = Charsets.UTF_8) {
    if (append) appendText(text, charset) else writeText(text, charset)
}

/**
 * write some bytes to file
 * @param append whether to append or overwrite
 */
fun File.writeBytes(append: Boolean = false, bytes: ByteArray) {
    if (append) appendBytes(bytes) else writeBytes(bytes)
}

/**
 *  copy file
 *  @param destFile dest file/folder
 *  @param overwrite whether to override dest file/folder if exist
 *  @param reserve Whether to reserve source file/folder
 */
fun File.moveTo(destFile: File, overwrite: Boolean = true, reserve: Boolean = true): Boolean {
    val dest = copyRecursively(destFile, overwrite)
    if (!reserve) deleteRecursively()
    return dest
}

/**
 * copy file with progress callback
 * @param destFolder dest folder
 * @param overwrite whether to override dest file/folder if exist
 * @param func progress callback (from 0 to 100)
 */
fun File.moveToWithProgress(
    destFolder: File,
    overwrite: Boolean = true,
    reserve: Boolean = true,
    func: ((file: File, i: Int) -> Unit)? = null
) {

    if (isDirectory) copyFolder(this, File(destFolder, name), overwrite, func)
    else copyFile(this, File(destFolder, name), overwrite, func)

    if (!reserve) deleteRecursively()
}

/** Rename to newName */
fun File.rename(newName: String) =
    rename(File("$parent${File.separator}$newName"))

/** Rename to newFile's name */
fun File.rename(newFile: File) =
    if (newFile.exists()) false else renameTo(newFile)


/**
 * Return the file size, include all sub files
 */
fun getFolderSize(file: File): Long {
    var total = 0L
    for (subFile in file.listFiles()) {
        total += if (subFile.isFile) subFile.length()
        else getFolderSize(subFile)
    }
    return total
}

/**
 * Return the formatted file size, like "4.78 GB"
 * @param unit 1000 or 1024, default to 1000
 */
fun getFormatFileSize(size: Long, unit: Int = 1000): String {
    val formatter = DecimalFormat("####.00")
    return when {
        size < 0 -> "0 B"
        size < unit -> "$size B"
        size < unit * unit -> "${formatter.format(size.toDouble() / unit)} KB"
        size < unit * unit * unit -> "${formatter.format(size.toDouble() / unit / unit)} MB"
        else -> "${formatter.format(size.toDouble() / unit / unit / unit)} GB"
    }
}

/**
 * Return all subFile in the folder
 */
fun getAllSubFile(folder: File): Array<File> {
    var fileList: Array<File> = arrayOf()
    if (!folder.canListFiles) return fileList
    for (subFile in folder.listFiles())
        fileList = if (subFile.isFile) fileList.plus(subFile)
        else fileList.plus(getAllSubFile(subFile))
    return fileList
}

/**
 * copy the [sourceFile] to the [destFile], only for file, not for folder
 * @param overwrite if the destFile is exist, whether to overwrite it
 */
fun copyFile(sourceFile: File, destFile: File, overwrite: Boolean, func: ((file: File, i: Int) -> Unit)? = null) {

    if (!sourceFile.exists()) return

    if (destFile.exists()) {
        val stillExists = if (!overwrite) true else !destFile.delete()

        if (stillExists) {
            return
        }
    }

    if (!destFile.exists()) destFile.createNewFile()

    val inputStream = FileInputStream(sourceFile)
    val outputStream = FileOutputStream(destFile)
    val iChannel = inputStream.channel
    val oChannel = outputStream.channel


    val totalSize = sourceFile.length()
    val buffer = ByteBuffer.allocate(1024)
    var hasRead = 0f
    var progress = -1
    while (true) {
        buffer.clear()
        val read = iChannel.read(buffer)
        if (read == -1)
            break
        buffer.limit(buffer.position())
        buffer.position(0)
        oChannel.write(buffer)
        hasRead += read

        func?.let {
            val newProgress = ((hasRead / totalSize) * 100).toInt()
            if (progress != newProgress) {
                progress = newProgress
                it(sourceFile, progress)
            }
        }
    }

    inputStream.close()
    outputStream.close()
}

/**
 * copy the [sourceFolder] to the [destFolder]
 * @param overwrite if the destFile is exist, whether to overwrite it
 */
fun copyFolder(sourceFolder: File, destFolder: File, overwrite: Boolean, func: ((file: File, i: Int) -> Unit)? = null) {
    if (!sourceFolder.exists()) return

    if (!destFolder.exists()) {
        val result = destFolder.mkdirs()
        if (!result) return
    }

    for (subFile in sourceFolder.listFiles()) {
        if (subFile.isDirectory) {
            copyFolder(subFile, File("${destFolder.path}${File.separator}${subFile.name}"), overwrite, func)
        } else {
            copyFile(subFile, File(destFolder, subFile.name), overwrite, func)
        }
    }
}
package com.hero.ambition.ui.home

import com.hero.base.ext.FileCoreInfo
import com.hero.base.ext.Hash
import com.hero.base.ext.TAG
import com.hero.base.ext.alogd
import com.hero.base.ext.extractCoreInfos
import com.hero.base.ext.hash
import com.hero.base.ext.then
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.system.measureTimeMillis

object DuplicateDetectUtils {
    /**
     * files: 文件目录
     * return : <类型, <相同文件的 sha-256, 相同文件列表<FileCoreInfo>>>
     */
    suspend fun detection(
        files: MutableList<File>
    ): MutableMap<String, MutableMap<String, MutableList<FileCoreInfo>>> {
        var result: MutableMap<String, MutableMap<String, MutableList<FileCoreInfo>>>
        withContext(Dispatchers.IO) {
            val cost = measureTimeMillis {
                result = findDetection(files, needVerify = false, needLog = true)
            }
            alogd("DuplicateDetectUtils", "整体花费: $cost")
        }
        return result
    }

    /**
     * files: 文件列表,
     * needVerify: 是否需要非常精确, 如果需要的话, 对于文件抽点算出的再次进行 hash 计算.
     * 返回值: <类型, <相同文件的 keyValue, 相同文件列表<FileCoreInfo>>>
     */
    private suspend fun findDetection(files: MutableList<File>, needVerify: Boolean, needLog: Boolean = false):
            MutableMap<String, MutableMap<String, MutableList<FileCoreInfo>>> {
        // 2. <mimeType, <keyValue, <FileCoreInfo>>>
        val trimmedFlatFileCores = trimFileCoresWithKeyValue(file2FileCores(files), needVerify, needLog)
        // 3. <类型, <相同文件的 sha-256, 相同文件列表<FileCoreInfo>>>
        return typeClassFileCores(trimmedFlatFileCores, needLog)
    }

    private fun typeClassFileCores(fileCores: MutableMap<String, MutableList<FileCoreInfo>>, needLog: Boolean):
            MutableMap<String, MutableMap<String, MutableList<FileCoreInfo>>> {
        val result = mutableMapOf<String, MutableMap<String, MutableList<FileCoreInfo>>>()
        fileCores.entries.forEach {
            val firstValue = it.value.first()
            if (result.containsKey(firstValue.extension)) {
                result[firstValue.extension]!![it.key] = it.value
            } else {
                result[firstValue.extension] = mutableMapOf<String, MutableList<FileCoreInfo>>().apply {
                    this[it.key] = it.value
                }
            }
        }
        logTypeClassFileCores(result, needLog, "最后结果")
        return result
    }

    /**
     * 通过 FileCoreInfo.keyValue 字段, 过滤 MutableList<FileCoreInfo> 列表.
     * 找出重复的 FileCoreInfo, 并按照 MutableMap<String, MutableList<FileCoreInfo>> 形式返回.
     */
    private suspend fun trimFileCoresWithKeyValue(
        fileCores: MutableList<FileCoreInfo>,
        needVerify: Boolean,
        needLog: Boolean
    ): MutableMap<String, MutableList<FileCoreInfo>> {
        // 粗整理 -> <keyValue, List<FileCoreInfo>>
        val flatFileCores = flatFileCores(fileCores)
        logFileCores(flatFileCores, needLog, "粗整理: ${flatFileCores.size}")
        // 精确整理需要加上 hash 值
        val accurateFileCores: MutableMap<String, MutableList<FileCoreInfo>> = if (needVerify) {
            convertAccuracyKeyValue(flatFileCores)
        } else {
            flatFileCores
        }
        logFileCores(accurateFileCores, needLog, "sha-256 精确后: ${accurateFileCores.size}")
        return accurateFileCores
    }

    private fun logFileCores(
        fileCores: MutableMap<String, MutableList<FileCoreInfo>>,
        needLog: Boolean,
        notice: String
    ) {
        if (!needLog) {
            return
        }
        alogd(TAG(), notice)
        fileCores.entries.forEachIndexed { index, entry ->
            entry.value.forEachIndexed { indexInside, it ->
                alogd(TAG(), "$index - $indexInside , $it")
            }
        }
    }

    private fun logTypeClassFileCores(
        fileCores: MutableMap<String, MutableMap<String, MutableList<FileCoreInfo>>>,
        needLog: Boolean,
        notice: String
    ) {
        if (!needLog) {
            return
        }
        alogd(TAG(), notice)
        fileCores.entries.forEach { typeEntry ->
            alogd(TAG(), "类型: ${typeEntry.key}, 有${typeEntry.value.size} 个")
            typeEntry.value.entries.forEachIndexed { index, entry ->
                entry.value.forEachIndexed { indexInside, it ->
                    alogd(TAG(), "$index - $indexInside , $it")
                }
            }
        }
    }

    private fun file2FileCores(files: MutableList<File>): MutableList<FileCoreInfo> {
        val fileCores = mutableListOf<FileCoreInfo>()
        files.forEach {
            if (it.isFile) {
                fileCores.add(it.extractCoreInfos())
            }
        }
        return fileCores
    }

    /**
     * 如果需要精细化处理, 需要按照 sha256 在过滤一次
     * 将 keyValue 改为 hash_keyValue
     */
    private suspend fun convertAccuracyKeyValue(map: MutableMap<String, MutableList<FileCoreInfo>>):
            MutableMap<String, MutableList<FileCoreInfo>> = flatFileCores(addKeyValueHashValue(map))

    /**
     * 从 MutableList<FileCoreInfo> 中找出 key 重复多次的数据,
     * 并转为 MutableMap<String, MutableList<FileCoreInfo>>
     */
    private fun flatFileCores(fileCores: MutableList<FileCoreInfo>):
            MutableMap<String, MutableList<FileCoreInfo>> {
        val fileCoresP = mutableMapOf<String, MutableList<FileCoreInfo>>()
        // 重复的数据
        val result = mutableMapOf<String, MutableList<FileCoreInfo>>()
        fileCores.forEach {
            (fileCoresP.containsKey(it.keyValue)).then({
                fileCoresP[it.keyValue]!!.add(it)
            }, {
                fileCoresP[it.keyValue] = mutableListOf(it)
            })
            (fileCoresP[it.keyValue]!!.size > 1).then {
                result[it.keyValue] = fileCoresP[it.keyValue]!!
            }
        }
        return result
    }

    private suspend fun addKeyValueHashValue(map: MutableMap<String, MutableList<FileCoreInfo>>):
            MutableList<FileCoreInfo> {
        val fileCores = mutableListOf<FileCoreInfo>()
        coroutineScope {
            val asyncTasks = ArrayList<Deferred<Any>>()
            map.entries.forEach { entries ->
                entries.value.forEach {
                    asyncTasks.add(async {
                        fileCores.add(it.changeKeyValue(it.keyValue + "_" + File(it.path).hash(Hash.SHA256)))
                    })
                }
            }
            asyncTasks.awaitAll()
        }
        return fileCores
    }
}
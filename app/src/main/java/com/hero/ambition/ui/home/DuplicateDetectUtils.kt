package com.hero.ambition.ui.home

import com.hero.base.ext.FileCoreInfo
import com.hero.base.ext.Hash
import com.hero.base.ext.TAG
import com.hero.base.ext.extractCoreInfos
import com.hero.base.ext.hash
import com.hero.base.ext.then
import com.hero.base.log.alogd
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
                result = findDetection(files, true)
            }
            alogd("DuplicateFileLog", "整体花费: $cost")
        }
        return result
    }

    /**
     * files 文件列表,
     * needVerify: 是否需要非常精确, 如果需要的话, 对于文件抽点算出的再次进行 hash 计算.
     * 返回值: <类型, <相同文件的 sha-256, 相同文件列表<FileCoreInfo>>>
     */
    private suspend fun findDetection(files: MutableList<File>, needVerify: Boolean):
            MutableMap<String, MutableMap<String, MutableList<FileCoreInfo>>> {
        alogd(TAG(), "sublist size: ${files.size}")
        // files<File> -> fileCores<FileCoreInfo> - doing
        val fileCores = mutableListOf<FileCoreInfo>()
        files.forEach {
            if (it.isFile) {
                fileCores.add(it.extractCoreInfos())
            }
        }
        alogd(TAG(), "sublist is file size: ${files.size}")
        // 粗整理 -> <keyValue, List<FileCoreInfo>>
        val fileCoresP1 = mutableMapOf<String, MutableList<FileCoreInfo>>()
        // 重复的数据
        val fileCoresP2 = mutableMapOf<String, MutableList<FileCoreInfo>>()
        fileCores.forEach {
            (fileCoresP1.containsKey(it.keyValue)).then({
                fileCoresP1[it.keyValue]!!.add(it)
            }, {
                fileCoresP1[it.keyValue] = mutableListOf(it)
            })
            (fileCoresP1[it.keyValue]!!.size > 1).then {
                fileCoresP2[it.keyValue] = fileCoresP1[it.keyValue]!!
            }
        }
        alogd(TAG(), "粗整理 ${fileCoresP2.size}")
        fileCoresP2.entries.forEachIndexed { index, entry ->
            entry.value.forEachIndexed { indexInside, it ->
                alogd(TAG(), "$index - $indexInside , $it")
            }
        }
        var fileCoresP3: MutableMap<String, MutableList<FileCoreInfo>>
        coroutineScope {
            // SHA-256 校验.
            fileCoresP3 = if (needVerify) {
                // 通过协程将所有的 keyValue 改为 sha-256
                convertAccuracyKeyValue(fileCoresP2)
            } else {
                fileCoresP2
            }
        }
        // <mimeType, <keyValue, <FileCoreInfo>>>
        alogd(TAG(), "待按照类别排序")
        // <类型, <相同文件的 sha-256, 相同文件列表<FileCoreInfo>>>
        val result = mutableMapOf<String, MutableMap<String, MutableList<FileCoreInfo>>>()
        fileCoresP3.entries.forEach {
            val firstValue = it.value.first()
            if (result.containsKey(firstValue.extension)) {
                result[firstValue.extension]!![it.key] = it.value
            } else {
                result[firstValue.extension] = mutableMapOf<String, MutableList<FileCoreInfo>>().apply {
                    this[it.key] = it.value
                }
            }
        }
        alogd(TAG(), "最后结果")
        result.entries.forEach { typeEntry ->
            alogd(TAG(), "类型: ${typeEntry.key}, 有${typeEntry.value.size} 个")
            typeEntry.value.entries.forEachIndexed { index, entry ->
                entry.value.forEachIndexed { indexInside, it ->
                    alogd(TAG(), "$index - $indexInside , $it")
                }
            }
        }
        return result
    }

    /**
     * 如果需要精细化处理, 需要按照 sha256 在过滤一次
     */
    private suspend fun convertAccuracyKeyValue(map: MutableMap<String, MutableList<FileCoreInfo>>): MutableMap<String, MutableList<FileCoreInfo>> {
        val fileCores = mutableListOf<FileCoreInfo>()
        coroutineScope {
            val asyncTasks = ArrayList<Deferred<Any>>()
            map.entries.forEach { entries ->
                entries.value.forEach {
                    asyncTasks.add(async {
                        fileCores.add(it.changeKeyValue(File(it.path).hash(Hash.SHA256)))
                    })
                }
            }
            asyncTasks.awaitAll()
        }
        // 粗整理 -> <keyValue, <FileCoreInfo>>
        val fileCoresP1 = mutableMapOf<String, MutableList<FileCoreInfo>>()
        // 重复的数据
        val fileCoresP2 = mutableMapOf<String, MutableList<FileCoreInfo>>()
        fileCores.forEach {
            (fileCoresP1.containsKey(it.keyValue)).then({
                fileCoresP1[it.keyValue]!!.add(it)
            }, {
                fileCoresP1[it.keyValue] = mutableListOf(it)
            })
            (fileCoresP1[it.keyValue]!!.size > 1).then {
                fileCoresP2[it.keyValue] = fileCoresP1[it.keyValue]!!
            }
        }
        alogd(TAG(), "sha-256 精确后 ${fileCoresP2.size}")
        fileCoresP2.entries.forEachIndexed { index, entry ->
            entry.value.forEachIndexed { indexInside, it ->
                alogd(TAG(), "$index - $indexInside , $it")
            }
        }
        return fileCoresP2
    }
}
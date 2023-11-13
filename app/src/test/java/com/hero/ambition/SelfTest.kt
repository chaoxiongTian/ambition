package com.hero.ambition

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

suspend fun main() {
    text1()
}

@OptIn(DelicateCoroutinesApi::class)
suspend fun text1()= runCatching {
    val time = measureTimeMillis{

    }
    var context = Job() + Dispatchers.IO + CoroutineName("aa")
    println("$context")
    println("${context[Job]}")
    println("${context[CoroutineName]}")
    context = context.minusKey(Job)
    println("$context")
//    println("test0")
//    var jobChildren: Job? = null
//    val job = GlobalScope.launch {
//        println("test1 ")
////        delay(1000)
////        println("test2")
////        delay(1000)
//        jobChildren = launch {
//            println("children test 1")
//            delay(200)
//            println("children test 2")
//        }
//        delay(1000)
//    }
//    println("test3, job ${job.string()}")
//    println("test3, children job ${jobChildren?.string()}")
////    job.cancelAndJoin()
//    job.cancel()
//    println("test4, job ${job.string()}")
//    println("test4, children job ${jobChildren?.string()}")
//    job.join()
//    println("test5, job ${job.string()}")
//    println("test5-1, children job ${jobChildren?.string()}")
//    jobChildren?.join()
//    println("test5-2, children job ${jobChildren?.string()}")
}

fun Job.string()=
    "active: $isActive, cancelled: $isCancelled, completed:$isCompleted"

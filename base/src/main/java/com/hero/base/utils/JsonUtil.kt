package com.hero.base.utils


import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.lang.reflect.Type

object JsonUtil {
    private val gson by lazy {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.create();
    }

    fun <T> fromJson(json: String, classOfT: Class<T>): T? {
        return try {
            gson.fromJson(json, classOfT)
        } catch (e: Throwable) {
            null
        }
    }

    fun <T> fromJson(json: Reader, classOfT: Class<T>): T? {
        return try {
            gson.fromJson(json, classOfT)
        } catch (e: Throwable) {
            null
        }
    }

    fun <T> fromJson(json: String, typeOfT: Type): T? {
        return try {
            gson.fromJson<T>(json, typeOfT)
        } catch (e: Throwable) {
            null
        }
    }

    fun <T> fromJson(json: Reader, typeOfT: Type): T? {
        return try {
            gson.fromJson<T>(json, typeOfT)
        } catch (e: Throwable) {
            null
        }
    }

    fun <T> listFromJson(json: String): List<T>? {
        return try {
            fromJson<List<T>>(json, object : TypeToken<List<T>>() {}.type)
        } catch (e: Throwable) {
            null
        }
    }

    fun <T> arrayFromJson(json: String): Array<T>? {
        return try {
            gson.fromJson<Array<T>>(json, object : TypeToken<Array<T>>() {}.type)
        } catch (e: Throwable) {
            null
        }
    }

    fun toJson(src: Any): String {
        return try {
            when (src) {
                is String -> src
                else -> gson.toJson(src)
            }
        } catch (e: Throwable) {
            "{}"
        }
    }
}

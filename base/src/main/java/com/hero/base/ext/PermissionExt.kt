package com.hero.base.ext

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.hero.base.app
import com.permissionx.guolindev.PermissionX

@SuppressLint("InlinedApi")
fun Context.sdCardPermission() = (targetSDk() >= Build.VERSION_CODES.TIRAMISU).then(
    arrayListOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_VIDEO
    ), arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE)
)

@SuppressLint("InlinedApi")
fun Context.sdCardPermissionStatus() = (targetSDk() >= Build.VERSION_CODES.TIRAMISU).then({
    "granted status, image: ${PermissionX.isGranted(app(), Manifest.permission.READ_MEDIA_IMAGES)}, " +
            "audio: ${PermissionX.isGranted(app(), Manifest.permission.READ_MEDIA_AUDIO)}, " +
            "video: ${PermissionX.isGranted(app(), Manifest.permission.READ_MEDIA_VIDEO)}"
}, {
    "granted status, isGranted: ${PermissionX.isGranted(app(), Manifest.permission.READ_EXTERNAL_STORAGE)}"
})


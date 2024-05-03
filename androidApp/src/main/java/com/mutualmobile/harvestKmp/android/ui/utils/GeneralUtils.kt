package com.mutualmobile.harvestKmp.android.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import dev.icerock.moko.resources.StringResource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun Number.toDecimalString(decimalPlaces: Int = 2) = "%.${decimalPlaces}f".format(this)

fun String.isNotAFloat(): Boolean = this.toFloatOrNull() == null
fun String.isAFloat():Boolean = isNotAFloat().not()

@Composable
fun StringResource.get() = stringResource(id = resourceId)

fun String.dateWithoutTimeZone() = substring(0, 10)

fun Context.showToast(
    msg: String, isLongToast: Boolean = true
) {
    Toast.makeText(
        this,
        msg,
        if (isLongToast) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}

//fun createMultipartBody(uri: Uri, multipartName: String): MultipartBody.Part {
//    val documentImage = Util.decodeFile(uri.path!!)
//    val file = File(uri.path!!)
//    val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
//    documentImage.compress(Bitmap.CompressFormat.JPEG, 100, os)
//    os.close()
//    val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//    return MultipartBody.Part.createFormData(name = multipartName, file.name, requestBody)
//}

//fun <A> String.fromJson(type: Class<A>): A {
//    return Gson().fromJson(this, type)
//}
//fun <A> A.toJson(): String? {
//    return Gson().toJson(this)
//}
abstract class JsonNavType<T> : NavType<T>(isNullableAllowed = false) {
    abstract fun fromJsonParse(value: String): T
    abstract fun T.getJsonParse(): String

    override fun get(bundle: Bundle, key: String): T? =
        bundle.getString(key)?.let { parseValue(it) }

    override fun parseValue(value: String): T = fromJsonParse(value)

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, value.getJsonParse())
    }
}
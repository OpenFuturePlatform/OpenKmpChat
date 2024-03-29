
package com.mutualmobile.harvestKmp.android.ui.utils

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ImagePicker(onResult: (ByteArray?) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

//    val launcher = rememberFilePickerLauncher(
//        type = FilePickerFileType.Image,
//        selectionMode = FilePickerSelectionMode.Single,
//        onResult = { files ->
//            coroutineScope.launch(Dispatchers.IO) {
//                onResult(files.firstOrNull()?.readByteArray())
//            }
//        },
//    )
//
//    LaunchedEffect(Unit) {
//        launcher.launch()
//    }
}

fun ByteArray.toComposeImageBitmap(): ImageBitmap {
    return BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap()
}


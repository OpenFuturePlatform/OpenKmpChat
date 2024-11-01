package com.mutualmobile.harvestKmp.android.ui.screens.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.R
import com.mutualmobile.harvestKmp.android.ui.utils.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun noAccountAnnotatedString() = buildAnnotatedString {
    append(MR.strings.dont_have_an_account.get())
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append(" ${MR.strings.try_harvest_free.get()}")
    }
}

@Composable
fun rememberQrBitmapPainter(
    content: String,
    size: Dp = 150.dp,
    padding: Dp = 0.dp
): BitmapPainter {

    val density = LocalDensity.current
    val sizePx = with(density) { size.roundToPx() }
    val paddingPx = with(density) { padding.roundToPx() }


    var bitmap by remember(content) {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(bitmap) {
        if (bitmap != null) return@LaunchedEffect

        launch(Dispatchers.IO) {
            val qrCodeWriter = QRCodeWriter()

            val encodeHints = mutableMapOf<EncodeHintType, Any?>()
                .apply {
                    this[EncodeHintType.MARGIN] = paddingPx
                }

            val bitmapMatrix = try {
                qrCodeWriter.encode(
                    content, BarcodeFormat.QR_CODE,
                    sizePx, sizePx, encodeHints
                )
            } catch (ex: WriterException) {
                null
            }

            val matrixWidth = bitmapMatrix?.width ?: sizePx
            val matrixHeight = bitmapMatrix?.height ?: sizePx

            val newBitmap = Bitmap.createBitmap(
                bitmapMatrix?.width ?: sizePx,
                bitmapMatrix?.height ?: sizePx,
                Bitmap.Config.ARGB_8888,
            )

            for (x in 0 until matrixWidth) {
                for (y in 0 until matrixHeight) {
                    val shouldColorPixel = bitmapMatrix?.get(x, y) ?: false
                    val pixelColor = if (shouldColorPixel) Color.BLACK else Color.WHITE

                    newBitmap.setPixel(x, y, pixelColor)
                }
            }

            bitmap = newBitmap
        }
    }

    return remember(bitmap) {
        val currentBitmap = bitmap ?: Bitmap.createBitmap(
            sizePx, sizePx,
            Bitmap.Config.ARGB_8888,
        ).apply { eraseColor(Color.TRANSPARENT) }

//        val canvas = android.graphics.Canvas(currentBitmap)
//        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
//        val logoSize = sizePx / 5
//        val left = (sizePx - logoSize) / 2
//        val top = (sizePx - logoSize) / 2
//        val scaledLogo = Bitmap.createScaledBitmap(getLogoBitmap(R.drawable.bnb), logoSize, logoSize, false)
//        canvas.drawBitmap(scaledLogo, left.toFloat(), top.toFloat(), null)

        BitmapPainter(currentBitmap.asImageBitmap())
    }
}

@Composable
fun getLogoBitmap(resourceId: Int): Bitmap {
    val context = LocalContext.current
    return BitmapFactory.decodeResource(context.resources, resourceId)
}
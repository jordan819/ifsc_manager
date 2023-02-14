package utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

object ImageLoader {

    fun loadImageOfClimber(imageUrl: String): ImageBitmap? {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()

            val inputStream = connection.inputStream
            val bufferedImage = ImageIO.read(inputStream)

            val stream = ByteArrayOutputStream()
            ImageIO.write(bufferedImage, "png", stream)
            val byteArray = stream.toByteArray()

            return Image.makeFromEncoded(byteArray).toComposeImageBitmap()
        } catch (e: Exception) {
            return null
        }
    }
}

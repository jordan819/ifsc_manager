package utils

import androidx.compose.ui.awt.ComposeWindow
import com.toxicbakery.logging.Arbor
import io.realm.internal.platform.OS_NAME
import java.awt.FileDialog
import java.io.File

class FileHelper {

    fun selectCsvFile(
        window: ComposeWindow? = null,
        title: String = "",
        allowedExtensions: List<String> = listOf("csv")
    ): File? = FileDialog(window, title, FileDialog.LOAD).apply {
        isMultipleMode = false

        if (OS_NAME.contains("Windows")) {
            file = allowedExtensions.joinToString(";") { "*$it" }
        } else if (OS_NAME.contains("Linux")) {
            setFilenameFilter { _, name ->
                allowedExtensions.any {
                    name.endsWith(it)
                }
            }
        } else {
            Arbor.wtf("Unsupported OS")
        }

        isVisible = true
    }.files.firstOrNull()

}

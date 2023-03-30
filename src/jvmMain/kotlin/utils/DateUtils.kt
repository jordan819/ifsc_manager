package utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun formatDate(dateString: String?): Date? {
        if (dateString == null) {
            return null
        }
        val formats = arrayOf("yyyy-MM-dd", "yyyy-MM", "yyyy")
        for (format in formats) {
            try {
                val dateFormat = SimpleDateFormat(format, Locale.getDefault())
                dateFormat.isLenient = false
                return dateFormat.parse(dateString)
            } catch (e: Exception) {
                // Ignore exception and try next format
            }
        }
        return null
    }

}

import com.mutualmobile.harvestKmp.utils.now
import kotlinx.datetime.*


fun timeToString(seconds: Long): String {
    val instant: Instant = Instant.fromEpochMilliseconds(seconds)
    val localTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val now = LocalDateTime.now()
    val durationDiff = now.toInstant(TimeZone.currentSystemDefault()).minus(instant)

    val m = localTime.minute
    val h = localTime.hour
    val d = localTime.date.toString()

    val mm = if (m < 10) {
        "0$m"
    } else {
        m.toString()
    }
    val hh = if (h < 10) {
        "0$h"
    } else {
        h.toString()
    }
    if (durationDiff.inWholeDays < 1)
        return "$hh:$mm"

    return "$d $hh:$mm"
}


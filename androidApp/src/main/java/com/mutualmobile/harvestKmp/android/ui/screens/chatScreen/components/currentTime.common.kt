import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun timeToString(seconds: Long): String {
    val instant: Instant = Instant.fromEpochMilliseconds(seconds)
    val localTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

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

    if (h < 10)
        return "$hh:$mm"
    return "$d $hh:$mm"
}


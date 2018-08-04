package kashish.com.utils

/**
 * Created by Kashish on 31-07-2018.
 */
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class DateUtils{

    companion object {
        fun getDayOfWeek(date: Date): String {
            val c = Calendar.getInstance()
            c.time = date
            return c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        }

        /**
         * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
         */

        fun isDateGreaterThanToday(date: String):Boolean{
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val strDate = sdf.parse(date)
            return System.currentTimeMillis() > strDate.time
        }

        fun getStringDate(date: String): String {
            //String date_ = date;
            val dateArray = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            try {
                val mDate = sdf.parse(date)
                val timeInMilliseconds = mDate.time
                val dateString = formatDate(timeInMilliseconds).split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return dateString[0].substring(0,3) + " " + dateString[1] + ", " + dateArray[0]
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return ""
        }

        fun getDateFromEpoch(epoch: Long):String{
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            return sdf.format(Date(epoch))
        }


        fun formatTime(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(timeInMillis)
        }

        fun formatTimeWithMarker(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            return dateFormat.format(timeInMillis)
        }

        fun getHourOfDay(timeInMillis: Long): Int {
            val dateFormat = SimpleDateFormat("H", Locale.getDefault())
            return Integer.valueOf(dateFormat.format(timeInMillis))!!
        }

        fun getMinute(timeInMillis: Long): Int {
            val dateFormat = SimpleDateFormat("m", Locale.getDefault())
            return Integer.valueOf(dateFormat.format(timeInMillis))!!
        }

        fun getYear(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("YYYY", Locale.getDefault())
            return dateFormat.format(timeInMillis)!!
        }

        /**
         * If the given time is of a different date, display the date.
         * If it is of the same date, display the time.
         * @param timeInMillis  The time to convert, in milliseconds.
         * @return  The time or date.
         */
        fun formatDateTime(timeInMillis: Long): String {
            return if (isToday(timeInMillis)) {
                formatTime(timeInMillis)
            } else if (isYesterday(timeInMillis)) {
                "Yesterday"
            } else {
                formatDate(timeInMillis)
            }
        }

        /**
         * Formats timestamp to 'date month' format (e.g. 'February 3').
         */
        fun formatDate(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
            return dateFormat.format(timeInMillis)
        }

        /**
         * Returns whether the given date is today, based on the user's current locale.
         */
        fun isToday(timeInMillis: Long): Boolean {
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val date = dateFormat.format(timeInMillis)
            return date == dateFormat.format(System.currentTimeMillis())
        }

        fun isYesterday(timeInMillis: Long): Boolean {
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val date = dateFormat.format(timeInMillis)
            return date == dateFormat.format(System.currentTimeMillis() - 86400000)
        }


        /**
         * Checks if two dates are of the same day.
         * @param millisFirst   The time in milliseconds of the first date.
         * @param millisSecond  The time in milliseconds of the second date.
         * @return  Whether {@param millisFirst} and {@param millisSecond} are off the same day.
         */
        fun hasSameDate(millisFirst: Long, millisSecond: Long): Boolean {
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            return dateFormat.format(millisFirst) == dateFormat.format(millisSecond)
        }
    }

}
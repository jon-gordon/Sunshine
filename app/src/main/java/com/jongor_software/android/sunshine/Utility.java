package com.jongor_software.android.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utility {

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(
                context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(
                context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    public static String formatTemperature(Context context, double temperature, boolean isMetric) {
        if (!isMetric) {
            temperature = ((9 / 5) * temperature) + 32;
        }

        return context.getString(R.string.format_temperature, temperature);
    }

    public static String formatDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return DateFormat.getDateInstance().format(date);
    }

    /**
     * Helper method to convert the database representation of the date into something to display to
     * users.  As classy and polished a user experience as "20150301" is, we can do better
     *
     * @param context Context to use for resource localisation
     * @param dateInMillis The date in milliseconds
     * @return A user-friendly representation of the date
     */
    public static String getFriendlyDayString(Context context, long dateInMillis) {
        // The day string for forecast uses the following logic:
        // For today: "Today, June 8"
        // For tomorrow: "Tomorrow"
        // For the next 5 days: "Wednesday" i.e. the day name
        // For all days after that: "Mon Jun 8"

        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);

        // If the date we're building the String for is today's date, the format is
        // "Today, June 24"
        if (julianDay == currentJulianDay) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;

            return String.format(context.getString(
                    formatId,
                    today,
                    getFormattedMonthDay(dateInMillis)));
        }
        else if (julianDay < currentJulianDay + 7) {
            // If the input date is less than a week in the future, just return the day name
            return getDayName(context, dateInMillis);
        }
        else {
            // Otherwise, use the form "Mon Jun 3"
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return  shortenedDateFormat.format(dateInMillis);
        }
    }

    /**
     * Given a day, returns just the name to use for that day
     * E.g. "today", "tomorrow", 'Wednesday"
     *
     * @param context Context to use for resource localisation
     * @param dateInMillis The date in milliseconds
     * @return Day representation in simple format
     */
    public static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localised version of "Today" instead of actual day name

        Time time = new Time();
        time.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), time.gmtoff);

        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        }
        else if (julianDay == currentJulianDay + 1) {
            return context.getString(R.string.tomorrow);
        }
        else {
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    /**
     * Converts the db date format to the format "Month day", e.g "June 24"
     *
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     *                     in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(long dateInMillis) {
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        return monthDayFormat.format(dateInMillis);
    }

    public static String getFormattedWind(Context context, float windSpeed, float degrees) {
        int windFormat;
        if (Utility.isMetric(context)) {
            windFormat = R.string.format_wind_kmh;
        }
        else {
            windFormat = R.string.format_wind_mph;
            windSpeed *= 0.621371192237334f;
        }

        // From wind direction in degrees, determine compass direction as a string (e.g. NW)
        int direction = R.string.direction_unknown;
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = R.string.direction_north;
        }
        else if (degrees >= 22.5 || degrees < 67.5) {
            direction = R.string.direction_northeast;
        }
        else if (degrees >= 67.5 || degrees < 112.5) {
            direction = R.string.direction_east;
        }
        else if (degrees >= 112.5 || degrees < 157.5) {
            direction = R.string.direction_southeast;
        }
        else if (degrees >= 157.5 || degrees < 202.5) {
            direction = R.string.direction_south;
        }
        else if (degrees >= 202.5 || degrees < 247.5) {
            direction = R.string.direction_southwest;
        }
        else if (degrees >= 247.5 || degrees < 292.5) {
            direction = R.string.direction_west;
        }
        else if (degrees >= 292.5 || degrees < 337.5) {
            direction = R.string.direction_northwest;
        }

        return String.format(context.getString(windFormat), windSpeed, context.getString(direction));
    }
}

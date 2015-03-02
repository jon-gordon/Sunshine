package com.jongor_software.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare high/lows for presentation
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        return Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
    }

    /**
     * This is ported from FetchWeatherTask
     */
    private String convertCursorRowToUXFormat(Cursor c) {
        String highAndLow = formatHighLows(
                c.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                c.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(c.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + c.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Our view is simply a text view - keep UI functional with a simple (slow!) binding

        // Read weather icon from cursor
        //int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        // Use placeholder for now
        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        iconView.setImageResource(R.drawable.ic_launcher);

        // Read date from cursor
        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        dateView.setText(Utility.getFriendlyDayString(context, date));

        // Read weather forecast
        String forecast = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        TextView forecastView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        forecastView.setText(forecast);

        // Read user preference for metric/imperial
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        TextView highView = (TextView) view.findViewById(R.id.list_item_high_textview);
        highView.setText(Utility.formatTemperature(high, isMetric));

        // Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        TextView lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
        lowView.setText(Utility.formatTemperature(low, isMetric));
    }
}

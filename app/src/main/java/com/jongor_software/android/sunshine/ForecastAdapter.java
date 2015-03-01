package com.jongor_software.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
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
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Our view is simply a text view - keep UI functional with a simple (slow!) binding
        TextView textView = (TextView)view;
        textView.setText(convertCursorRowToUXFormat(cursor));
    }
}

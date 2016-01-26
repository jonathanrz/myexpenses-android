package br.com.jonathanzanella.myexpenses.converter;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.joda.time.DateTime;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public final class DateTimeConverter extends TypeConverter<String, DateTime> {

    @Override
    public String getDBValue(DateTime model) {
        if (model != null) {
            return model.toString();
        }

        return null;
    }

    @Override
    public DateTime getModelValue(String data) {
        if (data != null) {
            return DateTime.parse(data);
        }

        return null;
    }

}
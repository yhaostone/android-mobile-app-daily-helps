package com.example.a100026051.customapp;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by 100026051 on 11/1/16.
 */

public class SetTime implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private EditText editText;
    private Calendar myCalendar;
    private Context ctx;

    public SetTime(EditText editText, Context ctx){
        this.ctx = ctx;
        this.editText = editText;
        this.editText.setOnClickListener(this);
        this.myCalendar = Calendar.getInstance();

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.HOUR, hourOfDay);
        myCalendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        this.editText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View view) {
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        new TimePickerDialog(ctx, this, hour, minute, true).show();
    }
}

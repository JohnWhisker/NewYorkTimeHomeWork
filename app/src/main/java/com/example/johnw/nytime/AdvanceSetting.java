package com.example.johnw.nytime;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdvanceSetting extends Activity implements AdapterView.OnItemSelectedListener {
    TextView tvBeginDate,tvEndDate;
    Switch swBeginDate,swEndDate,swField;
    int mYear,mMonth,mDay;
    Spinner spField;
    ActionBar actionBar;
    String spValue,beginDateValue,endDateValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_setting);
        actionBar = getActionBar();
        tvBeginDate = (TextView) findViewById(R.id.tvBeginDate);
        tvEndDate = (TextView) findViewById(R.id.tvEndDate);
        spField = (Spinner) findViewById(R.id.spSearchField);
        spField.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        categories.add("Arts");
        categories.add("Sports");
        categories.add("Fashion & Style");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spField.setAdapter(dataAdapter);
    }
    public String convertDateTime(int year,int month,int day){
        String y,m,d;
        y = String.valueOf(year);
        if(month<10) m = "0" + String.valueOf(month);
                else m= String.valueOf(month);
        if(day<10) d = "0" + String.valueOf(day);
                else d = String.valueOf(day);
        return y+m+d;
    }
    public void beginDateOnClick(View v){
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear,int dayOfMonth) {
                        beginDateValue = (dayOfMonth + "-"
                                + (monthOfYear + 1) + "-" + year);
                        tvBeginDate.setText(beginDateValue);
                        beginDateValue = convertDateTime(year,monthOfYear,dayOfMonth);
                        swBeginDate = (Switch) findViewById(R.id.swBeginDate);
                        swBeginDate.setChecked(true);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();


    }
    public void endDateOnClick(View v){
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear,int dayOfMonth) {
                        endDateValue = (dayOfMonth + "-"
                                + (monthOfYear + 1) + "-" + year);
                        tvBeginDate.setText(endDateValue);
                        endDateValue = convertDateTime(year,monthOfYear,dayOfMonth);
                        swEndDate = (Switch) findViewById(R.id.swEndDate);
                        swEndDate.setChecked(true);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();

    }
    public void cancelOnClick(View v){

        finish();
    }

    public void okOnClick(View v){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        swBeginDate = (Switch) findViewById(R.id.swBeginDate);
        swEndDate = (Switch) findViewById(R.id.swEndDate);
        swField = (Switch) findViewById(R.id.swField);
        if(swBeginDate.isChecked()) bundle.putString("Begin Date",beginDateValue);
        if(swEndDate.isChecked())bundle.putString("End Date",endDateValue);
        if(swField.isChecked())bundle.putString("Field",spValue);
        intent.putExtra("DATA",bundle);
        setResult(RESULT_OK, intent);
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        swField = (Switch) findViewById(R.id.swField);
        swField.setChecked(true);
        spValue = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



}

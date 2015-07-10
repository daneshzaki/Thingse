package com.daneshzaki.thingse;

import java.util.Calendar;

import android.app.*;
import android.os.Bundle;
import android.app.DialogFragment;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;

//This class displays the date picker and sets the selected date in the screen 
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{

	//create the dialog with the date picker
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	//set the selected date in the ui control
	public void onDateSet(DatePicker view, int year, int month, int day)
	{
		//set selected date
		month++;

		String monStr = String.valueOf(month);
		String dayStr = String.valueOf(day);
				
		//append zeros to fix sort issue
		if(month < 10)
		{
			monStr = "0"+month;
		}
		
		if(day < 10)
		{
			dayStr = "0"+day;
		}
		
		selDate = year+"-"+monStr+"-"+dayStr;
		
		Log.i("DatePickerFragment", "sel date is "+selDate);
		datePurchButton.setText(selDate);
	}

	
	//utility method to return the selected date
	public String getSelectedDate()
	{
		return selDate;
	}
	
	//to set the control that will display the selected date 
	public void setDatePurchButton(Button datePurchButton)
	{
		this.datePurchButton = datePurchButton;
	}
	
	
	//date purchased Button
	private Button datePurchButton = null;

	//selected date
	private String selDate = "";
}
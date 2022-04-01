package com.bobcikprogramming.kryptoevidence.Controller;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bobcikprogramming.kryptoevidence.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarManager {

    private SharedMethods shared;

    public CalendarManager(){
        shared = new SharedMethods();
    }

    public void openDateDialogWindow(Activity activity, DatePickerDialog.OnDateSetListener dateSetListener, String date){
        String[] dateSplit = null;
        if(date != null){
            dateSplit = date.split("\\.");
        }

        Calendar calendar = Calendar.getInstance();
        int year = dateSplit == null ? calendar.get(Calendar.YEAR) : Integer.parseInt(dateSplit[2]);
        int month = dateSplit == null ? calendar.get(Calendar.MONTH) : Integer.parseInt(dateSplit[1]) - 1;
        int day = dateSplit == null ? calendar.get(Calendar.DAY_OF_MONTH) : Integer.parseInt(dateSplit[0]); //day of month -> protože měsíce mají různý počet dní
        DatePickerDialog dialog = new DatePickerDialog(
                activity, R.style.TimeDatePicker, dateSetListener, year, month, day
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public String returnDate(int year, int month, int day){
        String result = "";

        month = month + 1; // bere se od 0
        String date = day + "." + month + "." + year;
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy");
        SimpleDateFormat dateFormatSecond = new SimpleDateFormat("dd.MM.yyyy");
        try{
            Date dateFormatToShow = dateFormat.parse(date);
            result = dateFormatSecond.format(dateFormatToShow);
        }
        catch (Exception e){
            System.err.println("Chyba při parsování data: "+e);
        }

        return result;
    }

    public void openTimeDialogWindow(Activity activity, TimePickerDialog.OnTimeSetListener timeSetListener, String time){
        String[] timeSplit = null;
        if(time != null){
            timeSplit = time.split(":");
        }

        Calendar calendar = Calendar.getInstance();
        int hour = timeSplit == null ? calendar.get(Calendar.HOUR_OF_DAY) : Integer.parseInt(timeSplit[0]);
        int minute = timeSplit == null ? calendar.get(Calendar.MINUTE) : Integer.parseInt(timeSplit[1]);
        TimePickerDialog dialog = new TimePickerDialog(
                activity, R.style.TimeDatePicker, timeSetListener, hour, minute, true
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public String returnTime(int hour, int minute){
        String result = "";

        String time = hour + ":" + minute;
        SimpleDateFormat dateFormat = new SimpleDateFormat("H:m");
        SimpleDateFormat dateFormatSecond = new SimpleDateFormat("HH:mm");
        try{
            Date timeToShow = dateFormat.parse(time);
            result = dateFormatSecond.format(timeToShow);
        }
        catch (Exception e){
            System.err.println("Chyba při parsování času: "+e);
        }

        return result;
    }

    public boolean checkDateAndTime(Context context, TextView dateToCheck, TextView dateToCheckDes, TextView timeToCheck, TextView timeToCheckDes){
        Animation animShake = AnimationUtils.loadAnimation(context, R.anim.shake);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try{
            Date actualDate = dateFormat.parse(getActualDay());
            Date transactionDate = dateFormat.parse(shared.getString(dateToCheck));
            /** https://stackoverflow.com/questions/2592501/how-to-compare-dates-in-java */
            if(actualDate.before(transactionDate)){
                dateToCheckDes.startAnimation(animShake);
                dateToCheckDes.setTextColor(ContextCompat.getColor(context, R.color.red));
                return false;
            }else if(!actualDate.after(transactionDate)) {
                Date actualTime = getTimeFormat(getActualTime());
                Date transactionTime = getTimeFormat(shared.getString(timeToCheck));
                if (actualTime.compareTo(transactionTime) < 0) {
                    timeToCheckDes.startAnimation(animShake);
                    timeToCheckDes.setTextColor(ContextCompat.getColor(context, R.color.red));
                    return false;
                }
            }
            return true;
        }
        catch (Exception e){
            System.err.println("Chyba při parsování data: "+e);
            dateToCheckDes.startAnimation(animShake);
            dateToCheckDes.setTextColor(ContextCompat.getColor(context, R.color.red));
            return false;
        }
    }

    public String getActualDay(){
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat dateFormatCompare = new SimpleDateFormat("dd.MM.yyyy");
        String actualDay = dateFormatCompare.format(calendarDate.getTime());
        return actualDay;
    }

    public String getActualYear(){
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat dateFormatCompare = new SimpleDateFormat("yyyy");
        String actualYear = dateFormatCompare.format(calendarDate.getTime());
        return actualYear;
    }

    public String getActualTime(){
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat dateFormatCompare = new SimpleDateFormat("HH:mm");
        String actualTime = dateFormatCompare.format(calendarDate.getTime());
        return actualTime;
    }

    public String getActualDateFolderNameFormat(){
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat dateFormatCompare = new SimpleDateFormat("yyyyMMddHHmmss");
        String actualDate = dateFormatCompare.format(calendarDate.getTime());
        return actualDate;
    }

    public Date getDateFormat(String dateInString){
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            date = dateFormat.parse(String.valueOf(dateInString));
        }catch (ParseException e) {
            System.err.println("Chyba při parsování data: "+e);
        }
        return date;
    }

    public Date getTimeFormat(String timeInString){
        Date time = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try{
            time = dateFormat.parse(String.valueOf(timeInString));
        }
        catch (Exception e){
            System.err.println("Chyba při parsování času: "+e);
        }
        return time;
    }

    public void openDateDialogWindowForFilter(boolean isDateFrom, DatePickerDialog.OnDateSetListener dateSetListener, TextView tvDateFrom, TextView tvDateTo, Activity activity, Calendar calendarDateTo){
        FragmentTransactionsController controller = new FragmentTransactionsController();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH); //day of month -> protože měsíce mají různý počet dní

        if(controller.isSetDateFrom() && isDateFrom){
            String[] dateSplit = shared.getString(tvDateFrom).split("\\.");
            day = Integer.parseInt(dateSplit[0]);
            month = Integer.parseInt(dateSplit[1]) - 1;
            year = Integer.parseInt(dateSplit[2]);
        }

        if(controller.isSetDateTo() && !isDateFrom){
            String[] dateSplit = shared.getString(tvDateTo).split("\\.");
            day = Integer.parseInt(dateSplit[0]);
            month = Integer.parseInt(dateSplit[1]) - 1;
            year = Integer.parseInt(dateSplit[2]);
        }

        DatePickerDialog dialog = new DatePickerDialog(
                activity, R.style.TimeDatePicker, dateSetListener, year, month, day
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        long maxDate = isDateFrom ? calendarDateTo.getTimeInMillis() : System.currentTimeMillis(); /** https://stackoverflow.com/a/11430439 */
        dialog.getDatePicker().setMaxDate(maxDate); /** https://stackoverflow.com/a/20971151 */
        dialog.show();
    }

    public long getDateMillis(String dateInString){
        return getDateFormat(dateInString).getTime();
    }

    public String getDateFromMillis(long millis){
        Date date = new Date(millis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(date);
    }
}

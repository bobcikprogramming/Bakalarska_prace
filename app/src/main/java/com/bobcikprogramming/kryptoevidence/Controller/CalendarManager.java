package com.bobcikprogramming.kryptoevidence.Controller;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bobcikprogramming.kryptoevidence.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Třída pro práci s daty
 */
public class CalendarManager {

    private SharedMethods shared;

    public CalendarManager(){
        this.shared = new SharedMethods();
    }

    /**
     * Metoda pro nastavení dialogového okna pro výběr data. Výchozí hodnoty jsou v případě již zvoleného data nastaveny na dané datum, jinak na aktuální datum
     * @param activity Třída activity ze které je metoda volána
     * @param dateSetListener OnDateSetListener jenž naslouhá na dokončení výběru
     * @param date Nastavené datum nebo null, nebylo-li ještě nastaveno
     *
     * Nastavení limitu data:
     * Zdroj:   Stack Overflow
     * Dotaz:   https://stackoverflow.com/q/20970963
     * Odpověď: https://stackoverflow.com/a/20971151
     * Autor:   laalto
     * Autor:   https://stackoverflow.com/users/101361/laalto
     * Datum:   7. ledna 2014
     */
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
        long maxDate = System.currentTimeMillis();
        dialog.getDatePicker().setMaxDate(maxDate);
        dialog.show();
    }

    /**
     * Metoda slouží pro zpracování výsledku DatePickerDialog na datum daného formátu (dd.MM.yyyy)
     * @param year Zvolený rok
     * @param month Zvolený měsíc
     * @param day Zvolený den
     * @return Stringová hodnota data v daném formátu
     */
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

    /**
     * Metoda pro nastavení dialogového okna pro výběr času. Výchozí hodnoty jsou v případě již zvoleného času nastaveny na daný čas, jinak na aktuální čas
     * @param activity Třída activity ze které je metoda volána
     * @param timeSetListener OnTimeSetListener jenž naslouhá na dokončení výběru
     * @param time Nastavený čas nebo null, nebyl-li ještě nastaven
     */
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

    /**
     * Metoda slouží pro zpracování výsledku TimePickerDialog na čas daného formátu (HH:mm)
     * @param hour Zvolená hodina
     * @param minute Zvolená minuta
     * @return Stringová hodnota času v daném formátu
     */
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

    /**
     * Metoda slouží ke kontrole, zda-li navolený datum a čas nepřekročili hodnotu aktuálního času
     * @param context Třída context activity, ze které je metoda volána
     * @param dateToCheck Ukazatel na text view obsahující datum
     * @param dateToCheckDes Ukazatel na text view s popisem pro datum
     * @param timeToCheck Ukazatel na text view obsahující čas
     * @param timeToCheckDes Ukazatel na text view s popisem pro čas
     * @return boolean hodnotu, zda-li je datum korektní
     *
     * Porovnání data a času:
     * Zdroj:   Stack Overflow
     * Dotaz:   https://stackoverflow.com/q/2592501
     * Odpověď: https://stackoverflow.com/a/2592513
     * Autor:   Bart Kiers
     * Autor:   https://stackoverflow.com/users/50476/bart-kiers
     * Datum:   7. dubna 2010
     */
    public boolean checkDateAndTime(Context context, TextView dateToCheck, TextView dateToCheckDes, TextView timeToCheck, TextView timeToCheckDes){
        Animation animShake = AnimationUtils.loadAnimation(context, R.anim.shake);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try{
            Date actualDate = dateFormat.parse(getActualDay());
            Date transactionDate = dateFormat.parse(shared.getString(dateToCheck));
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


    /**
     * Metoda pro získání aktuální data v daném formátu (dd.MM.yyyy)
     * @return Stringová hodnota aktuálního data
     */
    public String getActualDay(){
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat dateFormatCompare = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormatCompare.format(calendarDate.getTime());
    }

    /**
     * Metoda pro získání aktuálního roku
     * @return Stringová hodnota aktuálního roku
     */
    public String getActualYear(){
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat dateFormatCompare = new SimpleDateFormat("yyyy");
        return dateFormatCompare.format(calendarDate.getTime());
    }

    /**
     * Metoda pro získání aktuálního času
     * @return Stringová hodnota aktuálního času
     */
    public String getActualTime(){
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat dateFormatCompare = new SimpleDateFormat("HH:mm");
        return dateFormatCompare.format(calendarDate.getTime());
    }

    /**
     * Metoda pro získání aktuálního data a času ve formátu (yyyyMMddHHmmss)
     * @return Stringová hodnota aktuálního data a času
     */
    public String getActualDateFolderNameFormat(){
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat dateFormatCompare = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormatCompare.format(calendarDate.getTime());
    }

    /**
     * Metoda pro převod data ve stringová podobě na formát Data (dd.MM.yyyy)
     * @param dateInString Stringová hodnota data
     * @return Datum ve formátu Date
     */
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

    /**
     * Metoda pro převod času ve stringová hodnotě na formát Date (HH:mm)
     * @param timeInString Stringová hodnota času
     * @return Čas ve formátu Date
     */
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

    /**
     * Metoda pro nastavení dialogového okna pro výběr data. Výchozí hodnoty jsou zvoleny dle nastavení filteru
     * @param isDateFrom boolean hodnota, zda-li se jedná o nastavení dialogového okna pro volbu data "OD"
     * @param dateSetListener  OnDateSetListener jenž naslouhá na dokončení výběru
     * @param tvDateFrom Ukazatel na text view obsahující datum "OD"
     * @param tvDateTo Ukazatel na text view obsahující datum "DO"
     * @param activity Třída activity ze které je metoda volána
     * @param calendarDateTo Instance třídy Calendar pro nastavení vlastní maximální hodnoty
     */
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
        long maxDate = isDateFrom ? calendarDateTo.getTimeInMillis() : System.currentTimeMillis();
        dialog.getDatePicker().setMaxDate(maxDate);
        dialog.show();
    }

    /**
     * Metoda na převod data na milisekundy
     * @param dateInString Stringová hodnota data
     * @return Datum v milisekundách typu long
     */
    public long getDateMillis(String dateInString){
        return getDateFormat(dateInString).getTime();
    }

    /**
     * Metoda pro nastavení aktuálního data na milisekundy
     * @return Datum v milisekundách typu long
     */
    public long getActualDateMillis(){
        return getDateMillis(getActualDay());
    }

    /**
     * Metoda pro nastavení aktuální data a času na milisekundy
     * @return Datum a čas v milisekundách typu long
     */
    public long getActualDateTimeMillis(){
        return Long.parseLong(getActualDateFolderNameFormat());
    }

    /**
     * Metoda pro získání data z milisekund
     * @param millis Datum v milisekundách typu long
     * @return Stringová hodnota data
     */
    public String getDateFromMillis(long millis){
        Date date = new Date(millis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(date);
    }
}

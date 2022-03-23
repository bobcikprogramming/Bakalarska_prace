package com.bobcikprogramming.kryptoevidence.Controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;

import com.bobcikprogramming.kryptoevidence.R;
import com.bobcikprogramming.kryptoevidence.View.FragmentPDF;

import java.util.Calendar;

/** https://stackoverflow.com/a/53300524 */
public class MonthYearPickerDialog extends DialogFragment {

    private CalendarManager calendar;
    private DatePickerDialog.OnDateSetListener listener;
    private FragmentPDFController pdfController;

    public MonthYearPickerDialog(FragmentPDFController pdfController){
        this.pdfController = pdfController;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        calendar = new CalendarManager();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.month_year_picker_dialog, null);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);
        monthPicker.setWrapSelectorWheel(false);
        yearPicker.setWrapSelectorWheel(false);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(Integer.parseInt(calendar.getActualYear()));
        yearPicker.setValue(year);

        builder.setView(dialog).setPositiveButton("Vybrat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                pdfController.setSelectedYear(String.valueOf(yearPicker.getValue()));
            }
        }).setNegativeButton("Zrušit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        return builder.create();
    }
}

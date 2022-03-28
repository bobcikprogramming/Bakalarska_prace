package com.bobcikprogramming.kryptoevidence.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PDFGenerator {
    private AssetManager assetManager;

    private PDDocument doc;
    private PDFont font = null, fontBold = null;
    private PDPageContentStream contentStream;
    private PDPage page;

    private CalendarManager calendar;

    private float MARGINSIDE = 20;
    private float MARGINTOP = 60;
    private float MARGINBOTTOM = 30;
    private float curYVal;

    public PDFGenerator(AssetManager assetManager, Context context, Activity activity){
        PDFBoxResourceLoader.init(context);

        doc = new PDDocument();
        this.assetManager = assetManager;

        calendar = new CalendarManager();

        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
    }

    public void createPDF(String selectedYear,  ArrayList<SellTransactionPDFList> sellList) throws IOException {
        loadFonts();
        createNewPage();
        int lastPageNum = insertSell(sellList);


        // Přidání zápatí v případě, že stránka nebyla zcela zaplněna
        if(curYVal - 15f > 65) {
            createFooter(String.valueOf(lastPageNum));
        }

        contentStream.close();
        String dirName = Environment.getExternalStorageDirectory() + "/kryptoevidence_pdf";
        File directory = new File(dirName);
        if(!directory.exists()){
            directory.mkdir();
        }
        File path = new File(dirName, selectedYear +"_"+ calendar.getActualDateFolderNameFormat() + ".pdf");
        doc.save(path);
        doc.close();
    }

    private void loadFonts() throws IOException {
        font = PDType0Font.load(doc, assetManager.open("roboto_regular.ttf"));
        fontBold = PDType0Font.load(doc, assetManager.open("roboto_bold.ttf"));
    }

    private void createNewPage() throws IOException {
        page = new PDPage(PDRectangle.A4);
        contentStream = new PDPageContentStream(doc, page);

        float height = page.getMediaBox().getHeight();
        float width = page.getMediaBox().getWidth() - (MARGINSIDE * 2);
        curYVal = height - MARGINTOP;

        doc.addPage(page);
        contentStream.beginText();

        String taxDate = "Daňové období:";
        contentStream.setLeading(15f);
        writeTextNewLineAtOffset(taxDate, font, 14, 20, curYVal);

        float textWidth = (fontBold.getStringWidth(taxDate) / 1000.0f) * 14 + 2;
        writeTextNewLineAtOffset("2022", fontBold, 17, textWidth, 0);

        contentStream.setNonStrokingColor(100, 100, 100);
        String createdBy = "Vytvořeno pomocí aplikace";
        textWidth = (font.getStringWidth(createdBy) / 1000.0f) * 10 + textWidth + 5;
        writeTextNewLineAtOffset(createdBy, font, 10, width-textWidth, 17);

        contentStream.setNonStrokingColor(50, 50, 50);
        String appName = "KryptoEvidence";
        textWidth = (font.getStringWidth(createdBy) / 1000.0f) * 10 - (font.getStringWidth(appName) / 1000.0f) * 12;
        writeTextNewLineAtOffset(appName, font, 12, textWidth, -15);

        contentStream.endText();

        contentStream.setNonStrokingColor(200, 200, 200);
        contentStream.addRect(20, curYVal - 5, page.getMediaBox().getWidth()-(MARGINSIDE*2), 0.75f);
        contentStream.fill();

        contentStream.setNonStrokingColor(0, 0, 0);
        curYVal -= 55f;
    }

    private void writeTextNewLineAtOffset(String text, PDFont font, float fontSize, float tx, float ty ) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(tx, ty);
        contentStream.showText(text);
    }

    private void writeTextNewLine(String text, PDFont font, float fontSize) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.newLine();
        contentStream.showText(text);
    }

    private int insertSell(ArrayList<SellTransactionPDFList> sellList) throws IOException {
        int pageNum = 1;
        float width = page.getMediaBox().getWidth() - (MARGINSIDE * 2);
        float cellWidthXPos = (width/6f);
        boolean first = true;
        float textWidth = 0f;
        // Vložit popis tabulky
        createSellHeadline(width, cellWidthXPos);

        contentStream.beginText();
        System.out.println(">>>>>>>>>>>>>> sellList size: "+sellList.size());
        for(SellTransactionPDFList sell : sellList){
            // Dokud mám nějaký prodej
            if(curYVal - 20f > 70f) {
                // Dokud jsem nepřetekl obsah stránky
                // Vypsat prodej
                curYVal -= 20f;

                contentStream.setLeading(20f);
                contentStream.setFont(font, 12);
                if(first) {
                    contentStream.newLineAtOffset(20, curYVal);
                    first = false;
                }else{
                    float moveLeft = 5*cellWidthXPos + (cellWidthXPos-textWidth);
                    contentStream.newLineAtOffset(-moveLeft, -20);
                }
                contentStream.showText(sell.getDate());

                contentStream.newLineAtOffset(cellWidthXPos - 20, 0);
                contentStream.showText(sell.name);

                contentStream.newLineAtOffset(cellWidthXPos - 20, 0);
                contentStream.showText(sell.quantity);

                contentStream.newLineAtOffset(cellWidthXPos + 40, 0);
                contentStream.showText(sell.profit);

                contentStream.newLineAtOffset(cellWidthXPos, 0);
                contentStream.showText(sell.fee);

                textWidth = (font.getStringWidth(sell.total) / 1000.0f) * 12;
                contentStream.newLineAtOffset(cellWidthXPos + (cellWidthXPos-textWidth), 0);
                contentStream.showText(sell.total);

            }else{
                // Obsah přetekl stránku
                contentStream.endText();

               // Přidat zápatí
                createFooter(String.valueOf(pageNum));

                // Vytvořit novou stránku
                createNewPage();

                // Vložit prodej
                contentStream.beginText();

                pageNum += 1;
            }
        }
        contentStream.endText();
        return pageNum;
    }

    private void createFooter(String pageNum) throws IOException {
        float width = page.getMediaBox().getWidth() - (MARGINSIDE * 2);
        curYVal = 50;
        contentStream.setNonStrokingColor(200, 200, 200);
        contentStream.addRect(20, curYVal, width, 0.75f);
        contentStream.fill();

        curYVal -= 20;

        contentStream.beginText();
        String created = "Vytvořeno:";
        contentStream.setNonStrokingColor(100, 100, 100);
        contentStream.setLeading(15f);
        writeTextNewLineAtOffset(created, font, 10, 20, curYVal);

        float textWidth = (fontBold.getStringWidth(created) / 1000.0f) * 10 + 2;
        contentStream.setNonStrokingColor(50, 50, 50);
        writeTextNewLineAtOffset(calendar.getActualDay(), fontBold, 11, textWidth, 0);

        contentStream.setNonStrokingColor(0, 0, 0);
        textWidth = (font.getStringWidth(pageNum) / 1000.0f) * 10 + textWidth + 5;
        writeTextNewLineAtOffset(pageNum, font, 10, width-textWidth, 0);
    }

    private void createSellHeadline(float width, float cellWidthXPos) throws IOException {
        contentStream.beginText();

        contentStream.setLeading(25f);
        writeTextNewLineAtOffset("Prodej", fontBold, 16, 20, curYVal);
        curYVal -= 25f;

        writeTextNewLine("Datum", font, 14);

        contentStream.newLineAtOffset(cellWidthXPos - 20, 0);
        contentStream.showText("Název");

        contentStream.newLineAtOffset(cellWidthXPos - 20, 0);
        contentStream.showText("Množství");

        contentStream.newLineAtOffset(cellWidthXPos + 40, 0);
        contentStream.showText("Cena");

        contentStream.newLineAtOffset(cellWidthXPos, 0);
        contentStream.showText("Poplatek");

        String value = "Celkový výdaj";
        float textWidth = (font.getStringWidth(value) / 1000.0f) * 14;
        contentStream.newLineAtOffset(cellWidthXPos + (cellWidthXPos-textWidth), 0);
        contentStream.showText(value);

        curYVal -= 10f;
        contentStream.endText();

        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.addRect(20, curYVal, width, 3);
        contentStream.fill();
    }
}

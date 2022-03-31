package com.bobcikprogramming.kryptoevidence.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;

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
import java.math.BigDecimal;
import java.util.ArrayList;

public class PDFGenerator {
    private AssetManager assetManager;

    private PDDocument doc;
    private PDFont font = null, fontBold = null;
    private PDPageContentStream contentStream;
    private PDPage page;

    private CalendarManager calendar;
    private SharedMethods shared;

    private float MARGINSIDE = 20;
    private float MARGINTOP = 60;
    private float MARGINBOTTOM = 30;
    private float curXVal;
    private float curYVal;
    private float width;
    private float cellWidthXPos;
    private int pageNum = 1;
    private boolean firstPageRow;

    public PDFGenerator(AssetManager assetManager, Context context, Activity activity){
        PDFBoxResourceLoader.init(context);

        doc = new PDDocument();
        this.assetManager = assetManager;

        calendar = new CalendarManager();
        shared = new SharedMethods();

        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
    }

    public void createPDF(String selectedYear,  ArrayList<BuyTransactionPDFList> buyList,  ArrayList<SellTransactionPDFList> sellList) throws IOException {
        loadFonts();
        createNewPage();
        cellWidthXPos = (width/6f);
        createBuy(buyList);
        createSell(sellList);


        // Přidání zápatí v případě, že stránka nebyla zcela zaplněna
        if(curYVal - 15f > 65) {
            createFooter(String.valueOf(pageNum));
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

    private void writeTextNewLineAtOffset(String text, PDFont font, float fontSize, float tx, float ty ) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(tx, ty);
        contentStream.showText(text);
        curXVal += tx;
    }

    private void loadFonts() throws IOException {
        font = PDType0Font.load(doc, assetManager.open("roboto_regular.ttf"));
        fontBold = PDType0Font.load(doc, assetManager.open("roboto_bold.ttf"));
    }

    private void createNewPage() throws IOException {
        firstPageRow = true;
        page = new PDPage(PDRectangle.A4);
        contentStream = new PDPageContentStream(doc, page);

        float height = page.getMediaBox().getHeight();
        width = page.getMediaBox().getWidth() - (MARGINSIDE * 2);
        curYVal = height - MARGINTOP;
        curXVal = MARGINSIDE;

        doc.addPage(page);
        contentStream.beginText();
        curXVal = 0f;

        String taxDate = "Daňové období:";
        contentStream.setLeading(15f);
        writeTextNewLineAtOffset(taxDate, font, 14, MARGINSIDE, curYVal);

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
        contentStream.addRect(MARGINSIDE, curYVal - 5, page.getMediaBox().getWidth()-(MARGINSIDE*2), 0.75f);
        contentStream.fill();

        contentStream.setNonStrokingColor(0, 0, 0);
        curYVal -= 55f;
    }

    private void createFooter(String pageNum) throws IOException {
        float width = page.getMediaBox().getWidth() - (MARGINSIDE * 2);
        curYVal = 50;
        contentStream.setNonStrokingColor(200, 200, 200);
        contentStream.addRect(MARGINSIDE, curYVal, width, 0.75f);
        contentStream.fill();

        curYVal -= 20;

        contentStream.beginText();
        curXVal = 0f;
        String created = "Vytvořeno:";
        contentStream.setNonStrokingColor(100, 100, 100);
        contentStream.setLeading(15f);
        writeTextNewLineAtOffset(created, font, 10, MARGINSIDE, curYVal);

        float textWidth = (fontBold.getStringWidth(created) / 1000.0f) * 10 + 2;
        contentStream.setNonStrokingColor(50, 50, 50);
        writeTextNewLineAtOffset(calendar.getActualDay(), fontBold, 11, textWidth, 0);

        contentStream.setNonStrokingColor(0, 0, 0);
        textWidth = (font.getStringWidth(pageNum) / 1000.0f) * 10 + textWidth + 5;
        writeTextNewLineAtOffset(pageNum, font, 10, width-textWidth, 0);
        contentStream.endText();
    }

    /** PDF sekce nákupu */
    private void createBuy(ArrayList<BuyTransactionPDFList> buyList) throws IOException {
        float lastTextWidth = 0f;
        // Vložit popis tabulky
        createBuyHeadline(true);

        BigDecimal buyTotal = BigDecimal.ZERO;

        contentStream.beginText();
        curXVal = 0f;
        for(int i = 0; i < 19; i++) {
            for (BuyTransactionPDFList buy : buyList) {
                // Dokud mám nějaký prodej
                contentStream.setLeading(20f);

                if (curYVal - 20f > 70f) {
                    // Dokud jsem nepřetekl obsah stránky
                    // Vypsat prodej
                    lastTextWidth = insertBuy(cellWidthXPos, buy, lastTextWidth);
                    firstPageRow = false;

                } else {
                    // Obsah přetekl stránku
                    contentStream.endText();

                    // Přidat zápatí
                    createFooter(String.valueOf(pageNum));

                    // Vytvořit novou stránku
                    contentStream.close();
                    createNewPage();
                    contentStream.setNonStrokingColor(150, 150, 150);
                    createSellHeadline(false);
                    contentStream.setNonStrokingColor(0, 0, 0);
                    contentStream.setFont(font, 12);

                    // Vložit prodej
                    contentStream.beginText();
                    curXVal = 0f;
                    insertBuy(cellWidthXPos, buy, lastTextWidth);
                    firstPageRow = false;
                    pageNum += 1;
                }
                buyTotal = buyTotal.add(shared.getBigDecimal(buy.getTotal()));
            }
        }
        createBuyTotal(buyTotal.toPlainString());
        contentStream.endText();
    }

    private float insertBuy(float cellWidthXPos, BuyTransactionPDFList buy, float lastTextWidth) throws IOException {
        curYVal -= 20f;
        int textOverflowCounter = 0;
        ArrayList<String> textOverflow = new ArrayList<>();

        if(firstPageRow) {
            writeTextNewLineAtOffset(buy.getDate(), font, 12, MARGINSIDE, curYVal);
        }else{
            writeTextNewLineAtOffset(buy.getDate(), font, 12, -curXVal + MARGINSIDE, -20);
            curXVal = MARGINSIDE;
        }

        writeTextNewLineAtOffset(buy.getName(), font, 12, cellWidthXPos - MARGINSIDE, 0);

        if((font.getStringWidth(buy.getQuantity()) / 1000.0f) * 11 <= cellWidthXPos){
            writeTextNewLineAtOffset(buy.getQuantity(), font, 12, cellWidthXPos + 5, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos + 5, 0);
            textOverflow.add(buy.getQuantity());
        }

        if((font.getStringWidth(buy.getPrice()) / 1000.0f) * 11 <= cellWidthXPos){
            writeTextNewLineAtOffset(buy.getPrice(), font, 12, cellWidthXPos + 5, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos + 5, 0);
            textOverflow.add(buy.getPrice());
        }

        if((font.getStringWidth(buy.getFee()) / 1000.0f) * 11 <= cellWidthXPos){
            writeTextNewLineAtOffset(buy.getFee(), font, 12, cellWidthXPos + 5, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos + 5, 0);
            textOverflow.add(buy.getFee());
        }

        if((fontBold.getStringWidth(buy.getTotal()) / 1000.0f) * 11 <= cellWidthXPos){
            lastTextWidth = (fontBold.getStringWidth(buy.total) / 1000.0f) * 12;
            writeTextNewLineAtOffset(buy.getTotal(), fontBold, 12, cellWidthXPos + (cellWidthXPos-lastTextWidth) + 5, 0);
        }else{
            textOverflowCounter ++;
            lastTextWidth = (fontBold.getStringWidth("*" + textOverflowCounter) / 1000.0f) * 12;
            writeTextNewLineAtOffset("*" + textOverflowCounter, fontBold, 12, cellWidthXPos + (cellWidthXPos-lastTextWidth) + 5, 0);
            textOverflow.add(buy.getTotal());
        }

        boolean firstStar = true;
        for(int i = 1; i <= textOverflowCounter; i++){
            if(curYVal - 20f > 70f) {
                // Dokud jsem nepřetekl obsah stránky
                // Vypsat prodej
                curYVal -= 20f;

                contentStream.setFont(font, 12);
                if(firstStar) {
                    contentStream.newLineAtOffset(-curXVal + 25, -20);
                    firstStar = false;
                }else{
                    contentStream.newLine();
                }
                curXVal = 25;
                contentStream.showText("*" + i + " : " + textOverflow.get(i-1));


            }else{
                contentStream.endText();

                // Přidat zápatí
                createFooter(String.valueOf(pageNum));

                // Vytvořit novou stránku
                contentStream.close();
                createNewPage();
                contentStream.setNonStrokingColor(150, 150, 150);
                createBuyHeadline(false);
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.setFont(font, 12);

                // Vložit přetečený text
                contentStream.beginText();
                curXVal = 25f;
                curYVal -= 20f;
                contentStream.newLineAtOffset(curXVal, curYVal);
                contentStream.showText("*" + i + " : " + textOverflow.get(i-1));
                firstPageRow = false;

                pageNum += 1;
            }
        }
        return lastTextWidth;
    }

    private void createBuyHeadline(boolean showTransactionType) throws IOException {
        contentStream.beginText();
        curXVal = 0f;

        contentStream.setLeading(25f);
        if(showTransactionType) {
            writeTextNewLineAtOffset("Nákup", fontBold, 16, MARGINSIDE, curYVal);
            String currency = "Cena vede v CZK";
            float textWidth = (font.getStringWidth(currency) / 1000.0f) * 11;
            float textPos = width - textWidth;
            writeTextNewLineAtOffset(currency, font, 11, textPos, 0);
            curYVal -= 25f;
            writeTextNewLineAtOffset("Datum", font, 14, -curXVal + MARGINSIDE, -25);
        }else{
            writeTextNewLineAtOffset("Datum", font, 14, -curXVal + MARGINSIDE, curYVal);
        }

        contentStream.newLineAtOffset(cellWidthXPos - MARGINSIDE, 0);
        curXVal += cellWidthXPos - MARGINSIDE;
        contentStream.showText("Název");

        contentStream.newLineAtOffset(cellWidthXPos + 5, 0);
        curXVal += cellWidthXPos + 5;
        contentStream.showText("Množství");

        contentStream.newLineAtOffset(cellWidthXPos + 5, 0);
        curXVal += cellWidthXPos + 5;
        contentStream.showText("Cena");

        contentStream.newLineAtOffset(cellWidthXPos + 5, 0);
        curXVal += cellWidthXPos + 5;
        contentStream.showText("Poplatek");

        String value = "Celkové náklady";
        float textWidth = (fontBold.getStringWidth(value) / 1000.0f) * 14;
        writeTextNewLineAtOffset(value, fontBold, 14, cellWidthXPos + (cellWidthXPos-textWidth) + 5, 0);

        curYVal -= 10f;
        contentStream.endText();

        if(showTransactionType) {
            contentStream.setNonStrokingColor(0, 0, 0);
        }

        contentStream.addRect(MARGINSIDE, curYVal, width, 3);
        contentStream.fill();
    }

    private void createBuyTotal(String sellTotal) throws IOException {
        contentStream.endText();
        if(curYVal - 25f > 70f) {
            // Dokud jsem nepřetekl obsah stránky
            // Vypsat celkovou hodnotu prodeje
            String sellTotalText = "Výdaj za nákup:";
            float textWidthText = (font.getStringWidth(sellTotalText) / 1000.0f) * 12;
            float textWidth = (fontBold.getStringWidth(sellTotal) / 1000.0f) * 14;
            float moveRight = width - textWidth - textWidthText - 5 + MARGINSIDE;

            if(firstPageRow) {
                insertSellTotal(20f, moveRight, textWidth, textWidthText, sellTotalText);
                firstPageRow = false;
            }else{
                insertSellTotal(25f, moveRight, textWidth, textWidthText, sellTotalText);
            }

            writeTextNewLineAtOffset(sellTotal, fontBold, 14, textWidthText + 5, 0);
        }else{
            // Pokud jsem přetekl
            // Přidat zápatí
            createFooter(String.valueOf(pageNum));

            // Vytvořit novou stránku
            contentStream.close();
            createNewPage();

            // Vložit přetečený text
            String sellTotalText = "Výdaj za nákup:";
            float textWidthText = (font.getStringWidth(sellTotalText) / 1000.0f) * 12;
            float textWidth = (fontBold.getStringWidth(sellTotal) / 1000.0f) * 14;
            float moveRight = width - textWidth - textWidthText - 5 + MARGINSIDE;

            insertSellTotal(20f, moveRight, textWidth, textWidthText, sellTotalText);
            writeTextNewLineAtOffset(sellTotal, fontBold, 14, textWidthText + 5, 0);

            curYVal -= 5;
            firstPageRow = false;
            pageNum += 1;
        }
    }

    private void insertBuyTotal(float yVal, float moveRight, float textWidth, float textWidthText, String buyTotalText) throws IOException {
        curYVal -= yVal;
        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.addRect(moveRight, curYVal, textWidth + textWidthText + 5, 2);
        contentStream.fill();

        contentStream.beginText();
        curXVal = 0f;
        curYVal -= 15f;
        writeTextNewLineAtOffset(buyTotalText, font, 12, moveRight, curYVal);
    }
    /** PDF sekce nákupu */

    /** PDF sekce prodeje */
    private void createSell(ArrayList<SellTransactionPDFList> sellList) throws IOException {
        float lastTextWidth = 0f;
        // Vložit popis tabulky
        createSellHeadline(true);

        BigDecimal sellTotal = BigDecimal.ZERO;

        contentStream.beginText();
        curXVal = 0f;
        for(int i = 0; i < 19; i++) {
            for (SellTransactionPDFList sell : sellList) {
                // Dokud mám nějaký prodej
                contentStream.setLeading(20f);

                if (curYVal - 20f > 70f) {
                    // Dokud jsem nepřetekl obsah stránky
                    // Vypsat prodej
                    lastTextWidth = insertSell(cellWidthXPos, sell, lastTextWidth);
                    firstPageRow = false;

                } else {
                    // Obsah přetekl stránku
                    contentStream.endText();

                    // Přidat zápatí
                    createFooter(String.valueOf(pageNum));

                    // Vytvořit novou stránku
                    contentStream.close();
                    createNewPage();
                    contentStream.setNonStrokingColor(150, 150, 150);
                    createSellHeadline(false);
                    contentStream.setNonStrokingColor(0, 0, 0);
                    contentStream.setFont(font, 12);

                    // Vložit prodej
                    contentStream.beginText();
                    curXVal = 0f;
                    insertSell(cellWidthXPos, sell, lastTextWidth);
                    firstPageRow = false;
                    pageNum += 1;
                }
                sellTotal = sellTotal.add(shared.getBigDecimal(sell.getTotal()));
            }
        }
        createSellTotal(sellTotal.toPlainString());
        contentStream.endText();
    }

    private float insertSell(float cellWidthXPos, SellTransactionPDFList sell, float lastTextWidth) throws IOException {
        curYVal -= 20f;
        int textOverflowCounter = 0;
        ArrayList<String> textOverflow = new ArrayList<>();

        if(firstPageRow) {
            writeTextNewLineAtOffset(sell.getDate(), font, 12, MARGINSIDE, curYVal);
        }else{
            writeTextNewLineAtOffset(sell.getDate(), font, 12, -curXVal + MARGINSIDE, -20);
            curXVal = MARGINSIDE;
        }

        writeTextNewLineAtOffset(sell.getName(), font, 12, cellWidthXPos - MARGINSIDE, 0);

        if((font.getStringWidth(sell.getQuantity()) / 1000.0f) * 11 <= cellWidthXPos){
            writeTextNewLineAtOffset(sell.getQuantity(), font, 12, cellWidthXPos + 5, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos + 5, 0);
            textOverflow.add(sell.getQuantity());
        }

        if((font.getStringWidth(sell.getProfit()) / 1000.0f) * 11 <= cellWidthXPos){
            writeTextNewLineAtOffset(sell.getProfit(), font, 12, cellWidthXPos + 5, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos + 5, 0);
            textOverflow.add(sell.getProfit());
        }

        if((font.getStringWidth(sell.getFee()) / 1000.0f) * 11 <= cellWidthXPos){
            writeTextNewLineAtOffset(sell.getFee(), font, 12, cellWidthXPos + 5, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos + 5, 0);
            textOverflow.add(sell.getFee());
        }

        if((fontBold.getStringWidth(sell.getTotal()) / 1000.0f) * 11 <= cellWidthXPos){
            lastTextWidth = (fontBold.getStringWidth(sell.total) / 1000.0f) * 12;
            writeTextNewLineAtOffset(sell.getTotal(), fontBold, 12, cellWidthXPos + (cellWidthXPos-lastTextWidth) + 5, 0);
        }else{
            textOverflowCounter ++;
            lastTextWidth = (fontBold.getStringWidth("*" + textOverflowCounter) / 1000.0f) * 12;
            writeTextNewLineAtOffset("*" + textOverflowCounter, fontBold, 12, cellWidthXPos + (cellWidthXPos-lastTextWidth) + 5, 0);
            textOverflow.add(sell.getTotal());
        }

        boolean firstStar = true;
        for(int i = 1; i <= textOverflowCounter; i++){
            if(curYVal - 20f > 70f) {
                // Dokud jsem nepřetekl obsah stránky
                // Vypsat prodej
                curYVal -= 20f;

                contentStream.setFont(font, 12);
                if(firstStar) {
                    contentStream.newLineAtOffset(-curXVal + 25, -20);
                    firstStar = false;
                }else{
                    contentStream.newLine();
                }
                curXVal = 25;
                contentStream.showText("*" + i + " : " + textOverflow.get(i-1));


            }else{
                contentStream.endText();

                // Přidat zápatí
                createFooter(String.valueOf(pageNum));

                // Vytvořit novou stránku
                contentStream.close();
                createNewPage();
                contentStream.setNonStrokingColor(150, 150, 150);
                createSellHeadline(false);
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.setFont(font, 12);

                // Vložit přetečený text
                contentStream.beginText();
                curXVal = 25f;
                curYVal -= 20f;
                contentStream.newLineAtOffset(curXVal, curYVal);
                contentStream.showText("*" + i + " : " + textOverflow.get(i-1));
                firstPageRow = false;

                pageNum += 1;
            }
        }
        return lastTextWidth;
    }

    private void createSellHeadline(boolean showTransactionType) throws IOException {
        contentStream.beginText();
        curXVal = 0f;

        contentStream.setLeading(25f);
        if(showTransactionType) {
            writeTextNewLineAtOffset("Prodej", fontBold, 16, MARGINSIDE, curYVal);
            String currency = "Cena vede v CZK";
            float textWidth = (font.getStringWidth(currency) / 1000.0f) * 11;
            float textPos = width - textWidth;
            writeTextNewLineAtOffset(currency, font, 11, textPos, 0);
            curYVal -= 25f;
            writeTextNewLineAtOffset("Datum", font, 14, -curXVal + MARGINSIDE, -25);
        }else{
            writeTextNewLineAtOffset("Datum", font, 14, -curXVal + MARGINSIDE, curYVal);
        }

        contentStream.newLineAtOffset(cellWidthXPos - MARGINSIDE, 0);
        curXVal += cellWidthXPos - MARGINSIDE;
        contentStream.showText("Název");

        contentStream.newLineAtOffset(cellWidthXPos + 5, 0);
        curXVal += cellWidthXPos + 5;
        contentStream.showText("Množství");

        contentStream.newLineAtOffset(cellWidthXPos + 5, 0);
        curXVal += cellWidthXPos + 5;
        contentStream.showText("Příjem");

        contentStream.newLineAtOffset(cellWidthXPos + 5, 0);
        curXVal += cellWidthXPos + 5;
        contentStream.showText("Poplatek");

        String value = "Celkový příjem";
        float textWidth = (fontBold.getStringWidth(value) / 1000.0f) * 14;
        writeTextNewLineAtOffset(value, fontBold, 14, cellWidthXPos + (cellWidthXPos-textWidth) + 5, 0);

        curYVal -= 10f;
        contentStream.endText();

        if(showTransactionType) {
            contentStream.setNonStrokingColor(0, 0, 0);
        }

        contentStream.addRect(MARGINSIDE, curYVal, width, 3);
        contentStream.fill();
    }

    private void createSellTotal(String sellTotal) throws IOException {
        contentStream.endText();
        if(curYVal - 25f > 70f) {
            // Dokud jsem nepřetekl obsah stránky
            // Vypsat celkovou hodnotu prodeje
            String sellTotalText = "Příjem z prodeje:";
            float textWidthText = (font.getStringWidth(sellTotalText) / 1000.0f) * 12;
            float textWidth = (fontBold.getStringWidth(sellTotal) / 1000.0f) * 14;
            float moveRight = width - textWidth - textWidthText - 5 + MARGINSIDE;

            if(firstPageRow) {
                insertSellTotal(20f, moveRight, textWidth, textWidthText, sellTotalText);
                firstPageRow = false;
            }else{
                insertSellTotal(25f, moveRight, textWidth, textWidthText, sellTotalText);
            }

            writeTextNewLineAtOffset(sellTotal, fontBold, 14, textWidthText + 5, 0);
        }else{
            // Pokud jsem přetekl
            // Přidat zápatí
            createFooter(String.valueOf(pageNum));

            // Vytvořit novou stránku
            contentStream.close();
            createNewPage();

            // Vložit přetečený text
            String sellTotalText = "Příjem z prodeje:";
            float textWidthText = (font.getStringWidth(sellTotalText) / 1000.0f) * 12;
            float textWidth = (fontBold.getStringWidth(sellTotal) / 1000.0f) * 14;
            float moveRight = width - textWidth - textWidthText - 5 + MARGINSIDE;

            insertSellTotal(20f, moveRight, textWidth, textWidthText, sellTotalText);
            writeTextNewLineAtOffset(sellTotal, fontBold, 14, textWidthText + 5, 0);

            curYVal -= 5;
            firstPageRow = false;
            pageNum += 1;
        }
    }

    private void insertSellTotal(float yVal, float moveRight, float textWidth, float textWidthText, String sellTotalText) throws IOException {
        curYVal -= yVal;
        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.addRect(moveRight, curYVal, textWidth + textWidthText + 5, 2);
        contentStream.fill();

        contentStream.beginText();
        curXVal = 0f;
        curYVal -= 15f;
        writeTextNewLineAtOffset(sellTotalText, font, 12, moveRight, curYVal);
    }
    /** PDF sekce prodeje */
}

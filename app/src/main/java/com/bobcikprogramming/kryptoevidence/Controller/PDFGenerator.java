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

    private double eurExchangeRate;
    private double usdExchangeRate;

    private float MARGINSIDE = 20;
    private float MARGINTOP = 60;
    private float MARGINBOTTOM = 30;
    private float curXVal;
    private float curYVal;
    private float width;
    private float cellWidthXPos;
    private int pageNum = 1;
    private boolean firstPageRow;
    private BigDecimal buyTotal;
    private BigDecimal sellTotal;
    private BigDecimal changeTotal;

    public PDFGenerator(AssetManager assetManager, Context context, Activity activity, double eurExchangeRate, double usdExchangeRate){
        PDFBoxResourceLoader.init(context);

        doc = new PDDocument();
        this.assetManager = assetManager;
        this.eurExchangeRate = eurExchangeRate;
        this.usdExchangeRate = usdExchangeRate;

        calendar = new CalendarManager();
        shared = new SharedMethods();

        buyTotal = BigDecimal.ZERO;
        sellTotal = BigDecimal.ZERO;
        changeTotal = BigDecimal.ZERO;

        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
    }

    public void createPDF(String selectedYear,  ArrayList<BuyTransactionPDFList> buyList,  ArrayList<SellTransactionPDFList> sellList, ArrayList<ChangeTransactionPDFList> changeList) throws IOException {
        loadFonts();
        createNewPage();
        cellWidthXPos = (width/6f);
        createBuy(buyList);
        createSell(sellList);
        createChange(changeList);
        createTotalOverview();

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

    private void insertTotal(float yVal, float moveRight, float textWidth, float textWidthText, String totalText, String total) throws IOException {
        curYVal -= yVal;
        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.addRect(moveRight, curYVal, textWidth + textWidthText + 5, 2);
        contentStream.fill();

        contentStream.beginText();
        curXVal = 0f;
        curYVal -= 15f;
        writeTextNewLineAtOffset(totalText, font, 12, moveRight, curYVal);
        writeTextNewLineAtOffset(total, fontBold, 14, textWidthText + 5, 0);
        curYVal -= 35f;
    }

    /** PDF sekce nákupu */
    private void createBuy(ArrayList<BuyTransactionPDFList> buyList) throws IOException {
        float lastTextWidth = 0f;
        // Vložit popis tabulky
        createBuyHeadline(true);

        contentStream.beginText();
        curXVal = 0f;
        for(int i = 0; i < 1; i++) {
            for (BuyTransactionPDFList buy : buyList) {
                // Dokud mám nějaký prodej
                contentStream.setLeading(20f);

                if (curYVal - 20f > 70f) {
                    // Dokud jsem nepřetekl obsah stránky
                    // Vypsat prodej
                    insertBuy(cellWidthXPos, buy);
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
                    insertBuy(cellWidthXPos, buy);
                    firstPageRow = false;
                    pageNum += 1;
                }
                buyTotal = buyTotal.add(shared.getBigDecimal(buy.getTotal()));
            }
        }
        createBuyTotal(buyTotal.toPlainString());
        contentStream.endText();
    }

    private void insertBuy(float cellWidthXPos, BuyTransactionPDFList buy) throws IOException {
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
            float lastTextWidth = (fontBold.getStringWidth(buy.getTotal()) / 1000.0f) * 12;
            writeTextNewLineAtOffset(buy.getTotal(), fontBold, 12, cellWidthXPos + (cellWidthXPos-lastTextWidth) + 5, 0);
        }else{
            textOverflowCounter ++;
            float lastTextWidth = (fontBold.getStringWidth("*" + textOverflowCounter) / 1000.0f) * 12;
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
    }

    private void createBuyHeadline(boolean showTransactionType) throws IOException {
        if(curYVal - 55f < 70f) {
            createFooter(String.valueOf(pageNum));
            contentStream.close();
            createNewPage();
            pageNum += 1;
        }
        contentStream.beginText();
        curXVal = 0f;

        contentStream.setLeading(25f);
        if(showTransactionType) {
            writeTextNewLineAtOffset("Nákup", fontBold, 16, MARGINSIDE, curYVal);
            String currency = "Cena vedena v CZK";
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

    private void createBuyTotal(String buyTotal) throws IOException {
        contentStream.endText();
        if(curYVal - 25f > 70f) {
            // Dokud jsem nepřetekl obsah stránky
            // Vypsat celkovou hodnotu prodeje
            String buyTotalText = "Výdaj za nákup:";
            float textWidthText = (font.getStringWidth(buyTotalText) / 1000.0f) * 12;
            float textWidth = (fontBold.getStringWidth(buyTotal) / 1000.0f) * 14;
            float moveRight = width - textWidth - textWidthText - 5 + MARGINSIDE;

            if(firstPageRow) {
                insertTotal(20f, moveRight, textWidth, textWidthText, buyTotalText, buyTotal);
                firstPageRow = false;
            }else{
                insertTotal(25f, moveRight, textWidth, textWidthText, buyTotalText, buyTotal);
            }

            writeTextNewLineAtOffset(buyTotal, fontBold, 14, textWidthText + 5, 0);
        }else{
            // Pokud jsem přetekl
            // Přidat zápatí
            createFooter(String.valueOf(pageNum));

            // Vytvořit novou stránku
            contentStream.close();
            createNewPage();

            // Vložit přetečený text
            String buyTotalText = "Výdaj za nákup:";
            float textWidthText = (font.getStringWidth(buyTotalText) / 1000.0f) * 12;
            float textWidth = (fontBold.getStringWidth(buyTotal) / 1000.0f) * 14;
            float moveRight = width - textWidth - textWidthText - 5 + MARGINSIDE;

            insertTotal(20f, moveRight, textWidth, textWidthText, buyTotalText, buyTotal);
            writeTextNewLineAtOffset(buyTotal, fontBold, 14, textWidthText + 5, 0);

            curYVal -= 5;
            firstPageRow = false;
            pageNum += 1;
        }
    }
    /** PDF sekce nákupu */

    /** PDF sekce prodeje */
    private void createSell(ArrayList<SellTransactionPDFList> sellList) throws IOException {
        boolean firstRow = true;
        // Vložit popis tabulky
        createSellHeadline(true);

        contentStream.beginText();
        curXVal = 0f;
        for(int i = 0; i < 1; i++) {
            for (SellTransactionPDFList sell : sellList) {
                // Dokud mám nějaký prodej
                contentStream.setLeading(20f);

                if (curYVal - 20f > 70f) {
                    // Dokud jsem nepřetekl obsah stránky
                    // Vypsat prodej
                    insertSell(cellWidthXPos, sell, firstRow);
                    firstPageRow = false;
                    firstRow = false;

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
                    insertSell(cellWidthXPos, sell, false);
                    firstPageRow = false;
                    firstRow = false;
                    pageNum += 1;
                }
                sellTotal = sellTotal.add(shared.getBigDecimal(sell.getTotal()));
            }
        }
        createSellTotal(sellTotal.toPlainString());
        contentStream.endText();
    }

    private void insertSell(float cellWidthXPos, SellTransactionPDFList sell, boolean firstRow) throws IOException {
        curYVal -= 20f;
        int textOverflowCounter = 0;
        ArrayList<String> textOverflow = new ArrayList<>();

        if(firstPageRow) {
            writeTextNewLineAtOffset(sell.getDate(), font, 12, MARGINSIDE, curYVal);
        }else{
            if(firstRow){
                writeTextNewLineAtOffset(sell.getDate(), font, 12, -curXVal + MARGINSIDE, curYVal);
            }else {
                writeTextNewLineAtOffset(sell.getDate(), font, 12, -curXVal + MARGINSIDE, -20);
            }
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
            float lastTextWidth = (fontBold.getStringWidth(sell.total) / 1000.0f) * 12;
            writeTextNewLineAtOffset(sell.getTotal(), fontBold, 12, cellWidthXPos + (cellWidthXPos-lastTextWidth) + 5, 0);
        }else{
            textOverflowCounter ++;
            float lastTextWidth = (fontBold.getStringWidth("*" + textOverflowCounter) / 1000.0f) * 12;
            writeTextNewLineAtOffset("*" + textOverflowCounter, fontBold, 12, cellWidthXPos + (cellWidthXPos-lastTextWidth) + 5, 0);
            textOverflow.add(sell.getTotal());
        }

        boolean firstStar = true;
        for(int i = 16; i <= textOverflowCounter; i++){
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
    }

    private void createSellHeadline(boolean showTransactionType) throws IOException {
        if(curYVal - 55f < 70f) {
            createFooter(String.valueOf(pageNum));
            contentStream.close();
            createNewPage();
            pageNum += 1;
        }
        contentStream.beginText();
        curXVal = 0f;

        contentStream.setLeading(25f);
        if(showTransactionType) {
            writeTextNewLineAtOffset("Prodej", fontBold, 16, MARGINSIDE, curYVal);
            String currency = "Cena vedena v CZK";
            float textWidth = (font.getStringWidth(currency) / 1000.0f) * 11;
            float textPos = width - textWidth;
            writeTextNewLineAtOffset(currency, font, 11, textPos, 0);
            writeTextNewLineAtOffset("Datum", font, 14, -curXVal + MARGINSIDE, -25);
            curYVal -= 25f;
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
                insertTotal(20f, moveRight, textWidth, textWidthText, sellTotalText, sellTotal);
                firstPageRow = false;
            }else{
                insertTotal(25f, moveRight, textWidth, textWidthText, sellTotalText, sellTotal);
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

            insertTotal(20f, moveRight, textWidth, textWidthText, sellTotalText, sellTotal);
            writeTextNewLineAtOffset(sellTotal, fontBold, 14, textWidthText + 5, 0);

            curYVal -= 5;
            firstPageRow = false;
            pageNum += 1;
        }
    }
    /** PDF sekce prodeje */

    /** PDF sekce směny */
    private void createChange(ArrayList<ChangeTransactionPDFList> changeList) throws IOException {
        boolean firstRow = true;
        // Vložit popis tabulky
        createChangeHeadline(true);

        contentStream.beginText();
        curXVal = 0f;
        for(int i = 0; i < 1; i++) {
            for (ChangeTransactionPDFList change : changeList) {
                // Dokud mám nějakou směnu
                contentStream.setLeading(20f);

                if (curYVal - 20f > 70f) {
                    // Dokud jsem nepřetekl obsah stránky
                    // Vypsat směnu
                    insertChange(cellWidthXPos, change, firstRow);
                    firstPageRow = false;
                    firstRow = false;

                } else {
                    // Obsah přetekl stránku
                    contentStream.endText();

                    // Přidat zápatí
                    createFooter(String.valueOf(pageNum));

                    // Vytvořit novou stránku
                    contentStream.close();
                    createNewPage();
                    contentStream.setNonStrokingColor(150, 150, 150);
                    createChangeHeadline(false);
                    contentStream.setNonStrokingColor(0, 0, 0);
                    contentStream.setFont(font, 12);

                    // Vložit směnu
                    contentStream.beginText();
                    curXVal = 0f;
                    insertChange(cellWidthXPos, change, false);
                    firstPageRow = false;
                    firstRow = false;
                    pageNum += 1;
                }
                changeTotal = changeTotal.add(shared.getBigDecimal(change.getTotal()));
            }
        }
        createChangeTotal(changeTotal.toPlainString());
        contentStream.endText();
    }

    private void insertChange(float cellWidthXPos, ChangeTransactionPDFList change, boolean firstRow) throws IOException {
        curYVal -= 20f;
        int textOverflowCounter = 0;
        ArrayList<String> textOverflow = new ArrayList<>();

        if(firstPageRow) {
            writeTextNewLineAtOffset(change.getDate(), font, 12, MARGINSIDE, curYVal);
        }else{
            if(firstRow){
                writeTextNewLineAtOffset(change.getDate(), font, 12, -curXVal + MARGINSIDE, curYVal);
            }else {
                writeTextNewLineAtOffset(change.getDate(), font, 12, -curXVal + MARGINSIDE, -20);
            }
        }

        writeTextNewLineAtOffset(change.getNameSold(), font, 12, cellWidthXPos - MARGINSIDE, 0);

        if((font.getStringWidth(change.getQuantitySold()) / 1000.0f) * 11 <= cellWidthXPos + 25){
            writeTextNewLineAtOffset(change.getQuantitySold(), font, 12, cellWidthXPos - 20, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos - 20, 0);
            textOverflow.add(change.getQuantitySold());
        }

        writeTextNewLineAtOffset(change.getNameBought(), font, 12, cellWidthXPos + 30, 0);

        if((font.getStringWidth(change.getQuantityBought()) / 1000.0f) * 11 <= cellWidthXPos + 25){
            writeTextNewLineAtOffset(change.getQuantityBought(), font, 12, cellWidthXPos - 20, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos - 20, 0);
            textOverflow.add(change.getQuantityBought());
        }

        if((fontBold.getStringWidth(change.getTotal()) / 1000.0f) * 11 <= cellWidthXPos){
            float lastTextWidth = (fontBold.getStringWidth(change.total) / 1000.0f) * 12;
            writeTextNewLineAtOffset(change.getTotal(), fontBold, 12, cellWidthXPos + (cellWidthXPos-lastTextWidth) + 30, 0);
        }else{
            textOverflowCounter ++;
            float lastTextWidth = (fontBold.getStringWidth("*" + textOverflowCounter) / 1000.0f) * 12;
            writeTextNewLineAtOffset("*" + textOverflowCounter, fontBold, 12, cellWidthXPos + (cellWidthXPos-lastTextWidth) + 30, 0);
            textOverflow.add(change.getTotal());
        }

        boolean firstStar = true;
        for(int i = 1; i <= textOverflowCounter; i++){
            if(curYVal - 20f > 70f) {
                // Dokud jsem nepřetekl obsah stránky
                // Vypsat směnu
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
                createChangeHeadline(false);
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
    }

    private void createChangeHeadline(boolean showTransactionType) throws IOException {
        if(curYVal - 55f < 70f) {
            createFooter(String.valueOf(pageNum));
            contentStream.close();
            createNewPage();
            pageNum += 1;
        }
        contentStream.beginText();
        curXVal = 0f;

        contentStream.setLeading(25f);
        if(showTransactionType) {
            writeTextNewLineAtOffset("Směna", fontBold, 16, MARGINSIDE, curYVal);
            String currency = "Cena vedena v CZK";
            float textWidth = (font.getStringWidth(currency) / 1000.0f) * 11;
            float textPos = width - textWidth;
            writeTextNewLineAtOffset(currency, font, 11, textPos, 0);
            writeTextNewLineAtOffset("Datum", font, 14, -curXVal + MARGINSIDE, -25);
            curYVal -= 25f;
        }else{
            writeTextNewLineAtOffset("Datum", font, 14, -curXVal + MARGINSIDE, curYVal);
        }

        contentStream.newLineAtOffset(cellWidthXPos - MARGINSIDE, 0);
        curXVal += cellWidthXPos - MARGINSIDE;
        contentStream.showText("Prodej");

        contentStream.newLineAtOffset(cellWidthXPos - 20, 0);
        curXVal += cellWidthXPos - 20;
        contentStream.showText("Množství");

        contentStream.newLineAtOffset(cellWidthXPos + 30, 0);
        curXVal += cellWidthXPos + 30;
        contentStream.showText("Nákup");

        contentStream.newLineAtOffset(cellWidthXPos - 20, 0);
        curXVal += cellWidthXPos - 20;
        contentStream.showText("Množství");

        String value = "Příjem ze směny";
        float textWidth = (fontBold.getStringWidth(value) / 1000.0f) * 14;
        writeTextNewLineAtOffset(value, fontBold, 14, cellWidthXPos + (cellWidthXPos-textWidth) + 30, 0);

        curYVal -= 10f;
        contentStream.endText();

        if(showTransactionType) {
            contentStream.setNonStrokingColor(0, 0, 0);
        }

        contentStream.addRect(MARGINSIDE, curYVal, width, 3);
        contentStream.fill();
    }

    private void createChangeTotal(String changeTotal) throws IOException {
        contentStream.endText();
        if(curYVal - 25f > 70f) {
            // Dokud jsem nepřetekl obsah stránky
            // Vypsat celkovou hodnotu směny
            String changeTotalText = "Příjem ze směny:";
            float textWidthText = (font.getStringWidth(changeTotalText) / 1000.0f) * 12;
            float textWidth = (fontBold.getStringWidth(changeTotal) / 1000.0f) * 14;
            float moveRight = width - textWidth - textWidthText - 5 + MARGINSIDE;

            if(firstPageRow) {
                insertTotal(20f, moveRight, textWidth, textWidthText, changeTotalText, changeTotal);
                firstPageRow = false;
            }else{
                insertTotal(25f, moveRight, textWidth, textWidthText, changeTotalText, changeTotal);
            }

            writeTextNewLineAtOffset(changeTotal, fontBold, 14, textWidthText + 5, 0);
        }else{
            // Pokud jsem přetekl
            // Přidat zápatí
            createFooter(String.valueOf(pageNum));

            // Vytvořit novou stránku
            contentStream.close();
            createNewPage();

            // Vložit přetečený text
            String changeTotalText = "Příjem ze směny:";
            float textWidthText = (font.getStringWidth(changeTotalText) / 1000.0f) * 12;
            float textWidth = (fontBold.getStringWidth(changeTotal) / 1000.0f) * 14;
            float moveRight = width - textWidth - textWidthText - 5 + MARGINSIDE;

            insertTotal(20f, moveRight, textWidth, textWidthText, changeTotalText, changeTotal);

            curYVal -= 5;
            firstPageRow = false;
            pageNum += 1;
        }
    }
    /** PDF sekce směny */

    /** PDF celkový součet */
    private void createTotalOverview() throws IOException {
        if(curYVal - 75f < 70f) {
            createFooter(String.valueOf(pageNum));
            contentStream.close();
            createNewPage();
            pageNum += 1;
        }else{
            curYVal -= 25;
        }
        contentStream.beginText();
        curXVal = 0f;
        BigDecimal total = (sellTotal.add(changeTotal)).subtract(buyTotal);
        String profitLoseText = "Zisk (CZK):";
        if(total.compareTo(BigDecimal.ZERO)<0){
            profitLoseText = "Ztráta (CZK):";
        }

        float textWidthText = (font.getStringWidth(profitLoseText) / 1000.0f) * 12;
        float textWidth = (fontBold.getStringWidth(total.toPlainString()) / 1000.0f) * 14;
        float moveRight = width - textWidth - textWidthText - 5 + MARGINSIDE;

        writeTextNewLineAtOffset(profitLoseText, font, 12, moveRight, curYVal);
        writeTextNewLineAtOffset(total.toPlainString(), fontBold, 14, textWidthText + 5, 0);
        contentStream.endText();

        curYVal -= 10;
        contentStream.setNonStrokingColor(150, 150, 150);
        contentStream.addRect(moveRight, curYVal, textWidth + textWidthText + 5, 1);
        contentStream.fill();
        curYVal -= 15;

        contentStream.beginText();
        curXVal = 0f;

        contentStream.setNonStrokingColor(50, 50, 50);
        String euroExchRate = "Využitý kurz EUR: " + eurExchangeRate;
        textWidth = (font.getStringWidth(euroExchRate) / 1000.0f) * 12;
        moveRight = width - textWidth + MARGINSIDE;
        writeTextNewLineAtOffset(euroExchRate, font, 12, moveRight, curYVal);

        String dollarExchRate = "Využitý kurz USD: " + usdExchangeRate;
        contentStream.setLeading(15);
        contentStream.newLine();
        contentStream.showText(dollarExchRate);
        contentStream.endText();
    }
    /** PDF celkový součet */
}

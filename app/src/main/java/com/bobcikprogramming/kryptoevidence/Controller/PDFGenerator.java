package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import androidx.annotation.Nullable;

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
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class PDFGenerator {
    private AssetManager assetManager;
    private Context context;

    private PDDocument doc;
    private PDFont font = null, fontBold = null;
    private PDPageContentStream contentStream;
    private PDPage page;

    private CalendarManager calendar;
    private SharedMethods shared;

    private double eurExchangeRate;
    private double usdExchangeRate;
    private boolean correctRate;

    private float MARGINSIDE = 20;
    private float MARGINTOP = 60;
    private float MARGINBOTTOM = 30;
    private float curXVal;
    private float curYVal;
    private float width;
    private float cellWidthXPos;
    private int pageNum = 1;
    private boolean firstPageRow;
    private BigDecimal total;
    private String fileName;

    public PDFGenerator(AssetManager assetManager, Context context, double eurExchangeRate, double usdExchangeRate, boolean correctRate){
        PDFBoxResourceLoader.init(context);

        doc = new PDDocument();
        this.assetManager = assetManager;
        this.eurExchangeRate = eurExchangeRate;
        this.usdExchangeRate = usdExchangeRate;
        this.context = context;
        this.correctRate = correctRate;

        calendar = new CalendarManager();
        shared = new SharedMethods();

        total = BigDecimal.ZERO;
    }

    /**
     * Metoda inicializující vytvoření PDF souboru.
     * Postupně zavolá vypsání nákupu, prodeje a směny.
     * Následující soubor poté uloží.
     * @param selectedYear Rok daňového období
     * @param buyList Seznam nákupů za dané období
     * @param sellList Seznam prodejů za dané období
     * @param changeList Seznam směn za dané období
     * @return true - proběhlo-li uložení v pořádku, jinak false
     * @throws IOException
     */
    public boolean createPDF(String selectedYear,  ArrayList<BuyTransactionPDFList> buyList,  ArrayList<SellTransactionPDFList> sellList, ArrayList<ChangeTransactionPDFList> changeList) throws IOException {
        BigDecimal buyTotal;
        BigDecimal sellTotal;
        BigDecimal changeTotal;

        loadFonts();
        createNewPage();
        cellWidthXPos = (width/6f);
        buyTotal = createBuy(buyList);
        sellTotal = createSell(sellList);
        changeTotal = createChange(changeList);
        createTotalOverview(buyTotal, sellTotal, changeTotal);

        // Přidání zápatí v případě, že stránka nebyla zcela zaplněna
        if(curYVal - 15f > 65) {
            createFooter(String.valueOf(pageNum));
        }

        contentStream.close();
        File path;
        fileName = selectedYear +"_"+ calendar.getActualDateFolderNameFormat() + ".pdf";
        File file = getAppSpecificStorageDir();
        if(file == null){
            return false;
        }
        path = new File(file, fileName);

        doc.save(path);
        doc.close();
        return true;
    }
    
    /**
     * Metoda pro získání cesty do sloužky s PDF soubory.
     * Neexistuje-li daná složka, tak dojde k jejímu vytvoření.
     * @return Cestu do složky pokud existuje, jinak null
     * 
     * Metoda inspirována z:
     * https://developer.android.com/training/data-storage/app-specific#external-select-location
     */
    @Nullable
    File getAppSpecificStorageDir() {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), "kryptoevidence_pdf");
        if(!file.exists()){
            if(!file.mkdirs()){
                System.err.println("Soubor nebyl vytvořen.");
                return null;
            }
        }
        return file;
    }

    /**
     * Pomocná metoda pro zapsání nového řádku do PDF souboru.
     * @param text Text jenž má být vypsán
     * @param font Font písma
     * @param fontSize Velikost písma
     * @param tx X souřadnice začátku textu
     * @param ty Y souřadnice začátku textu
     * @throws IOException
     */
    private void writeTextNewLineAtOffset(String text, PDFont font, float fontSize, float tx, float ty ) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(tx, ty);
        contentStream.showText(text);
        curXVal += tx;
    }

    /**
     * Metoda pro načtení fontů.
     * @throws IOException
     */
    private void loadFonts() throws IOException {
        font = PDType0Font.load(doc, assetManager.open("roboto_regular.ttf"));
        fontBold = PDType0Font.load(doc, assetManager.open("roboto_bold.ttf"));
    }

    /**
     * Metoda pro vytvoření nové stránky a vypsání záhlaví.
     * @throws IOException
     */
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

    /**
     * Metoda pro vytvoření zápatí.
     * @param pageNum
     * @throws IOException
     */
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

    /**
     * Metoda pro vypsání celkového součtu operace.
     * @param yVal Hodnota posunu na Y ose
     * @param moveRight Hodnota posunu na X ose
     * @param textWidthDes Šířka popisujícího textu
     * @param textWidthTotal Šířka hodnoty celkového součtu operace
     * @param des Popisující text
     * @param total Hodnota celkového součtu operace
     * @throws IOException
     */
    private void insertTotal(float yVal, float moveRight, float textWidthDes, float textWidthTotal, String des, String total) throws IOException {
        curYVal -= yVal;
        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.addRect(moveRight, curYVal, textWidthDes + textWidthTotal + 5, 2);
        contentStream.fill();

        contentStream.beginText();
        curXVal = 0f;
        curYVal -= 15f;
        writeTextNewLineAtOffset(des, font, 12, moveRight, curYVal);
        writeTextNewLineAtOffset(total, fontBold, 14, textWidthTotal + 5, 0);
        curYVal -= 35f;
    }

    /* PDF sekce nákupu */
    /**
     * Metoda pro generování seznamu nákupů.
     * @param buyList Seznam nákupů
     * @return Celkovou částku nákladů
     * @throws IOException
     */
    private BigDecimal createBuy(ArrayList<BuyTransactionPDFList> buyList) throws IOException {
        BigDecimal buyTotal = BigDecimal.ZERO;
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
                    insertBuy(buy);
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
                    insertBuy(buy);
                    firstPageRow = false;
                    pageNum += 1;
                }
                buyTotal = buyTotal.add(shared.getBigDecimal(buy.getTotal()));
            }
        }
        createBuyTotal(buyTotal.toPlainString());
        contentStream.endText();

        return buyTotal;
    }

    /**
     * Metoda pro výpis jednotlivých nákupů.
     * @param buy Vypisovaný nákup
     * @throws IOException
     */
    private void insertBuy(BuyTransactionPDFList buy) throws IOException {
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

        if((font.getStringWidth(buy.getQuantity()) / 1000.0f) * 11 + 5 <= cellWidthXPos){
            writeTextNewLineAtOffset(buy.getQuantity(), font, 12, cellWidthXPos + 5, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos + 5, 0);
            textOverflow.add(buy.getQuantity());
        }

        if((font.getStringWidth(buy.getPrice()) / 1000.0f) * 11  + 5 <= cellWidthXPos){
            writeTextNewLineAtOffset(buy.getPrice(), font, 12, cellWidthXPos + 5, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos + 5, 0);
            textOverflow.add(buy.getPrice());
        }

        if((font.getStringWidth(buy.getFee()) / 1000.0f) * 11  + 5 <= cellWidthXPos){
            writeTextNewLineAtOffset(buy.getFee(), font, 12, cellWidthXPos + 5, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos + 5, 0);
            textOverflow.add(buy.getFee());
        }

        if((fontBold.getStringWidth(buy.getTotal()) / 1000.0f) * 11 + 5 <= cellWidthXPos){
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

    /**
     * Metoda pro vytvoření hlavičky seznamu nákupů.
     * @param showTransactionType boolean hodnota, zda-li se má vypsat typ transakce (true - začátek výpisu, 
     *                            false - výpis pokračuje na nové straně)
     * @throws IOException
     */
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

    /**
     * Metoda k vypsání celkových nákladů.
     * @param buyTotal Celkové náklady
     * @throws IOException
     */
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

            //writeTextNewLineAtOffset(buyTotal, fontBold, 14, textWidthText + 5, 0);
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
    /* PDF sekce nákupu */

    /* PDF sekce prodeje */
    /**
     * Metoda pro generování seznamu prodejů.
     * @param sellList Seznam prodejů
     * @return Celkovou částku zisku
     * @throws IOException
     */
    private BigDecimal createSell(ArrayList<SellTransactionPDFList> sellList) throws IOException {
        boolean firstRow = true;
        BigDecimal sellTotal = BigDecimal.ZERO;
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
                    insertSell(sell, firstRow);
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
                    insertSell(sell, false);
                    firstPageRow = false;
                    firstRow = false;
                    pageNum += 1;
                }
                sellTotal = sellTotal.add(shared.getBigDecimal(sell.getTotal()));
            }
        }
        createSellTotal(sellTotal.toPlainString());
        contentStream.endText();

        return sellTotal;
    }


    /**
     * Metoda pro výpis jednotlivých prodejů.
     * @param sell Vypisovaný prodej
     * @param firstRow boolean hodnota, zda-li se jedná o první řádek výpisu
     * @throws IOException
     */
    private void insertSell(SellTransactionPDFList sell, boolean firstRow) throws IOException {
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

        if((font.getStringWidth(sell.getQuantity()) / 1000.0f) * 11 + 5 <= cellWidthXPos){
            writeTextNewLineAtOffset(sell.getQuantity(), font, 12, cellWidthXPos + 5, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos + 5, 0);
            textOverflow.add(sell.getQuantity());
        }

        if((font.getStringWidth(sell.getProfit()) / 1000.0f) * 11 + 5 <= cellWidthXPos){
            writeTextNewLineAtOffset(sell.getProfit(), font, 12, cellWidthXPos + 5, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos + 5, 0);
            textOverflow.add(sell.getProfit());
        }

        if((font.getStringWidth(sell.getFee()) / 1000.0f) * 11 + 5 <= cellWidthXPos){
            writeTextNewLineAtOffset(sell.getFee(), font, 12, cellWidthXPos + 5, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos + 5, 0);
            textOverflow.add(sell.getFee());
        }

        if((fontBold.getStringWidth(sell.getTotal()) / 1000.0f) * 11 + 5 <= cellWidthXPos){
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

    /**
     * Metoda pro vytvoření hlavičky seznamu prodejů.
     * @param showTransactionType boolean hodnota, zda-li se má vypsat typ transakce (true - začátek výpisu,
     *                            false - výpis pokračuje na nové straně)
     * @throws IOException
     */
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
        contentStream.showText("Cena");

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

    /**
     * Metoda k vypsání celkového zisku.
     * @param sellTotal Celkový zisk
     * @throws IOException
     */

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

            //writeTextNewLineAtOffset(sellTotal, fontBold, 14, textWidthText + 5, 0);
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
    /* PDF sekce prodeje */

    /* PDF sekce směny */
    /**
     * Metoda pro generování seznamu směn.
     * @param changeList Seznam směn
     * @return Celkovou částku zisku
     * @throws IOException
     */
    private BigDecimal createChange(ArrayList<ChangeTransactionPDFList> changeList) throws IOException {
        boolean firstRow = true;
        BigDecimal changeTotal = BigDecimal.ZERO;
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
                    insertChange(change, firstRow);
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
                    insertChange(change, false);
                    firstPageRow = false;
                    firstRow = false;
                    pageNum += 1;
                }
                changeTotal = changeTotal.add(shared.getBigDecimal(change.getTotal()));
            }
        }
        createChangeTotal(changeTotal.toPlainString());
        contentStream.endText();

        return changeTotal;
    }

    /**
     * Metoda pro výpis jednotlivých směn.
     * @param change Vypisovaná směna
     * @param firstRow boolean hodnota, zda-li se jedná o první řádek výpisu
     * @throws IOException
     */
    private void insertChange(ChangeTransactionPDFList change, boolean firstRow) throws IOException {
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

        if((font.getStringWidth(change.getQuantitySold()) / 1000.0f) * 11 + 5 <= cellWidthXPos + 25){
            writeTextNewLineAtOffset(change.getQuantitySold(), font, 12, cellWidthXPos - 20, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos - 20, 0);
            textOverflow.add(change.getQuantitySold());
        }

        writeTextNewLineAtOffset(change.getNameBought(), font, 12, cellWidthXPos + 30, 0);

        if((font.getStringWidth(change.getQuantityBought()) / 1000.0f) * 11 + 5 <= cellWidthXPos + 25){
            writeTextNewLineAtOffset(change.getQuantityBought(), font, 12, cellWidthXPos - 20, 0);
        }else{
            textOverflowCounter ++;
            writeTextNewLineAtOffset("*" + textOverflowCounter, font, 12, cellWidthXPos - 20, 0);
            textOverflow.add(change.getQuantityBought());
        }

        if((fontBold.getStringWidth(change.getTotal()) / 1000.0f) * 11 + 5 <= cellWidthXPos){
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

    /**
     * Metoda pro vytvoření hlavičky seznamu směn.
     * @param showTransactionType boolean hodnota, zda-li se má vypsat typ transakce (true - začátek výpisu,
     *                            false - výpis pokračuje na nové straně)
     * @throws IOException
     */
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


    /**
     * Metoda k vypsání celkového zisku.
     * @param changeTotal Celkové zisk
     * @throws IOException
     */
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

            //writeTextNewLineAtOffset(changeTotal, fontBold, 14, textWidthText + 5, 0);
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
    /* PDF sekce směny */

    /* PDF celkový součet */

    /**
     * Metoda pro vytvoření celkového součtu za daňové období.
     * @param buyTotal Celkové náklady za nákup
     * @param sellTotal Celkový zisk za prodej
     * @param changeTotal Celkový zisk za směnu
     * @throws IOException
     */
    private void createTotalOverview(BigDecimal buyTotal, BigDecimal sellTotal, BigDecimal changeTotal) throws IOException {
        if(curYVal - 95f < 70f) {
            createFooter(String.valueOf(pageNum));
            contentStream.close();
            createNewPage();
            pageNum += 1;
        }else{
            curYVal -= 25;
        }
        contentStream.beginText();
        curXVal = 0f;
        total = (sellTotal.add(changeTotal)).subtract(buyTotal);
        String totalToShow;
        String profitLoseText = "Zisk (CZK):";
        if(total.compareTo(BigDecimal.ZERO)<0){
            profitLoseText = "Ztráta (CZK):";
            totalToShow = (total.multiply(shared.getBigDecimal(-1.0))).toPlainString();
        }else{
            totalToShow = total.toPlainString();
        }

        float textWidthText = (font.getStringWidth(profitLoseText) / 1000.0f) * 12;
        float textWidth = (fontBold.getStringWidth(totalToShow) / 1000.0f) * 14;
        float moveRight = width - textWidth - textWidthText - 5 + MARGINSIDE;

        writeTextNewLineAtOffset(profitLoseText, font, 12, moveRight, curYVal);
        writeTextNewLineAtOffset(totalToShow, fontBold, 14, textWidthText + 5, 0);
        contentStream.endText();

        curYVal -= 10;
        contentStream.setNonStrokingColor(150, 150, 150);
        contentStream.addRect(moveRight, curYVal, textWidth + textWidthText + 5, 1);
        contentStream.fill();
        curYVal -= 15;

        contentStream.beginText();
        curXVal = 0f;

        contentStream.setNonStrokingColor(50, 50, 50);
        DecimalFormat df = new DecimalFormat("#.00");
        String euroExchRate = "Využitý kurz EUR: " + df.format(eurExchangeRate);
        textWidth = (font.getStringWidth(euroExchRate) / 1000.0f) * 12;
        moveRight = width - textWidth + MARGINSIDE;
        writeTextNewLineAtOffset(euroExchRate, font, 12, moveRight, curYVal);

        String dollarExchRate = "Využitý kurz USD: " + df.format(usdExchangeRate);
        contentStream.setLeading(15);
        contentStream.newLine();
        contentStream.showText(dollarExchRate);
        curYVal -= 15;
        if(!correctRate){
            String warning = "POZOR! Nejedná se o jednotný kurz za dané daňové období.";
            textWidth = (font.getStringWidth(dollarExchRate) / 1000.0f) * 12;
            float warningWidth = (font.getStringWidth(warning) / 1000.0f) * 12;
            float moveLeft = textWidth - warningWidth;
            contentStream.setNonStrokingColor(5, 5, 5);
            writeTextNewLineAtOffset(warning, font, 12, moveLeft, -20);
        }

        contentStream.endText();
    }
    /* PDF celkový součet */

    public String getFileName(){
        return fileName;
    }

    public BigDecimal getTotal(){
        return total;
    }
}

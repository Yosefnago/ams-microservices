package com.ams.accountantUser.service;


import com.ams.accountantUser.entity.Invoice;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * InvoiceOCRExtractor is responsible for extracting structured invoice data
 * from PDF files using OCR (Optical Character Recognition) via Tesseract.
 *
 * It reads a PDF file, converts the first page into an image, applies OCR,
 * and then parses specific invoice fields like invoice number, client name, and prices.
 */
@Service
public class InvoiceOCRExtractor {

    private static final Tesseract tesseract = new Tesseract();

    static {
        tesseract.setDatapath("C:\\Users\\user\\AppData\\Local\\Programs\\Tesseract-OCR\\tessdata");
        tesseract.setLanguage("eng");
    }
    /**
     * Extracts structured {@link Invoice} data from a PDF byte array using OCR.
     * Assumes the invoice contains keywords like "Invoice Number", "Client", "Total", etc.
     *
     * @param fileContent the byte array of the uploaded PDF file
     * @return an {@link Invoice} object populated with extracted data
     * @throws IOException if PDF or image conversion fails
     * @throws TesseractException if OCR processing fails
     */
    public Invoice extractInvoiceFromImage(byte[] fileContent) throws IOException, TesseractException {
        BufferedImage image = convertPdfToImage(fileContent);

        File tempImage = File.createTempFile("invoice", ".png");
        ImageIO.write(image, "png", tempImage);

        String rawText = tesseract.doOCR(tempImage);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(extractFieldGeneric(rawText, "Invoice Number"));
        invoice.setSapakName(extractFieldGeneric(rawText, "Client"));
        invoice.setPrice(new BigDecimal(extractFieldNumeric(rawText, "Total")));
        invoice.setVatAmount(new BigDecimal(extractFieldNumeric(rawText, "VAT (17%)")));
        invoice.setPriceBeforeVat(new BigDecimal(extractFieldNumeric(rawText, "Subtotal")));
        invoice.setPriceAfterVat(invoice.getPrice());

        return invoice;
    }
    /**
     * Converts the first page of a PDF file (byte array) into a high-resolution image.
     *
     * @param pdfData the PDF file content as byte array
     * @return rendered {@link BufferedImage} of the first page
     * @throws IOException if PDF parsing fails
     */
    private BufferedImage convertPdfToImage(byte[] pdfData) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfData))) {
            PDFRenderer renderer = new PDFRenderer(document);
            return renderer.renderImageWithDPI(0, 300);
        }
    }
    /**
     * Extracts a generic field (non-numeric) from OCR text by label.
     * Example: "Invoice Number: 12345"
     *
     * @param text  the OCR-processed raw text
     * @param label the field label to search for (e.g., "Client")
     * @return the first matching line after the label, trimmed
     */
    private String extractFieldGeneric(String text, String label) {
        Pattern pattern = Pattern.compile(label + "\\s*[:\\-]??\\s*(.+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).split("\\R")[0].trim();
        }
        return "";
    }
    /**
     * Extracts a numeric field from OCR text by label.
     * Handles thousands separators (commas).
     * Example: "Total: 1,234.56"
     *
     * @param text  the OCR-processed raw text
     * @param label the numeric field label (e.g., "Total", "VAT")
     * @return the numeric value as a string without commas
     */
    private String extractFieldNumeric(String text, String label) {
        Pattern pattern = Pattern.compile(label + "\\s*[:\\-]??\\s*([\\d,.]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).replace(",", "");
        }
        return "0.00";
    }
}
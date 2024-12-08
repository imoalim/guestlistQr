package com.example.weddingcheckinapp;

import com.example.weddingcheckinapp.dal.entity.Invitation;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import jakarta.persistence.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class QRCodeValidator {

    public static void main(String[] args) {
        String qrCodeFolder = "qrcodes"; // Folder with QR code images
        String cardsFolder = "generated_invitations"; // Folder with invitation card PDFs

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

        try (emf; EntityManager em = emf.createEntityManager()) {
            // Fetch data from the database
            Map<String, String> dbQRCodes = fetchQRCodesFromDatabase(em);

            // Compare QR codes and generate summary
            compareQRCodesWithDatabase(qrCodeFolder, cardsFolder, dbQRCodes);

        }
    }

    /**
     * Fetch QR codes from the database.
     */
    private static Map<String, String> fetchQRCodesFromDatabase(EntityManager em) {
        Map<String, String> qrCodeMap = new HashMap<>();

        List<Invitation> invitations = em.createQuery("SELECT i FROM Invitation i", Invitation.class).getResultList();

        for (Invitation invitation : invitations) {
            qrCodeMap.put(invitation.getGuestName(), invitation.getQrCodeHash());
        }

        return qrCodeMap;
    }

    /**
     * Compare QR codes extracted from files (qrcodes and cards) with the database.
     */
    private static void compareQRCodesWithDatabase(String qrCodeFolder, String cardsFolder, Map<String, String> dbQRCodes) {
        File qrFolder = new File(qrCodeFolder);
        File cardFolder = new File(cardsFolder);

        if (!qrFolder.exists() || !qrFolder.isDirectory() || !cardFolder.exists() || !cardFolder.isDirectory()) {
            System.err.println("Invalid folder paths. Ensure QR code and card folders exist.");
            return;
        }

        File[] qrCodeFiles = qrFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        File[] cardFiles = cardFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (qrCodeFiles == null || qrCodeFiles.length == 0 || cardFiles == null || cardFiles.length == 0) {
            System.err.println("No QR codes or card files found.");
            return;
        }

        int matched = 0, mismatched = 0, missing = 0;

        // Decode QR codes from the qrcodes folder
        Map<String, String> qrCodeMap = new HashMap<>();
        for (File qrCodeFile : qrCodeFiles) {
            try {
                String decodedText = decodeQRCode(qrCodeFile);
                if (decodedText != null) {
                    qrCodeMap.put(qrCodeFile.getName(), decodedText);
                } else {
                    System.err.println("❌ No QR code detected in: " + qrCodeFile.getName());
                }
            } catch (Exception e) {
                System.err.println("❌ Error decoding QR code for: " + qrCodeFile.getName());
                e.printStackTrace();
            }
        }

        // Compare cards with the database
        for (File cardFile : cardFiles) {
            System.out.println("Processing card: " + cardFile.getName());
            try {
                String decodedText = extractQRCodeFromPDF(cardFile);

                if (decodedText == null) {
                    System.err.println("❌ No QR code found in card: " + cardFile.getName());
                    missing++;
                    continue;
                }

                // Compare the extracted QR code with the database
                if (dbQRCodes.containsValue(decodedText)) {
                    System.out.println("✅ QR code in " + cardFile.getName() + " matches the database.");
                    matched++;
                } else {
                    System.err.println("❌ QR code in " + cardFile.getName() + " does NOT match the database.");
                    mismatched++;
                }

            } catch (Exception e) {
                System.err.println("❌ Error processing card: " + cardFile.getName());
                e.printStackTrace();
            }
        }

        // Summary
        System.out.println("\n--- Summary ---");
        System.out.println("Total Cards Processed: " + cardFiles.length);
        System.out.println("Total QR Codes Processed: " + qrCodeFiles.length);
        System.out.println("Matched: " + matched);
        System.out.println("Mismatched: " + mismatched);
        System.out.println("Missing: " + missing);
    }

    /**
     * Decode a QR code from an image file.
     */
    private static String decodeQRCode(File qrCodeFile) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(qrCodeFile);

        if (bufferedImage == null) {
            throw new IOException("Image could not be read.");
        }

        try {
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return null; // No QR code found
        }
    }

    /**
     * Extract and decode a QR code from a PDF file.
     */
    private static String extractQRCodeFromPDF(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);

            // Iterate through all pages (usually only one page has the QR code)
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = renderer.renderImageWithDPI(page, 300); // Render at 300 DPI for better accuracy

                String decodedText = decodeQRCodeFromImage(image);
                if (decodedText != null) {
                    return decodedText; // Return as soon as we find a QR code
                }
            }
        }
        return null; // No QR code found in the PDF
    }

    /**
     * Decode QR code from a BufferedImage.
     */
    private static String decodeQRCodeFromImage(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return null; // No QR code found
        }
    }
}

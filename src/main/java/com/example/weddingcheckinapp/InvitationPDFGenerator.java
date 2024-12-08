package com.example.weddingcheckinapp;

import com.example.weddingcheckinapp.dal.entity.Invitation;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import jakarta.persistence.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class InvitationPDFGenerator {

    public static void main(String[] args) {
        String qrCodeFolder = "qrcodes"; // Folder containing QR codes
        String templatePath = "Einladungskarte 2.pdf"; // Path to the PDF template
        String outputFolder = "generated_invitations"; // Folder to save generated invitations

        // Create directories if they don't exist
        new File(outputFolder).mkdirs();

        // Database connection
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        try (emf; EntityManager em = emf.createEntityManager()) {
            // Fetch guest data from the database
            List<Invitation> invitations = em.createQuery("SELECT i FROM Invitation i", Invitation.class).getResultList();

            for (Invitation invitation : invitations) {
                String guestName = invitation.getGuestName();
                String qrCodePath = qrCodeFolder + "/" + guestName + ".png";

                // Check if QR code exists
                File qrCodeFile = new File(qrCodePath);
                if (!qrCodeFile.exists()) {
                    System.err.println("❌ QR code not found for: " + guestName);
                    continue;
                }

                try {
                    // Generate personalized invitation
                    String outputFilePath = outputFolder + "/" + guestName + "_Einladung.pdf";
                    createPersonalizedPDF(templatePath, outputFilePath, qrCodePath, guestName);

                    System.out.println("✅ Invitation created for: " + guestName);
                } catch (Exception e) {
                    System.err.println("❌ Failed to generate invitation for: " + guestName);
                    e.printStackTrace();
                }
            }
        }
    }

    private static void createPersonalizedPDF(String templatePath, String outputFilePath, String qrCodePath, String guestName) throws IOException {
        try (PDDocument document = PDDocument.load(new File(templatePath))) {
            PDPage page = document.getPage(0); // Assuming the template has only one page
            PDRectangle pageSize = page.getMediaBox();

            // Add QR code
            PDImageXObject qrCodeImage = PDImageXObject.createFromFile(qrCodePath, document);
            float qrCodeWidth = 75; // Width of the QR code
            float qrCodeHeight = 75; // Height of the QR code
            float qrCodeX = pageSize.getWidth() - qrCodeWidth - 110; // Place QR code 50px from the right edge
            float qrCodeY = 70; // Place QR code 50px from the bottom

            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
            contentStream.drawImage(qrCodeImage, qrCodeX, qrCodeY, qrCodeWidth, qrCodeHeight);

            // Optional: Add guest name or other text (if needed)
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(100, 700); // Adjust text position
            contentStream.showText("Dear " + guestName + ",");
            contentStream.endText();

            contentStream.close();

            // Save the modified document
            document.save(outputFilePath);
        }
    }
}

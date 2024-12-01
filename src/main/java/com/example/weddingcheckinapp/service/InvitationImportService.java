package com.example.weddingcheckinapp.service;

import com.example.weddingcheckinapp.dal.entity.Invitation;
import com.example.weddingcheckinapp.dal.repo.InvitationRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class InvitationImportService {

    @Autowired
    private InvitationRepository invitationRepository;

    public void importInvitations(String excelFilePath) throws IOException, WriterException {
        // Öffne die Excel-Datei
        try (FileInputStream file = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0); // Erste Tabelle
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Überspringe die Kopfzeile

                String guestName = row.getCell(0).getStringCellValue(); // Name
                int allowedGuests = (int) row.getCell(1).getNumericCellValue(); // Erlaubte Gäste

                // Überprüfen, ob die Person bereits existiert
                if (invitationRepository.existsByGuestName(guestName)) {
                    System.out.println("Person bereits vorhanden: " + guestName);
                    continue; // Überspringe diese Person
                }

                // Generiere QR-Code-Hash
                String qrCodeHash = UUID.randomUUID().toString();

                // Speichere die Einladung in der Datenbank
                Invitation invitation = new Invitation();
                invitation.setGuestName(guestName);
                invitation.setAllowedGuests(allowedGuests);
                invitation.setRemainingGuests(allowedGuests);
                invitation.setQrCodeHash(qrCodeHash);
                invitationRepository.save(invitation);

                // Generiere QR-Code-Bild
                generateQRCode(qrCodeHash, guestName);
                System.out.println("Einladung hinzugefügt und QR-Code generiert: " + guestName);
            }
        }
    }

    private void generateQRCode(String qrCodeContent, String guestName) throws WriterException, IOException {
        // Ersetze ungültige Zeichen im Gastnamen
        String sanitizedGuestName = guestName.replaceAll("[/\\\\:*?\"<>|]", "_");

        // Stelle sicher, dass das Verzeichnis existiert
        File directory = new File("qrcodes");
        if (!directory.exists()) {
            directory.mkdirs(); // Erstelle das Verzeichnis, falls es nicht existiert
        }

        // Generiere den QR-Code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        var bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 300, 300);

        // Speichere den QR-Code im Verzeichnis
        Path path = Paths.get("qrcodes/" + sanitizedGuestName + ".png");
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        System.out.println("QR-Code erfolgreich generiert: " + sanitizedGuestName);
    }

}


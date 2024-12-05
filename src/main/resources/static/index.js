let scanner; // Referenz auf den QR-Scanner
let isProcessing = false; // Flag, um mehrfaches Senden zu verhindern

document.addEventListener("DOMContentLoaded", () => {
    const startButton = document.getElementById("start-scan-button");
    const resultContainer = document.getElementById("result");
    const qrReaderElement = document.getElementById("qr-reader");

    // Funktion zum Anzeigen von Rückmeldungen
    const displayResult = (message, success) => {
        resultContainer.innerHTML = `
            <span class="${success ? 'result-success' : 'result-error'}">
                ${success ? '✅' : '❌'} ${message}
            </span>
        `;
    };

    // Funktion zum Starten des Scannens
    const startScanning = () => {
        startButton.style.display = "none"; // Verstecke den Button
        qrReaderElement.style.display = "block"; // Zeige den QR-Reader an

        scanner = new Html5Qrcode("qr-reader");
        scanner
            .start(
                { facingMode: "environment" }, // Rückkamera verwenden
                { fps: 10, qrbox: 250 },
                onScanSuccess,
                onScanFailure
            )
            .catch((err) => {
                console.error("Fehler beim Starten des Scanners:", err);
                displayResult("Kamera konnte nicht gestartet werden.", false);
            });
    };

    // Erfolg nach Scan
    const onScanSuccess = (decodedText) => {
        //TODO: FIX error when scanning, it scans the same qr code multiple times
        if (isProcessing) return; // Verhindere mehrfaches Senden
        isProcessing = true;

        console.log(`QR-Code gescannt: ${decodedText}`);

        // QR-Code an das Backend senden
        fetch("http://localhost:8080/api/checkin", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ qrCode: decodedText }),
        })
            .then((response) => {
                isProcessing = false;
                if (!response.ok) {
                    return response.json().then((err) => {
                        throw new Error(err.message || `HTTP Fehler: ${response.status}`);
                    });
                }
                return response.json();
            })
            .then((data) => {
                // Erfolgsmeldung anzeigen
                displayResult(data.message, true);
            })
            .catch((err) => {
                isProcessing = false;
                console.error("Fehler beim Senden der Daten:", err);
                displayResult(`Fehler: ${err.message}`, false);
            });
    };

    // Fehler beim Scannen
    const onScanFailure = (error) => {
        console.warn(`Scan fehlgeschlagen: ${error}`);
    };

    // Event-Listener für den Start-Button
    startButton.addEventListener("click", startScanning);
});

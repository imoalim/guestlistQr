let scanner; // Referenz auf den QR-Scanner
let isProcessing = false; // Flag, um mehrfaches Senden zu verhindern

document.addEventListener("DOMContentLoaded", () => {
    const startButton = document.getElementById("start-scan-button");
    const resultContainer = document.getElementById("result");
    const qrReaderElement = document.getElementById("qr-reader");

    const displayResult = (message, success) => {
        resultContainer.innerHTML = `
            <span class="${success ? 'result-success' : 'result-error'}">
                ${success ? '✅' : '❌'} ${message}
            </span>
        `;
    };

    const startScanning = async () => {
        startButton.style.display = "none";
        qrReaderElement.style.display = "block";

        scanner = new Html5Qrcode("qr-reader");
        try {
            const cameras = await Html5Qrcode.getCameras();
            console.log("Gefundene Kameras:", cameras);

            if (cameras.length === 0) {
                displayResult("Keine Kamera gefunden. Bitte prüfen Sie Ihre Geräteeinstellungen.", false);
                return;
            }

            const cameraConfig = cameras.find((cam) => cam.label.toLowerCase().includes("back"))
                ? { facingMode: "environment" }
                : { facingMode: "user" };

            scanner.start(cameraConfig, { fps: 15, qrbox: 300 }, onScanSuccess, onScanFailure);
        } catch (err) {
            console.error("Fehler beim Starten des Scanners:", err);
            displayResult("Kamera konnte nicht gestartet werden.", false);
        }
    };

    const onScanSuccess = (decodedText) => {
        if (isProcessing) return;
        isProcessing = true;

        console.log(`QR-Code gescannt: ${decodedText}`);
        displayResult("Ergebnis wird verarbeitet...", true);

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
                displayResult(data.message, true);
            })
            .catch((err) => {
                isProcessing = false;
                console.error("Fehler beim Senden der Daten:", err);
                displayResult(`Fehler: ${err.message}`, false);
            });
    };

    const onScanFailure = (error) => {
        if (error.message !== "No MultiFormat Readers were able to detect the code.") {
            console.warn("Scan fehlgeschlagen. Fehlerdetails:", error);
        }
    };

    startButton.addEventListener("click", startScanning);
});

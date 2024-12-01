import React, { useState, useRef } from "react";
import { Html5Qrcode } from "html5-qrcode";
import "../styling/QRScanner.css";

function QRScanner() {
    const [isCameraActive, setIsCameraActive] = useState(false);
    const [result, setResult] = useState("");
    const scannerRef = useRef(null);

    const startScanning = () => {
        setIsCameraActive(true);

        // HTML5 QR Code Scanner initialisieren
        scannerRef.current = new Html5Qrcode("qr-reader");

        scannerRef.current
            .start(
                { facingMode: "environment" }, // Rückkamera verwenden
                {
                    fps: 10,
                    qrbox: 250, // Größe des QR-Scan-Fensters
                },
                (decodedText) => {
                    handleScanSuccess(decodedText);
                },
                (error) => {
                    if (error.message !== "No MultiFormat Readers were able to detect the code.") {
                        console.error("Scan-Fehler:", error);
                    }
                }
            )
            .catch((err) => {
                console.error("Fehler beim Starten des Scanners:", err);
                setResult(`Fehler: ${err.message}`);
            });
    };

    const stopScanning = () => {
        if (scannerRef.current) {
            scannerRef.current.stop().then(() => {
                scannerRef.current.clear();
            });
        }
        setIsCameraActive(false);
    };

    const handleScanSuccess = (decodedText) => {
        setResult(`QR-Code gescannt: ${decodedText}`);
        // QR-Code an das Backend senden
        fetch("http://localhost:8080/checkin", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ qrCode: decodedText }),
        })
            .then((response) => {
                if (!response.ok) {
                    return response.json().then((err) => {
                        throw new Error(err.message || `HTTP Fehler: ${response.status}`);
                    });
                }
                return response.json();
            })
            .then((data) => {
                setResult(`Erfolgreich eingecheckt: ${data.message}`);
            })
            .catch((err) => {
                setResult(`Fehler: ${err.message}`);
            });
    };

    return (
        <div className="qr-scanner-container">
            <h1 className="title">Wedding Check-In</h1>
            <p className="instructions">Scanne den QR-Code, um den Check-In zu starten.</p>

            {!isCameraActive && (
                <button className="start-camera-button" onClick={startScanning}>
                    Kamera starten
                </button>
            )}

            {isCameraActive && (
                <>
                    <div
                        id="qr-reader"
                        style={{
                            width: "300px",
                            height: "300px",
                            margin: "0 auto",
                            border: "2px solid #007bff",
                            borderRadius: "10px",
                        }}
                    ></div>
                    <p
                        className={`result-text ${
                            result.includes("Fehler") ? "error" : "success"
                        }`}
                    >
                        {result}
                    </p>
                    <button className="stop-camera-button" onClick={stopScanning}>
                        Kamera stoppen
                    </button>
                </>
            )}
        </div>
    );
}

export default QRScanner;

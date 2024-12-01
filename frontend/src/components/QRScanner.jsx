import React, { useState } from 'react';
import { Html5QrcodeScanner } from 'html5-qrcode';

function QRScanner() {
    const [result, setResult] = useState('');

    const onScanSuccess = (decodedText) => {
        setResult(`Erfolgreich gescannt: ${decodedText}`);
        // Sende den QR-Code an das Backend
        fetch('http://localhost:8080/checkin', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ qrCode: decodedText })
        })
            .then((response) => response.json())
            .then((data) => alert(data.message))
            .catch((err) => console.error('Fehler beim Senden:', err));
    };

    React.useEffect(() => {
        const scanner = new Html5QrcodeScanner('qr-reader', { fps: 10, qrbox: 250 });
        scanner.render(onScanSuccess, console.error);
        return () => scanner.clear();
    }, []);

    return (
        <div>
            <h1>QR-Code-Scanner</h1>
            <div id="qr-reader" style={{ width: '300px' }}></div>
            <p>{result}</p>
        </div>
    );
}

export default QRScanner;

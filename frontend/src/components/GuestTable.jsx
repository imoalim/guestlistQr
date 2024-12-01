import React, { useEffect, useState } from 'react';
import EditModal from "./EditModal";

function GuestTable() {
    const [guests, setGuests] = useState([]);
    const [editingGuest, setEditingGuest] = useState(null);

    useEffect(() => {
        fetch('http://localhost:8080/guests') // API-Endpoint, um Gäste zu laden
            .then((response) => response.json())
            .then((data) => setGuests(data))
            .catch((err) => console.error('Fehler beim Laden der Gäste:', err));
    }, []);

    const handleEdit = (guest) => {
        setEditingGuest(guest);
    };

    const handleSave = (updatedGuest) => {
        // Speichern der Änderungen im Backend
        fetch(`http://localhost:8080/guests/${updatedGuest.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updatedGuest),
        })
            .then(() => {
                setGuests((prevGuests) =>
                    prevGuests.map((g) => (g.id === updatedGuest.id ? updatedGuest : g))
                );
                setEditingGuest(null);
            })
            .catch((err) => console.error('Fehler beim Speichern:', err));
    };

    return (
        <div>
            <h1>Gästeliste</h1>
            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Erlaubte Gäste</th>
                    <th>Verbleibende Gäste</th>
                    <th>Eingecheckt</th>
                    <th>QR-Code</th>
                    <th>Aktionen</th>
                </tr>
                </thead>
                <tbody>
                {guests.map((guest) => (
                    <tr key={guest.id}>
                        <td>{guest.name}</td>
                        <td>{guest.allowedGuests}</td>
                        <td>{guest.remainingGuests}</td>
                        <td>{guest.checkedIn ? 'Ja' : 'Nein'}</td>
                        <td>
                            <img src={`http://localhost:8080/qrcodes/${guest.qrCodeHash}.png`} alt="QR-Code" />
                        </td>
                        <td>
                            <button onClick={() => handleEdit(guest)}>Bearbeiten</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            {editingGuest && (
                <EditModal
                    guest={editingGuest}
                    onSave={handleSave}
                    onCancel={() => setEditingGuest(null)}
                />
            )}
        </div>
    );
}

export default GuestTable;

import React, { useState } from 'react';

function EditModal({ guest, onSave, onCancel }) {
    const [formData, setFormData] = useState({ ...guest });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: name === "allowedGuests" ? parseInt(value, 10) : value, // Falls nötig, konvertiere
        }));
    };

    const handleSubmit = () => {
        // Speichere Änderungen durch Aufruf der onSave-Funktion
        onSave(formData);
    };

    return (
        <div className="modal">
            <h2>Gast bearbeiten</h2>
            <label>
                Name:
                <input
                    name="guestName" // Passend zur Backend-Feldstruktur
                    value={formData.guestName}
                    onChange={handleChange} // Aktualisiere den lokalen Zustand
                />
            </label>
            <label>
                Erlaubte Gäste:
                <input
                    name="allowedGuests"
                    type="number"
                    value={formData.allowedGuests}
                    onChange={handleChange} // Aktualisiere den lokalen Zustand
                />
            </label>
            <label>
                Bereits eingecheckt:
                <select
                    name="checkedIn"
                    value={formData.checkedIn}
                    onChange={(e) =>
                        setFormData({ ...formData, checkedIn: e.target.value === "true" })
                    }
                >
                    <option value={true}>Ja</option>
                    <option value={false}>Nein</option>
                </select>
            </label>
            <button onClick={handleSubmit}>Speichern</button>
            <button onClick={onCancel}>Abbrechen</button>
        </div>
    );
}

export default EditModal;

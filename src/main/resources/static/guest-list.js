document.addEventListener("DOMContentLoaded", () => {
    const tableBody = document.querySelector("#guest-table tbody");

    // Lade die Gästeliste
    fetch("http://localhost:8080/api/checkin/guests")
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP-Fehler! Status: ${response.status}`);
            }
            return response.json();
        })
        .then((guests) => {
            // Tabelle leeren, falls bereits Einträge vorhanden sind
            tableBody.innerHTML = "";

            // Füge jeden Gast in die Tabelle ein
            guests.forEach((guest) => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${guest.guestName}</td>
                    <td>${guest.allowedGuests}</td>
                    <td>${guest.remainingGuests}</td>
                    <td>${guest.checkedIn ? "Ja" : "Nein"}</td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch((err) => {
            console.error("Fehler beim Laden der Gästeliste:", err);
            const errorRow = document.createElement("tr");
            errorRow.innerHTML = `
                <td colspan="4" style="color: red; text-align: center;">Fehler beim Laden der Gästeliste.</td>
            `;
            tableBody.appendChild(errorRow);
        });
});

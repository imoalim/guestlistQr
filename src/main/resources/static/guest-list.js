document.addEventListener("DOMContentLoaded", () => {
    const tableBody = document.querySelector("#guest-table tbody");
    const formContainer = document.querySelector("#form-container");
    const guestForm = document.querySelector("#guest-form");
    const addButton = document.querySelector("#add-button");
    const cancelButton = document.querySelector("#cancel-button");
    const formTitle = document.querySelector("#form-title");
    const searchInput = document.querySelector("#search-input");

    const guestIdInput = document.querySelector("#guest-id");
    const guestNameInput = document.querySelector("#guest-name");
    const allowedGuestsInput = document.querySelector("#allowed-guests");
    const remainingGuestsInput = document.querySelector("#remaining-guests");
    const checkedInInput = document.querySelector("#checked-in");

    let isEditing = false;

    const loadGuests = () => {
        fetch("http://localhost:8080/api/checkin/guests")
            .then((response) => response.json())
            .then((guests) => {
                tableBody.innerHTML = "";
                guests.forEach((guest) => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${guest.guestName}</td>
                        <td>${guest.allowedGuests}</td>
                        <td>${guest.remainingGuests}</td>
                        <td>${guest.checkedIn ? "Ja" : "Nein"}</td>
                        <td>
                            <img src="https://api.qrserver.com/v1/create-qr-code/?data=${guest.qrCodeHash}&size=100x100" alt="QR-Code">
                        </td>
                        <td>
                            <button class="edit-button" data-id="${guest.id}">Bearbeiten</button>
                            <button class="delete-button" data-id="${guest.id}">Löschen</button>
                        </td>
                    `;
                    tableBody.appendChild(row);
                });

                document.querySelectorAll(".edit-button").forEach((button) => {
                    button.addEventListener("click", handleEdit);
                });

                document.querySelectorAll(".delete-button").forEach((button) => {
                    button.addEventListener("click", handleDelete);
                });
            })
            .catch((err) => console.error("Fehler beim Laden der Gästeliste:", err));
    };

    const handleEdit = (event) => {
        const guestId = event.target.dataset.id;
        fetch(`http://localhost:8080/api/checkin/guests/${guestId}`)
            .then((response) => response.json())
            .then((guest) => {
                isEditing = true;
                formTitle.textContent = "Gast bearbeiten";
                guestIdInput.value = guest.id;
                guestNameInput.value = guest.guestName;
                allowedGuestsInput.value = guest.allowedGuests;
                remainingGuestsInput.value = guest.remainingGuests;
                checkedInInput.value = guest.checkedIn;
                formContainer.style.display = "block";
                addButton.style.display = "none";
            });
    };

    const handleDelete = (event) => {
        const guestId = event.target.dataset.id;
        if (confirm("Möchten Sie diesen Gast wirklich löschen?")) {
            fetch(`http://localhost:8080/api/checkin/guests/${guestId}`, {
                method: "DELETE",
            })
                .then(() => loadGuests())
                .catch((err) => console.error("Fehler beim Löschen:", err));
        }
    };

    const handleFormSubmit = (event) => {
        event.preventDefault();
        const guest = {
            guestName: guestNameInput.value,
            allowedGuests: parseInt(allowedGuestsInput.value, 10),
            remainingGuests: parseInt(remainingGuestsInput.value, 10),
            checkedIn: checkedInInput.value === "true",
        };

        let url = "http://localhost:8080/api/checkin/guests";
        let method = "POST";

        if (isEditing) {
            url += `/${guestIdInput.value}`;
            method = "PUT";
        }

        fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(guest),
        })
            .then(() => {
                loadGuests();
                formContainer.style.display = "none";
                addButton.style.display = "block";
                guestForm.reset();
                isEditing = false;
            })
            .catch((err) => console.error("Fehler beim Speichern:", err));
    };

    searchInput.addEventListener("input", (event) => {
        const query = event.target.value.toLowerCase();
        document.querySelectorAll("#guest-table tbody tr").forEach((row) => {
            const name = row.children[0].textContent.toLowerCase();
            row.style.display = name.includes(query) ? "" : "none";
        });
    });

    addButton.addEventListener("click", () => {
        formTitle.textContent = "Gast hinzufügen";
        formContainer.style.display = "block";
        addButton.style.display = "none";
        isEditing = false;
        guestForm.reset();
    });

    cancelButton.addEventListener("click", () => {
        formContainer.style.display = "none";
        addButton.style.display = "block";
    });

    guestForm.addEventListener("submit", handleFormSubmit);

    loadGuests();
});

import {
    initThemeToggle,
    initLanguageSwitcher,
    applyPageTranslations,
    getStartLanguage
} from "../theme-language/theme-language-core.js";

const translations = {
    en: {
        pageTitle: "Settings - Chuhax",
        pageHeading: "Settings",
        backButtonAria: "Back to Dashboard",
        profileSectionHeader: "Profile Details",
        billingSectionHeader: "Billing & Tax",
        btnEdit: "EDIT",
        legendFullName: "Full Name",
        legendHomeAddress: "Home/Business Address",
        legendHstNumber: "HST Number",
        legendHourlyRate: "Default Hourly Rate ($)",
        btnSave: "SAVE CHANGES",
        btnLogout: "LOG OUT",
        themeButtonLabel: "Toggle dark mode",
        languageGroup: "Language"
    },
    fr: {
        pageTitle: "Paramètres - Chuhax",
        pageHeading: "Paramètres",
        backButtonAria: "Retour au tableau de bord",
        profileSectionHeader: "Détails du profil",
        billingSectionHeader: "Facturation et taxes",
        btnEdit: "MODIFIER",
        legendFullName: "Nom complet",
        legendHomeAddress: "Adresse du domicile / entreprise",
        legendHstNumber: "Numéro de TVH",
        legendHourlyRate: "Taux horaire par défaut ($)",
        btnSave: "ENREGISTRER LES MODIFICATIONS",
        btnLogout: "SE DÉCONNECTER",
        themeButtonLabel: "Basculer le mode sombre",
        languageGroup: "Langue"
    },
    uk: {
        pageTitle: "Налаштування - Chuhax",
        pageHeading: "Налаштування",
        backButtonAria: "Назад до панелі керування",
        profileSectionHeader: "Особисті дані",
        billingSectionHeader: "Оплата та податки",
        btnEdit: "РЕДАГУВАТИ",
        legendFullName: "Повне ім’я",
        legendHomeAddress: "Домашня / юридична адреса",
        legendHstNumber: "Номер HST",
        legendHourlyRate: "Погодинна ставка за замовчуванням ($)",
        btnSave: "ЗБЕРЕГТИ ЗМІНИ",
        btnLogout: "ВИЙТИ З ОБЛІКОВОГО ЗАПИСУ",
        themeButtonLabel: "Перемкнути темний режим",
        languageGroup: "Мова"
    }
};

/**
 * Enables editing for inputs belonging to the requested section
 * @param {string} sectionName - Identifier of section to unlock ('profile' or 'billing').
 */
function toggleEdit(sectionName) {
    let targetFieldIds = [];
    if (sectionName === 'profile') {
        targetFieldIds = ['fullName', 'homeAddress'];
    } else if (sectionName === 'billing') {
        targetFieldIds = ['hstNumber', 'hourlyRate'];
    }

    targetFieldIds.forEach(fieldId => {
        const inputElement = document.getElementById(fieldId);
        const fieldsetElement = document.getElementById('fs-' + fieldId);

        if (inputElement && fieldsetElement && inputElement.hasAttribute('readonly')) {
            inputElement.removeAttribute('readonly');
            fieldsetElement.classList.add('editing');
            document.getElementById('saveButton').style.display = 'block';
        }
    });
}

/**
 * Locks all fields back to read-only state and hides the save button
 */
function saveChanges() {
    const allFieldIds = ['fullName', 'homeAddress', 'hstNumber', 'hourlyRate'];
    allFieldIds.forEach(fieldId => {
        const inputElement = document.getElementById(fieldId);
        const fieldsetElement = document.getElementById('fs-' + fieldId);

        if (inputElement && fieldsetElement) {
            inputElement.setAttribute('readonly', 'true');
            fieldsetElement.classList.remove('editing');
        }
    });
    document.getElementById('saveButton').style.display = 'none';
}

document.addEventListener('DOMContentLoaded', () => {
    initThemeToggle();
    initLanguageSwitcher(translations);
    applyPageTranslations(getStartLanguage(translations), translations);

    document.getElementById('btnEditProfile')?.addEventListener('click', () => toggleEdit('profile'));
    document.getElementById('btnEditBilling')?.addEventListener('click', () => toggleEdit('billing'));
    document.getElementById('saveButton')?.addEventListener('click', saveChanges);
});
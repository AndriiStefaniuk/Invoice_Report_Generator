import {
    initThemeToggle,
    initLanguageSwitcher,
    applyPageTranslations,
    getStartLanguage
} from "../theme-language/theme-language-core.js";

const translations = {
    en: {
        pageTitle: "Create Report - Chuhax",
        navTitle: "Create Report",
        backButtonAria: "Back to Dashboard",
        datesSectionLabel: "Report Date Range",
        legendStartDate: "Report Start Date",
        legendEndDate: "Report End Date",
        sitesSectionLabel: "Site Address(es)",
        legendSiteAddress: "New Site Address",
        siteAddressPlaceholder: "Enter a site address...",
        btnAddSite: "ADD SITE",
        addedSitesTitle: "Added Sites:",
        removeSiteAria: "Remove site address",
        hoursSectionLabel: "Weekly Hours Worked",
        legendWeekOneHours: "Hours Worked: Week 1",
        legendWeekTwoHours: "Hours Worked: Week 2",
        btnGenerate: "GENERATE EXCEL REPORT",
        themeButtonLabel: "Toggle dark mode",
        languageGroup: "Language"
    },
    fr: {
        pageTitle: "Créer un rapport - Chuhax",
        navTitle: "Créer un rapport",
        backButtonAria: "Retour au tableau de bord",
        datesSectionLabel: "Période du rapport",
        legendStartDate: "Date de début du rapport",
        legendEndDate: "Date de fin du rapport",
        sitesSectionLabel: "Adresse(s) du site",
        legendSiteAddress: "Nouvelle adresse du site",
        siteAddressPlaceholder: "Entrez une adresse...",
        btnAddSite: "AJOUTER",
        addedSitesTitle: "Sites ajoutés :",
        removeSiteAria: "Supprimer l'adresse",
        hoursSectionLabel: "Heures travaillées par semaine",
        legendWeekOneHours: "Heures travaillées : Semaine 1",
        legendWeekTwoHours: "Heures travaillées : Semaine 2",
        btnGenerate: "GÉNÉRER LE RAPPORT EXCEL",
        themeButtonLabel: "Basculer le mode sombre",
        languageGroup: "Langue"
    },
    uk: {
        pageTitle: "Створити звіт - Chuhax",
        navTitle: "Створити звіт",
        backButtonAria: "Назад до панелі керування",
        datesSectionLabel: "Період звіту",
        legendStartDate: "Дата початку звіту",
        legendEndDate: "Дата завершення звіту",
        sitesSectionLabel: "Адреса(и) об'єктів",
        legendSiteAddress: "Нова адреса об'єкта",
        siteAddressPlaceholder: "Введіть адресу об'єкта...",
        btnAddSite: "ДОДАТИ",
        addedSitesTitle: "Додані об'єкти:",
        removeSiteAria: "Видалити адресу",
        hoursSectionLabel: "Відпрацьовані години за тиждень",
        legendWeekOneHours: "Відпрацьовані години: Тиждень 1",
        legendWeekTwoHours: "Відпрацьовані години: Тиждень 2",
        btnGenerate: "СФОРМУВАТИ ЗВІТ EXCEL",
        themeButtonLabel: "Перемкнути темний режим",
        languageGroup: "Мова"
    }
};

const siteAddresses = [
    "123 Mississauga Rd",
    "456 Oak Ave, Toronto",
    "789 Pine St, Kitchener"
];

/**
 * Re-renders the list of added site addresses in the DOM.
 */
function renderSiteAddresses() {
    const siteListElement = document.getElementById("addedSitesList");
    if (!siteListElement) return;

    siteListElement.innerHTML = "";
    siteAddresses.forEach((address, index) => {
        const listItemElement = document.createElement("li");

        const textGroupElement = document.createElement("div");
        textGroupElement.className = "site-text-group";
        textGroupElement.innerHTML = `<span aria-hidden="true">📍</span><span>${address}</span>`;

        const removeButtonElement = document.createElement("button");
        removeButtonElement.type = "button";
        removeButtonElement.className = "remove-site-btn";
        removeButtonElement.innerHTML = "&times;";
        removeButtonElement.setAttribute("data-translate-aria-label", "removeSiteAria");
        removeButtonElement.setAttribute("aria-label", "Remove site address");
        removeButtonElement.addEventListener("click", () => {
            siteAddresses.splice(index, 1);
            renderSiteAddresses();
        });

        listItemElement.appendChild(textGroupElement);
        listItemElement.appendChild(removeButtonElement);
        siteListElement.appendChild(listItemElement);
    });
}

/**
 * Adds the current input value as a new site address.
 */
function handleAddSite() {
    const siteInputElement = document.getElementById("siteAddressInput");
    if (!siteInputElement) return;

    const trimmedAddress = siteInputElement.value.trim();
    if (trimmedAddress.length > 0) {
        siteAddresses.push(trimmedAddress);
        siteInputElement.value = "";
        renderSiteAddresses();
    }
}

document.addEventListener("DOMContentLoaded", () => {
    initThemeToggle();
    initLanguageSwitcher(translations);

    const activeLanguage = getStartLanguage(translations);
    applyPageTranslations(activeLanguage, translations);
    renderSiteAddresses();

    document.getElementById("btnAddSite")?.addEventListener("click", handleAddSite);

    document.getElementById("siteAddressInput")?.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
            event.preventDefault();
            handleAddSite();
        }
    });

    document.getElementById("generateReportButton")?.addEventListener("click", () => {
        console.log("Submitting report generation payload with sites:", siteAddresses);
    });
});
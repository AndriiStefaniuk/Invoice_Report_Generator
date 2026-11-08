import {
    initThemeToggle,
    initLanguageSwitcher,
    applyPageTranslations,
    getStartLanguage
} from "../theme-language/theme-language-core.js";

const translations = {
    en: {
        pageTitle: "Report Details - Chuhax",
        navTitle: "Report Details",
        backButtonAria: "Back to Dashboard",
        sitesHeader: "Site Address(es)",
        hoursHeader: "Hours Worked",
        week1Label: "Week 1",
        week2Label: "Week 2",
        unitHours: "Hours",
        totalEarnedHeader: "Total Earned",
        btnDownload: "Download Report (.xlsx)",
        themeButtonLabel: "Toggle dark mode",
        languageGroup: "Language"
    },
    fr: {
        pageTitle: "Détails du rapport - Chuhax",
        navTitle: "Détails du rapport",
        backButtonAria: "Retour au tableau de bord",
        sitesHeader: "Adresse(s) du site",
        hoursHeader: "Heures travaillées",
        week1Label: "Semaine 1",
        week2Label: "Semaine 2",
        unitHours: "Heures",
        totalEarnedHeader: "Total gagné",
        btnDownload: "Télécharger le rapport (.xlsx)",
        themeButtonLabel: "Basculer le mode sombre",
        languageGroup: "Langue"
    },
    uk: {
        pageTitle: "Деталі звіту - Chuhax",
        navTitle: "Деталі звіту",
        backButtonAria: "Назад до панелі керування",
        sitesHeader: "Адреса(и) об'єктів",
        hoursHeader: "Відпрацьовані години",
        week1Label: "Тиждень 1",
        week2Label: "Тиждень 2",
        unitHours: "Годин",
        totalEarnedHeader: "Загальний заробіток",
        btnDownload: "Завантажити звіт (.xlsx)",
        themeButtonLabel: "Перемкнути темний режим",
        languageGroup: "Мова"
    }
};

/**
 * Current report payload matching the Java server DTO
 */
const currentReport = {
    id: 1,
    site_address_array: [
        "123 Dundas St E, Mississauga",
        "456 Mavis Rd, Mississauga"
    ],
    first_week_start_date: "2026-10-01",
    first_week_end_date: "2026-10-07",
    first_week_hours: 18.0,
    second_week_start_date: "2026-10-08",
    second_week_end_date: "2026-10-14",
    second_week_hours: 17.5,
    total_amount: "$4,011.50"
};

/**
 * Formats an ISO date string (YYYY-MM-DD) into a localized short month and day
 * @param {string} isoDate - Date string in YYYY-MM-DD format
 * @param {string} language - Two-letter language code (e.g., "en", "fr", "uk")
 * @returns {string} Formatted localized date string (e.g., "Oct 1" or "1 жовт.")
 */
function formatShortDate(isoDate, language) {
    if (!isoDate) return "";
    const [year, month, day] = isoDate.split("-").map(Number);
    // Use UTC to prevent local timezone offset shifts
    const date = new Date(Date.UTC(year, month - 1, day));

    return new Intl.DateTimeFormat(language, {
        month: "short",
        day: "numeric",
        timeZone: "UTC"
    }).format(date);
}

/**
 * Updates dynamic report data fields in the DOM using the selected language.
 * @param {string} language - Target language code (e.g., "en", "fr", "uk")
 */
function renderReportData(language) {
    if (!currentReport) return;

    // Overall Header Range (e.g. OCT 1 – OCT 14, 2026)
    const overallStartDate = formatShortDate(currentReport.first_week_start_date, language).toUpperCase();
    const overallEndDate = formatShortDate(currentReport.second_week_end_date, language).toUpperCase();
    const currentYear = currentReport.second_week_end_date.split("-")[0];

    const reportDateRangeElement = document.getElementById("reportDateRange");
    if (reportDateRangeElement) {
        reportDateRangeElement.textContent = `${overallStartDate} – ${overallEndDate}, ${currentYear}`;
    }

    // Week Date Subtitles
    const weekOneDateRangeElement = document.getElementById("week1DateRange");
    if (weekOneDateRangeElement) {
        const weekOneStartDate = formatShortDate(currentReport.first_week_start_date, language);
        const weekOneEndDate = formatShortDate(currentReport.first_week_end_date, language);
        weekOneDateRangeElement.textContent = `${weekOneStartDate} – ${weekOneEndDate}`;
    }

    const weekTwoDateRangeElement = document.getElementById("week2DateRange");
    if (weekTwoDateRangeElement) {
        const weekTwoStartDate = formatShortDate(currentReport.second_week_start_date, language);
        const weekTwoEndDate = formatShortDate(currentReport.second_week_end_date, language);
        weekTwoDateRangeElement.textContent = `${weekTwoStartDate} – ${weekTwoEndDate}`;
    }

    // Hours and Amount
    const weekOneHoursElement = document.getElementById("week1HoursVal");
    if (weekOneHoursElement) {
        weekOneHoursElement.textContent = currentReport.first_week_hours.toFixed(1);
    }

    const weekTwoHoursElement = document.getElementById("week2HoursVal");
    if (weekTwoHoursElement) {
        weekTwoHoursElement.textContent = currentReport.second_week_hours.toFixed(1);
    }

    const totalEarnedElement = document.getElementById("totalEarnedVal");
    if (totalEarnedElement) {
        totalEarnedElement.textContent = currentReport.total_amount;
    }

    // Site Addresses
    const siteAddressesListElement = document.getElementById("siteAddressesList");
    if (siteAddressesListElement && currentReport.site_address_array) {
        siteAddressesListElement.innerHTML = "";
        currentReport.site_address_array.forEach(address => {
            const listItemElement = document.createElement("li");
            listItemElement.className = "site-item";
            listItemElement.innerHTML = `<span class="site-pin">📍</span><span>${address}</span>`;
            siteAddressesListElement.appendChild(listItemElement);
        });
    }
}

document.addEventListener("DOMContentLoaded", () => {
    initThemeToggle();

    initLanguageSwitcher(translations, (selectedLanguage) => {
        renderReportData(selectedLanguage);
    });

    const activeLanguage = getStartLanguage(translations);
    applyPageTranslations(activeLanguage, translations);
    renderReportData(activeLanguage);

    document.getElementById("downloadReportBtn")?.addEventListener("click", () => {
        console.log(`Downloading XLSX report for ID ${currentReport.id}...`);
    });
});
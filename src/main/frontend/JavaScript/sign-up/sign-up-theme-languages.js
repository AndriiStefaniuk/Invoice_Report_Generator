import {errorMessages} from "./error-messages.js";

const languageButtons = document.querySelectorAll(".lang-btn")
const themeToggleButton = document.getElementById("themeToggle")

const textElements = document.querySelectorAll("[data-translate]")
const placeholderElements = document.querySelectorAll("[data-translate-placeholder]")
const ariaLabelElements = document.querySelectorAll("[data-translate-aria-label]")
const titleElements = document.querySelectorAll("[data-translate-title]")

const translations = {
    en: {
        pageTitle: "Sign Up - Chuhax",
        heading: "Create New Account",
        personalInfo: "Personal Info",
        firstName: "First Name",
        lastName: "Last Name",
        email: "Email",
        password: "Password",
        ruleLength: "At least 8 characters",
        ruleDigit: "At least 1 number",
        ruleSpecialChar: "At least 1 special character (!@#$%^&*()_-.)",
        confirmPassword: "Confirm Password",
        homeAddress: "Home Address",
        streetAddress: "Street Address",
        addressLine2: "Apt, Suite, Unit (Optional)",
        city: "City",
        province: "Province",
        postalCode: "Postal Code",
        businessDetails: "Business & Billing Details",
        hstNumber: "HST Number",
        hourlyRate: "Default Hourly Rate ($)",
        registerButton: "Register & Save",
        themeButtonLabel: "Toggle dark mode",
        languageGroup: "Language",
        provinceAB: "Alberta",
        provinceBC: "British Columbia",
        provinceMB: "Manitoba",
        provinceNB: "New Brunswick",
        provinceNL: "Newfoundland and Labrador",
        provinceNS: "Nova Scotia",
        provinceNT: "Northwest Territories",
        provinceNU: "Nunavut",
        provinceON: "Ontario",
        provincePE: "Prince Edward Island",
        provinceQC: "Quebec",
        provinceSK: "Saskatchewan",
        provinceYT: "Yukon"
    },
    fr: {
        pageTitle: "Inscription - Chuhax",
        heading: "Créer un nouveau compte",
        personalInfo: "Informations personnelles",
        firstName: "Prénom",
        lastName: "Nom de famille",
        email: "Courriel",
        password: "Mot de passe",
        ruleLength: "Au moins 8 caractères",
        ruleDigit: "Au moins 1 chiffre",
        ruleSpecialChar: "Au moins 1 caractère spécial (!@#$%^&*()_-.)",
        confirmPassword: "Confirmer le mot de passe",
        homeAddress: "Adresse domiciliaire",
        streetAddress: "Adresse (numéro et rue)",
        addressLine2: "Appartement, bureau, unité (facultatif)",
        city: "Ville",
        province: "Province",
        postalCode: "Code postal",
        businessDetails: "Informations commerciales et de facturation",
        hstNumber: "Numéro de TVH",
        hourlyRate: "Taux horaire par défaut ($)",
        registerButton: "S’inscrire et enregistrer",
        themeButtonLabel: "Basculer le mode sombre",
        languageGroup: "Langue",
        provinceAB: "Alberta",
        provinceBC: "Colombie-Britannique",
        provinceMB: "Manitoba",
        provinceNB: "Nouveau-Brunswick",
        provinceNL: "Terre-Neuve-et-Labrador",
        provinceNS: "Nouvelle-Écosse",
        provinceNT: "Territoires du Nord-Ouest",
        provinceNU: "Nunavut",
        provinceON: "Ontario",
        provincePE: "Île-du-Prince-Édouard",
        provinceQC: "Québec",
        provinceSK: "Saskatchewan",
        provinceYT: "Yukon"
    },
    uk: {
        pageTitle: "Реєстрація - Chuhax",
        heading: "Створити новий обліковий запис",
        personalInfo: "Особисті дані",
        firstName: "Ім’я",
        lastName: "Прізвище",
        email: "Електронна пошта",
        password: "Пароль",
        ruleLength: "Щонайменше 8 символів",
        ruleDigit: "Щонайменше 1 цифра",
        ruleSpecialChar: "Щонайменше 1 спеціальний символ (!@#$%^&*()_-.)",
        confirmPassword: "Підтвердьте пароль",
        homeAddress: "Домашня адреса",
        streetAddress: "Вулиця та номер будинку",
        addressLine2: "Квартира, офіс, блок (необов’язково)",
        city: "Місто",
        province: "Провінція",
        postalCode: "Поштовий індекс",
        businessDetails: "Бізнес-дані та платіжна інформація",
        hstNumber: "Номер HST",
        hourlyRate: "Погодинна ставка за замовчуванням ($)",
        registerButton: "Зареєструватися та зберегти",
        themeButtonLabel: "Перемкнути темний режим",
        languageGroup: "Мова",
        provinceAB: "Альберта",
        provinceBC: "Британська Колумбія",
        provinceMB: "Манітоба",
        provinceNB: "Нью-Брансвік",
        provinceNL: "Ньюфаундленд і Лабрадор",
        provinceNS: "Нова Шотландія",
        provinceNT: "Північно-західні території",
        provinceNU: "Нунавут",
        provinceON: "Онтаріо",
        provincePE: "Острів Принца Едварда",
        provinceQC: "Квебек",
        provinceSK: "Саскачеван",
        provinceYT: "Юкон"
    }
}


// Trigger as soon as the DOM is parsed/page loaded
document.addEventListener('DOMContentLoaded', () => {
    addLanguageListeners();
    addThemeListener();
    applyLanguage(getStartLanguage());
});


/**
 * Reads a value from localStorage
 * @param {string} key name of the saved setting
 * @returns {string|null} saved value, or null if nothing is saved or storage is blocked
 */
function getSavedSetting(key) {
    try {
        return localStorage.getItem(key);
    } catch (error) {
        return null;
    }
}


/**
 * Writes a value to localStorage
 * If storage is blocked (e.g. some private modes), the setting is not remembered and nothing else happens
 * @param {string} key name of the setting
 * @param {string} value value to save
 */
function saveSetting(key, value) {
    try {
        localStorage.setItem(key, value);
    } catch (error) {}
}


/**
 * Chooses the language the page opens in:
 *  - saved language, if there is one
 *  - otherwise the browser language, if it is supported
 *  - otherwise English
 * @returns {string} language code: "en", "fr" or "uk"
 */
function getStartLanguage() {
    const savedLanguage = getSavedSetting("language");
    if (savedLanguage && translations[savedLanguage]) return savedLanguage;

    const browserLanguage = navigator.language.slice(0, 2).toLowerCase();
    if (translations[browserLanguage]) return browserLanguage;

    return "en";
}


/**
 * Binds a click listener to every language (flag) button
 * Side effects:
 *  - clicking a flag switches the page language and saves the choice
 */
function addLanguageListeners() {
    for (let i = 0; i < languageButtons.length; i++) {
        languageButtons[i].addEventListener("click", () => {
            applyLanguage(languageButtons[i].getAttribute("data-lang"));
        })
    }
}


/**
 * Binds a click listener to the theme toggle button
 */
function addThemeListener() {
    themeToggleButton.addEventListener("click", toggleTheme)
}


/**
 * Switches between light and dark theme and saves the choice
 * Side effects:
 *  - sets data-theme attribute on <html>, the CSS reacts to it
 */
function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute("data-theme");
    const nextTheme = currentTheme === "dark" ? "light" : "dark";

    document.documentElement.setAttribute("data-theme", nextTheme);
    saveSetting("theme", nextTheme);
}


/**
 * Translates the whole page into the given language
 * Side effects:
 *  - replaces text of elements with data-translate
 *  - replaces placeholders of elements with data-translate-placeholder
 *  - replaces aria-label of elements with data-translate-aria-label
 *  - replaces title of elements with data-translate-title
 *  - updates <html lang>, the page title and the pressed state of the flag buttons
 *  - saves the language
 * Precondition: every key used in the HTML exists in every language of translations,
 * a missing key shows "undefined" on the page
 * @param {string} language language code: "en", "fr" or "uk"
 */
function applyLanguage(language) {
    if (!translations[language]) language = "en";
    const dictionary = translations[language];

    document.documentElement.lang = language;
    document.title = dictionary["pageTitle"];

    for (let i = 0; i < textElements.length; i++) {
        textElements[i].textContent = dictionary[textElements[i].getAttribute("data-translate")];
    }

    for (let i = 0; i < placeholderElements.length; i++) {
        placeholderElements[i].placeholder = dictionary[placeholderElements[i].getAttribute("data-translate-placeholder")];
    }

    for (let i = 0; i < ariaLabelElements.length; i++) {
        ariaLabelElements[i].setAttribute("aria-label", dictionary[ariaLabelElements[i].getAttribute("data-translate-aria-label")]);
    }

    for (let i = 0; i < titleElements.length; i++) {
        titleElements[i].title = dictionary[titleElements[i].getAttribute("data-translate-title")];
    }

    for (let i = 0; i < languageButtons.length; i++) {
        const isSelected = languageButtons[i].getAttribute("data-lang") === language;
        languageButtons[i].setAttribute("aria-pressed", isSelected ? "true" : "false");

    }

    for (const errorLabel of document.querySelectorAll(".field-error-message")) {
        const errorKey = errorLabel.getAttribute('data-error-key');
        const fieldLabel = errorLabel.getAttribute('data-field-label');

        if (errorKey && errorMessages[language] && errorMessages[language][errorKey]) {
            let message = errorMessages[language][errorKey];
            if (fieldLabel) {
                message = message.replace("{fieldLabel}", errorMessages[language][fieldLabel]);
            }
            errorLabel.textContent = message;
        }
    }
    saveSetting("language", language);
}
import { errorMessages } from "./error-messages.js";
import {
    initThemeToggle,
    initLanguageSwitcher,
    applyPageTranslations,
    getStartLanguage
} from "../theme-language/theme-language-core.js";

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
};



/**
 * Checks the DOM for currently displayed validation error labels and updates their text
 * content to match the selected language
 * Looks up error templates using data-error-key and substitutes any {fieldLabel} placeholder
 * by resolving the field name key against errorMessages or translations dictionaries.
 * @param {string} language - Target two-letter language code (e.g., "en", "fr", "uk")
 */
function updateActiveErrors(language) {
    const activeErrorLabels = document.querySelectorAll(".field-error-message");
    for (let i = 0; i < activeErrorLabels.length; i++) {
        const errorKey = activeErrorLabels[i].getAttribute('data-error-key');
        const fieldLabelKey = activeErrorLabels[i].getAttribute('data-field-label');

        if (errorKey && errorMessages[language] && errorMessages[language][errorKey]) {
            let message = errorMessages[language][errorKey];

            if (fieldLabelKey) {

                let resolvedLabel = fieldLabelKey;

                if (errorMessages[language] && errorMessages[language][fieldLabelKey]) {
                    resolvedLabel = errorMessages[language][fieldLabelKey];
                }

                message = message.replace("{fieldLabel}", resolvedLabel);
            }

            activeErrorLabels[i].textContent = message;
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    initThemeToggle();
    initLanguageSwitcher(translations, updateActiveErrors);

    const initialLang = getStartLanguage(translations);
    applyPageTranslations(initialLang, translations);
    updateActiveErrors(initialLang);
});
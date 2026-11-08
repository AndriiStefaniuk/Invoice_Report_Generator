import {
    initThemeToggle,
    initLanguageSwitcher,
    applyPageTranslations,
    getStartLanguage
} from "../theme-language/theme-language-core.js";

const translations = {
    en: {
        pageTitle: "Dashboard - Chuhax",
        heading: "Dashboard",
        greeting: "Hello, User!",
        btnHistory: "View Report History",
        descHistory: "Browse and review past work hours and generated logs.",
        btnSettings: "Manage App Settings",
        descSettings: "Configure account parameters, default rates, and HST.",
        btnGenerate: "Generate New Report",
        descGenerate: "Record hours, assign addresses, and export report files.",
        themeButtonLabel: "Toggle dark mode",
        languageGroup: "Language"
    },
    fr: {
        pageTitle: "Tableau de bord - Chuhax",
        heading: "Tableau de bord",
        greeting: "Bonjour, Utilisateur !",
        btnHistory: "Historique des rapports",
        descHistory: "Consultez vos heures de travail passées et vos journaux.",
        btnSettings: "Paramètres de l'application",
        descSettings: "Configurez les paramètres du compte, taux horaire et TVH.",
        btnGenerate: "Générer un rapport",
        descGenerate: "Enregistrez les heures, assignez des adresses et exportez.",
        themeButtonLabel: "Basculer le mode sombre",
        languageGroup: "Langue"
    },
    uk: {
        pageTitle: "Панель керування - Chuhax",
        heading: "Панель керування",
        greeting: "Вітаємо, Користувачу!",
        btnHistory: "Історія звітів",
        descHistory: "Переглядайте робочі години та збережені звіти.",
        btnSettings: "Налаштування додатку",
        descSettings: "Керуйте обліковими даними, погодинною ставкою та HST.",
        btnGenerate: "Створити новий звіт",
        descGenerate: "Заповнюйте години, вказуйте адреси та експортуйте файли.",
        themeButtonLabel: "Перемкнути темний режим",
        languageGroup: "Мова"
    }
};

document.addEventListener('DOMContentLoaded', () => {
    initThemeToggle();
    initLanguageSwitcher(translations);
    applyPageTranslations(getStartLanguage(translations), translations);
});
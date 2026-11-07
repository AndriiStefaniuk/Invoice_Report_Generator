const languageButtons = document.querySelectorAll(".lang-btn");
const themeToggleButton = document.getElementById("themeToggle");

const textElements = document.querySelectorAll("[data-translate]");
const ariaLabelElements = document.querySelectorAll("[data-translate-aria-label]");
const titleElements = document.querySelectorAll("[data-translate-title]");

const translations = {
    en: {
        pageTitle: "Enter Verification Code - Chuhax",
        heading: "Enter Confirmation Code",
        subtitleText: "We have sent a 6-digit verification code to",
        verifyButton: "Verify Code",
        resendPrompt: "Didn't receive the code?",
        resendLink: "Resend",
        themeButtonLabel: "Toggle dark mode",
        languageGroup: "Language"
    },
    fr: {
        pageTitle: "Entrer le code de vérification - Chuhax",
        heading: "Entrer le code de confirmation",
        subtitleText: "Nous avons envoyé un code de vérification à 6 chiffres à",
        verifyButton: "Vérifier le code",
        resendPrompt: "Vous n'avez pas reçu le code ?",
        resendLink: "Renvoyer",
        themeButtonLabel: "Basculer le mode sombre",
        languageGroup: "Langue"
    },
    uk: {
        pageTitle: "Введіть код підтвердження - Chuhax",
        heading: "Введіть код підтвердження",
        subtitleText: "Ми надіслали 6-значний код підтвердження на",
        verifyButton: "Підтвердити код",
        resendPrompt: "Не отримали код?",
        resendLink: "Надіслати знову",
        themeButtonLabel: "Перемкнути темний режим",
        languageGroup: "Мова"
    }
};

document.addEventListener('DOMContentLoaded', () => {
    addLanguageListeners();
    addThemeListener();
    applyLanguage(getStartLanguage());
});

function getSavedSetting(key) {
    try {
        return localStorage.getItem(key);
    } catch (error) {
        return null;
    }
}

function saveSetting(key, value) {
    try {
        localStorage.setItem(key, value);
    } catch (error) {}
}

function getStartLanguage() {
    const savedLanguage = getSavedSetting("language");
    if (savedLanguage && translations[savedLanguage]) return savedLanguage;

    const browserLanguage = navigator.language.slice(0, 2).toLowerCase();
    if (translations[browserLanguage]) return browserLanguage;

    return "en";
}

function addLanguageListeners() {
    for (let i = 0; i < languageButtons.length; i++) {
        languageButtons[i].addEventListener("click", () => {
            applyLanguage(languageButtons[i].getAttribute("data-lang"));
        });
    }
}

function addThemeListener() {
    themeToggleButton.addEventListener("click", toggleTheme);
}

function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute("data-theme");
    const nextTheme = currentTheme === "dark" ? "light" : "dark";

    document.documentElement.setAttribute("data-theme", nextTheme);
    saveSetting("theme", nextTheme);
}

function applyLanguage(language) {
    if (!translations[language]) language = "en";
    const dictionary = translations[language];

    document.documentElement.lang = language;
    document.title = dictionary["pageTitle"];

    for (let i = 0; i < textElements.length; i++) {
        textElements[i].textContent = dictionary[textElements[i].getAttribute("data-translate")];
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

    saveSetting("language", language);
}
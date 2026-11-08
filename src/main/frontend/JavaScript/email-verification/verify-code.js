import {
    initThemeToggle,
    initLanguageSwitcher,
    applyPageTranslations,
    getStartLanguage
} from "../theme-language/theme-language-core.js";

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
    initThemeToggle();
    initLanguageSwitcher(translations);
    applyPageTranslations(getStartLanguage(translations), translations);
});
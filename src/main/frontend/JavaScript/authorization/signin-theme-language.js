import {
    initThemeToggle,
    initLanguageSwitcher,
    applyPageTranslations,
    getStartLanguage
} from "../theme-language/theme-language-core.js";

const translations = {
    en: {
        pageTitle: "Sign In - Chuhax",
        heading: "Report Manager Log In",
        usernamePlaceholder: "Email or Username",
        passwordPlaceholder: "Password",
        btnLogin: "Log In",
        forgotPassword: "Forgot Password?",
        noAccountText: "Don't have an account?",
        btnCreateAccount: "Create New Account",
        themeButtonLabel: "Toggle dark mode",
        languageGroup: "Language",
        togglePasswordAria: "Show password"
    },
    fr: {
        pageTitle: "Connexion - Chuhax",
        heading: "Connexion au gestionnaire de rapports",
        usernamePlaceholder: "Courriel ou nom d'utilisateur",
        passwordPlaceholder: "Mot de passe",
        btnLogin: "Se connecter",
        forgotPassword: "Mot de passe oublié ?",
        noAccountText: "Vous n'avez pas de compte ?",
        btnCreateAccount: "Créer un nouveau compte",
        themeButtonLabel: "Basculer le mode sombre",
        languageGroup: "Langue",
        togglePasswordAria: "Afficher le mot de passe"
    },
    uk: {
        pageTitle: "Вхід - Chuhax",
        heading: "Вхід до менеджера звітів",
        usernamePlaceholder: "Електронна пошта або логін",
        passwordPlaceholder: "Пароль",
        btnLogin: "Увійти",
        forgotPassword: "Забули пароль?",
        noAccountText: "Не маєте облікового запису?",
        btnCreateAccount: "Створити новий акаунт",
        themeButtonLabel: "Перемкнути темний режим",
        languageGroup: "Мова",
        togglePasswordAria: "Показати пароль"
    }
};

/**
 * Initializes the show/hide password toggle eye button logic
 */
function initPasswordToggle() {
    const toggleButton = document.getElementById("togglePasswordBtn");
    const passwordInput = document.getElementById("passwordInput");

    if (!toggleButton || !passwordInput) return;

    toggleButton.addEventListener("click", () => {
        const isPasswordHidden = passwordInput.type === "password";
        passwordInput.type = isPasswordHidden ? "text" : "password";
        toggleButton.classList.toggle("password-visible", isPasswordHidden);
    });
}

document.addEventListener("DOMContentLoaded", () => {
    initThemeToggle();
    initLanguageSwitcher(translations);
    applyPageTranslations(getStartLanguage(translations), translations);
    initPasswordToggle();

    const loginForm = document.getElementById("loginForm");
    loginForm?.addEventListener("submit", (event) => {
        event.preventDefault();
        window.location.href = "main.html";
    });
});
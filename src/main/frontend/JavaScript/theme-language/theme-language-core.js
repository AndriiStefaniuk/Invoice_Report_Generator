/**
 * Reads a stored string value from browser localStorage
 * Returns null if the key does not exist or if storage access is blocked by the browser
 * @param {string} settingKey - Name of the persistent setting key to retrieve (e.g., "language", "theme")
 * @returns {string|null} Saved string value associated with the settingKey, or null if missing or unavailable
 */
export function getSavedSetting(settingKey) {
    try {
        return localStorage.getItem(settingKey);
    } catch (error) {
        return null;
    }
}

/**
 * Writes a string value to browser localStorage
 * Fails silently without crashing if localStorage is unavailable, disabled, or blocked
 * @param {string} settingKey - Name of the persistent setting key to store
 * @param {string} settingValue - Value to save under the specified settingKe.
 */
export function saveSetting(settingKey, settingValue) {
    try {
        localStorage.setItem(settingKey, settingValue);
    } catch (error) {}
}

/**
 * Resolves the initial active language in order based on priority:
 * 1. Saved preference from localStorage
 * 2. Browser navigator language
 * 3. Default fallback to English ("en")
 *
 * @param {Object} translations - Dictionary containing localized UI string mappings grouped by language codes (e.g., "en", "fr", "uk").
 * @returns {string} Resolved two-letter language code ("en", "fr", or "uk").
 */
export function getStartLanguage(translations) {
    const savedLanguage = getSavedSetting("language");
    if (savedLanguage && translations[savedLanguage]) return savedLanguage;

    const browserLanguage = navigator.language.slice(0, 2).toLowerCase();
    if (translations[browserLanguage]) return browserLanguage;

    return "en";
}

/**
 * Initializes click listener on the theme toggle button (#themeToggle)
 * Toggles the document root attribute between data-theme="dark" and data-theme="light"
 * Stores the selected choice into localStorage under "theme"
 */
export function initThemeToggle() {
    const themeToggleButton = document.getElementById("themeToggle");
    if (!themeToggleButton) return;

    themeToggleButton.addEventListener("click", () => {
        const currentTheme = document.documentElement.getAttribute("data-theme");
        const nextTheme = currentTheme === "dark" ? "light" : "dark";
        document.documentElement.setAttribute("data-theme", nextTheme);
        saveSetting("theme", nextTheme);
    });
}

/**
 * Translates static and partially-static DOM elements across the document
 * Updates textContent, placeholder, aria-label, and title attributes based on matching data-translate tags
 * Sets the documentElement lang attribute and updates aria-pressed states on all language buttons
 * Saves the active language selection into localStorage
 * @param {string} language - Two letter long code representing the active target language (e.g., "en", "fr", "uk")
 * @param {Object} translations - Dictionary containing localized string tables grouped by language codes
 */
export function applyPageTranslations(language, translations) {
    if (!translations || typeof translations !== "object") {
        console.error("applyPageTranslations error: 'translations' object is missing or undefined.", translations);
        return;
    }

    const dictionary = translations[language] || translations["en"] || Object.values(translations)[0];

    if (!dictionary) {
        console.error(`applyPageTranslations error: No dictionary found for language '${language}'. Available keys:`, Object.keys(translations));
        return;
    }

    document.documentElement.lang = language || "en";

    if (dictionary["pageTitle"]) {
        document.title = dictionary["pageTitle"];
    }

    document.querySelectorAll("[data-translate]").forEach(el => {
        const key = el.getAttribute("data-translate");
        if (dictionary[key] !== undefined) el.textContent = dictionary[key];
    });

    document.querySelectorAll("[data-translate-placeholder]").forEach(el => {
        const key = el.getAttribute("data-translate-placeholder");
        if (dictionary[key] !== undefined) el.placeholder = dictionary[key];
    });

    document.querySelectorAll("[data-translate-aria-label]").forEach(el => {
        const key = el.getAttribute("data-translate-aria-label");
        if (dictionary[key] !== undefined) el.setAttribute("aria-label", dictionary[key]);
    });

    document.querySelectorAll("[data-translate-title]").forEach(el => {
        const key = el.getAttribute("data-translate-title");
        if (dictionary[key] !== undefined) el.title = dictionary[key];
    });

    document.querySelectorAll(".lang-btn").forEach(btn => {
        const isSelected = btn.getAttribute("data-lang") === language;
        btn.setAttribute("aria-pressed", isSelected ? "true" : "false");
    });

    saveSetting("language", language);
}

/**
 * Attaches click event listeners to all language selector buttons (.lang-btn)
 * On click, reads data-lang, applies page-wide translations, and triggers any custom callback hook
 * @param {Object} translations - Dictionary containing localized string tables grouped by language codes
 * @param {Function} [onLanguageChanged] - Optional callback function immediately after language switch,
 *                                         receiving the selected language string
 */
export function initLanguageSwitcher(translations, onLanguageChanged) {
    document.querySelectorAll(".lang-btn").forEach(button => {
        button.addEventListener("click", () => {
            const selectedLanguage = button.getAttribute("data-lang");
            applyPageTranslations(selectedLanguage, translations);
            if (typeof onLanguageChanged === "function") {
                onLanguageChanged(selectedLanguage);
            }
        });
    });
}
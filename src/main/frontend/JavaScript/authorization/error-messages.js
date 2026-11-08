/**
 * Error messages shown under the sign-up form fields, one object per language.
 * The key describes the error, the value is the text shown to the user.
 *
 * Messages containing {fieldLabel} are for the first and last name fields.
 * Replace {fieldLabel} with firstNameLabel or lastNameLabel of the same language
 * before showing the message.
 * Example: errorMessages["fr"]["nameTooShort"].replace("{fieldLabel}", errorMessages["fr"]["firstNameLabel"])
 *
 * Hourly rate messages say "period" on purpose: isHourlyRateValid accepts only "22.50",
 * not "22,50", and French and Ukrainian users usually type a comma.
 */
export const errorMessages = {
    en: {
        // any field left empty
        required: "* Required",

        // password
        passwordsDoNotMatch: "Passwords do not match",

        // first and last name
        firstNameLabel: "First Name",
        lastNameLabel: "Last Name",
        nameTooShort: "{fieldLabel} must be at least 3 characters long",
        nameTooLong: "{fieldLabel} must be 50 characters or fewer",
        nameInvalidChars: "{fieldLabel} can only contain letters, hyphens, and apostrophes",

        // other fields
        invalidEmail: "Invalid Email",
        invalidStreetAddress: "Invalid street address",
        invalidAddressLine2: "Invalid unit or suite",
        invalidCity: "Invalid city name",
        invalidPostalCode: "Invalid postal code",
        invalidHstNumber: "Invalid HST number",
        invalidHourlyRate: "Enter valid hourly rate (e.g. 22.50)"
    },
    fr: {
        required: "* Obligatoire",

        passwordsDoNotMatch: "Les mots de passe ne correspondent pas",

        firstNameLabel: "Prénom",
        lastNameLabel: "Nom de famille",
        nameTooShort: "Le champ « {fieldLabel} » doit contenir au moins 3 caractères",
        nameTooLong: "Le champ « {fieldLabel} » doit contenir 50 caractères ou moins",
        nameInvalidChars: "Le champ « {fieldLabel} » ne peut contenir que des lettres, des traits d’union et des apostrophes",

        invalidEmail: "Courriel invalide",
        invalidStreetAddress: "Adresse de rue invalide",
        invalidAddressLine2: "Appartement, bureau ou unité invalide",
        invalidCity: "Nom de ville invalide",
        invalidPostalCode: "Code postal invalide",
        invalidHstNumber: "Numéro de TVH invalide",
        invalidHourlyRate: "Entrez un taux horaire valide (ex. 22.50, avec un point)"
    },
    uk: {
        required: "* Обов’язково",

        passwordsDoNotMatch: "Паролі не збігаються",

        firstNameLabel: "Ім’я",
        lastNameLabel: "Прізвище",
        nameTooShort: "Поле «{fieldLabel}» має містити щонайменше 3 символи",
        nameTooLong: "Поле «{fieldLabel}» має містити не більше 50 символів",
        nameInvalidChars: "Поле «{fieldLabel}» може містити лише літери, дефіси та апострофи",

        invalidEmail: "Некоректна електронна пошта",
        invalidStreetAddress: "Некоректна адреса вулиці",
        invalidAddressLine2: "Некоректний номер квартири, офісу або блоку",
        invalidCity: "Некоректна назва міста",
        invalidPostalCode: "Некоректний поштовий індекс",
        invalidHstNumber: "Некоректний номер HST",
        invalidHourlyRate: "Введіть коректну погодинну ставку (напр. 22.50, через крапку)"
    }
}
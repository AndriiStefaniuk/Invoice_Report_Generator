import { errorMessages } from './error-messages.js';

const firstNameField = document.getElementById("firstName")
const lastNameField = document.getElementById("lastName")
const emailField = document.getElementById("email")
const passwordField = document.getElementById("password")
const confirmPasswordField = document.getElementById("confirmPassword")
const hstNumberField = document.getElementById("hstNumber")
const hourlyRateField = document.getElementById("hourlyRate")
const streetAddressField = document.getElementById("streetAddress");
const addressLine2Field = document.getElementById("addressLine2");
const cityField = document.getElementById("city");
const provinceField = document.getElementById("province");
const postalCodeField = document.getElementById("postalCode");

const ruleLength = document.getElementById("ruleLength");
const ruleDigit = document.getElementById("ruleDigit");
const ruleSpecialChar = document.getElementById("ruleSpecialChar");

let attemptedRegister = false
const inputFields = [
    firstNameField,
    lastNameField,
    emailField,
    passwordField,
    confirmPasswordField,
    streetAddressField,
    addressLine2Field,
    cityField,
    provinceField,
    postalCodeField,
    hstNumberField,
    hourlyRateField,]


// Trigger as soon as the DOM is parsed/page loaded
document.addEventListener('DOMContentLoaded', () => {
    addEventListeners();
    addPasswordListener();
    addHSTListener();
    addHourlyRateListener();
    addSubmitButtonListener();
});


/**
 * Binds save and register (submit) button on click event to execute registerUser()
 */
function addSubmitButtonListener(){
    document.getElementById("submitButton").addEventListener("click", () => {
        registerUser()
    })
}


/**
 * Binds input listener to the confirmPasswordField after user attempted to register
 * Listener actively checks if the confirmation password matches the password
 * Side effects:
 *  - if confirmation password does not match, confirmPasswordField is marked red
 *  - if confirmation password match, confirmPasswordField appears normal
 */
function addPasswordListener() {
    confirmPasswordField.addEventListener("input", () => {
        if (attemptedRegister) {
            if (doPasswordsMatch()) {
                removeMarkFieldError();
            } else {
                markFieldError(confirmPasswordField, "passwordsDoNotMatch")
            }
        }
    })
}


/**
 * Prevents invalid input on the hourly rate field by checking the `beforeinput` event
 * Blocks any input that would result in more than two decimal places
 */
function addHourlyRateListener(){
    hourlyRateField.addEventListener("beforeinput", (event) =>{
        const nextValue =
            hourlyRateField.value.slice(0, hourlyRateField.selectionStart) +
            event.data +
            hourlyRateField.value.slice(hourlyRateField.selectionEnd)
        const hasExcessiveDecimalsRegex = /^\d+\.\d{3,}$/
        const hasNonNumericCharsRegex = /^\D$/
        if(hasExcessiveDecimalsRegex.test(nextValue) || hasNonNumericCharsRegex.test(nextValue)){
            event.preventDefault()
        }
    })
}



/**
 * Binds input listener to the hstNumberField after user attempted to register
 * Listener actively checks if provided HST number is valid
 * Side effects:
 *  - if HST number is not valid, hstNumberField is marked red, with warning label below
 *  - if HST number is valid, hstNumberField appears normal
 */
function addHSTListener(){
    hstNumberField.addEventListener("input", () => {
        if (attemptedRegister) {
            if (isHSTValid(hstNumberField.value)) {
                removeMarkFieldError();
            } else {
                markFieldError(hstNumberField, "invalidHstNumber")
            }
        }
    })
}



/**
 * Binds input and keyboard navigation listeners to all form fields.
 * Behaviors added:
 * - "input": Clears the empty/required error indicator as soon as the user types
 * - "keydown": Intercepts Enter/Return on physical keyboards (laptop/desktop) to
 *    advance focus to the next field, excluding multi-line address input
 * - "beforeinput": Intercepts the "Next" action key on mobile virtual keyboards
 *    (insertLineBreak) to advance focus to the next field without dismissing the keyboard
 */
function addEventListeners() {
    for (let i = 0; i < inputFields.length; i++) {
        // remove marker that field is empty
        inputFields[i].addEventListener("input", () => {
            removeMarkFieldError(inputFields[i]);
        })
        // tracks laptop "Enter" key pressed
        inputFields[i].addEventListener("keydown", (e) => {
            if ((e.key === "Enter" || e.key === "Return") && inputFields[i] !== hourlyRateField) {
                e.preventDefault();

                if (i + 1 < inputFields.length) {
                    inputFields[i + 1].focus();
                }
            }
        })
        // tracks Android "Enter" key pressed
        inputFields[i].addEventListener('beforeinput', (e) => {
            if (e.inputType === 'insertLineBreak' && inputFields[i] !== hourlyRateField) {
                e.preventDefault();


                if (i + 1 < inputFields.length) {
                    inputFields[i + 1].focus();
                }
            }
        });
    }

    passwordField.addEventListener("input", () => {
        const passwordCriteria = validatePasswordCriteria(passwordField.value);
        updatePasswordCriteriaCheckList(passwordCriteria)
    })
}


/**
 * Updates the password requirement check list based on the criteria met
 * @param passwordCriteria
 */
function updatePasswordCriteriaCheckList(passwordCriteria) {
    if (passwordCriteria['8_char_long']) {
        ruleLength.classList.remove("missed")
        ruleLength.classList.add("valid")
    } else ruleLength.classList.remove("valid")

    if (passwordCriteria['contains_digit']) {
        ruleDigit.classList.remove("missed")
        ruleDigit.classList.add("valid")
    }
    else ruleDigit.classList.remove("valid")

    if(passwordCriteria['contains_special_char']) {
        ruleSpecialChar.classList.remove("missed")
        ruleSpecialChar.classList.add("valid")
    }
    else ruleSpecialChar.classList.remove("valid")

}


/**
 * Indicates the field as invalid:
 * - turns the field borders red
 * - puts the localized error message below the field
 *
 * @param {HTMLElement} inputElement The input or textarea element to highlight
 * @param {string} errorKey The error key from the dictionary (e.g., "nameTooShort")
 * @param fieldLabel - The localized label of the field to replace {fieldLabel}.
 *                     Must contain one of these specific values:
 *   - 'firstNameLabel'
 *   - 'lastNameLabel'
 */
function markFieldError(inputElement, errorKey, fieldLabel ="") {
    if (!inputElement) return;

    const languageButton = document.querySelector(".lang-btn[aria-pressed=\"true\"]")
    let language = languageButton ? languageButton.getAttribute("data-lang") : "en"

    inputElement.style.borderColor = '#d93025';
    inputElement.style.borderWidth = '2px';

    const parentContainer = inputElement.parentElement;
    if (!parentContainer) return;

    let errorLabel = parentContainer.querySelector(".field-error-message");

    if (!errorLabel) {
        // Create the element if it doesn't exist
        errorLabel = document.createElement('span');
        errorLabel.className = 'field-error-message';

        errorLabel.style.color = '#d93025';
        errorLabel.style.fontSize = '12px';
        errorLabel.style.display = 'block';
        errorLabel.style.marginTop = '4px';

        parentContainer.appendChild(errorLabel);
    }

    errorLabel.setAttribute('data-error-key', errorKey)
    errorLabel.setAttribute('data-field-label', fieldLabel)
    let message = errorMessages[language][errorKey]
    if (!message) message = " "
    if (fieldLabel){
        message = message.replace("{fieldLabel}", errorMessages[language][fieldLabel]);
    }
    errorLabel.textContent = message;
}


/**
 * removes the indicator, if present:
 * - turns the field borders normal
 * - removes informational (red) label below the field
 */
function removeMarkFieldError(htmlElement) {
    if(!htmlElement) return;

    const parentContainer = htmlElement.parentElement;
    let message = parentContainer.querySelector('.field-error-message')

    if(!message){
        return; // there is no mark to remove
    }

    htmlElement.removeAttribute('data-error-key')
    htmlElement.removeAttribute('data-field-label')
    htmlElement.style.borderColor = "#0B2A4A"
    htmlElement.style.borderWidth = "1px"
    parentContainer.removeChild(message)
}


/**
 * Evaluates a password against standard complexity criteria and returns
 * a breakdown of which requirements are met
 *
 * Requirements checked:
 * - At least 8 characters long
 * - Contains at least one digit (0-9)
 * - Contains at least one special character (!@#$%^&*()_-.)
 * @param {string} password - The password string to evaluate.
 * @returns {{ "8_char_long": boolean, "contains_digit": boolean, "contains_special_char": boolean }}
 *           An object detailing the pass/fail status for each criterion
 */
function validatePasswordCriteria(password) {
    let rightLength = password.length >= 8;
    let containsDigit = false;
    let containsSpecialChar = false;

    let specialCharacters = "!@#$%^&*()_-.";

    for (let i = 0; i < password.length; i++) {
        let char = password.charAt(i);
        if (char >= '0' && char <= '9') {
            containsDigit = true;
        } else if (specialCharacters.includes(char)) {
            containsSpecialChar = true;
        }
    }
    return {
        "8_char_long": rightLength,
        "contains_digit": containsDigit,
        "contains_special_char": containsSpecialChar
    };
}


/**
 * Returns true if values of passwordField and confirmPasswordField are identical, false otherwise
 */
function doPasswordsMatch(){
    const password = passwordField.value;
    const confirmedPassword = confirmPasswordField.value;
    return password === confirmedPassword;
}


/**
 * Returns true if all input fields on the page are non-empty, false otherwise
 * @returns {boolean} true if all fields are not empty, false otherwise
 */
function isFormFilled(){
    for(let i = 0; i < inputFields.length; i ++){
        if(inputFields[i] === addressLine2Field){
            continue
        }
        if(inputFields[i].value ===""){
            return false;
        }
    }
    return true
}



/**
 * Iterates over a list of input fields on the page.
 * Marks fields that are not filled in as follows:
 *  - puts "*Required" label below the field
 *  - turns the border of field red
 */
function indicateEmptyFields(){
    for(let i = 0; i < inputFields.length; i ++){
        if(inputFields[i].value ===""){
            markFieldError(inputFields[i], "required");
        }
    }
}


/**
 * Marks failed password requirement criteria
 */
function highlightFailedPasswordCriteria(passwordCriteriaStatus) {
    if (!passwordCriteriaStatus['8_char_long'])
        ruleLength.classList.add("missed")

    if (!passwordCriteriaStatus['contains_digit'])
        ruleDigit.classList.add("missed")

    if (!passwordCriteriaStatus['contains_special_char'])
        ruleSpecialChar.classList.add("missed")
}



/**
 * Marks the confirmation password does not match the password
 */
function markPasswordMismatch() {
    markFieldError(confirmPasswordField, "passwordsDoNotMatch")
}


/**
 * Checks whether name passes the length requirements
 * @param name string object to check
 * @returns {{ "minLength": boolean, "withinLength": boolean, "validChars": boolean }}
 */
function validateNameCriteria(name){
    if (!name || typeof name !== "string") {
        return { "minLength": false, "withinLength": false, "validChars": false };
    }
    let minLength= name.trim().length >= 3;
    let withinLength = name.trim().length < 50;

    const regexPatter =/^[\p{L}\p{M}'-]+( [\p{L}\p{M}'-]+)*$/u
    let validChars = regexPatter.test(name.trim());

    return {
        "minLength": minLength,
        "withinLength": withinLength,
        "validChars": validChars
    };
}

/**
 * Applied only for first and last names fields
 * Highlights a field with the appropriate error message based on failed criteria:
 * - minLength: min 3 char
 * - withinLength: max 50 chars
 * - validChars: allowed letters, hyphens, and apostrophes
 * @param inputElement The input or textarea element to highligh
 * @param nameCriteriaStatus The specific failure key. Must contain only one of these specific values:
 *   - "minLength" (min 3 chars),
 *   - "withinLength" (max 50 chars), or
 *   - "validChars" (allowed letters, hyphens, and apostrophes)
 * @param fieldLabel - The localized label of the field to replace {fieldLabel}.
 *                     Must contain one of these specific values:
 *   - 'firstNameLabel'
 *   - 'lastNameLabel'
 */
function highlightFailedNameCriteria(inputElement, nameCriteriaStatus, fieldLabel) {
    if (!inputElement || !nameCriteriaStatus || !fieldLabel) return;

    const languageButton = document.querySelector(".lang-btn[aria-pressed=\"true\"]")
    let language = languageButton ? languageButton.getAttribute("data-lang") : "en"

    if (!nameCriteriaStatus["minLength"]) {
        markFieldError(inputElement, "nameTooShort",fieldLabel);
        return;
    }

    if (!nameCriteriaStatus["withinLength"]) {
        markFieldError(inputElement, "nameTooLong", fieldLabel);
        return;
    }

    if (!nameCriteriaStatus["validChars"]) {
        markFieldError(inputElement, "nameInvalidChars", fieldLabel);
    }
}


/**
 * Checks whether the HSTNumber is of a valid format:
 *  - 9 digits representing the Business Number
 *  - 2 letters representing the program identifier
 *  - 4 digits representing the reference number
 * @param HSTNumber string HST number to check
 * @returns true if HST number is valid, false otherwise
 */
function isHSTValid(HSTNumber){
    if(!HSTNumber || typeof HSTNumber !== "string") return false

    const cleaned = HSTNumber.replaceAll(" ", "").replaceAll("-", "").toUpperCase();
    const regexExpression = /^\d{9}[A-Z]{2}\d{4}$/u;
    return regexExpression.test(cleaned)
}


/**
 * Checks whether email is valid based on the following requirements:
 *  - length is between 5 and 254 characters
 *  - contains exactly one "@" symbol
 *  - local part (before "@") does not start or end with a dot (".")
 *  - domain part (after "@") contains at least one dot (".")
 *  - domain part begins and ends with an alphanumeric character
 *  - top-level domain (extension after last dot) is at least 2 letters long
 *  - no spaces of any kind
 *  - no consecutive dots ("..") in either the local or domain part
 *
 * @param {string} email The email string to evaluate
 * @returns true if email satisfies all requirements, false otherwise
 */
function isEmailValid(email) {
    if (!email || typeof email !== "string") return false
    if(email.trim().length < 5 || email.trim().length > 256) return false

    const regexEmail =  /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z0-9-]{2,}$/
    return regexEmail.test(email)
}


/**
 * Checks whether streetAddress is valid based on the following requirements:
 *  - length is between 3 and 100 characters (after trimming)
 *  - must contain at least one digit
 *  - must contain at least one letter
 *  - allowed alphanumeric (a-z, A-Z, 0-9), spaces, and safe delimiters (-, ., ', #, /)
 *  - alphanumeric start only
 *  - ends with  alphanumeric or "."
 *  - no consecutive identical delimiters (e.g., "--", "..", "//")
 * @param {string} street The street address string to evaluate
 * @returns {boolean} True if the street satisfies all requirements, false otherwise
 */
function isStreetAddressValid(street){
    if(!street || typeof street !== "string") return false
    const trimmed = street.trim()

    if(trimmed.length < 3 || trimmed.length > 100) return false

    if (!/\d/.test(trimmed) || !/[a-zA-Z]/.test(trimmed)) return false;

    const consecutiveDelimiters = /([-./'#])\1/
    if(consecutiveDelimiters.test(trimmed)) return false

    const streetAddressPattern = /^[a-zA-Z\d][\dA-Za-z\-.,'#/\s]*[a-zA-Z\d.,]$/u
    return streetAddressPattern.test(trimmed)
}



/**
 * Checks whether addressLine2 (Apt, Suite, Unit) is valid based on the following requirements:
 *  - optional field: completely empty string (0 characters after trimming) is valid
 *  - if provided, length is between 1 and 50 characters
 *  - contains only alphanumeric characters (a-z, A-Z, 0-9), spaces, and safe delimiters (#, -, ., /, ,)
 *  - start allowed: Alphanumeric or #
 *  - end allowed: Alphanumeric or .
 * @param {string} addressLine2  secondary address string to evaluate
 * @returns {boolean} True if empty or satisfies all requirements, false otherwise
 */
function isAddressLine2Valid(addressLine2){
    if(!addressLine2 || typeof addressLine2 !== "string") return false

    if(addressLine2.trim().length === 0) return true
    if(addressLine2.trim().length > 50) return false
    if(addressLine2.trim().endsWith("#")) return false;

    const duplicateDelimitersRegex = /([#.,\/-])\1/
    if(duplicateDelimitersRegex.test(addressLine2.trim())) return false

    if((addressLine2.trim().match(/#/g) || []).length > 1) return false

    const addressLine2Pattern = /^[A-Za-z0-9#]+[A-Za-z0-9 -.,\/]*[A-Za-z0-9.]$/u
    return addressLine2Pattern.test(addressLine2)
}



/**
 * Checks whether cityName is valid based on the following requirements:
 *  - length is between 2 and 60 characters (after trimming)
 *  - letters allowed: standard Latin (a-z, A-Z) and accented characters (e.g., é, è, ê, ç, ï, à, â)
 *  - delimiters allowed: space, hyphen (-), apostrophe ('), and period (.)
 *  - no digits
 *  - no special symbols (#, /, _, @, !, etc.)
 *  - start with letter
 *  - ends with letter or "."
 *  - no consecutive identical delimiters (e.g., "--", "''", "..", or "  ")
 * @param {string} city The city name string to evaluate
 * @returns {boolean} True if the city name conforms to Canadian naming standards, false otherwise.
 */
function isCityValid(city){
    if(!city || typeof city !== "string") return false
    const trimmed = city.trim()

    if(trimmed.length < 2 || trimmed.length > 60) return false

    const repeatedDelimiterPattern = /(['. -])\1/
    if(repeatedDelimiterPattern.test(trimmed)) return false

    const cityNameAddressPattern = /^\p{sc=Latin}[\p{sc=Latin}.' -]*[\p{sc=Latin}.]$/u
    return cityNameAddressPattern.test(trimmed)
}

/**
 * Checks whether postal code is valid based on the following requirements:
 *  - cannot be empty or solely whitespace
 *  - length must be exactly 6 characters (without space) or 7 characters (with a single space)
 *  - patter Letter-Digit-Letter [Space] Digit-Letter-Digit
 *  - not case-sensitive
 *  - excludes illegal letters: D, F, I, O, Q, U
 *  - first letter cannot be W or Z
 *  - start allowed: (A-C, E, G-H, J-N, P, R-V, X-Y)
 *  - end allowed: digit only (0-9)
 * @param {string} postalCode  secondary address string to evaluate
 * @returns {boolean} True if postal code satisfies all requirements, false otherwise
 */
function isPostalCodeValid(postalCode){
    if(!postalCode || typeof postalCode !== "string") return false

    const cleaned = postalCode.replaceAll(/\s+/g, "").toUpperCase()
    if(cleaned.length !== 6) return false

    const illegalLettersRegex = /[DFIOQU]/
    if(illegalLettersRegex.test(cleaned)) return false

    const illegalStartRegex = /^[WZ]/
    if(illegalStartRegex.test(cleaned)) return false

    const postalCodePatternRegex = /^([A-Z]\d){3}$/
    return postalCodePatternRegex.test(cleaned)
}

/**
 * Checks whether hourly rate is valid based on the following requirements:
 *  - does not end with dot
 *  - does not begin with dot
 *  - does not have any character other than dot and numbers
 *  - does not have more than two decimal places
 * @param {string} hourlyRate Hourly rate to evaluate
 * @returns true if hourly rate satisfies all requirements, false otherwise
 */
function isHourlyRateValid(hourlyRate){
    if(!hourlyRate || typeof hourlyRate !== "string") return false

    const validHourlyRateRegex = /^\d+(\.\d{1,2})?$/
    return validHourlyRateRegex.test(hourlyRate)
}


/**
 * Capitalizes the first letter of a single word and converts the remainder to lowercase
 * @param {string} word word to capitalize
 * @returns {string} The capitalized word, or an empty string if input is falsy
 */
function capitalizeWord(word) {
    if (!word) return "";
    return word.slice(0, 1).toUpperCase() + word.slice(1).toLowerCase();
}


/**
 * Converts string into Title case.
 * Capitalizes first letter of each word keeping the rest in lower case
 * Capitalizes words after dash and apostrophe, keeping the delimiter
 * Trims string, keeps one space between each word
 * @param {string} string raw string to turn into Title case
 * @returns {string} string in title case, or an empty string if argument is falsy
 */
function toTitleCase(string) {
    if (!string || typeof string !== "string") return "";

    let words = string.trim().toLowerCase().split(/\s+/);

    for (let i = 0; i < words.length; i ++){
        let parts = words[i].split(/([-'])/g)
        for (let j = 0; j < parts.length; j++) {
            if(parts[j] === "s" && parts[Math.max(0, j -1)] === "'"){

            } else {
                parts[j] = capitalizeWord(parts[j])
            }
            words[i] = parts.join("")
        }
    }
    return words.join(" ");
}


/**
 * Returns name with all parts capitalized:
 * Separates by apostrophe, hyphen and space.
 * @param {string} name
 * @returns {string} Name in title case
 */
function formatName(name) {
    let parts = name.trim().split("'");

    for (let i = 0; i < parts.length; i++) {
        let hyphenParts = parts[i].split("-");

        for (let j = 0; j < hyphenParts.length; j++) {
            let spaceParts = hyphenParts[j].split(" ");

            for (let k = 0; k < spaceParts.length; k++) {
                spaceParts[k] = toTitleCase(spaceParts[k]);
            }

            hyphenParts[j] = spaceParts.join(" ");
        }

        parts[i] = hyphenParts.join("-");
    }

    return parts.join("'");
}

/**
 * Returns user's full name:
 * First name followed by space and last name, both in title case.
 * @returns {string} First and last name separated with space
 */
function getFullName() {
    let first = formatName(firstNameField.value);
    let last = formatName(lastNameField.value);

    return first + " " + last;
}


/**
 * Normalizes and formats an address line containing unit, apartment, or suite information
 * - Trims leading and trailing whitespace
 * - Deletes multiple spaces into a single space
 * - Capitalizes standard English/French descriptor words to title case
 * - Forces any part containing numbers and letters to uppercase
 * - Forces recognized non-numeric abbreviations to full uppercase (e.g., "ph" -> "PH" for Penthouse).
 * @param {string} unit - Raw unit or apartment input string
 * @returns {string} Normalized unit string, or an empty string if input is falsy
 */
function capitalizeUnit(unit){
    if(!unit || typeof unit  !== "string") return ""

    let parts = unit.trim().toLowerCase().split(/\s+/)
    for(let i = 0; i < parts.length; i++){
        if(parts[i] === "ph"){
            parts[i] = parts[i].toUpperCase()
        } else if(/\d/.test(parts[i])){
            parts[i] = parts[i].toUpperCase()
        } else {
            parts[i] = toTitleCase(parts[i])
        }
    }
    return parts.join(" ").replace(/[,.]/g, "")
}


/**
 * Normalizes and formats the primary street address line (building number, street name, street type, and direction).
 *
 * Rules:
 * - Strips leading and trailing whitespace;
 * - Turns multiple consecutive spaces into a single space
 * - Keeps numeric values untouched
 * - Uppercases any letter following the numbers
 * - Turns street name into title case, keeping hyphens and apostrophes ("ivano-frankivsk" -> "Ivano-Frankivsk")
 * - Standardizes single and double letter directional letters to uppercase
 * - Capitalizes standard street types to title case
 * @param {string} streetAddress - Raw input containing the civic number and street name
 * @returns {string} Standardized street address line, or an empty string if input is null, undefined, or empty.
 */
function capitalizeStreet(streetAddress){
    if(!streetAddress || streetAddress === "") return ""

    streetAddress = streetAddress.toLowerCase().replace(/[\s,.]+/g, " ").trim()
    let parts = streetAddress.split(" ");
    for (let i = 0; i < parts.length; i ++) {
        if(/^\d+st$|^\d+nd$|^\d+rd$|^\d+th$/.test(parts[i])){
            parts[i] = parts[i].toLowerCase()
        } else if (parts[i].includes("'")) {
            parts[i] = parts[i].split("'").map(toTitleCase).join("'");
        } else if (parts[i].includes("-")) {
            parts[i] = parts[i].split("-").map(toTitleCase).join("-");
        } else if(/^n$|^ne$|^e$|^se$|^s$|^sw$|^w$|^nw$/.test(parts[i])) {
            parts[i] = parts[i].toUpperCase()
        } else if(/\d+/.test(parts[i])){
            parts[i] = parts[i].toUpperCase()
        } else {
            parts[i] = toTitleCase(parts[i])
        }
    }
    return parts.join(" ")
}


/**
 *
 * @param {string} postalCode
 * @returns {string} Standardized postal code in format "A0A A0A"
 */
function getPostalCode(postalCode){
    postalCode = postalCode.toUpperCase().replace(/\s+/g, "");
    return postalCode.slice(0, 3) + " " + postalCode.slice(3 ,postalCode.length)
}


/**
 * Returns a formated home address line
 * Example: "4200 University Ave, Unit 12|Mississauga, ON L5L 1C6"
 * Precondition: all fields related to the address must be valid and filled in
 * @returns {string}
 */
function getHomeAddress(){
    // "4200 University Ave, Unit 12|Mississauga, ON L5L 1C6"
    let streetAddress = capitalizeStreet(streetAddressField.value)
    let addressLineTwo = capitalizeUnit(addressLine2Field.value.trim())
    let city = toTitleCase(cityField.value.trim())
    let province = provinceField.value
    let postalCode = getPostalCode(postalCodeField.value)
    return streetAddress + ", " + addressLineTwo + "|" + city + ", " + province + " " + postalCode
}

/**
 * @returns {string} HST number trimmed, with all spaces and dashes removed
 */
function getHSTNumber(){
    return hstNumberField.value.replaceAll(/\s/g, "").replaceAll("-", "").toUpperCase();
}


function getUserRegistrationData() {
    return {
        "user_name": emailField.value.trim().toLowerCase(),
        "password": passwordField.value,
        "full_name": getFullName(),
        "home_address": getHomeAddress(),
        "hst_number": getHSTNumber(),
        "hourly_rate": parseFloat(hourlyRateField.value)
    }
}

function registerUser(){
    attemptedRegister = true
    const firstNameCriteriaStatus = validateNameCriteria(firstNameField.value)
    const validFirstName = firstNameCriteriaStatus["minLength"]
        && firstNameCriteriaStatus["withinLength"]
        && firstNameCriteriaStatus["validChars"]

    const lastNameCriteriaStatus = validateNameCriteria(lastNameField.value)
    const validLastName = lastNameCriteriaStatus["minLength"]
        && lastNameCriteriaStatus["withinLength"]
        && lastNameCriteriaStatus["validChars"]

    const passwordCriteriaStatus = validatePasswordCriteria(passwordField.value)
    const validPassword = passwordCriteriaStatus["8_char_long"]
        && passwordCriteriaStatus["contains_digit"]
        && passwordCriteriaStatus["contains_special_char"]

    let readyToRegister = true

    if(!isHourlyRateValid(hourlyRateField.value)){
        readyToRegister = false
        hourlyRateField.focus()
        markFieldError(hourlyRateField, "invalidHourlyRate")
    }

    if(!isHSTValid(hstNumberField.value)){
        readyToRegister = false;
        hstNumberField.focus();
        markFieldError(hstNumberField, "invalidHstNumber");
    }

    if (!isStreetAddressValid(streetAddressField.value)) {
        readyToRegister = false;
        streetAddressField.focus();
        markFieldError(streetAddressField, "invalidStreetAddress");
    }

    if (!isAddressLine2Valid(addressLine2Field.value)) {
        readyToRegister = false;
        addressLine2Field.focus();
        markFieldError(addressLine2Field, "invalidAddressLine2");
    }

    if (!isCityValid(cityField.value)) {
        readyToRegister = false;
        cityField.focus();
        markFieldError(cityField, "invalidCity");
    }


    if (!isPostalCodeValid(postalCodeField.value)) {
        readyToRegister = false;
        postalCodeField.focus();
        markFieldError(postalCodeField, "invalidPostalCode");
    }

    if(!doPasswordsMatch()){
        readyToRegister = false;
        markPasswordMismatch();
    }

    if(!validPassword){
        readyToRegister = false;
        passwordField.focus();
        highlightFailedPasswordCriteria(passwordCriteriaStatus);
    }

    if(!isEmailValid(emailField.value)){
        readyToRegister = false
        emailField.focus();
        markFieldError(emailField, "invalidEmail")
    }

    if(!validLastName){
        readyToRegister = false
        lastNameField.focus()
        highlightFailedNameCriteria(lastNameField, lastNameCriteriaStatus, "lastNameLabel");
    }

    if(!validFirstName){
        readyToRegister = false
        firstNameField.focus()
        highlightFailedNameCriteria(firstNameField, firstNameCriteriaStatus, "firstNameLabel");
    }

    if(!isFormFilled()){
        readyToRegister = false
        indicateEmptyFields();
    }


    if(readyToRegister){
        const userRegistration = getUserRegistrationData();
        console.log(userRegistration)
        window.location.href = "email_confirmation.html"
    }
}

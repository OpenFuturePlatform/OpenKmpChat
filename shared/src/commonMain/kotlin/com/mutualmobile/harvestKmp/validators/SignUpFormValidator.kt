package com.mutualmobile.harvestKmp.validators

import com.mutualmobile.harvestKmp.validators.exceptions.EmptyFieldException
import com.mutualmobile.harvestKmp.validators.exceptions.InvalidEmailException
import com.mutualmobile.harvestKmp.validators.exceptions.InvalidPasswordException

class SignUpFormValidator {

    val ERR_LEN = "Password must have at least eight characters!"
    val ERR_WHITESPACE = "Password must not contain whitespace!"
    val ERR_DIGIT = "Password must contain at least one digit!"
    val ERR_UPPER = "Password must have at least one uppercase letter!"
    val ERR_SPECIAL = "Password must have at least one special character, such as: _%-=+#@"
    private fun validatePassword(pwd: String) = runCatching {
        require(pwd.length >= 8) { ERR_LEN }
        require(pwd.none { it.isWhitespace() }) { ERR_WHITESPACE }
        require(pwd.any { it.isDigit() }) { ERR_DIGIT }
        require(pwd.any { it.isUpperCase() }) { ERR_UPPER }
        require(pwd.any { !it.isLetterOrDigit() }) { ERR_SPECIAL }
    }
    operator fun invoke(email: String, password: String) {
//        fun String.isLongEnough() = length >= 8
//        fun String.hasEnoughDigits() = count(Char::isDigit) > 0
//        fun String.isMixedCase() = any(Char::isLowerCase) && any(Char::isUpperCase)
//        fun String.hasSpecialChar() = any { it in "!,+^" }
//        val requirements = listOf(String::isLongEnough, String::hasEnoughDigits, String::isMixedCase, String::hasSpecialChar)
//        val runCatching = runCatching { requirements.forEach { it(password) } }

        if (email.isEmpty()) {
            throw EmptyFieldException("Email")
        }
        if (password.isEmpty() || validatePassword(password).isFailure ) {
            throw EmptyFieldException("Password")
        }
        if (!isValidEmail(email)) {
            throw InvalidEmailException()
        }
    }
    operator fun invoke(firstName: String, lastName: String, email: String, password: String) {
        if (firstName.isEmpty()) {
            throw EmptyFieldException("First Name")
        }
        if (lastName.isEmpty()) {
            throw EmptyFieldException("Last Name")
        }
        if (email.isEmpty()) {
            throw EmptyFieldException("Email")
        }
        if (password.isEmpty()) {
            throw EmptyFieldException("Password")
        }
        if (!isValidEmail(email)) {
            throw InvalidEmailException()
        }
    }

    operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        orgName: String,
        website: String,
        orgIdentifier: String
    ) {
        if (firstName.isEmpty()) {
            throw EmptyFieldException("First Name")
        }
        if (lastName.isEmpty()) {
            throw EmptyFieldException("Last Name")
        }
        if (email.isEmpty()) {
            throw EmptyFieldException("Email")
        }
        if (password.isEmpty()) {
            throw EmptyFieldException("Password")
        }
        if (website.isEmpty()) {
            throw EmptyFieldException("Website")
        }
        if (orgName.isEmpty()) {
            throw EmptyFieldException("Org Name")
        }
        if (orgIdentifier.isEmpty()) {
            throw EmptyFieldException("Org Identifier")
        }
        if (!isValidEmail(email)) {
            throw InvalidEmailException()
        }
        if (!isValidPassword(password)) {
            throw InvalidPasswordException()
        }
    }

    private fun isValidEmail(target: String): Boolean {
        return target.isNotEmpty() && EMAIL_ADDRESS_PATTERN.matches(target)
    }

    private fun isValidPassword(target: String): Boolean {
        return target.isNotEmpty() && PASSWORD_PATTERN.matches(target)
    }

    companion object {
        private val RFC_5322_REGEX =
            Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
        private val PASSWORD_REGEX =
            Regex("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")

        val EMAIL_ADDRESS_PATTERN = RFC_5322_REGEX
        val PASSWORD_PATTERN = PASSWORD_REGEX
    }


}
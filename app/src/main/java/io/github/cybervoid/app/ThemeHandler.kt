package io.github.cybervoid.app

object ThemeHandler {
    var theme = 0

    fun getPrimaryColor(): Int {
        return if (theme == R.style.LightTheme) {
            R.color.colorPrimary
        } else {
            R.color.colorPrimarySecondary
        }
    }

    fun getPrimaryDarkColor(): Int {
        return if (theme == R.style.LightTheme) {
            R.color.colorPrimaryDark
        } else {
            R.color.colorPrimaryDarkSecondary
        }
    }

    fun getAccentColor(): Int {
        return if (theme == R.style.LightTheme) {
            R.color.colorAccent
        } else {
            R.color.colorAccentSecondary
        }
    }

    fun getTextColor(): Int {
        return if (theme == R.style.LightTheme) {
            R.color.colorText
        } else {
            R.color.colorTextSecondary
        }
    }

    fun switchTheme() {
        theme = if (theme == R.style.LightTheme) {
            R.style.DarkTheme
        } else {
            R.style.LightTheme
        }
    }

    fun loadSettings() {
        theme = R.style.LightTheme
    }

    fun saveSettings() {

    }
}
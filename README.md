If you plan on using our free API you must include a link to our site.
For example “Powered by SunriseSunset.io“.




Add class Constants.kt at the same level as MainActivity

class Constants {

    companion object {
        private val app_name = "Your app name"
        private val user = "your email"
        val intercept_string: String = "$app_name, $user"
    }
}


data class User(
    val chatId: Long,
    val login: String,
) {
    constructor(chatAndLogin: List<String>) : this(chatAndLogin[0].toLong(), chatAndLogin[1])
    companion object {
        const val DIVIDER = " "
    }
    override fun toString(): String {
        return "$chatId$DIVIDER$login"
    }
}

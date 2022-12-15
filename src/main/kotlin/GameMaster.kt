import java.io.File

class GameMaster {
    companion object {
        private const val DATA_DIR_PATH = "./dataDir"

        fun init() {
            val dir = File(DATA_DIR_PATH)
            dir.mkdir()
        }

        fun isGameCreating(confId: String): Boolean {
            val dataFile = File("$DATA_DIR_PATH/$confId.txt")
            return dataFile.exists()
        }

        fun deleteGame(confId: String) {
            val dataFile = File("$DATA_DIR_PATH/$confId.txt")
            dataFile.delete()
        }

        fun notificationPlayer(result: List<Pair<User, User>>, notify: (Pair<User, User>) -> Unit) {
            result.forEach(notify)
        }

        fun startGame(players: List<User>): List<Pair<User, User>> {
            val shuffledPlayers = players.shuffled()
            val randomElement = shuffledPlayers.mapIndexed { i, value ->
                val nextValue = if (i + 1 != shuffledPlayers.size) shuffledPlayers[i + 1] else shuffledPlayers[0]
                Pair(value, nextValue)
            }
            println("$randomElement")
            return randomElement
        }

        fun getPlayers(confId: String): List<User> {
            val dataFile = File("$DATA_DIR_PATH/$confId.txt")
            val players = mutableListOf<User>()
            if (dataFile.exists()) {
                dataFile.forEachLine {
                    val userParam = it.split(User.DIVIDER)
                    if (userParam.size < 2) return@forEachLine
                    players.add(User(userParam))
                }
            }
            return players
        }

        fun isAddPlayer(confId: String, client: User): Boolean {
            val players = getPlayers(confId)
            return players.any { client == it }
        }

        fun addPlayer(confId: String, user: User, pass: String): Boolean {
            return isAddPlayer(confId, user).run {
                if (!this) {
                    appendPlayer(confId, user, pass)
                } else {
                    false
                }
            }
        }

        private fun appendPlayer(confId: String, user: User, pass: String): Boolean {
            val dataFile = File("$DATA_DIR_PATH/$confId.txt")
            if (!dataFile.exists()) {
                dataFile.appendText("$pass\n")
            }
            return if (dataFile.readLines().firstOrNull() == pass) {
                dataFile.appendText("$user\n")
                true
            } else {
                false
            }
        }
    }
}
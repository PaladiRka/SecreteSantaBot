import org.junit.jupiter.api.Test

class TestMain {

    @Test
    fun testStartGame() {
        val list = listOf(
            User(0, "Sasha"),
            User(1, "Olya"),
            User(2, "Boris"),
            User(3, "Andrey")
        )
        GameMaster.startGame(list)
    }
}
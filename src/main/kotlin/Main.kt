import Dir.START_BOT_LINK_WITH_QUERY
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId

object Dir {
    const val START_BOT_LINK_WITH_QUERY = "https://t.me/secretDeadMorozBot?start="
}

fun main() {
    GameMaster.init()
    val bot = bot {
        dispatch {
            command("start") {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "Привет, мой друг!\n Это бот для игры в Тайного Санту!"
                )
                if (args.isEmpty()) {
                    return@command
                }
                val arguments = args[0].split('_')
                val confId = arguments[0]
                val passId = arguments[1]
                if (!GameMaster.isGameCreating(confId)) return@command
                val user = User(
                    message.chat.id,
                    message.from?.username ?: "null",
                )
                if (GameMaster.addPlayer(confId, user, passId)) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(user.chatId),
                        text = "Я тебя зарегистировал в игре \"$confId\" как \"${user.login}\"\n"
                    )
                } else {
                    bot.sendMessage(
                        chatId = ChatId.fromId(user.chatId),
                        text = "Ты уже зарегистирован в игре \"$confId\" как \"${user.login}\""
                    )
                }

            }
            command("create") {
                if (args.isEmpty()) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Надо ввести навзание группы и пароль (опционально)"
                    )
                    return@command
                }

                val confId = args[0]
                val confPass = if (args.size > 1) args[1] else ""
                val user = User(
                    message.chat.id,
                    message.from?.username ?: "null",
                )

                if (GameMaster.isGameCreating(confId)) {
                    if (GameMaster.isAddPlayer(confId, user)) {
                        bot.sendMessage(
                            chatId = ChatId.fromId(user.chatId),
                            text = "Игра уже существует!\n" +
                                    "И ты в ней зарегистрирован!\n" +
                                    "$START_BOT_LINK_WITH_QUERY${confId}_$confPass"
                        )
                    } else {
                        bot.sendMessage(
                            chatId = ChatId.fromId(user.chatId),
                            text = "Игра уже существует!\n" +
                                    "А ты в ней не зарегистрирован!\n" +
                                    "Попроси ссылку у создателя чтобы зарегестрироваться\n"
                        )
                    }
                } else {
                    GameMaster.addPlayer(confId, user, confPass)
                    bot.sendMessage(
                        chatId = ChatId.fromId(user.chatId),
                        text = "Отправь ссылку друзьям $START_BOT_LINK_WITH_QUERY${confId}_$confPass\n" +
                                "И когда все зарегистрируются любой из игроков сможет начать игру введя `/startGame $confId`"
                    )
                }
            }
            command("startgame") {
                if (args.isEmpty()) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Надо ввести название игры"
                    )
                    return@command
                }
                val confId = args[0]
                val client = message.chat.id
                val players = GameMaster.getPlayers(confId)
                val isParticipate = players.any { it.chatId == client }

                if (!isParticipate) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(client),
                        text = "Ты не зарегистрирован в эту игру и не можешь её начать!"
                    )
                } else {
                    val tossResult = GameMaster.startGame(players)
                    GameMaster.notificationPlayer(tossResult) {
                        val player = it.first.chatId
                        val target = it.second.login
                        val result = bot.sendMessage(
                            chatId = ChatId.fromId(player),
                            text = "Ты ищешь и даришь подарок \uD83C\uDF81 для @$target в игре $confId"
                        )
                        result.fold({message ->
                            bot.sendMessage(
                                chatId = ChatId.fromId(player),
                                text = "Счастливого Нового года!"
                            )

                            bot.pinChatMessage(ChatId.fromId(player), message.messageId, false)
                        },{ })
                    }
                    GameMaster.deleteGame(confId)
                }
            }
        }
    }
    bot.startPolling()
}

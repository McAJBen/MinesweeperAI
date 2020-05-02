import game.Block
import game.Game
import game.GameConfig
import game.GameController

object Main {
	const val WINDOW_NAME = "Minesweeper"
}

fun main() {

	Block.loadImages()

	val controller = GameController.prompt()

	val config = GameConfig.prompt()

	val game = Game(controller, config)

	game.start()
}

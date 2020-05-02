package game

import javax.swing.JOptionPane

enum class GameController {
	Player,
	AI;

	companion object {
		fun prompt(): GameController {
			return values()[
					JOptionPane.showOptionDialog(
						null,
						"Do you want to play, or watch the AI play?",
						Main.WINDOW_NAME,
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						values(),
						0
					)
			]
		}
	}
}

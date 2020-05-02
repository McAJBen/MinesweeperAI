package game

import javax.swing.JOptionPane

enum class GameConfig(
	width: Int,
	height: Int,
	mines: Int
) {
	Beginner(9, 9, 10),
	Intermediate(16, 16, 40),
	Expert(30, 16, 99),
	Custom(-1, -1, -1);

	companion object {
		fun prompt(): GameConfig {
			val config = values()[
					JOptionPane.showOptionDialog(
						null,
						"Please select field size",
						Main.WINDOW_NAME,
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						values(),
						0
					)
			]

			while (!config.valid) {
				config.width = JOptionPane.showInputDialog(
					"Please enter field width",
					9
				).toInt()

				config.height = JOptionPane.showInputDialog(
					"Please enter field height",
					9
				).toInt()

				config.mines = JOptionPane.showInputDialog(
					"Please enter number of mines",
					10
				).toInt()
			}

			return config
		}
	}

	var width = width
		private set

	var height = height
		private set

	var mines = mines
		private set

	private val valid: Boolean
		get() = width > 0 && height > 0 && mines > 0 && mines <= width * height
}

package ai

import java.awt.Point
import java.util.LinkedList

data class MatrixColumn(
	private val points: MutableList<Point>,
	val column: BooleanArray
) {

	constructor(p: Point, column: BooleanArray): this(mutableListOf(p), column)

	fun add(mc: MatrixColumn) {
		points.addAll(mc.points)
	}

	fun size(): Int {
		return points.size
	}

	fun getPoints(): LinkedList<Point> {
		return LinkedList(points)
	}

	fun equalsColumn(matrixColumn: MatrixColumn): Boolean {
		return column.contentEquals(matrixColumn.column)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as MatrixColumn

		return points == other.points && column.contentEquals(other.column)
	}

	override fun hashCode(): Int {
		var result = points.hashCode()
		result = 31 * result + column.contentHashCode()
		return result
	}
}
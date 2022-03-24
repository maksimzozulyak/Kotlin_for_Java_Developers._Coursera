package board

import board.Direction.*

open class DefaultSquareBoard(override val width: Int) : SquareBoard {

    val cells: List<Cell>

    init {
        if (width <= 0) {
            throw IllegalArgumentException()
        }

        cells = IntRange(1, width).flatMap { i  -> IntRange(1, width).map { j -> Cell(i, j) } }
    }

    fun toIndex(i: Int, j: Int): Int =
        (i - 1) * width + (j - 1)

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        if (i > width || j > width) {
            return null
        }

        return cells[toIndex(i, j)]
    }

    override fun getCell(i: Int, j: Int): Cell {
        return getCellOrNull(i, j) ?: throw IllegalArgumentException()
    }

    override fun getAllCells(): Collection<Cell> {
        return cells
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> = jRange.mapNotNull { j -> getCellOrNull(i, j) }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> = iRange.mapNotNull { i -> getCellOrNull(i, j) }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return when(direction){
            UP -> if (this.i == 1) null else cells[toIndex(this.i-1,this.j)]
            DOWN -> if (this.i == width) null else cells[toIndex(this.i+1,this.j)]
            RIGHT -> if (this.j == width) null else cells[toIndex(this.i,this.j+1)]
            LEFT -> if (this.j == 1) null else cells[toIndex(this.i,this.j-1)]
        }
    }

}

class DefaultGameBoard<T>(width: Int) : GameBoard<T>, DefaultSquareBoard(width){

    private val idx = hashMapOf<Cell, T?>()

    init {
        getAllCells().map { idx[it] = null }
    }
    override fun get(cell: Cell): T? = idx[cell]

    override fun set(cell: Cell, value: T?) {
        idx[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> = idx.filterValues(predicate).keys

    override fun find(predicate: (T?) -> Boolean): Cell? = idx.filterValues(predicate).keys.firstOrNull()

    override fun any(predicate: (T?) -> Boolean): Boolean = mapCellsToValues().any(predicate)

    override fun all(predicate: (T?) -> Boolean): Boolean = mapCellsToValues().all(predicate)

    private fun mapCellsToValues() = getAllCells().map { idx[it] }
}

fun createSquareBoard(width: Int): SquareBoard = DefaultSquareBoard(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = DefaultGameBoard<T>(width)


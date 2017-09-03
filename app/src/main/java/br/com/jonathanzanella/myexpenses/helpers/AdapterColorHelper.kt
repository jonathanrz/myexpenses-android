package br.com.jonathanzanella.myexpenses.helpers

class AdapterColorHelper(private val oddColor: Int, private val evenColor: Int) {

    private fun isMultipleOfTwo(position: Int) = position % 2 == 0
    private fun isMultipleOfFour(position: Int) = position % 4 == 0

    fun getColorForGridWithTwoColumns(position: Int) =
        when {
            isMultipleOfFour(position) -> oddColor
            isMultipleOfTwo(position) -> evenColor
            isMultipleOfFour(position + 1) -> oddColor
            isMultipleOfTwo(position + 1) -> evenColor
            else -> oddColor
        }

    fun getColorForLinearLayout(position: Int) = if (position % 2 == 0) oddColor else evenColor
}

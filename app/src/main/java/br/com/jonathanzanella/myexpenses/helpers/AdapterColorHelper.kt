package br.com.jonathanzanella.myexpenses.helpers

class AdapterColorHelper(private val oddColor: Int, private val evenColor: Int) {

    private fun isMultipleOfTwo(position: Int): Boolean {
        return position % 2 == 0
    }

    private fun isMultipleOfFour(position: Int): Boolean {
        return position % 4 == 0
    }

    fun getColorForGridWithTwoColumns(position: Int): Int {
        if (isMultipleOfFour(position))
            return oddColor
        else if (isMultipleOfTwo(position))
            return evenColor
        else if (isMultipleOfFour(position + 1))
            return oddColor
        else if (isMultipleOfTwo(position + 1))
            return evenColor
        else
            return oddColor
    }

    fun getColorForLinearLayout(position: Int): Int {
        if (position % 2 == 0)
            return oddColor
        else
            return evenColor
    }
}

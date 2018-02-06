package liuhan;

import cloudsim.Log

/**
 * Created by liuha on 2017/04/28 0028.
 */
class TablePrinter(val title : List<String>, val minTitleDistance : Int = 4) {
        private val indent = "".padEnd(minTitleDistance, ' ')
        private val centerFixed = getCenterFixed()

        fun printTitleLine() {
                for (k in title) {
                        Log.print(k + indent)
                }
                Log.printLine()
        }

        fun printLine(elem : List<String>) {
                assert(elem.size == title.size)
                val prefixAndShuffix = getElemPrefixAndShuffixLength(elem)
                for (idx in elem.indices) {
                        Log.print("".padEnd(prefixAndShuffix[idx].first, ' '))
                        Log.print(elem[idx])
                        Log.print("".padEnd(prefixAndShuffix[idx].second, ' '))
                }
                Log.printLine()
        }

        private fun getElemPrefixAndShuffixLength(elem : List<String>) : List<Pair<Int,Int>> {
                val prefixAndShuffix = mutableListOf<Pair<Int,Int>>()
                val startAndEnd = getElemStartAndEndIndex(elem)
                for (idx in elem.indices) {
                        prefixAndShuffix.add(Pair<Int,Int>(
                                startAndEnd[idx].first - getTitleStartIndex(idx),
                                getTitleStartIndex(idx + 1) - startAndEnd[idx].second - 1
                        ))
                }
                prefixAndShuffix[prefixAndShuffix.size - 1] =
                        Pair<Int,Int>(prefixAndShuffix[prefixAndShuffix.size - 1].first, 0)
                for (idx in prefixAndShuffix.indices) {
                        if (idx == 0) {
                                continue
                        }
                        if (prefixAndShuffix[idx].first < 0) {
                                prefixAndShuffix[idx - 1] = Pair(prefixAndShuffix[idx - 1].first, prefixAndShuffix[idx - 1].second + prefixAndShuffix[idx].first)
                                prefixAndShuffix[idx] = Pair(0, prefixAndShuffix[idx].second)
                        }
                }
                return prefixAndShuffix
        }

        private fun getElemStartAndEndIndex(elem : List<String>) : List<Pair<Int,Int>> {
                val startAndEnd = mutableListOf<Pair<Int,Int>>()
                for (idx in elem.indices) {
                        startAndEnd.add(Pair<Int,Int>(
                                centerFixed[idx] - elem[idx].length/2,
                                centerFixed[idx] + elem[idx].length/2
                        ))
                }
                return startAndEnd
        }

        private fun getCenterFixed() : List<Int> {
                val center = mutableListOf<Int>()
                for (idx in title.indices) {
                        val startIndex = getTitleStartIndex(idx)
                        val c = startIndex + title[idx].length/2
                        center.add(c)
                }
                return center
        }

        private fun getTitleStartIndex(titleIndex : Int) : Int {
                var index = 0
                var i = 0
                while (i < titleIndex && i < title.size) {
                        index += title[i].length + indent.length
                        i++
                }
                return index
        }
}

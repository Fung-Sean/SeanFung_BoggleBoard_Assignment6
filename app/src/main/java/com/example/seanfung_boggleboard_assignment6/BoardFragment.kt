package com.example.seanfung_boggleboard_assignment6

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.seanfung_boggleboard_assignment6.databinding.BoardFragmentBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.math.abs

class BoardFragment : Fragment() {
    interface BoardFragmentListener {
        fun updateScore(score: Int)
    }
    private var currentWord: String = ""
    private var listener: BoardFragmentListener? = null
    private var _binding: BoardFragmentBinding? = null
    private val selectedButtonIds = mutableListOf<Button>()

    val TAG = "Board Frag"
    override fun onStart() {
        super.onStart()


    }

    private val binding
        get() = checkNotNull(_binding)
        {
            "Cannot access binding because it is null"
        }



    // Function to read the dictionary file and return a list of words
    private val dictionary = HashSet<String>()

    private fun loadDictionary() {
        try {
            val inputStream = requireContext().assets.open("dictionary.txt")
            inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    dictionary.add(line.trim().lowercase())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boggleBoardLetters = generateBoggleBoardLetters()

        // Add buttons for each letter to the RelativeLayout
        val buttonIds = arrayOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9, R.id.button10, R.id.button11,
            R.id.button12, R.id.button13, R.id.button14, R.id.button15
        )

        for (i in buttonIds.indices) {
            val button = view.findViewById<Button>(buttonIds[i])
            button.text = boggleBoardLetters[i].toString()
            button.setOnClickListener {
                onLetterClick(button)
            }
            // Set row and column information as tags
            button.tag = i
        }

        // Populate each button with a letter
        for (i in boggleBoardLetters.indices) {
            val button = view.findViewById<Button>(buttonIds[i])
            button.text = boggleBoardLetters[i].toString()
        }
        binding.clearButton.setOnClickListener {
            clearWord()
        }
        binding.submitButton.setOnClickListener{
            if (currentWord.length == 0) {
                Toast.makeText(context, "Please guess a word", Toast.LENGTH_SHORT).show()
            } else {
                val score = onSubmitButtonClick(currentWord)
                if (score == -10) {
                    Toast.makeText(context, "Incorrect, -$score", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Correct, +$score", Toast.LENGTH_SHORT).show()
                }
                listener?.updateScore(score)
                clearWord()
            }
        }
//        binding.newGameButton.setOnClickListener {
//            // Notify the MainActivity that the new game button is clicked
//            (activity as? NewGameListener)?.onNewGameClicked()
//        }
    }
    private fun generateBoggleBoardLetters(): List<Char> {
        val vowels = listOf('A', 'E', 'I', 'O', 'U')
        val consonants = ('A'..'Z').filter { it !in vowels }
        val random = java.util.Random()

        // Generate the Boggle board with at least two vowels and not too many
        val vowelLetters = mutableListOf<Char>()
        val consonantLetters = mutableListOf<Char>()

        // Add two to four vowels
        val numVowels = random.nextInt(3) + 2
        repeat(numVowels) {
            vowelLetters.add(vowels[random.nextInt(vowels.size)])
        }

        // Add the remaining letters
        val numConsonants = 16 - numVowels
        repeat(numConsonants) {
            consonantLetters.add(consonants[random.nextInt(consonants.size)])
        }

        // Shuffle and combine the vowel and consonant letters
        val boggleBoardLetters = (vowelLetters + consonantLetters).shuffled(random)
        return boggleBoardLetters
    }

    private fun isValidNextLetter(button: Button): Boolean {
        // Get the last selected button
        val lastButton = selectedButtonIds.lastOrNull()

        // If there's no last button, any button is valid
        if (lastButton == null) {
            return true
        }

        // Calculate the row and column of the current button
        val row = button.tag as Int / 4
        val col = button.tag as Int % 4

        // Calculate the row and column of the last selected button
        val lastRow = lastButton.tag as Int / 4
        val lastCol = lastButton.tag as Int % 4

        // Check if the current button is adjacent to the last selected button
        val rowDiff = abs(row - lastRow)
        val colDiff = abs(col - lastCol)

        return (rowDiff <= 1 && colDiff <= 1) && !selectedButtonIds.contains(button)
    }

    private fun onLetterClick(button: Button) {
        if (currentWord.isEmpty() || isValidNextLetter(button)) {
            currentWord += button.text.toString()
            binding.textWord.text = currentWord
            button.setBackgroundColor(Color.parseColor("#c6cfc8"))
            button.isEnabled = false
            selectedButtonIds.add(button)
        } else {
            Toast.makeText(requireContext(), "Invalid letter selection", Toast.LENGTH_SHORT).show()
        }
    }







    private fun isAdjacent(row: Int, col: Int, lastRow: Int, lastCol: Int): Boolean {
        Log.d(TAG, "Row: $row, Col: $col, LastRow: $lastRow, LastCol: $lastCol")
        val rowDiff = abs(row - lastRow)
        val colDiff = abs(col - lastCol)
        val isAdjacent = (rowDiff == 1 && colDiff <= 1) || (colDiff == 1 && rowDiff <= 1) || (rowDiff == 1 && colDiff == 1)
        Log.d(TAG, "Is adjacent: $isAdjacent")
        return isAdjacent
    }

    private fun clearWord() {
        currentWord = ""
        binding.textWord.text = ""
        selectedButtonIds.clear()
        val parentLayout = binding.gridLayout // Get reference to the parent layout (RelativeLayout)
        for (i in 0 until parentLayout.childCount) {
            val child = parentLayout.getChildAt(i)
            if (child is Button) {
                child.setBackgroundColor(Color.parseColor("#815d55")) // Set background color to default (white)
                child.isEnabled = true
            }
        }
    }

    fun newGame() {
        // Generate a new board
        val boggleBoardLetters = generateBoggleBoardLetters()

        // Update the buttons with the new letters
        val buttonIds = arrayOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9, R.id.button10, R.id.button11,
            R.id.button12, R.id.button13, R.id.button14, R.id.button15
        )
        for (i in buttonIds.indices) {
            val button = requireView().findViewById<Button>(buttonIds[i])
            button.text = boggleBoardLetters[i].toString()
            button.setBackgroundColor(Color.WHITE) // Set background color to default (white)
            button.isEnabled = true
        }

        // Clear the current word and the text view displaying it
        clearWord()
    }



    private fun onSubmitButtonClick(word: String): Int {
        var wordScore = 0

        // Check if the word meets the minimum length requirement
        if (word.length >= 4) {
            val vowels = setOf('a', 'e', 'i', 'o', 'u')
            val specialConsonants = setOf('s', 'z', 'p', 'x', 'q')
            var vowelCount = 0
            var specialConsonantUsed = false

            // Calculate the score for each letter in the word
            for (letter in word) {
                if (letter.isLetter()) {
                    if (letter.toLowerCase() in vowels) {
                        // Vowels are worth 5 points
                        wordScore += 5
                        vowelCount++
                    } else {
                        // Consonants are worth one point
                        wordScore++

                        // Check for special consonants and set the flag if present
                        if (letter.toLowerCase() in specialConsonants) {
                            specialConsonantUsed = true
                        }
                    }
                }
            }

            // Check if the word contains at least two vowels
            if (vowelCount >= 2) {
                // Double the score if a special consonant is present
                if (specialConsonantUsed) {
                    wordScore *= 2
                }

                // Check if the word exists in the dictionary
                if (dictionary.contains(word.toLowerCase())) {
                    // Word found in the dictionary, return the calculated score
                    return wordScore
                } else {
                    // Word not found in the dictionary, decrement the score by 10 points
                    return -10
                }
            } else {
                // Word does not contain at least two vowels, decrement the score by 10 points
                return -10
            }
        } else {
            // Word length is less than 4, decrement the score by 10 points
            return -10
        }
    }



    private fun Char.isVowel(): Boolean {
        val vowels = setOf('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U')
        return this in vowels
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BoardFragmentBinding.inflate(inflater, container, false)
        currentWord = ""
        loadDictionary()
        Log.d(TAG, dictionary.toString())
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BoardFragment.BoardFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ScoreFragmentListener")
        }
    }
}

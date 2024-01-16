package com.vansuita.wordle

import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.vansuita.wordle.databinding.ActivityMainBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

private const val KEY_PREF = "KEY_PREF"
private const val KEY_WINS = "KEY_WINS"
private const val MAX_GUESSES = 3

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding

	private var wordsThatStartsWith = ""
	private lateinit var wordToGuess: String
	private var guessCounter = 0
	private val sharedPref by lazy {
		getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		setSupportActionBar(binding.toolbar)
		setupListeners()
		loadWins()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main_menu, menu)
		val item = menu.findItem(R.id.item_options)
		val spinner = item.actionView as Spinner?
		spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
				wordsThatStartsWith = arrayOf("", "A", "B", "C")[position]
				resetGame()
			}

			override fun onNothingSelected(p0: AdapterView<*>?) {}

		}
		val adapter = ArrayAdapter.createFromResource(
			this,
			R.array.options, android.R.layout.simple_spinner_item
		)
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		spinner!!.adapter = adapter
		return true
	}


	private fun resetGame() {
		with(binding) {
			listOf(
				guessOneLabel, guessOneCheckLabel, guessOne, guessOneCheck,
				guessTwoLabel, guessTwoCheckLabel, guessTwo, guessTwoCheck,
				guessThreeLabel, guessThreeCheckLabel, guessThree, guessThreeCheck
			).forEach {
				it.visibility = View.GONE
			}
		}
		binding.wordToGuessTextView.visibility = View.GONE

		guessCounter = 0
		wordToGuess = FourLetterWordList.getRandomFourLetterWord(wordsThatStartsWith)
		binding.wordToGuessTextView.text = wordToGuess
		binding.guessButton.isEnabled = false
		binding.guessButton.text = getString(R.string.guess)
		binding.userInputEditText.isEnabled = true
	}

	private fun setupListeners() {
		binding.guessButton.setOnClickListener {
			if (isEndGame()) {
				resetGame()
			} else {
				tryToGuess(binding.userInputEditText.text.toString().uppercase())
			}
		}
		binding.userInputEditText.doAfterTextChanged { text ->
			binding.guessButton.isEnabled = isEndGame() || isValidInput()
		}
	}

	private fun tryToGuess(word: String) = with(binding) {
		guessCounter++
		when (guessCounter) {
			1 -> {
				guessOne.text = word
				guessOneCheck.text = checkGuess(word)
				listOf(guessOneLabel, guessOneCheckLabel, guessOne, guessOneCheck).forEach {
					it.visibility = View.VISIBLE
				}
				checkWin(word, false)
			}

			2 -> {
				guessTwo.text = word
				guessTwoCheck.text = checkGuess(word)
				listOf(guessTwoLabel, guessTwoCheckLabel, guessTwo, guessTwoCheck).forEach {
					it.visibility = View.VISIBLE
				}
				checkWin(word, false)
			}

			3 -> {
				guessThree.text = word
				guessThreeCheck.text = checkGuess(word)
				listOf(guessThreeLabel, guessThreeCheckLabel, guessThree, guessThreeCheck).forEach {
					it.visibility = View.VISIBLE
				}
				checkWin(word, true)
			}
		}
	}

	private fun isValidInput(input: String = binding.userInputEditText.text.toString()) =
		(input.length == 4) && input.all { it.isLetter() }

	private fun checkGuess(guess: String): SpannableStringBuilder {
		val builder = SpannableStringBuilder()

		for (i in 0..3) {
			if (guess[i] == wordToGuess[i]) {
				builder.color(getColor(R.color.green)) { append(guess[i]) }
			} else if (guess[i] in wordToGuess) {
				builder.color(getColor(R.color.red)) { append(guess[i]) }
			} else {
				builder.append(guess[i])
			}
		}
		return builder
	}

	private fun isEndGame() = binding.wordToGuessTextView.isVisible || (guessCounter >= MAX_GUESSES)

	private fun checkWin(word: String, force: Boolean = false) {
		binding.userInputEditText.text.clear()

		if (word == wordToGuess) {
			showKonfetti()
			sharedPref.edit().putInt(KEY_WINS, sharedPref.getInt(KEY_WINS, 0) + 1).apply()
			loadWins()
		}

		if (force || (word == wordToGuess)) {
			binding.guessButton.text = getString(R.string.reset)
			binding.guessButton.isEnabled = true
			binding.wordToGuessTextView.visibility = View.VISIBLE
			binding.userInputEditText.isEnabled = false
		}
	}

	private fun showKonfetti() {
		binding.konfetti.start(
			Party(
				speed = 0f,
				maxSpeed = 30f,
				damping = 0.9f,
				spread = 360,
				colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
				emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
				position = Position.Relative(0.5, 0.3)
			)
		)
	}

	private fun loadWins() {
		val wins = sharedPref.getInt(KEY_WINS, 0)
		if (wins > 0)
			title = " ${getString(R.string.app_name)} - $wins Wins"
	}
}
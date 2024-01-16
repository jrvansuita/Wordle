package com.vansuita.wordle

// author: calren
object FourLetterWordList {
	// List of most common 4 letter words from: https://7esl.com/4-letter-words/
	private const val fourLetterWords =
		"Area,Army,Baby,Back,Ball,Band,Bank,Base,Bill,Body,Book,Call,Card,Care,Case,Cash,City,Club,Cost,Date,Deal,Door,Duty,East,Edge,Face,Fact,Farm,Fear,File,Film,Fire,Firm,Fish,Food,Foot,Form,Fund,Game,Girl,Goal,Gold,Hair,Half,Hall,Hand,Head,Help,Hill,Home,Hope,Hour,Idea,Jack,John,Kind,King,Lack,Lady,Land,Life,Line,List,Look,Lord,Loss,Love,Mark,Mary,Mind,Miss,Move,Name,Need,News,Note,Page,Pain,Pair,Park,Part,Past,Path,Paul,Plan,Play,Post,Race,Rain,Rate,Rest,Rise,Risk,Road,Rock,Role,Room,Rule,Sale,Seat,Shop,Show,Side,Sign,Site,Size,Skin,Sort,Star,Step,Task,Team,Term,Test,Text,Time,Tour,Town,Tree,Turn,Type,Unit,User,View,Wall,Week,West,Wife,Will,Wind,Wine,Wood,Word,Work,Year"

	private fun getAllFourLetterWords(): List<String> {
		return fourLetterWords.split(",")
	}

	// Returns a random four letter word from the list in all caps
	fun getRandomFourLetterWord(startingWith: String = ""): String {
		val allWords = getAllFourLetterWords().filter { it.startsWith(startingWith) }
		val randomNumber = (0..allWords.size).shuffled().last()
		return allWords[randomNumber].uppercase()
	}
}
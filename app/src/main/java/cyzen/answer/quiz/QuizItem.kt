package cyzen.answer.quiz

data class QuizItem(
    val category: String,
    val question: String,
    val options: List<String>,
    var answer: String = "" //默认为空
)
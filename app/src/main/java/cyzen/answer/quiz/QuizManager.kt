package cyzen.answer.quiz

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import cyzen.answer.ui.MainAct
import cyzen.answer.ui.SettingsAct
import cyzen.utils.toast
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.text.DecimalFormat

class QuizManager(private val context: Context, private val clipboardManager: ClipboardManager) {

    private var sheet: Sheet? = null
    lateinit var uri: Uri

    private var rowCount = 0

    //是否加载了题库
    val isQuizLoaded
        get() = quizMap.isNotEmpty()

    //题库
    private val quizMap = HashMap<String, QuizItem>()

    //中文字符正则
    private val regexChinese = Regex("[^\\u4e00-\\u9fa5]")

    //格式化题目的正则，去除标点、空格、下划线
    private val regexFormed = Regex("[\\p{P}\\s_]")

    //选项字符串列表
    private val optionsStrList = listOf("A", "B", "C", "D", "E", "F")

    //用于excel选项格式化
    private val decimalFormat = DecimalFormat("##########.#####")

    /**
     * 从下载文件夹的输入流中加载 Excel
     */
    fun loadFromExcel(inputStream: BufferedInputStream) {
        val workbook = WorkbookFactory.create(inputStream)
        sheet = workbook!!.getSheetAt(0)

        rowCount = 0
        var skipCount = 0
        for (row in sheet!!) {
            rowCount++
            if (skipCount++ < 2) continue //跳过前两行

            val category = row.getCell(0)?.stringCellValue ?: break
            val question = row.getCell(1)?.stringCellValue?.trim() ?: break
            val questionFormed = question.replace(regexFormed, "") //去除标点后的题目
            val options = mutableListOf<String>()
            val answer = row.getCell(2)?.stringCellValue ?: ""

            for (i in 3..6) { //获取答案
                val cell = row.getCell(i) ?: break
                val data = when (cell.cellType) {
                    CellType.STRING -> cell.stringCellValue
                    CellType.NUMERIC -> decimalFormat.format(cell.numericCellValue)
                    else -> ""
                }
                if (data.isEmpty()) break
                else options.add(data)
            }

            //避免同一题目不同分类
            quizMap["$category$questionFormed"] = QuizItem(category, question, options, answer)
        }
    }

    /**
     * 批量处理粘贴的内容并输出答案
     */
    fun getBatchAnswers(pastedText: String): Pair<String, Boolean> {
        if (quizMap.isEmpty()) return "题库为空" to false

        val lines = pastedText.lines().map { it.trim() }.filter { it.isNotEmpty() } //将每行转成列表，并排除空行
        if (lines.isEmpty()) return "输入内容为空" to false

        val output = StringBuilder()
        val clipStr = StringBuilder("按题目序号给出以下题目正确答案：")
        val copyStr = StringBuilder()

        var hasNewQuiz = false

        //判断是有奖答题还是闯关答题
        val firstLine = lines[0]
        val quizDataList = try {
            when {
                firstLine.startsWith("基础") -> parseChallengeQuiz(lines)
                firstLine.getOrNull(0) == '(' && firstLine.getOrNull(3) == '）' -> parseAwardQuiz(lines)
                else -> return "未识别到支持的题目格式" to false
            }
        } catch (e: Exception) {
            return "解析格式错误: ${e.message}" to false
        }

        //遍历处理题目
        quizDataList.forEachIndexed { index, data ->
            val count = index + 1
            val category = data.category
            val question = data.question
            val questionFormed = question.replace(regexFormed, "")
            val quizItem = quizMap["$category$questionFormed"]

            if (quizItem == null || quizItem.answer.isEmpty()) { //处理缺失题目或空答案
                val options = data.options //粘贴的选项
                if (quizItem == null) {
                    output.append("$count.无该题\n")
                    if (SettingsAct.saveQuiz) {
                        //处理剪贴板
                        copyStr.append(category).append("\t").append(question).append("\t")
                        for (option in options)
                            copyStr.append("\t").append(option.drop(1))
                        copyStr.append("\n")

                        //处理excel
                        addRow(category, question, options)
                        quizMap["$category$questionFormed"] = data
                        hasNewQuiz = true
                    }
                } else output.append("$count.答案为空\n")

                //加入题目到剪贴板
                addClipStr(clipStr, count, category, question, options)
            } else { //处理已知题目
                formatOutput(output, count, question, quizItem.answer, quizItem.options)
            }
        }

        //复制到剪贴板
        handleClipboard(clipStr)

        if (SettingsAct.saveQuiz) { //处理新题
            MainAct.strCopy = copyStr.toString() //新题字符串
            if (hasNewQuiz) saveQuizFile() //有新题时保存题库
        }

        return if (output.isEmpty()) "未识别到有效题目" to false
        else output.toString() to true
    }

    /**
     * 解析闯关答题
     */
    private fun parseChallengeQuiz(lines: List<String>): List<QuizItem> {
        val result = mutableListOf<QuizItem>()
        var i = 0
        while (i < lines.size) {
            val category = lines[i].let { if (it.endsWith("题")) it.drop(2) else throw Exception("第${i + 1}行不是分类") }
            val question = lines.getOrNull(i + 1) ?: break

            val options = mutableListOf<String>()
            var next = i + 2
            while (next < lines.size && lines[next].take(1) in optionsStrList) {
                options.add(lines[next++])
            }
            result.add(QuizItem(category, question, options))
            i = next
        }
        return result
    }

    /**
     * 解析有奖答题
     */
    private fun parseAwardQuiz(lines: List<String>): List<QuizItem> {
        val result = mutableListOf<QuizItem>()
        var i = 0
        while (i < lines.size) {
            val lineStr = lines[i]
            val category = lineStr.run { if (getOrNull(0) == '(' && getOrNull(3) == '）') "${substring(1, 3)}题" else throw Exception("第${i + 1}行分类解析错误") }
            val question = lineStr.substring(4)

            val options = mutableListOf<String>()
            var next = i + 1
            while (next < lines.size && lines[next].take(1) in optionsStrList) {
                options.add(lines[next++])
            }
            result.add(QuizItem(category, question, options))
            i = next
        }
        return result
    }

    /**
     * 格式化输出
     */
    private fun formatOutput(output: StringBuilder, count: Int, question: String, answer: String, options: List<String>) {
        when (SettingsAct.answerDisplayType) {
            1 -> output.append("$count.$answer\n") //只显示选项
            2 -> { //显示选项+内容
                output.append("$count.")
                appendAnswer(output, answer, options)
            }

            3 -> output.append("$count.$question\n答案：$answer\n") //显示题目+选项
            4 -> {
                output.append("$count.$question\n答案：") //显示题目+选项+内容
                appendAnswer(output, answer, options)
            }

            else -> throw IllegalArgumentException("参数错误")
        }
    }

    /**
     * 复制到剪贴板
     */
    private fun handleClipboard(clipStr: StringBuilder) {
        if (clipStr.lines().size >= 3) { //至少一题
            val clip = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, clipStr)
            clipboardManager.setPrimaryClip(clip)
            context.toast("已复制AI提示词到剪贴板")
        }
    }


    /**
     * 添加答案内容
     */
    fun appendAnswer(output: StringBuilder, answer: String, options: List<String>) {
        if (answer.length > 1) { //多选
            for (c in answer) {
                val optionIndex = optionsStrList.indexOf(c.toString()) //找到选项的位置坐标
                if (optionIndex in options.indices) //是否在选项的坐标范围
                    output.append("$c.${options[optionIndex]}   ") //显示选项和文字
                else output.append("$c   ") //没有文字只显示选项
            }
            output.append("\n")
        } else { //单选或无答案
            val optionIndex = optionsStrList.indexOf(answer) //找到选项的位置坐标
            if (optionIndex in options.indices) //是否在选项的坐标范围
                output.append("$answer.${options[optionIndex]}\n") //显示选项和文字
            else output.append("$answer\n") //没有文字只显示选项
        }
    }

    /**
     * 添加题目到excel文件
     */
    fun addRow(category: String, question: String, optionsPasted: List<String>) {
        val row = sheet!!.createRow(rowCount++)
        row.createCell(0).setCellValue(category)
        row.createCell(1).setCellValue(question)
        row.createCell(2)
        for (i in optionsPasted.indices)
            row.createCell(3 + i).setCellValue(optionsPasted[i].drop(1))
    }

    /**
     * 添加题目到剪贴板
     */
    fun addClipStr(clipStr: StringBuilder, count: Int, category: String, question: String, optionsPasted: List<String>) {
        var optionStr = ""
        for (i in optionsPasted.indices)
            optionStr += "\n${optionsPasted[i]}"
        clipStr.append("\n$category\n${count}.$question$optionStr")
    }

    /**
     * 保存题库文件
     */
    fun saveQuizFile() {
        context.contentResolver.openOutputStream(uri, "wt")?.use { originalOut ->
            BufferedOutputStream(originalOut).use { bufferedOut ->
                sheet?.workbook?.write(bufferedOut)
                bufferedOut.flush()
            }
        }
    }

    fun destroy() {
        sheet?.workbook?.close()
    }
}
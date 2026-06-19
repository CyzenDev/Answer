package cyzen.answer.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Space
import android.widget.TextView
import cyzen.answer.R
import cyzen.answer.quiz.QuizManager
import cyzen.utils.gone
import cyzen.utils.hasInstalledApp
import cyzen.utils.mainHandler
import cyzen.utils.show
import cyzen.utils.startAct
import cyzen.utils.toast
import java.io.BufferedInputStream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class MainAct : Activity() {

    private val tvTips: TextView by lazy { findViewById(R.id.tv_tips) }
    private val etQuiz: EditText by lazy { findViewById(R.id.et_quiz) }
    private val tvLoad: TextView by lazy { findViewById(R.id.tv_load) }
    private val tvGet: TextView by lazy { findViewById(R.id.tv_get) }
    private val tvAnswer: TextView by lazy { findViewById(R.id.tv_answer) }
    private val spCopy: Space by lazy { findViewById(R.id.sp_copy) }
    private val tvCopy: TextView by lazy { findViewById(R.id.tv_copy) }
    private val pbLoading: ProgressBar by lazy { findViewById(R.id.pb_loading) }

    /**
     * 自定义主线程的[Executor]
     */
    private val mainExecutor = Executor { mainHandler.post(it) }

    private val clipboardManager by lazy { getSystemService(CLIPBOARD_SERVICE) as ClipboardManager }

    private val REQUEST_CODE = 1

    private val quizManager by lazy { QuizManager(this, clipboardManager) }

    private val mimeTypes = arrayOf(
        "application/vnd.ms-excel", //xls文件
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" //xlsx文件
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        if (!hasInstalledApp("org.autojs.autojs")) {
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("本软件依赖Auto.js 4.1.1 Alpha2，请下载安装，并导入答题脚本")
                .setPositiveButton("确定", null)
                .show()
        }

        etQuiz.setCustomSelectionActionModeCallback(object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                etQuiz.selectAll() //让EditText进入选择模式（长按或双击）时，全选
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean = false
            override fun onDestroyActionMode(mode: ActionMode?) {}
        })

        tvLoad.setOnClickListener {
            try {
                startActivityForResult( //打开文件选择器
                    Intent(Intent.ACTION_OPEN_DOCUMENT)
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        .setType(mimeTypes[1])
                        .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes), REQUEST_CODE
                )
            } catch (e: Exception) {
                e.printStackTrace()
                toast(e.message)
            }
        }

        tvGet.setOnClickListener {
            try {
                if (pbLoading.isShown) {
                    toast("获取中，请稍后…")
                    return@setOnClickListener
                }

                val quizStr = etQuiz.text.toString()
                if (!quizManager.isQuizLoaded)
                    toast("未加载题库")
                else if (quizStr.isEmpty())
                    toast("题目为空")
                else getAnswer(quizStr)
            } catch (e: Exception) {
                e.printStackTrace()
                tvAnswer.text = e.message
                tvAnswer.show()
            }
        }

        tvCopy.setOnClickListener {
            try {
                val clip = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, strCopy)
                clipboardManager.setPrimaryClip(clip)
                toast("已复制新题到剪贴板，可直接粘贴到题库")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadExcel(uri: Uri) {
        ProgressDialog(this).apply {
            setMessage("导入题库中，请稍后…")
            setCancelable(false)
            setOnShowListener {
                try {
                    quizManager.uri = uri
                    contentResolver.openInputStream(uri)?.use { originalInput ->
                        BufferedInputStream(originalInput).use { bufferedInput ->
                            quizManager.loadFromExcel(bufferedInput)
                        }
                    }
                    dismiss()
                    toast("题库导入成功")
                    tvLoad.setText(R.string.quiz_bank_loaded)
                    tvTips.gone()
                } catch (e: Exception) {
                    e.printStackTrace()
                    toast("题库导入失败: ${e.message}", true)
                }
            }
        }.show()
    }

    private fun getAnswer(quizStr: String, auto: Boolean = false) {
        pbLoading.show()
        etQuiz.clearFocus() //清除EditText的焦点，否则一获得焦点就会弹软键盘

        CompletableFuture.supplyAsync { //异步获取答案
            quizManager.getBatchAnswers(quizStr)
        }.thenAcceptAsync({ result ->
            if (result.second && auto) { //自动解析成功时
                etQuiz.setText(quizStr)
                tvAnswer.text = result.first
                handleCopy()
            } else if (!auto) {
                tvAnswer.text = result.first
                handleCopy()
            }
            pbLoading.gone()
        }, mainExecutor).handleAsync({ res, t ->
            t.printStackTrace()
            tvAnswer.text = "错误：${t.message}"
            tvAnswer.show()
            res
        }, mainExecutor)
    }

    private fun handleCopy() {
        if (SettingsAct.saveQuiz && strCopy.isNotEmpty()) {
            spCopy.show()
            tvCopy.show()
        } else {
            spCopy.gone()
            tvCopy.gone()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus || !quizManager.isQuizLoaded) return //未获得焦点或题库未加载
        //自动解析剪贴板
        val clipData = clipboardManager.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val newText = clipData.getItemAt(0).text.toString()
            val oldText = etQuiz.text.toString()
            if (oldText == newText) return //内容未变
            getAnswer(newText, true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri == null) toast("题库数据为空")
            else loadExcel(uri)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_settings) {
            startAct<SettingsAct>()
            return true
        }
        return false
    }

    override fun onDestroy() {
        quizManager.destroy()
        super.onDestroy()
    }

    companion object {
        //新题文本
        var strCopy: String = ""
    }
}
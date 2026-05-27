package cyzen.answer.ui

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.TextView
import cyzen.answer.Prefs
import cyzen.answer.R
import cyzen.answer.adapter.MyLvAdapter
import cyzen.answer.bean.ListBean

class SettingsAct : Activity() {

    private val lvSettings: ListView by lazy { findViewById(R.id.lv_settings) }
    private val tvCopyright: TextView by lazy { findViewById(R.id.tv_copyright) }

    private val mListSettings = arrayListOf<ListBean>()

    private var adapterSettings: MyLvAdapter? = null

    private var popupMenu: PopupMenu? = null

    private val answerDisplayTypeArr by lazy { resources.getStringArray(R.array.answer_display_types) }

    private fun showPopupMenu(itemView: View, menuArr: Array<String>, checkedPos: Int, listener: PopupMenu.OnMenuItemClickListener) {
        val contentEnd = itemView.findViewById<TextView>(R.id.value_end) ?: return
        popupMenu?.dismiss()
        popupMenu = PopupMenu(itemView.context, contentEnd).apply {
            for (i in menuArr.indices) {
                if (checkedPos == i) menu.add(0, i + 1, 0, menuArr[i]).setCheckable(true).isChecked = true
                else menu.add(0, i + 1, 0, menuArr[i])
            }
            setOnMenuItemClickListener(listener)
            show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_settings)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        refreshListSettings()

        lvSettings.adapter = adapterSettings
        lvSettings.setOnItemClickListener { parent, view, position, id ->
            val switch = view.findViewById<Switch>(R.id.Switch)
            val isChecked: Boolean = switch.isChecked
            switch.setChecked(!isChecked)
            when (position) {
                0 -> showPopupMenu(view, answerDisplayTypeArr, Prefs.answerDisplayType - 1) { item ->
                    answerDisplayType = item.itemId
                    Prefs.answerDisplayType = item.itemId
                    refreshListSettings()
                    true
                }

                1 -> {
                    saveQuiz = !isChecked
                    Prefs.saveQuiz = !isChecked
                }
            }
        }

//        tvCopyright.text = getString(R.string.copyright_text, Calendar.getInstance().get(Calendar.YEAR))
    }

    private fun refreshListSettings() {
        if (!mListSettings.isEmpty()) mListSettings.clear()

        mListSettings.add(ListBean(0, R.string.answer_display_type, R.string.answer_display_type_tips, answerDisplayTypeArr[Prefs.answerDisplayType - 1], 0, null))
        mListSettings.add(ListBean(0, R.string.save_quiz, R.string.save_quiz_tips, null, 0, Prefs.saveQuiz))

        if (adapterSettings == null) {
            adapterSettings = MyLvAdapter(mListSettings)
        } else adapterSettings!!.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        //存放设置
        var answerDisplayType = Prefs.answerDisplayType
        var saveQuiz = Prefs.saveQuiz
    }
}
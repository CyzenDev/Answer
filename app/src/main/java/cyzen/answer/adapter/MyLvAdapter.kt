package cyzen.answer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Switch
import android.widget.TextView
import cyzen.answer.R
import cyzen.answer.bean.ListBean
import cyzen.utils.gone
import cyzen.utils.show

open class MyLvAdapter(val mData: List<ListBean>?) : BaseAdapter() {

    val TYPE_CATEGORY = 0
    val TYPE_CONTENT = 1
    val TYPE_ALERT = 2

    override fun getCount(): Int = mData?.size ?: 0

    override fun getItem(position: Int): Any = mData?.get(position) ?: position

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemViewType(position: Int): Int {
        return if (mData!![position].category != null) TYPE_CATEGORY
        else if (mData[position].alert != null) TYPE_ALERT
        else TYPE_CONTENT
    }

    override fun getViewTypeCount(): Int = 3

    override fun isEnabled(position: Int): Boolean = getItemViewType(position) == TYPE_CONTENT

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        val holder: ViewHolder
        val itemType = getItemViewType(position)
        val bean = mData!![position]

        if (view == null) {
            holder = ViewHolder()
            when (itemType) {
                TYPE_CATEGORY -> {
                    view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_category, parent, false)
                    holder.category = view.findViewById(R.id.category)
                }

                TYPE_ALERT -> {
                    view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_alert, parent, false)
                    holder.alert = view.findViewById(R.id.item_alert)
                }

                else -> {
                    view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
                    holder.image = view.findViewById(R.id.image)
                    holder.title = view.findViewById(R.id.title)
                    holder.content = view.findViewById(R.id.value)
                    holder.contentEnd = view.findViewById(R.id.value_end)
                    holder.imageEnd = view.findViewById(R.id.image_end)
                    holder.switch = view.findViewById(R.id.Switch)
                }
            }
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        var obj: Any?

        when (itemType) {
            TYPE_CATEGORY -> {
                obj = bean.category
                if (obj is Int) {
                    holder.category!!.setText(obj)
                } else if (obj is CharSequence) {
                    holder.category!!.text = obj
                }
            }

            TYPE_ALERT -> {
                obj = bean.alert
                if (obj is Int) {
                    holder.alert!!.setText(obj)
                } else if (obj is CharSequence) {
                    holder.alert!!.text = obj
                }
            }

            TYPE_CONTENT -> {
                if (bean.imageStart == 0) {
                    holder.image!!.gone()
                } else {
                    holder.image!!.setBackgroundResource(bean.imageStart)
                    holder.image!!.show()
                }
                obj = bean.title
                if (obj == null) {
                    holder.title!!.gone()
                } else {
                    if (obj is Int) {
                        holder.title!!.setText(obj)
                    } else if (obj is CharSequence) {
                        holder.title!!.text = obj
                    }
                    holder.title!!.show()
                }

                obj = bean.content
                if (obj == null) {
                    holder.content!!.gone()
                } else {
                    if (obj is Int) {
                        holder.content!!.setText(obj)
                    } else if (obj is CharSequence) {
                        holder.content!!.text = obj
                    }
                    holder.content!!.show()
                }

                obj = bean.contentEnd
                if (obj == null) {
                    holder.contentEnd!!.gone()
                } else {
                    if (obj is Int) {
                        holder.contentEnd!!.setText(obj)
                    } else if (obj is CharSequence) {
                        holder.contentEnd!!.text = obj
                    }
                    holder.contentEnd!!.show()
                }

                if (bean.imageEnd == 0) {
                    holder.imageEnd!!.gone()
                } else {
                    holder.imageEnd!!.setBackgroundResource(bean.imageEnd)
                    holder.imageEnd!!.show()
                }

                obj = bean.checked
                if (obj == null) {
                    holder.switch!!.gone()
                } else {
                    holder.switch!!.setChecked(obj)
                    holder.switch!!.show()
                }
            }
        }

        return view!!
    }

    inner class ViewHolder {
        var category: TextView? = null
        var image: View? = null
        var title: TextView? = null
        var content: TextView? = null
        var contentEnd: TextView? = null
        var alert: TextView? = null
        var imageEnd: View? = null
        var switch: Switch? = null
    }
}
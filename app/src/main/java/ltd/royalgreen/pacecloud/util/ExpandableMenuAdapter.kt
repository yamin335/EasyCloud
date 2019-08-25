package ltd.royalgreen.pacecloud.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import ltd.royalgreen.pacecloud.R
import java.util.HashMap
import kotlinx.android.synthetic.main.side_menu_row_item.view.*
import kotlinx.android.synthetic.main.side_submenu_row_item.view.*

class ExpandableMenuAdapter(
    private val mContext: Context,
    private val mListDataHeader: List<ExpandableMenuModel>,
    private val mListDataChild: HashMap<ExpandableMenuModel, List<String>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return this.mListDataHeader.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return if (groupPosition != 1) {
            mListDataChild[mListDataHeader[groupPosition]]?.size?:0
        } else {
            0
        }
    }

    override fun getGroup(groupPosition: Int): Any {
        return this.mListDataHeader[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any? {
        return this.mListDataChild[this.mListDataHeader[groupPosition]]?.get(childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        val headerTitle = getGroup(groupPosition) as ExpandableMenuModel
        val convertedView = if (convertView == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.side_menu_row_item, null)
        } else {
            convertView
        }

        when (groupPosition) {
            1 -> convertedView.indicator.visibility = View.VISIBLE
            else -> convertedView.indicator.visibility = View.INVISIBLE
        }

        if (isExpanded) {
            convertedView.indicator.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_arrow_drop_up_gray_24dp))
        } else {
            convertedView.indicator.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_arrow_drop_down_gray_24dp))
        }
        convertedView.headerTitle.text = headerTitle.iconName
        convertedView.headerIcon.setImageResource(headerTitle.iconImg)
        return convertedView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val childText = getChild(groupPosition, childPosition) as String
        val convertedView = if (convertView == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.side_submenu_row_item, null)
        } else {
            convertView
        }
        convertedView.subMenuTitle.text = childText
        return convertedView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}
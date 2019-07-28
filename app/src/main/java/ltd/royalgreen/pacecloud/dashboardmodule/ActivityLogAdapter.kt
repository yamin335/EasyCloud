package ltd.royalgreen.pacecloud.dashboardmodule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import kotlinx.android.synthetic.main.dash_user_log_row.view.*
import ltd.royalgreen.pacecloud.R

class ActivityLogAdapter : PagedListAdapter<CloudActivityLog, ActivityLogViewHolder>(ActivityLogDiffUtilCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityLogViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.dash_user_log_row, parent, false)
    return ActivityLogViewHolder(view)
  }

  override fun onBindViewHolder(holder: ActivityLogViewHolder, position: Int) {
    val item = getItem(position)
//    val resources = holder.itemView.context.resources
    holder.itemView.logTitle.text = item?.userActivityLogDetails
  }
}

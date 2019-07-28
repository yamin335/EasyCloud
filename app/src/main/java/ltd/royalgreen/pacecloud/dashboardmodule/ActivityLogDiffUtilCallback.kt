package ltd.royalgreen.pacecloud.dashboardmodule

import androidx.recyclerview.widget.DiffUtil

class ActivityLogDiffUtilCallback : DiffUtil.ItemCallback<CloudActivityLog>() {

  override fun areItemsTheSame(oldItem: CloudActivityLog, newItem: CloudActivityLog): Boolean {
    return oldItem.userActivityLogId == newItem.userActivityLogId
  }

  override fun areContentsTheSame(oldItem: CloudActivityLog, newItem: CloudActivityLog): Boolean {
  return oldItem.userActivityLogId == newItem.userActivityLogId
      && oldItem.userActivityLogDetails == newItem.userActivityLogDetails
      && oldItem.userActivityLogType == newItem.userActivityLogType
  }
}

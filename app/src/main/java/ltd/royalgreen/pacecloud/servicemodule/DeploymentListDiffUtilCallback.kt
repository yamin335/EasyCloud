package ltd.royalgreen.pacecloud.servicemodule

import androidx.recyclerview.widget.DiffUtil

class DeploymentListDiffUtilCallback : DiffUtil.ItemCallback<Deployment>() {

  override fun areItemsTheSame(oldItem: Deployment, newItem: Deployment): Boolean {
    return oldItem.deploymentId == newItem.deploymentId
  }

  override fun areContentsTheSame(oldItem: Deployment, newItem: Deployment): Boolean {
  return oldItem.deploymentId?.toLong() == newItem.deploymentId?.toLong()
      && oldItem.deploymentName == newItem.deploymentName
  }
}

package ltd.royalgreen.pacecloud.servicemodule

import androidx.recyclerview.widget.DiffUtil

class VMListDiffUtilCallback : DiffUtil.ItemCallback<VM>() {

  override fun areItemsTheSame(oldItem: VM, newItem: VM): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: VM, newItem: VM): Boolean {
  return oldItem.name == newItem.name
      && oldItem.hostName == newItem.hostName
      && oldItem.id == newItem.id
  }
}

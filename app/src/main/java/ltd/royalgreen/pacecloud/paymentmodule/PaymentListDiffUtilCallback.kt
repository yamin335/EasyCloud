package ltd.royalgreen.pacecloud.paymentmodule

import androidx.recyclerview.widget.DiffUtil

class PaymentListDiffUtilCallback : DiffUtil.ItemCallback<BilCloudUserLedger>() {

  override fun areItemsTheSame(oldItem: BilCloudUserLedger, newItem: BilCloudUserLedger): Boolean {
    return oldItem.cloudUserLedgerId?.toLong() == newItem.cloudUserLedgerId?.toLong()
  }

  override fun areContentsTheSame(oldItem: BilCloudUserLedger, newItem: BilCloudUserLedger): Boolean {
  return oldItem.cloudUserLedgerId?.toLong() == newItem.cloudUserLedgerId?.toLong()
      && oldItem.cloudUserId?.toLong() == newItem.cloudUserId?.toLong()
      && oldItem.transactionDate == newItem.transactionDate
  }
}

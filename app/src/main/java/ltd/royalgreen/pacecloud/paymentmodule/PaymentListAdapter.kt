package ltd.royalgreen.pacecloud.paymentmodule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import kotlinx.android.synthetic.main.payment_history_row.view.*
import ltd.royalgreen.pacecloud.R
import java.math.BigDecimal
import java.math.RoundingMode



class PaymentListAdapter : PagedListAdapter<BilCloudUserLedger, PaymentListViewHolder>(PaymentListDiffUtilCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentListViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.payment_history_row, parent, false)
    return PaymentListViewHolder(view)
  }

  override fun onBindViewHolder(holder: PaymentListViewHolder, position: Int) {
      val item = getItem(position)
//    val resources = holder.itemView.context.resources

      holder.itemView.paymentTitle.text = item?.particulars
//      holder.itemView.config.text = item?.numberOfCpus.toString()+" CPU, "+item?.memorySize?.toDouble()?.div(1024.0).toString().split(".")[0]+" GB Memory"
      holder.itemView.balance.text = BigDecimal(item?.balanceAmount?.toDouble()?:0.00).setScale(4, RoundingMode.HALF_UP).toString()
      holder.itemView.debit.text = BigDecimal(item?.debitAmount?.toDouble()?:0.00).setScale(4, RoundingMode.HALF_UP).toString()
      holder.itemView.credit.text = BigDecimal(item?.creditAmount?.toDouble()?:0.00).setScale(4, RoundingMode.HALF_UP).toString()

      if (item?.transactionDate != null && item.transactionDate.contains("T")) {
          val tempStringArray = item.transactionDate.split("T")
          var tempString1 = tempStringArray[1]
          if (tempString1.contains(".")){
              tempString1 = tempString1.split(".")[0]
              tempString1 = if(tempString1.split(":")[0].toInt()>=12) "$tempString1 PM" else "$tempString1 AM"
          } else {
              tempString1 = if(tempString1.split(":")[0].toInt()>=12) "$tempString1 PM" else "$tempString1 AM"
          }
          holder.itemView.date.text = tempStringArray[0]+"\n"+tempString1
      }
  }
}

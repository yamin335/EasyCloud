package ltd.royalgreen.pacecloud.servicemodule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.service_vm_list_action_popup_menu.view.*
import kotlinx.android.synthetic.main.service_vm_row.view.*
import ltd.royalgreen.pacecloud.R
import java.math.BigDecimal
import java.math.RoundingMode

class VMListAdapter internal constructor(private val vmList: List<VM>, private val callBack: ActionCallback) : RecyclerView.Adapter<VMListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.service_vm_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = vmList[position]
        val context = holder.itemView.context
        val resources = holder.itemView.context.resources

        //Custom popup menu for action button
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewPopupMenu = inflater.inflate(R.layout.service_vm_list_action_popup_menu, null)
        val actionMenuPopupWindow = PopupWindow(viewPopupMenu, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        actionMenuPopupWindow.isOutsideTouchable = true
        actionMenuPopupWindow.setOnDismissListener {
            //        Toast.makeText(context, "Dismissed!!!", Toast.LENGTH_LONG).show()
        }

        val popupMenu = actionMenuPopupWindow.contentView
        popupMenu.menuAttachVolume.setOnClickListener {
            callBack.onAttachDetach()
            actionMenuPopupWindow.dismiss()
        }
        popupMenu.menuStartStop.labelStartStop.text = if (item.status.equals("Running", true)) {
            "Stop"
        } else {
            "Start"
        }

        popupMenu.menuStartStop.switchStartStop.isChecked = item.status.equals("Running", true)

        popupMenu.menuStartStop.setOnClickListener {
            callBack.onStartStop()
            if (item.status.equals("Running", true)) {
                it.switchStartStop.isChecked = false
                it.labelStartStop.text = "Start"
                item.status = "Stopped"
//              holder.itemView.status.setTextColor(context.resources.getColor(R.color.colorRed))
                holder.itemView.statusIcon.setColorFilter(context.resources.getColor(R.color.colorRed))
            } else {
                it.switchStartStop.isChecked = true
                it.labelStartStop.text = "Stop"
                item.status = "Running"
//              holder.itemView.status.setTextColor(context.resources.getColor(R.color.textColor1))
                holder.itemView.statusIcon.setColorFilter(context.resources.getColor(R.color.textColor1))
            }
//          holder.itemView.status.text = item?.status
            actionMenuPopupWindow.dismiss()
        }

        popupMenu.menuReboot.setOnClickListener {
            callBack.onReboot()
            actionMenuPopupWindow.dismiss()
        }

        popupMenu.menuTerminate.setOnClickListener {
            callBack.onTerminate()
            actionMenuPopupWindow.dismiss()
        }
        actionMenuPopupWindow.elevation = 16F

        holder.itemView.vmName.text = item.vmName
//      holder.itemView.config.text = item?.numberOfCpus.toString()+" CPU, "+item?.memorySize?.toDouble()?.div(1024.0).toString().split(".")[0]+" GB Memory"
        holder.itemView.nodeHour.text = BigDecimal(item?.nodeHours?.toDouble()?:0.00).setScale(4, RoundingMode.HALF_UP).toString()
        holder.itemView.cloudCost.text = BigDecimal(item?.costPerHour?.toDouble()?:0.00).setScale(3, RoundingMode.HALF_UP).toString()
//      holder.itemView.status.text = item?.status
        if (item.status.equals("Running", true)) {
//          holder.itemView.status.setTextColor(context.resources.getColor(R.color.textColor1))
            holder.itemView.statusIcon.setColorFilter(context.resources.getColor(R.color.textColor1))
        } else {
//          holder.itemView.status.setTextColor(context.resources.getColor(R.color.colorRed))
            holder.itemView.statusIcon.setColorFilter(context.resources.getColor(R.color.colorRed))
        }

        holder.itemView.action.setOnClickListener {
            actionMenuPopupWindow.showAsDropDown(holder.itemView.action)
        }
    }

    override fun getItemCount(): Int {
        return vmList.size
    }

    interface ActionCallback{
        fun onStartStop()
        fun onAttachDetach()
        fun onReboot()
        fun onTerminate()
    }
}

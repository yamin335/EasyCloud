package ltd.royalgreen.pacecloud.servicemodule

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.paging.PagedListAdapter
import kotlinx.android.synthetic.main.service_vm_list_action_popup_menu.view.*
import kotlinx.android.synthetic.main.service_vm_row.view.*
import ltd.royalgreen.pacecloud.R
import java.math.BigDecimal
import java.math.RoundingMode



class VMListAdapter(val context: Context) : PagedListAdapter<VM, VMListViewHolder>(VMListDiffUtilCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VMListViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.service_vm_row, parent, false)
    return VMListViewHolder(view)
  }

  override fun onBindViewHolder(holder: VMListViewHolder, position: Int) {
      val item = getItem(position)
//    val resources = holder.itemView.context.resources

      // Custom popup menu for action button
      val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
      val viewPopupMenu = inflater.inflate(R.layout.service_vm_list_action_popup_menu, null)
      val actionMenuPopupWindow = PopupWindow(viewPopupMenu, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      actionMenuPopupWindow.isOutsideTouchable = true
      actionMenuPopupWindow.setOnDismissListener {
          //        Toast.makeText(context, "Dismissed!!!", Toast.LENGTH_LONG).show()
      }
      val popupMenu = actionMenuPopupWindow.contentView
      popupMenu.menuAttachVolume.setOnClickListener {
          Toast.makeText(context, "Attach Volume Clicked!!!", Toast.LENGTH_LONG).show()
          actionMenuPopupWindow.dismiss()
      }
      popupMenu.menuStartStop.labelStartStop.text = if (item?.status.equals("Running", true)) {
          "Stop"
      } else {
          "Start"
      }

      popupMenu.menuStartStop.switchStartStop.isChecked = item?.status.equals("Running", true)

      popupMenu.menuStartStop.setOnClickListener {
          if (item?.status.equals("Running", true)) {
              it.switchStartStop.isChecked = false
              it.labelStartStop.text = "Start"
              item?.status = "Stopped"
//              holder.itemView.status.setTextColor(context.resources.getColor(R.color.colorRed))
              holder.itemView.statusIcon.setColorFilter(context.resources.getColor(R.color.colorRed))
          } else {
              it.switchStartStop.isChecked = true
              it.labelStartStop.text = "Stop"
              item?.status = "Running"
//              holder.itemView.status.setTextColor(context.resources.getColor(R.color.textColor1))
              holder.itemView.statusIcon.setColorFilter(context.resources.getColor(R.color.textColor1))
          }
//          holder.itemView.status.text = item?.status
          actionMenuPopupWindow.dismiss()
      }

      popupMenu.menuReboot.setOnClickListener {
          Toast.makeText(context, "Reboot!!!", Toast.LENGTH_LONG).show()
          actionMenuPopupWindow.dismiss()
      }

      popupMenu.menuTerminate.setOnClickListener {
          Toast.makeText(context, "Terminate!!!", Toast.LENGTH_LONG).show()
          actionMenuPopupWindow.dismiss()
      }
      actionMenuPopupWindow.elevation = 16F

      holder.itemView.vmName.text = item?.vmName
//      holder.itemView.config.text = item?.numberOfCpus.toString()+" CPU, "+item?.memorySize?.toDouble()?.div(1024.0).toString().split(".")[0]+" GB Memory"
      holder.itemView.nodeHour.text = BigDecimal(item?.nodeHours?.toDouble()?:0.00).setScale(4, RoundingMode.HALF_UP).toString()
      holder.itemView.cloudCost.text = BigDecimal(item?.costPerHour?.toDouble()?:0.00).setScale(3, RoundingMode.HALF_UP).toString()
//      holder.itemView.status.text = item?.status
      if (item?.status.equals("Running", true)) {
//          holder.itemView.status.setTextColor(context.resources.getColor(R.color.textColor1))
          holder.itemView.statusIcon.setColorFilter(context.resources.getColor(R.color.textColor1))
      } else {
//          holder.itemView.status.setTextColor(context.resources.getColor(R.color.colorRed))
          holder.itemView.statusIcon.setColorFilter(context.resources.getColor(R.color.colorRed))
      }

      holder.itemView.action.setOnClickListener {
          actionMenuPopupWindow.showAsDropDown(holder.itemView.action)

//      val menuBuilder = MenuBuilder(context)
//      val menuInflater = MenuInflater(context)
//      menuInflater.inflate(R.menu.vm_list_action, menuBuilder)
//      val menuHelper = MenuPopupHelper(context, menuBuilder, it)
//      menuHelper.setForceShowIcon(true)
//      menuBuilder.setCallback(object : MenuBuilder.Callback {
//        override fun onMenuModeChange(menu: MenuBuilder?) {
//
//        }
//
//        override fun onMenuItemSelected(menu: MenuBuilder?, item: MenuItem?): Boolean {
//          when(item?.itemId) {
//            R.id.stop -> {
//              if (item.title == "Stop") {
//                item.title = "Start"
//                item.icon.setTint(context.resources.getColor(R.color.colorGreen))
//              } else {
//                item.title = "Stop"
//                item.icon.setTint(context.resources.getColor(R.color.colorRed))
//              }
//              Toast.makeText(context, "stop clicked!", Toast.LENGTH_LONG).show()
//              return true
//            }
//            R.id.reboot -> {
//              Toast.makeText(context, "reboot clicked!", Toast.LENGTH_LONG).show()
//              return true
//            }
//            R.id.terminate -> {
//              Toast.makeText(context, "terminate clicked!", Toast.LENGTH_LONG).show()
//              return true
//            }
//            R.id.view -> {
//              Toast.makeText(context, "view clicked!", Toast.LENGTH_LONG).show()
//              return true
//            }
//            else -> return false
//          }
//        }
//
//      })
//      menuHelper.show()


//      val popup = PopupMenu(context, it)
//      popup.menuInflater.inflate(R.menu.vm_list_action, popup.menu)
//      popup.setOnMenuItemClickListener { item ->
//        when(item.itemId) {
//          R.id.stop -> {
//            Toast.makeText(context, "stop clicked!", Toast.LENGTH_LONG).show()
//            return@setOnMenuItemClickListener true
//          }
//          R.id.reboot -> {
//            Toast.makeText(context, "reboot clicked!", Toast.LENGTH_LONG).show()
//            return@setOnMenuItemClickListener true
//          }
//          R.id.terminate -> {
//            Toast.makeText(context, "terminate clicked!", Toast.LENGTH_LONG).show()
//            return@setOnMenuItemClickListener true
//          }
//          R.id.view -> {
//            Toast.makeText(context, "view clicked!", Toast.LENGTH_LONG).show()
//            return@setOnMenuItemClickListener true
//          }
//          else -> return@setOnMenuItemClickListener false
//        }
//      }
//      popup.setForceShowIcon(true)
//      popup.show()
      }


  }
}

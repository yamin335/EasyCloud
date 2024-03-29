package ltd.royalgreen.pacecloud.servicemodule


import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.service_deployment_row.view.*

import ltd.royalgreen.pacecloud.R

import ltd.royalgreen.pacecloud.util.RecyclerItemDivider

class DeploymentListAdapter(private val callBack: VMListAdapter.ActionCallback,
                            private val renameSuccessCallback: RenameSuccessCallback,
                            private val fragmentManager: FragmentManager,
                            private val fullName: String?,
                            viewModel: ServiceFragmentViewModel,
                            lifecycleOwner: LifecycleOwner) : PagedListAdapter<Deployment, DeploymentListViewHolder>(DeploymentListDiffUtilCallback()) {

    val parentViewModel = viewModel
    val owner = lifecycleOwner

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeploymentListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.service_deployment_row, parent, false)
        return DeploymentListViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeploymentListViewHolder, position: Int) {
        val item = getItem(position)
        val context = holder.itemView.context
        holder.itemView.edit.setOnClickListener {
            val renameDialog = DeploymentRenameDialog(object : DeploymentRenameDialog.RenameCallback {
                override fun onSavePressed(renamedValue: String) {
                    parentViewModel.renameDeployment(item?.deploymentId, renamedValue).observe(owner, Observer {
                        if (it) {
                            renameSuccessCallback.onRenamed()
                        }
                    })
                }
            }, fullName, item?.deploymentName)
            renameDialog.isCancelable = false
            renameDialog.show(fragmentManager, "#rename_dialog")
        }

        holder.itemView.deploymentName.text = item?.deploymentName.toString()

        item?.vmLists?.let {
            val vmAdapter = VMListAdapter(it, callBack, fragmentManager)
            holder.itemView.vmRecycler.layoutManager = LinearLayoutManager(context)
            holder.itemView.vmRecycler.addItemDecoration(RecyclerItemDivider(context, LinearLayoutManager.VERTICAL, 8))
            holder.itemView.vmRecycler.adapter = vmAdapter
        }

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

    interface RenameSuccessCallback{
        fun onRenamed()
    }
}

package ltd.royalgreen.pacecloud.servicemodule

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.service_vm_list_action_popup_menu.view.*
import kotlinx.android.synthetic.main.service_vm_row.view.*
import kotlinx.android.synthetic.main.toast_custom_red.view.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.mainactivitymodule.ConfirmationCheckingDialog
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.LiveDataCallAdapterFactory
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

class VMListAdapter internal constructor(private val vmList: List<VM>, private val callBack: ActionCallback, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<VMListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LifecycleOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun onAppear() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun onDisappear() {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }

    private val client = OkHttpClient().newBuilder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .callTimeout(5, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService = Retrofit.Builder()
        .client(client)
        .baseUrl("http://123.136.26.98:8081")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .build()
        .create(ApiService::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.service_vm_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = vmList[position]
        val context = holder.itemView.context
//        val resources = holder.itemView.context.resources

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

        fun enablePopupMenu() {
            popupMenu.menuStartStop.isEnabled = true
            popupMenu.switchStartStop.isEnabled = true
            popupMenu.labelStartStop.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryLight))

            popupMenu.menuReboot.isEnabled = true
            popupMenu.labelReboot.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryLight))

            popupMenu.menuNote.isEnabled = true
            popupMenu.labelNote.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryLight))
        }

        fun disablePopupMenu() {
            popupMenu.menuStartStop.isEnabled = false
            popupMenu.switchStartStop.isEnabled = false
            popupMenu.labelStartStop.setTextColor(ContextCompat.getColor(context, R.color.colorGrayLight))

            popupMenu.menuReboot.isEnabled = false
            popupMenu.labelReboot.setTextColor(ContextCompat.getColor(context, R.color.colorGrayLight))

            popupMenu.menuNote.isEnabled = false
            popupMenu.labelNote.setTextColor(ContextCompat.getColor(context, R.color.colorGrayLight))
        }

        var vmStartStatus: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        vmStartStatus.observe(holder, androidx.lifecycle.Observer {
            if (it) {
                item.status = "Running"
                holder.itemView.statusIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorGreenTheme))
                holder.itemView.loader.visibility = View.GONE
                popupMenu.labelStartStop.text = "Stop"
                popupMenu.switchStartStop.isChecked = true
                enablePopupMenu()
                val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
                val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val toastView = layoutInflater.inflate(R.layout.toast_custom_green, null)
                toastView.message.text = context.getString(R.string.vm_started_successfully)
                toast.view = toastView
                toast.show()
            } else {
                holder.itemView.loader.visibility = View.GONE
                enablePopupMenu()
                val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
                val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val toastView = layoutInflater.inflate(R.layout.toast_custom_red, null)
                toastView.message.text = context.getString(R.string.vm_can_not_be_started)
                toast.view = toastView
                toast.show()
            }
        })

        var vmStopStatus: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        vmStopStatus.observe(holder, androidx.lifecycle.Observer {
            if (it) {
                item.status = "Stopped"
                holder.itemView.statusIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorRed))
                holder.itemView.loader.visibility = View.GONE
                popupMenu.switchStartStop.isChecked = false
                popupMenu.labelStartStop.text = "Start"
                enablePopupMenu()
                val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
                val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val toastView = layoutInflater.inflate(R.layout.toast_custom_green, null)
                toastView.message.text = context.getString(R.string.vm_stopped_successfully)
                toast.view = toastView
                toast.show()
            } else {
                holder.itemView.loader.visibility = View.GONE
                enablePopupMenu()
                val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
                val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val toastView = layoutInflater.inflate(R.layout.toast_custom_red, null)
                toastView.message.text = context.getString(R.string.vm_can_not_be_stopped)
                toast.view = toastView
                toast.show()
            }
        })

        var vmRebootStatus: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        vmRebootStatus.observe(holder, androidx.lifecycle.Observer {
            if (it) {
                holder.itemView.loader.visibility = View.GONE
                enablePopupMenu()
                val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
                val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val toastView = layoutInflater.inflate(R.layout.toast_custom_green, null)
                toastView.message.text = context.getString(R.string.vm_rebooted_successfully)
                toast.view = toastView
                toast.show()
            } else {
                holder.itemView.loader.visibility = View.GONE
                enablePopupMenu()
                val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
                val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val toastView = layoutInflater.inflate(R.layout.toast_custom_red, null)
                toastView.message.text = context.getString(R.string.vm_can_not_be_rebooted)
                toast.view = toastView
                toast.show()
            }
        })

        var vmNoteStatus: MutableLiveData<String> = MutableLiveData<String>()
        vmNoteStatus.observe(holder, androidx.lifecycle.Observer {
            if (it != "^^^^^") {
                item.vmNote = it
                holder.itemView.loader.visibility = View.GONE
                enablePopupMenu()
                val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
                val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val toastView = layoutInflater.inflate(R.layout.toast_custom_green, null)
                toastView.message.text = context.getString(R.string.vm_note_saved)
                toast.view = toastView
                toast.show()
            } else {
                holder.itemView.loader.visibility = View.GONE
                enablePopupMenu()
                val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
                val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val toastView = layoutInflater.inflate(R.layout.toast_custom_red, null)
                toastView.message.text = context.getString(R.string.vm_note_not_saved)
                toast.view = toastView
                toast.show()
            }
        })

        popupMenu.menuStartStop.setOnClickListener {
            if (item.status.equals("Running", true)) {
                val confirmationDialog = ConfirmationCheckingDialog(object : ConfirmationCheckingDialog.ConfirmationCallback {
                    override fun onYesPressed() {
                        holder.itemView.loader.visibility = View.VISIBLE
                        disablePopupMenu()
                        val firstObject = JsonObject().apply {
                            addProperty("UserID", item.userId)
                            addProperty("id", item.id)
                            addProperty("IsTrue", true)
                            addProperty("CostPerHour", item.costPerHour)
                        }
                        val secondObject = JsonObject().apply {
                            addProperty("resourceType", "VIRTUAL_MACHINE")
                            add("executionSpecs", JsonArray())
                            val jsonArray = JsonArray().apply {
                                val jsonObject = JsonObject().apply {
                                    addProperty("id", item.id)
                                }
                                add(jsonObject)
                            }
                            add("executionResources", jsonArray)
                        }
                        val thirdObject = JsonObject().apply {
                            addProperty("acknowledgedByUser", true)
                        }
                        val param = JsonArray().apply {
                            add(firstObject)
                            add(secondObject)
                            add(thirdObject)
                        }

                        val handler = CoroutineExceptionHandler { _, exception ->
                            exception.printStackTrace()
                            vmStopStatus.postValue(false)
                            callBack.onStop(false)
                        }

                        CoroutineScope(Dispatchers.Default).launch(handler) {
                            val response = apiService.cloudvmstartstop(param).execute()
                            when (val apiResponse = ApiResponse.create(response)) {
                                is ApiSuccessResponse -> {
                                    if (JsonParser().parse(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("resstate").asBoolean) {
                                        delay(45000L)
                                        vmStopStatus.postValue(true)
                                        callBack.onStop(true)
                                    } else {
                                        callBack.onStop(false)
                                        vmStopStatus.postValue(false)
                                    }
                                }
                                is ApiEmptyResponse -> {
                                    callBack.onStop(false)
                                    vmStopStatus.postValue(false)
                                }
                                is ApiErrorResponse -> {
                                    callBack.onStop(false)
                                    vmStopStatus.postValue(false)
                                }
                            }
                        }
                    }
                }, "Are you sure to stop ${item.vmName?:"this"} VM ?")
                confirmationDialog.isCancelable = false
                confirmationDialog.show(fragmentManager, "#conf_dialog_1")
            } else {
                val confirmationDialog = ConfirmationCheckingDialog(object : ConfirmationCheckingDialog.ConfirmationCallback {
                    override fun onYesPressed() {
                        holder.itemView.loader.visibility = View.VISIBLE
                        disablePopupMenu()
                        val firstObject = JsonObject().apply {
                            addProperty("UserID", item.userId)
                            addProperty("id", item.id)
                            addProperty("IsTrue", false)
                            addProperty("CostPerHour", item.costPerHour)
                        }
                        val secondObject = JsonObject().apply {
                            addProperty("resourceType", "VIRTUAL_MACHINE")
                            add("executionSpecs", JsonArray())
                            val jsonArray = JsonArray().apply {
                                val jsonObject = JsonObject().apply {
                                    addProperty("id", item.id)
                                }
                                add(jsonObject)
                            }
                            add("executionResources", jsonArray)
                        }
                        val thirdObject = JsonObject().apply {
                            addProperty("acknowledgedByUser", true)
                        }
                        val param = JsonArray().apply {
                            add(firstObject)
                            add(secondObject)
                            add(thirdObject)
                        }

                        val handler = CoroutineExceptionHandler { _, exception ->
                            exception.printStackTrace()
                            vmStartStatus.postValue(false)
                            callBack.onStart(false)
                        }

                        CoroutineScope(Dispatchers.Default).launch(handler) {
                            val response = apiService.cloudvmstartstop(param).execute()
                            when (val apiResponse = ApiResponse.create(response)) {
                                is ApiSuccessResponse -> {
                                    if (JsonParser().parse(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("resstate").asBoolean) {
                                        delay(70000L)
                                        vmStartStatus.postValue(true)
                                        callBack.onStart(true)
                                    } else {
                                        vmStartStatus.postValue(false)
                                        callBack.onStart(false)
                                    }
                                }
                                is ApiEmptyResponse -> {
                                    vmStartStatus.postValue(false)
                                    callBack.onStart(false)
                                }
                                is ApiErrorResponse -> {
                                    vmStartStatus.postValue(false)
                                    callBack.onStart(false)
                                }
                            }
                        }
                    }
                }, "Are you sure to start ${item.vmName?:"this"} VM ?")
                confirmationDialog.isCancelable = false
                confirmationDialog.show(fragmentManager, "#conf_dialog_2")
            }
            actionMenuPopupWindow.dismiss()
        }

        popupMenu.menuReboot.setOnClickListener {
            val confirmationDialog = ConfirmationCheckingDialog(object : ConfirmationCheckingDialog.ConfirmationCallback {
                override fun onYesPressed() {
                    holder.itemView.loader.visibility = View.VISIBLE
                    disablePopupMenu()
                    val firstObject = JsonObject().apply {
                        addProperty("UserID", item.userId)
                        addProperty("id", item.id)
                        addProperty("IsTrue", true)
                    }
                    val secondObject = JsonObject().apply {
                        addProperty("resourceType", "VIRTUAL_MACHINE")
                        add("executionSpecs", JsonArray())
                        val jsonArray = JsonArray().apply {
                            val jsonObject = JsonObject().apply {
                                addProperty("id", item.id)
                            }
                            add(jsonObject)
                        }
                        add("executionResources", jsonArray)
                    }
                    val param = JsonArray().apply {
                        add(firstObject)
                        add(secondObject)
                    }

                    val handler = CoroutineExceptionHandler { _, exception ->
                        exception.printStackTrace()
                        vmRebootStatus.postValue(false)
                        callBack.onReboot()
                    }

                    CoroutineScope(Dispatchers.Default).launch(handler) {
                        val response = apiService.cloudvmreboot(param).execute()
                        when (val apiResponse = ApiResponse.create(response)) {
                            is ApiSuccessResponse -> {
                                if (JsonParser().parse(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get(
                                        "resstate"
                                    ).asBoolean
                                ) {
//                                        delay(50000L)
                                    vmRebootStatus.postValue(true)
                                    callBack.onReboot()
                                } else {
                                    callBack.onReboot()
                                    vmRebootStatus.postValue(false)
                                }
                            }
                            is ApiEmptyResponse -> {
                                callBack.onReboot()
                                vmRebootStatus.postValue(false)
                            }
                            is ApiErrorResponse -> {
                                callBack.onReboot()
                                vmRebootStatus.postValue(false)
                            }
                        }
                    }
                }
            }, "Are you sure to reboot ${item.vmName?:"this"} VM ?")
            confirmationDialog.isCancelable = false
            confirmationDialog.show(fragmentManager, "#conf_dialog_3")
            actionMenuPopupWindow.dismiss()
        }

        popupMenu.menuTerminate.setOnClickListener {
            callBack.onTerminate()
            actionMenuPopupWindow.dismiss()
        }

        popupMenu.menuNote.setOnClickListener {
            val vmNoteDialog = VMNoteDialog(object : VMNoteDialog.NoteCallback{
                override fun onNoteSaved(noteValue: String) {
                    if (isNetworkAvailable(context)) {
                        holder.itemView.loader.visibility = View.VISIBLE
                        disablePopupMenu()
                        val jsonObject = JsonObject().apply {
                            addProperty("vmNote", noteValue)
                            addProperty("id", item.id)
                        }
                        val param = JsonArray().apply {
                            add(jsonObject)
                        }

                        val handler = CoroutineExceptionHandler { _, exception ->
                            exception.printStackTrace()
                            vmNoteStatus.postValue("^^^^^")
                        }

                        CoroutineScope(Dispatchers.Default).launch(handler) {
                            val response = apiService.updatevmnote(param).execute()
                            when (val apiResponse = ApiResponse.create(response)) {
                                is ApiSuccessResponse -> {
                                    if (JsonParser().parse(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("resstate").asBoolean) {
                                        vmNoteStatus.postValue(noteValue)
                                        callBack.onNote()
                                    } else {
                                        vmNoteStatus.postValue("^^^^^")
                                    }
                                }
                                is ApiEmptyResponse -> {
                                    vmNoteStatus.postValue("^^^^^")
                                }
                                is ApiErrorResponse -> {
                                    vmNoteStatus.postValue("^^^^^")
                                }
                            }
                        }
                    } else {
                        val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
                        val toastInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                        val toastView = toastInflater.inflate(R.layout.toast_custom_red, null)
                        toastView.message.text = context.getString(R.string.net_error_msg)
                        toast.view = toastView
                        toast.show()
                    }
                }
            }, item.vmNote)
            vmNoteDialog.isCancelable = false
            vmNoteDialog.show(fragmentManager, "#vm_note_dialog")
            actionMenuPopupWindow.dismiss()
        }
        actionMenuPopupWindow.elevation = 16F

        holder.itemView.vmName.text = item.vmName
//      holder.itemView.config.text = item?.numberOfCpus.toString()+" CPU, "+item?.memorySize?.toDouble()?.div(1024.0).toString().split(".")[0]+" GB Memory"
        holder.itemView.nodeHour.text = BigDecimal(item.nodeHours?.toDouble()?:0.00).setScale(2, RoundingMode.HALF_UP).toString()
        holder.itemView.cloudCost.text = BigDecimal(item.costPerHour?.toDouble()?:0.00).setScale(2, RoundingMode.HALF_UP).toString()
//      holder.itemView.status.text = item?.status
        if (item.status.equals("Running", true)) {
//          holder.itemView.status.setTextColor(context.resources.getColor(R.color.textColor1))
            holder.itemView.statusIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorGreenTheme))
        } else {
//          holder.itemView.status.setTextColor(context.resources.getColor(R.color.colorRed))
            holder.itemView.statusIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorRed))
        }

        holder.itemView.action.setOnClickListener {
            actionMenuPopupWindow.showAsDropDown(holder.itemView.action)
        }
    }

    override fun getItemCount(): Int {
        return vmList.size
    }

    interface ActionCallback{
        fun onStart(success: Boolean)
        fun onStop(success: Boolean)
        fun onAttachDetach()
        fun onReboot()
        fun onTerminate()
        fun onNote()
    }

    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAppear()
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDisappear()
    }
}

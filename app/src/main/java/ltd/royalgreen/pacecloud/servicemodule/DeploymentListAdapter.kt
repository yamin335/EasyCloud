package ltd.royalgreen.pacecloud.servicemodule

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.service_deployment_row.view.*
import kotlinx.android.synthetic.main.toast_custom_red.view.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.loginmodule.LoggedUserData
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.LiveDataCallAdapterFactory
import ltd.royalgreen.pacecloud.util.RecyclerItemDivider
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


class DeploymentListAdapter(val context: Context,
                            private val callBack: VMListAdapter.ActionCallback,
                            private val renameSuccessCallback: RenameSuccessCallback,
                            private val fragmentManager: FragmentManager,
                            private val loggedUser: LoggedUserData?) : PagedListAdapter<Deployment, DeploymentListViewHolder>(DeploymentListDiffUtilCallback()) {

    val interceptor = Interceptor { chain ->
        val newRequest = chain.request().newBuilder()
            .addHeader("AuthorizedToken", "cmdsX3NlY3JldF9hcGlfa2V5")
            .build()
        chain.proceed(newRequest)
    }

    private val client = OkHttpClient().newBuilder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .callTimeout(5, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .build()

    val apiService: ApiService = Retrofit.Builder()
        .client(client)
        .baseUrl("http://123.136.26.98:8081")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .build()
        .create(ApiService::class.java)

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
                  if (isNetworkAvailable(context)) {
                      val jsonObject = JsonObject().apply {
                          addProperty("UserID", loggedUser?.userID)
                          addProperty("id", item?.deploymentId)
                          addProperty("name", renamedValue)
                      }

                      val param = JsonArray().apply {
                          add(jsonObject)
                      }

                      val handler = CoroutineExceptionHandler { _, exception ->
                          exception.printStackTrace()
                      }

                      CoroutineScope(Dispatchers.IO).launch(handler) {
                          val response = apiService.updatedeploymentname(param).execute()
                          when (val apiResponse = ApiResponse.create(response)) {
                              is ApiSuccessResponse -> {
                                  if (JsonParser().parse(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("resstate").asBoolean) {
                                      renameSuccessCallback.onRenamed()
                                  }
                              }
                              is ApiEmptyResponse -> {
                                  Log.d("EMPTY","EMPTY_VALUE")
                              }
                              is ApiErrorResponse -> {
                                  Log.d("ERROR","ERROR_RESPONSE")
                              }
                          }
                      }
                  } else {
                      val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
                      val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                      val toastView = inflater.inflate(R.layout.toast_custom_red, null)
                      toastView.message.text = context.getString(R.string.net_error_msg)
                      toast.view = toastView
                      toast.show()
                  }
              }
          }, loggedUser?.fullName, item?.deploymentName)
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

package ltd.royalgreen.pacecloud.supportmodule

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.content.PermissionChecker
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.SupportFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.mainactivitymodule.CustomAlertDialog
import ltd.royalgreen.pacecloud.network.ApiService
import ltd.royalgreen.pacecloud.util.autoCleared
import javax.inject.Inject

class SupportFragment : Fragment(), Injectable {

    val CALL_REQUEST_CODE = 222

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    private val viewModel: SupportFragmentViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(SupportFragmentViewModel::class.java)
    }

    private var binding by autoCleared<SupportFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            val exitDialog = CustomAlertDialog(object :  CustomAlertDialog.YesCallback{
                override fun onYes() {
                    preferences.edit().apply {
                        putString("LoggedUser", "")
                        apply()
                    }
                    requireActivity().finish()
                }
            }, "Do you want to exit?", "")
            fragmentManager?.let {
                exitDialog.show(it, "#app_exit_dialog")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.support_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.contact1.setOnClickListener{
            viewModel.currentNumber.postValue("09603-111999")
            callPhone("09603-111999")
        }

        binding.contact2.setOnClickListener{
            viewModel.currentNumber.postValue("01777706745")
            callPhone("01777706745")
        }

        binding.contact3.setOnClickListener{
            viewModel.currentNumber.postValue("01777706746")
            callPhone("01777706746")
        }

        binding.mail.setOnClickListener{
            mailTo("support@royalgreen.net")
        }

        binding.web.setOnClickListener{
            openWebPage("www.royalgreen.net")
        }

        binding.facebook.setOnClickListener{
            try {
                val applicationInfo = requireContext().packageManager.getApplicationInfo("com.facebook.katana", 0)
                if (applicationInfo.enabled) {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/n/?Royalgreenbd"))
                    // Verify the intent will resolve to at least one activity
                    if (webIntent.resolveActivity(requireActivity().packageManager) != null) {
                        requireActivity().startActivity(webIntent)
                    }
                } else {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/n/?Royalgreenbd"))
                    val chooser = Intent.createChooser(webIntent, "View Facebook Page Using")
                    // Verify the intent will resolve to at least one activity
                    if (webIntent.resolveActivity(requireActivity().packageManager) != null) {
                        requireActivity().startActivity(chooser)
                    }
                }
            } catch (exception: PackageManager.NameNotFoundException) {
                exception.printStackTrace()
            }
        }
    }

    private fun callPhone(number: String) {
        if (PermissionChecker.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val callingIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
            requireActivity().startActivity(callingIntent)
        } else {
            // Permission is not granted
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                val explanationDialog = CustomAlertDialog(object :  CustomAlertDialog.YesCallback{
                    override fun onYes() {
                        requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), CALL_REQUEST_CODE)
                    }
                }, "Allow Permission", "You have to allow permission for making call.\n\nDo you want to allow permission?")
                fragmentManager?.let {
                    explanationDialog.show(it, "#call_permission_dialog")
                }

            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), CALL_REQUEST_CODE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private fun mailTo(recipient: String) {
        try {
            val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                // The intent does not have a URI, so declare the "text/plain" MIME type
                type = "text/plain"
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient)) // recipients
                // You can also attach multiple items by passing an ArrayList of Uris
            }
            val chooser = Intent.createChooser(mailIntent, "Send Mail")
            // Verify the intent will resolve to at least one activity
            if (mailIntent.resolveActivity(requireActivity().packageManager) != null) {
                requireActivity().startActivity(chooser)
            }
        } catch (exception: ActivityNotFoundException) {
            exception.printStackTrace()
        }
    }

    private fun openWebPage(url: String) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://$url"))
        val chooser = Intent.createChooser(webIntent, "View Website Using")
        // Verify the intent will resolve to at least one activity
        if (webIntent.resolveActivity(requireActivity().packageManager) != null) {
            requireActivity().startActivity(chooser)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (PermissionChecker.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        val callingIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${viewModel.currentNumber.value}"))
                        requireActivity().startActivity(callingIntent)
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.refresh -> {
//                refreshUI()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

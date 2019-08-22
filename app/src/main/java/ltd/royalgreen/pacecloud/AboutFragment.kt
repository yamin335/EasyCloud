package ltd.royalgreen.pacecloud

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.AboutFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.network.ApiService
import ltd.royalgreen.pacecloud.util.autoCleared
import javax.inject.Inject

class AboutFragment : Fragment(), Injectable {
    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    private val viewModel: AboutFragmentViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(AboutFragmentViewModel::class.java)
    }

    private var binding by autoCleared<AboutFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.about_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }
}

package ltd.royalgreen.pacecloud.supportmodule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ltd.royalgreen.pacecloud.R


/**
 * Shows a register support_graph to showcase UI state persistence. It has a button that goes to [Registered]
 */
class SupportFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.support_fragment, container, false)

        return view
    }
}

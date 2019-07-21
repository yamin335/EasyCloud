package ltd.royalgreen.pacecloud.supportmodule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ltd.royalgreen.pacecloud.R

/**
 * Shows "Done".
 */
class Registered : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_registered, container, false)
    }
}

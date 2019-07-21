package ltd.royalgreen.pacecloud.dashboardmodule

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.dashboard_fragment.view.*
import ltd.royalgreen.pacecloud.R
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.DashboardFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.util.autoCleared


class DashboardFragment : Fragment(), Injectable {

    private val viewModel: DashboardViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this).get(DashboardViewModel::class.java)
    }

    var binding by autoCleared<DashboardFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dashboard_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.viewModel = viewModel

//        val user = viewModel.loggedUser

        val NoOfEmp: List<PieEntry>
        NoOfEmp = ArrayList<PieEntry>()

        NoOfEmp.add(PieEntry(945f, 0F))
        NoOfEmp.add(PieEntry(1040f, 1F))
        NoOfEmp.add(PieEntry(1133f, 2F))
        NoOfEmp.add(PieEntry(1240f, 3F))
        NoOfEmp.add(PieEntry(1369f, 4F))
        NoOfEmp.add(PieEntry(1487f, 5F))
        NoOfEmp.add(PieEntry(1501f, 6F))
        NoOfEmp.add(PieEntry(1645f, 7F))
        NoOfEmp.add(PieEntry(1578f, 8F))
        NoOfEmp.add(PieEntry(1695f, 9F))
        val dataSet = PieDataSet(NoOfEmp, "Number Of Employees")

        val year: List<String>
        year = ArrayList()

        year.add("2008")
        year.add("2009")
        year.add("2010")
        year.add("2011")
        year.add("2012")
        year.add("2013")
        year.add("2014")
        year.add("2015")
        year.add("2016")
        year.add("2017")
        val data = PieData(dataSet)
        view.osStatusPieChart.data = data
        view.osStatusPieChart.holeRadius = 0F
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        view.osStatusPieChart.description.text="This chart shows the OS usage status at a glance."
        view.osStatusPieChart.isRotationEnabled = false
        view.osStatusPieChart.animateXY(1000, 1000)
    }
}

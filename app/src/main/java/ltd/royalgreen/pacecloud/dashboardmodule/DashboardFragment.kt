package ltd.royalgreen.pacecloud.dashboardmodule

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.dashboard_fragment.view.*
import ltd.royalgreen.pacecloud.R
import com.google.gson.Gson
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.DashboardFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.util.autoCleared
import javax.inject.Inject
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.dashboard_fragment.*
import kotlinx.android.synthetic.main.dashboard_fragment.view.userActivityLogList
import ltd.royalgreen.pacecloud.util.RecyclerItemDivider
import kotlin.math.roundToInt


class DashboardFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    private val viewModel: DashboardViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(DashboardViewModel::class.java)
    }

    private var binding by autoCleared<DashboardFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    //For Cloud User Activity Log
    private val adapter = ActivityLogAdapter()

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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        userActivityLogList.layoutManager = LinearLayoutManager(activity)
        userActivityLogList.addItemDecoration(RecyclerItemDivider(activity!!.applicationContext, LinearLayoutManager.VERTICAL, 8))
        userActivityLogList.adapter = adapter

        //1
        val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setEnablePlaceholders(false)
            .build()

        //2
        viewModel.userLogs = viewModel.initializedPagedListBuilder(config).build()

        //3
        viewModel.userLogs.observe(this, Observer<PagedList<CloudActivityLog>> { pagedList ->
            adapter.submitList(pagedList)
        })

        //Pie Chart Configuration
        view.osStatusPieChart.isLogEnabled = false
        view.osStatusPieChart.holeRadius = 35F
        view.osStatusPieChart.transparentCircleRadius = 43F
        view.osStatusPieChart.centerText = "VM Status"
        view.osStatusPieChart.setNoDataText("No Chart Data Found")
//        view.osStatusPieChart.setDrawMarkers(false)
        view.osStatusPieChart.setDrawEntryLabels(true)
        view.osStatusPieChart.setEntryLabelTextSize(11f)
        view.osStatusPieChart.setNoDataTextColor(resources.getColor(R.color.pieColor1))
//        view.osStatusPieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        view.osStatusPieChart.description.isEnabled = false
        view.osStatusPieChart.isRotationEnabled = false
//        view.osStatusPieChart.setUsePercentValues(true)
        view.osStatusPieChart.animateXY(900, 900)

        //Bar Chart Configuration
        view.osSummaryBarChart.isLogEnabled = false
//        view.osSummaryBarChart.xAxis.setCenterAxisLabels(true)
        view.osSummaryBarChart.setFitBars(true)
        view.osSummaryBarChart.setNoDataText("No Chart Data Found")
        view.osSummaryBarChart.description.isEnabled = false
        view.osSummaryBarChart.setScaleEnabled(false)
        view.osSummaryBarChart.xAxis.setDrawGridLines(false)
        view.osSummaryBarChart.animateY(900)

        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        if (user != null) {
            viewModel.getOsStatus(user)
            viewModel.getOsSummary(user)
            binding.name = user.resdata?.loggeduser?.fullName
            binding.versionNo = "Beta Version:1.0.1"
        }

        viewModel.osStatus.observe(this, Observer { status ->
            val dataSet = status?.resdata?.dashboardchartdata
            val entries: List<PieEntry>
            entries = ArrayList<PieEntry>()
            if (dataSet != null) {
                dataSet.forEach {
                    entries.add(PieEntry(it.dataValue?.toFloat()?: 0.00F, it.dataName))
                }
                val pieDataSet = PieDataSet(entries, "")
                pieDataSet.valueTextColor = resources.getColor(R.color.colorWhite)
                pieDataSet.valueTextSize = 11f
                pieDataSet.colors = arrayListOf(resources.getColor(R.color.pieColor2), resources.getColor(R.color.pieColor1), resources.getColor(R.color.colorRed))
                val pieData = PieData(pieDataSet)
                pieData.setValueFormatter(CustomValueFormatter())
                view.osStatusPieChart.data = pieData
                view.osStatusPieChart.invalidate()
            }
        })

        viewModel.osSummary.observe(this, Observer { summary ->
            val dataSet = summary?.resdata?.dashboardchartdata
            val entries: List<BarEntry>
            entries = ArrayList<BarEntry>()
            val titles: List<String>
            titles = ArrayList<String>()
            if (dataSet != null) {
                for ((index, value) in dataSet.withIndex()) {
                    entries.add(BarEntry(index.toFloat(), value.dataValue?.toFloat()?: 0.00F, value.dataName?: ""))
                    if (value.dataName?.contains(" ") == true) {
                        val temp = value.dataName.split(" ")[0]
                        titles.add(index, temp)
                    } else {
                        titles.add(index, value.dataName?: "")
                    }
                }
                val barDataSet = BarDataSet(entries, "OS Summary")
                barDataSet.valueFormatter = CustomValueFormatter()
                barDataSet.valueTextSize = 11f
                val barData = BarData(barDataSet)
                barDataSet.colors = arrayListOf(resources.getColor(R.color.barColor1),
                    resources.getColor(R.color.barColor2), resources.getColor(R.color.barColor3),
                    resources.getColor(R.color.barColor4))
                view.osSummaryBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(titles)
                view.osSummaryBarChart.xAxis.labelCount = titles.size
                view.osSummaryBarChart.data = barData
                view.osSummaryBarChart.invalidate()
            }
        })
    }

    inner class CustomValueFormatter : ValueFormatter() {

        // override this for BarChart
        override fun getBarLabel(barEntry: BarEntry?): String {
            return barEntry?.y?.roundToInt().toString()
        }

        override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
            return pieEntry?.y?.roundToInt().toString()
        }
    }

}
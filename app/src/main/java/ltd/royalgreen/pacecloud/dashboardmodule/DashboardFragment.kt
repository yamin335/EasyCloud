package ltd.royalgreen.pacecloud.dashboardmodule

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.LegendEntry
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
import kotlin.math.roundToInt
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarDataSet
import ltd.royalgreen.pacecloud.mainactivitymodule.CustomAlertDialog
import ltd.royalgreen.pacecloud.network.ApiCallStatus
import ltd.royalgreen.pacecloud.util.showErrorToast
import ltd.royalgreen.pacecloud.util.showWarningToast


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
//    private val adapter = ActivityLogAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // This callback will only be called when DashboardFragment is at least Started.
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
            exitDialog.show(parentFragmentManager, "#app_exit_dialog")
        }
    }

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

//        userActivityLogList.layoutManager = LinearLayoutManager(requireContext())
//        userActivityLogList.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 8))
//        userActivityLogList.adapter = adapter

        //1
//        val config = PagedList.Config.Builder()
//            .setPageSize(30)
//            .setEnablePlaceholders(false)
//            .build()

        //2
//        viewModel.userLogs = viewModel.initializedPagedListBuilder(config).build()

        //3
//        viewModel.userLogs.observe(this, Observer<PagedList<CloudActivityLog>> { pagedList ->
//            adapter.submitList(pagedList)
//        })

        //Pie Chart Configuration
        binding.osStatusPieChart.isLogEnabled = false
        binding.osStatusPieChart.holeRadius = 35F
        binding.osStatusPieChart.transparentCircleRadius = 43F
        binding.osStatusPieChart.centerText = "VM Status"
        binding.osStatusPieChart.setNoDataText("No Chart Data Found")
//        view.osStatusPieChart.setDrawMarkers(false)
        binding.osStatusPieChart.setDrawEntryLabels(true)
        binding.osStatusPieChart.setEntryLabelTextSize(11f)
        binding.osStatusPieChart.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.pieColor1))
//        view.osStatusPieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        binding.osStatusPieChart.description.isEnabled = false
        binding.osStatusPieChart.isRotationEnabled = false
        binding.osStatusPieChart.setEntryLabelColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
//        view.osStatusPieChart.setUsePercentValues(true)

        //Bar Chart Configuration
        binding.osSummaryBarChart.isLogEnabled = false
//        view.osSummaryBarChart.xAxis.setCenterAxisLabels(true)
        binding.osSummaryBarChart.setFitBars(true)
        binding.osSummaryBarChart.setNoDataText("No Chart Data Found")
        binding.osSummaryBarChart.description.isEnabled = false
        binding.osSummaryBarChart.setScaleEnabled(false)
        binding.osSummaryBarChart.xAxis.setDrawGridLines(false)

        refreshUI()
        binding.versionNo = "Version:1.0.0"
    }

    private fun refreshPieChart() {
        viewModel.getOsStatus().observe(this, Observer { status ->
            binding.osStatusPieChart.legend.resetCustom()
            binding.osStatusPieChart.notifyDataSetChanged()
            val dataSet = status?.resdata?.dashboardchartdata
            val entries: List<PieEntry>
            entries = ArrayList<PieEntry>()
            if (dataSet != null) {
                val legendLabel = ArrayList<String>()
                val legends = ArrayList<LegendEntry>()
                dataSet.forEach { dashOsStatusChart ->
                    val data = dashOsStatusChart.dataValue?.toFloat()
                    if ( data != null && data > 0.0F) {
                        entries.add(PieEntry(data+5, dashOsStatusChart.dataName))
                    }

                    if (!legendLabel.contains(dashOsStatusChart.dataName)) {
                        dashOsStatusChart.dataName?.let {
                            val legend = LegendEntry()
                            when(it) {
                                "Running" -> {
                                    legend.formColor = ContextCompat.getColor(requireContext(), R.color.pieColor2)
                                }
                                "Stopped" -> {
                                    legend.formColor = ContextCompat.getColor(requireContext(), R.color.pieColor1)
                                }
                                "Terminated" -> {
                                    legend.formColor = ContextCompat.getColor(requireContext(), R.color.colorRed)
                                }
                                "Error" -> {
                                    legend.formColor = ContextCompat.getColor(requireContext(), R.color.barColor4)
                                }
                                else -> {
                                    legend.formColor = ContextCompat.getColor(requireContext(), R.color.barColor3)
                                }
                            }
                            legend.label = it
                            legends.add(legend)
                            legendLabel.add(it)
                        }
                    }
                }
                val pieDataSet = MyPieDataSet(entries, "")
                pieDataSet.sliceSpace = 3f
//                pieDataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
//                pieDataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
//                pieDataSet.valueLinePart1OffsetPercentage = 90.0f
//                pieDataSet.valueLinePart1Length = 0.65f
//                pieDataSet.valueLinePart2Length = 0.4f
                pieDataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.colorWhite)
                pieDataSet.valueTextSize = 11f
                pieDataSet.colors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.pieColor2),
                    ContextCompat.getColor(requireContext(), R.color.pieColor1),
                    ContextCompat.getColor(requireContext(), R.color.colorRed),
                    ContextCompat.getColor(requireContext(), R.color.barColor4),
                    ContextCompat.getColor(requireContext(), R.color.barColor3))
                val pieData = PieData(pieDataSet)
                pieData.setValueFormatter(CustomValueFormatter())
                binding.osStatusPieChart.data = pieData
                binding.osStatusPieChart.legend.setCustom(legends)
                binding.osStatusPieChart.invalidate()
            }
            binding.osStatusPieChart.animateXY(900, 900)
        })
    }

    private fun refreshBarChart() {
        viewModel.getOsSummary().observe(this, Observer { summary ->
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
                barDataSet.colors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.barColor1),
                    ContextCompat.getColor(requireContext(), R.color.barColor2), ContextCompat.getColor(requireContext(), R.color.barColor3),
                    ContextCompat.getColor(requireContext(), R.color.barColor4))
                binding.osSummaryBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(titles)
                binding.osSummaryBarChart.xAxis.labelCount = titles.size
                binding.osSummaryBarChart.data = barData
                binding.osSummaryBarChart.invalidate()
            }
            binding.osSummaryBarChart.animateY(900)
        })
    }

//    private fun getOsStatusChartData() {
//        viewModel.getOsStatusChart().observe(this, Observer {
//            val t= it
//        })
//    }

    private fun refreshUI() {
        refreshPieChart()
        refreshBarChart()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.refresh -> {
                refreshUI()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class CustomValueFormatter : ValueFormatter() {

        // override this for BarChart
        override fun getBarLabel(barEntry: BarEntry?): String {
            return barEntry?.y?.roundToInt().toString()
        }

        override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
            var modifiedValue = 0
            pieEntry?.y?.let {
                modifiedValue = if (it.roundToInt()>0) {
                    it.roundToInt().minus(5)
                } else {
                    it.roundToInt()
                }
            }
            return modifiedValue.toString()
        }
    }

    inner class MyPieDataSet(yVals: List<PieEntry>, label: String) : PieDataSet(yVals, label){
        override fun getEntryIndex(e: PieEntry?): Int {
         return super.getEntryIndex(e)
        }

        override fun getColor(index: Int): Int {
            return when(getEntryForIndex(index).label) {
                "Running" -> colors[0]
                "Stopped" -> colors[1]
                "Terminated" -> colors[2]
                "Error" -> colors[3]
                else -> colors[4]
            }
        }


    }

}

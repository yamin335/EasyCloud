package ltd.royalgreen.pacecloud.dashboardmodule

//Balance response data for dashboard
data class BalanceModel(val resdata: BalanceResData?)

data class BillCloudUserBalance(val cloudUserId: Number?, val balanceAmount: Number?, val isActive: Boolean?, val companyId: Number?, val createDate: String?, val createdBy: Number?)

data class BalanceResData(val billCloudUserBalance: BillCloudUserBalance?)

//OS Status response data for dashboard
data class DashOsStatus(val resdata: DashOsStatusResdata?)

data class DashOsStatusChart(val dataName: String?, val dataValue: Number?)

data class DashOsStatusResdata(val dashboardchartdata: List<DashOsStatusChart>?)

//OS Summery response data for dashboard
data class DashOsSummary(val resdata: DashOsSummaryResdata?)

data class DashOsSummaryChart(val dataName: String?, val dataValue: Number?)

data class DashOsSummaryResdata(val dashboardchartdata: List<DashOsSummaryChart>?)
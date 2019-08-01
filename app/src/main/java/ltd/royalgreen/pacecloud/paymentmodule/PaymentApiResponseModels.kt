package ltd.royalgreen.pacecloud.paymentmodule

data class PaymentHistory(val resdata: PaymentHistoryResdata?)

data class BilCloudUserLedger(val cloudUserLedgerId: Number?, val cloudUserId: Number?, val vmid: Number?, val transactionDate: String?, val debitAmount: Number?, val creditAmount: Number?, val balanceAmount: Number?, val particulars: String?, val isActive: Boolean?, val companyId: Number?, val createDate: String?, val createdBy: Number?)

data class PaymentHistoryResdata(val listBilCloudUserLedger: List<BilCloudUserLedger>?, val recordsTotal: Number?)
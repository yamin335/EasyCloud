package ltd.royalgreen.pacecloud.paymentmodule

data class PaymentHistory(val resdata: PaymentHistoryResdata?)

data class BilCloudUserLedger(val cloudUserLedgerId: Number?, val cloudUserId: Number?,
                              val vmid: Number?, val transactionDate: String?, val debitAmount: Number?,
                              val creditAmount: Number?, val balanceAmount: Number?, val particulars: String?,
                              val isActive: Boolean?, val companyId: Number?, val createDate: String?, val createdBy: Number?)

data class PaymentHistoryResdata(val listBilCloudUserLedger: List<BilCloudUserLedger>?, val recordsTotal: Number?)

//Recharge response model
data class RechargeResponse(val resdata: RechargeResdata?)

data class RechargeResdata(val message: String?, val resstate: Boolean?, val paymentProcessUrl: String?, val paymentStatusUrl: String?, val amount: String?)

data class LastRechargeBalance(val resdata: LastRechargeResdata?)

data class LastRechargeUserLedger(val cloudUserLedgerId: Number?, val cloudUserId: Number?,
                                  val vmid: Number?, val transactionDate: String?, val debitAmount: Number?,
                                  val creditAmount: Number?, val balanceAmount: Number?, val particulars: String?,
                                  val isActive: Boolean?, val companyId: Number?, val createDate: String?, val createdBy: Number?)

data class LastRechargeResdata(val objBilCloudUserLedger: LastRechargeUserLedger?)

data class RechargeStatusFosterCheckModel(val resdata: RechargeStatusFosterResdata)

data class RechargeStatusFosterResdata(val resstate: Boolean, val fosterRes: String)

data class FosterModel(val MerchantTxnNo: String?, val TxnResponse: String?, val TxnAmount: String?, val Currency: String?, val ConvertionRate: String?, val OrderNo: String?, val fosterid: String?, val hashkey: String?, val message: String?)
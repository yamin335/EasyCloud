package ltd.royalgreen.pacecloud.paymentmodule.bkash

import java.io.Serializable

class CreateBkashModel : Serializable {
    var authToken: String? = null
    var rechargeAmount: String? = null
    var currency: String? = null
    var mrcntNumber: String? = null
}

class PaymentRequest : Serializable {
    var amount: String? = null
    var intent: String? = "sale"
}

class BkashDataModel {
    var paymentRequest = PaymentRequest()
    var createBkashModel = CreateBkashModel()
}
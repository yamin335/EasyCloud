package ltd.royalgreen.pacecloud.paymentmodule.bkash

import java.io.Serializable

class PaymentRequest : Serializable {
    var amount: String? = null
    var intent: String? = "sale"
}
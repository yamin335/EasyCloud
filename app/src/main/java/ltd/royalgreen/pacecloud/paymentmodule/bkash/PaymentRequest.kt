package ltd.royalgreen.pacecloud.paymentmodule.bkash

import java.io.Serializable

class PaymentRequest : Serializable {
    var amount: String? = null
    var intent: String? = null

    override fun toString(): String {
        return "PaymentRequest{" +
                "amount='" + amount + '\''.toString() +
                ", intent='" + intent + '\'' +
                '}'
    }

}
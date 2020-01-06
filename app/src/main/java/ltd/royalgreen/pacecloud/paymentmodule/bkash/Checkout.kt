package ltd.royalgreen.pacecloud.paymentmodule.bkash

import java.io.Serializable

class Checkout : Serializable {
    var amount: String? = null
    var intent: String? = null
    var version: String? = null

    override fun toString(): String {
        return "Checkout{" +
                "amount='" + amount + '\''.toString() +
                ", intent='" + intent + '\'' +
                '}'
    }

}
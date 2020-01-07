package ltd.royalgreen.pacecloud.paymentmodule.bkash

import java.io.Serializable

class CreateBkashModel : Serializable {
    var authToken: String? = null
    var rechargeAmount: String? = null
    var currency: String? = null
    var mrcntNumber: String? = null
}
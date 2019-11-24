package ltd.royalgreen.pacecloud.loginmodule

data class LoggedUser(val resdata: LoginResData?)

data class LoggedUserData(val userID: Number?, val userType: String?, val roleID: Any?, val userName: String?,
                      val fullName: String?, val displayName: String?, val email: String?,
                      val companyID: Number?, val phone: Any?, val balance: Number?,
                      val activationProfileId: String?, val created: String?,
                      val lastUpdated: String?, val companyName: String?,
                      val status: String?, val type: String?)


data class LoginResData(val loggeduser: LoggedUserData?, val message: String?, val resstate: Boolean?)
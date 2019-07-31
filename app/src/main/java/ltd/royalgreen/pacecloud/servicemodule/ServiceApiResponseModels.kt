package ltd.royalgreen.pacecloud.servicemodule

// Response model for VM List
data class VMListResponse(val resdata: VMListResdata?)

data class VM(val id: String?, val resource: Any?, val type: String?, val nodeId: String?,
              val name: String?, val hostName: String?, val approvalRequestStatus: String?,
              val approvalRequestAction: String?, val nodeStartTime: Number?, val dtNodeStartTime: String?,
              val nodeEndTime: Number?, val dtNodeEndTime: String?, val numberOfCpus: Number?,
              val memorySize: Number?, val storageSize: Number?, val osName: String?,
              val osType: String?, val osVersion: String?, val costPerHour: Number?,
              val customCost: Any?, var status: String?, val nodeStatus: String?,
              val reviewNodeStatus: Boolean?, val cloudFamily: String?, val vmStartTime: Any?,
              val vmStopTime: Any?, val cloudId: String?, val cloudName: String?,
              val cloudAccountId: String?, val cloudAccountName: String?, val regionId: String?,
              val regionName: String?, val regionDisplayName: String?, val tenantId: String?,
              val userId: String?, val firstName: String?, val lastName: String?, val email: String?,
              val instanceTypeId: String?, val instanceTypeName: String?, val instanceCost: Number?,
              val cloudNameAndAccountName: String?, val agentVersion: String?, val jobId: String?,
              val jobName: String?, val jobStartTime: Number?, val dtJobStartTime: String?,
              val jobEndTime: Number?, val dtJobEndTime: String?, val parentJobId: String?,
              val parentJobName: String?, val parentJobStatus: String?, val benchmarkId: Number?,
              val deploymentEnvironmentId: String?, val deploymentEnvironmentName: String?,
              val appId: String?, val appName: String?, val appVersion: String?,
              val appLogoPath: String?, val serviceId: String?, val serviceName: String?,
              val tags: String?, val publicIpAddresses: String?, val privateIpAddresses: String?,
              val cloudCost: Number?, val nodeHours: Number?, val userFavorite: Boolean?,
              val recordTimestamp: Number?, val dtRecordTimestamp: String?, val imageId: String?,
              val terminateProtection: Boolean?, val importedTime: Number?, val dtImportedTime: String?,
              val running: Boolean?, val runTime: Number?, val dtRunTime: String?, val balanceAmount: Any?,
              val isAutoRenewal: Boolean?, val vmName: String?)

data class VMListResdata(val listCloudvm: List<VM>?, val recordsTotal: Number?, val totalNumberOfVMs: Number?,
                   val totalNumberOfRunningVMs: Number?, val totalCloudCost: Number?, val totalNodeHours: Number?)

package ltd.royalgreen.pacecloud.servicemodule

// Response model for VM List
data class Deployment(val depCloudUserId: Number?, val deploymentName: String?,
                      val serviceId: Number?, val cloudId: Number?, val deploymentId: Number?,
                      val vmLists: List<VM>?, val recordsTotal: Number?, val totalNumberOfVMs: Number?,
                      val totalNumberOfRunningVMs: Number?, val totalCloudCost: Number?,
                      val totalNodeHours: Number?, val IsSyncVM: Boolean?)

data class VM(val tenantId: Number?, val userId: Number?, val id: String?, val resource: String?,
              val type: String?, val nodeId: String?, val name: String?, val hostName: String?,
              val approvalRequestStatus: String?, val approvalRequestAction: String?, val numberOfCpus: Number?,
              val memorySize: Number?, val storageSize: Number?, val osName: String?, val osType: String?,
              val osVersion: String?, val costPerHour: Number?, val nodeStatus: String?,
              val reviewNodeStatus: Boolean?, val instanceTypeId: String?, val instanceTypeName: String?,
              val instanceCost: Number?, val firstName: String?, val lastName: String?, val email: String?,
              var status: String?, val nodeStartTime: Number?, val dtNodeStartTime: String?, val nodeEndTime: Number?,
              val dtNodeEndTime: String?, val cloudId: String?, val cloudName: String?, val cloudFamily: String?,
              val regionId: String?, val regionName: String?, val regionDisplayName: String?,
              val cloudAccountId: String?, val cloudAccountName: String?, val cloudNameAndAccountName: String?,
              val agentVersion: String?, val jobId: String?, val jobName: String?, val jobStartTime: Number?,
              val dtJobStartTime: String?, val jobEndTime: Number?, val dtJobEndTime: String?, val parentJobId: String?,
              val parentJobName: String?, val parentJobStatus: String?, val benchmarkId: Number?,
              val deploymentEnvironmentId: String?, val deploymentEnvironmentName: String?, val appId: String?,
              val appName: String?, val vmName: String?, val appVersion: String?, val appLogoPath: String?,
              val serviceId: String?, val serviceName: String?, val tags: String?, val publicIpAddresses: String?,
              val privateIpAddresses: String?, val cloudCost: Number?, val nodeHours: Number?, val userFavorite: Boolean?,
              val recordTimestamp: Number?, val dtRecordTimestamp: String?, val imageId: String?,
              val terminateProtection: Boolean?, val importedTime: Number?, val dtImportedTime: String?,
              val running: Boolean?, val runTime: Number?, val dtRunTime: String?, val vmNote: String?,
              val serviceTierId: String?, val noOfNic: Number?, val isProcessing: Boolean?)

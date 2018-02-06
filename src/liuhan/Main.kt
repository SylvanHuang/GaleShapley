package liuhan;

import cloudsim.*
import cloudsim.container.containerProvisioners.ContainerBwProvisionerSimple
import cloudsim.container.containerProvisioners.ContainerPe
import cloudsim.container.containerProvisioners.ContainerRamProvisionerSimple
import cloudsim.container.containerProvisioners.CotainerPeProvisionerSimple
import cloudsim.container.containerVmProvisioners.ContainerVmBwProvisionerSimple
import cloudsim.container.containerVmProvisioners.ContainerVmPe
import cloudsim.container.containerVmProvisioners.ContainerVmPeProvisionerSimple
import cloudsim.container.containerVmProvisioners.ContainerVmRamProvisionerSimple
import cloudsim.container.core.*
import cloudsim.container.resourceAllocators.PowerContainerAllocationPolicySimple
import cloudsim.container.resourceAllocators.PowerContainerVmAllocationSimple
import cloudsim.container.schedulers.ContainerCloudletSchedulerDynamicWorkload
import cloudsim.container.schedulers.ContainerSchedulerTimeSharedOverSubscription
import cloudsim.container.schedulers.ContainerVmSchedulerTimeSharedOverSubscription
import cloudsim.container.utils.IDs
import cloudsim.core.CloudSim
import cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G4Xeon3040
import java.io.IOException
import java.text.DecimalFormat
import java.util.*

/**
 * Created by liuha on 2017/04/28 0028.
 */
object Main {
	@JvmStatic val seed : Long = 12339981
	@JvmStatic val numberOfCloudlets = 8000
	@JvmStatic val rangeOfCloudlets = 500..2000
	@JvmStatic val usedGS = true
	@JvmStatic val correlation = TanimotoCorrelation()
	@JvmStatic val gs = GaleShapleySM(correlation)

	@JvmStatic fun main(argv: Array<String>) {
		try {
			CloudSim.init(1, Calendar.getInstance(), false)

			val broker = ContainerDatacenterBroker("Broker", 80.0)

			val cloudletList = createCloudletList(broker.id)
			val hostList = createHostList((0.5 + sumCloudletMips(cloudletList) / 68000.0).toInt())
			val vmList = createVmList(broker.id, hostList.size * 8)
			val containerList = createContainerList(broker.id, cloudletList)

			val pcap = if (usedGS) {
				PowerContainerAllocationPolicyGaleShapley(gs.optimizeAllocate(containerList, vmList))
			} else {
				PowerContainerAllocationPolicySimple()
			}
			val dc = PowerContainerDatacenter(
					"datacenter",
					ContainerDatacenterCharacteristics(
							"x86", "Linux", "Xen",
							hostList,
							10.0, 3.0, 0.05, 0.001, 0.0
					),
					PowerContainerVmAllocationSimple(hostList),
					pcap,
					LinkedList<Storage>(),
					300.0,
					getExperimentName("ContainerCloudSimExample-1", 80.toString()),
					"~/Results")
			dc.isDisableVmMigrations = true

			broker.submitCloudletList(cloudletList)
			broker.submitContainerList(containerList)
			broker.submitVmList(vmList)

			CloudSim.terminateSimulation(86400.00)

			CloudSim.startSimulation()

			printCloudletList(broker.getCloudletReceivedList());
			printDataCenterList(listOf(dc))

			Log.printLine("ContainerCloudSimExample1 finished!")
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	@Throws(IOException::class)
	fun createCloudletList(brokerId: Int): List<ContainerCloudlet> {
		val r = Random(seed)
		val cloudletList = ArrayList<ContainerCloudlet>()
		val fileSize = 300L
		val outputSize = 300L
		val utilizationModelNull = UtilizationModelNull()
		val inputFolderName = Main::class.java.classLoader.getResource("workload/planetlab")!!.path
		val inputFolder = java.io.File(inputFolderName)
		val files = inputFolder.listFiles()
		var createdCloudlets = 0
		for (file in files!!) {
			val input = java.io.File(file.toString())
			val fileList = input.listFiles()
			for (i in fileList!!.indices) {
				if (createdCloudlets < numberOfCloudlets) {
					val cloudlet = ContainerCloudlet(
							IDs.pollId(ContainerCloudlet::class.java),
							(r.nextInt() % (rangeOfCloudlets.endInclusive - rangeOfCloudlets.start) + rangeOfCloudlets.start).toLong(),
							1,
							fileSize, outputSize,
							UtilizationModelPlanetLabInMemory(fileList[i].absolutePath, 300.0),
							utilizationModelNull, utilizationModelNull)

					cloudlet.userId = brokerId
					cloudletList.add(cloudlet)
					createdCloudlets += 1
				} else {
					return cloudletList
				}
			}
		}
		return cloudletList
	}

	fun createContainerList(brokerId: Int, cloudletList: List<ContainerCloudlet>): List<PowerContainer> {
		val containers = ArrayList<PowerContainer>()
		for (i in cloudletList.indices) {
			val c = PowerContainer(
					IDs.pollId(Container::class.java),
					brokerId,
					(cloudletList[i].cloudletLength / 8).toDouble(),
					1,
					128,
					1,
					0L,
					"Xen",
					ContainerCloudletSchedulerDynamicWorkload((cloudletList[i].cloudletLength / 8).toDouble(), 1),
					300.0
			)
			cloudletList[i].setContainerId(c.id)
			containers.add(c)
		}
		return containers
	}

	private fun createVmList(brokerId: Int, containerVmsNumber: Int): ArrayList<PowerContainerVm> {
		val containerVms = ArrayList<PowerContainerVm>()
		for (i in 0..containerVmsNumber - 1) {
			val peList = ArrayList<ContainerPe>()
			peList.add(ContainerPe(1, CotainerPeProvisionerSimple(8500.0)))
			containerVms.add(
					PowerContainerVm(
							IDs.pollId(ContainerVm::class.java),
							brokerId,
							8500.0,
							16000f,
							50000,
							50000,
							"Xen",
							ContainerSchedulerTimeSharedOverSubscription(peList),
							ContainerRamProvisionerSimple(16000f),
							ContainerBwProvisionerSimple(50000),
							peList,
							300.0))


		}
		return containerVms
	}

	private fun createHostList(hostsNumber: Int): List<PowerContainerHost> {
		val hostList = ArrayList<PowerContainerHost>()
		for (i in 0..hostsNumber - 1) {
			val peList = ArrayList<ContainerVmPe>()
			peList.add(ContainerVmPe(1, ContainerVmPeProvisionerSimple(34000.0)))
			peList.add(ContainerVmPe(2, ContainerVmPeProvisionerSimple(34000.0)))

			hostList.add(
					PowerContainerHostUtilizationHistory(
							IDs.pollId(PowerContainerHost::class.java),
							ContainerVmRamProvisionerSimple(128000),
							ContainerVmBwProvisionerSimple(1000000L),
							1000000L,
							peList,
							ContainerVmSchedulerTimeSharedOverSubscription(peList),
							PowerModelSpecPowerHpProLiantMl110G4Xeon3040()
					)
			)
		}
		return hostList
	}

	fun printDataCenterList(list: List<PowerContainerDatacenter>) {
		val printer = TablePrinter(listOf("DataCenter ID", "Power"), 6)

		Log.printLine()
		Log.printLine("========== OUTPUT ==========")
		printer.printTitleLine()

		val dft = DecimalFormat("###.##")
		for (d in list) {
			val elem = listOf<String>(
					d.id.toString(),
					dft.format(d.power))
			printer.printLine(elem)
		}
	}

	fun printCloudletList(list: List<ContainerCloudlet>) {
		val printer = TablePrinter(listOf("Cloudlet ID", "Status" , "Data Center ID" , "Vm ID" , "Container ID" , "Time" , "Start Time" , "Finish Time" ))

		Log.printLine()
		Log.printLine("========== OUTPUT ==========")
		printer.printTitleLine()

		val dft = DecimalFormat("###.##")
		for (cloudlet in list) {
			if (cloudlet.status == Cloudlet.SUCCESS) {
				val elem = listOf<String>(
						cloudlet.cloudletId.toString(),
						"SUCCESS",
						cloudlet.resourceId.toString(),
						cloudlet.vmId.toString(),
						cloudlet.containerId.toString(),
						dft.format(cloudlet.actualCPUTime),
						dft.format(cloudlet.execStartTime),
						dft.format(cloudlet.finishTime))
				printer.printLine(elem)
			}else{
				Log.printLine(cloudlet.cloudletId.toString() + " Failed")
			}
		}
	}

	private fun sumCloudletMips(cloudletList: List<ContainerCloudlet>): Long {
		var mips: Long = 0
		for (cl in cloudletList) {
			mips += cl.cloudletLength
		}
		return mips
	}

	private fun getExperimentName(vararg args: String): String {
		val experimentName = StringBuilder()
		for (i in args.indices) {
			if (!args[i].isEmpty()) {
				if (i != 0) {
					experimentName.append("_")
				}
				experimentName.append(args[i])
			}
		}
		return experimentName.toString()
	}
}
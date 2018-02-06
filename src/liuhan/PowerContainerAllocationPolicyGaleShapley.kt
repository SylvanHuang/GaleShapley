package liuhan

import cloudsim.Log
import cloudsim.container.core.Container
import cloudsim.container.core.ContainerVm
import cloudsim.container.core.PowerContainer
import cloudsim.container.core.PowerContainerVm
import cloudsim.container.resourceAllocators.PowerContainerAllocationPolicy
import cloudsim.core.CloudSim
import java.util.*

/**
 * Created by liuha on 2017/04/28 0028.
 */
class PowerContainerAllocationPolicyGaleShapley : PowerContainerAllocationPolicy {
	constructor(allocationList : List<Pair<PowerContainer, PowerContainerVm>>) : super() {
		for (ac in allocationList) {
			allocationTable[ac.first] = ac.second
		}
	}

	private val allocationTable = mutableMapOf<PowerContainer, PowerContainerVm>()
	private val containerTable = HashMap<String, ContainerVm>()

	override fun allocateVmForContainer(container: Container, containerVmList: List<ContainerVm>): Boolean {
		setContainerVmList(containerVmList)
		val vm = findVmForContainer(container)
		return allocateVmForContainer(container, vm)
	}

	override fun allocateVmForContainer(container: Container, containerVm: ContainerVm?): Boolean {
		if (containerVm == null) {
			Log.formatLine("%.2f: No suitable VM found for Container#" + container.id + "\n", CloudSim.clock())
			return false
		}
		if (allocationTable[container] == containerVm){
			if (containerVm.containerCreate(container)) {
				containerTable.put(container.uid, containerVm)
				Log.formatLine(
						"%.2f: Container #" + container.id + " has been allocated to the VM #" + containerVm.id,
						CloudSim.clock())
				return true
			}
		}
		Log.formatLine(
				"%.2f: Creation of Container #" + container.id + " on the Vm #" + containerVm.id + " failed\n",
				CloudSim.clock())
		return false
	}

	override fun findVmForContainer(container: Container): ContainerVm? {
		return allocationTable[container]
	}

	override fun deallocateVmForContainer(container: Container) {
		val containerVm = containerTable.remove(container.uid)
		containerVm?.containerDestroy(container)
	}

	override fun getContainerVm(container: Container): ContainerVm? {
		return containerTable[container.uid]
	}

	override fun getContainerVm(containerId: Int, userId: Int): ContainerVm? {
		return containerTable[Container.getUid(userId, containerId)]
	}

	override fun optimizeAllocation(containerList: List<Container>): List<Map<String, Any>>? {
		return null
	}
}
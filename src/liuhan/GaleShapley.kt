package liuhan

import cloudsim.container.core.PowerContainer
import cloudsim.container.core.PowerContainerVm

/**
 * Created by liuha on 2017/04/28 0028.
 */

class Triad<V> (var mips : Double, var ram : Double, var ext : V) {
}

class GaleShapleySM(private val correlation : Correlation) {
	fun optimizeAllocate(containerList : List<PowerContainer>, vmList : List<PowerContainerVm>) : List<Pair<PowerContainer, PowerContainerVm>> {
		// 分配列表，表示 PowerContainer 分配到的 PowerContainerVm
		val r = mutableListOf<Pair<PowerContainer, PowerContainerVm>>()
		// vm剩余的可用资源
		val vmResourceList : MutableList<Triad<PowerContainerVm>> = vmList.map { Triad(it.availableMips, it.ram.toDouble(), it) }.toMutableList()
		// 还没有被分配的容器
		var cList = containerList.toMutableList()
		// 如果还有没分配的容器
		while (cList.size > 0) {
			// 取和VM数量相同的未分配容器，如果不够则取全部
			val c = cList.takeLast(Math.min(vmList.size, containerList.size))
			// 从未分配列表中删除
			cList = cList.subList(0, cList.size - c.size)
			// 分配
			val op = allocate(c, vmResourceList, correlation)
			// 添加分配结果到分配列表
			r.addAll(op)
			// 循环迭代分配列表
			for (ac in op) {
				// 查找当前被分配的VM
				val vmc = vmResourceList.find { ac.second === it.ext }
				if (vmc != null) {
					// 修改剩余资源
					vmc.mips -= ac.first.currentRequestedTotalMips
					vmc.ram -= ac.first.currentRequestedRam
				}
			}
		}
		return r
	}

	fun allocate(
			containerList : List<PowerContainer>,
	        vmList : List<Triad<PowerContainerVm>>,
	        correlation : Correlation
	) : List<Pair<PowerContainer, PowerContainerVm>> {
		assert(containerList.size <= vmList.size)

		val link = mutableMapOf<PowerContainer, PowerContainerVm>()
		val relink = mutableMapOf<PowerContainerVm, PowerContainer>()
		var modifyNumber = 0
		do {
			modifyNumber = 0
			for(c in containerList) {
				if (link[c] == null) {
					val vmSortedList = correlation.getVmListSorted(vmList, c)
					for(vm in vmSortedList) {
						val rec = relink[vm.ext]
						if (rec != null) {
							if(correlation.isCorrelationConatiner1Greater2(c, rec, vm)) {
								link[c] = vm.ext
								relink[vm.ext] = c
								link.remove(rec)
								modifyNumber++
								break
							}
						} else {
							link[c] = vm.ext
							relink[vm.ext] = c
							modifyNumber++
							break
						}
					}
				}
			}
		} while (modifyNumber > 0)
		return link.map { it.toPair() }
	}
}

class GaleShapleyMM(private val correlation : Correlation) {
	fun optimizeAllocate(containerList : List<PowerContainer>, vmList : List<PowerContainerVm>) : List<Pair<PowerContainer, PowerContainerVm>> {
		val r = mutableListOf<Pair<PowerContainer, PowerContainerVm>>()
		val vmResourceList : MutableList<Triad<PowerContainerVm>> = vmList.map { Triad(it.availableMips, it.ram.toDouble(), it) }.toMutableList()
		var cList = containerList.toMutableList()
		while (cList.size > 0) {
			val op = allocate(cList, vmResourceList, correlation)
			r.addAll(op)
			cList.removeAll(op.map { it.first })
			for (ac in op) {
				val vmc = vmResourceList.find { ac.second === it.ext }
				if (vmc != null) {
					vmc.mips -= ac.first.currentRequestedTotalMips
					vmc.ram -= ac.first.currentRequestedRam
				}
			}
		}
		return r
	}

	fun allocate(
			containerList : List<PowerContainer>,
			vmList : List<Triad<PowerContainerVm>>,
			correlation : Correlation
	) : List<Pair<PowerContainer, PowerContainerVm>> {
		val link = mutableMapOf<PowerContainer, PowerContainerVm>()
		val relink = mutableMapOf<PowerContainerVm, PowerContainer>()
		var modifyNumber = 0
		do {
			modifyNumber = 0
			for(c in containerList) {
				if (link[c] == null) {
					val vmSortedList = correlation.getVmListSorted(vmList, c)
					for(vm in vmSortedList) {
						val rec = relink[vm.ext]
						if (rec != null) {
							if(correlation.isCorrelationConatiner1Greater2(c, rec, vm)) {
								link[c] = vm.ext
								relink[vm.ext] = c
								link.remove(rec)
								modifyNumber++
								break
							}
						} else {
							link[c] = vm.ext
							relink[vm.ext] = c
							modifyNumber++
							break
						}
					}
				}
			}
		} while (modifyNumber > 0)
		return link.map { it.toPair() }
	}
}
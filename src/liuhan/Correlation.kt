package liuhan

import cloudsim.container.core.PowerContainer
import cloudsim.container.core.PowerContainerVm

/**
 * Created by liuha on 2017/05/22 0022.
 */

open abstract class Correlation {
	abstract fun getVmListSorted(vmList : List<Triad<PowerContainerVm>>, c : PowerContainer) : List<Triad<PowerContainerVm>>
	abstract fun isCorrelationConatiner1Greater2(c1 : PowerContainer, c2 : PowerContainer, vm : Triad<PowerContainerVm>) : Boolean
}

class EuclideanMetricCorrelation : Correlation() {
	override fun getVmListSorted(vmList : List<Triad<PowerContainerVm>>, c : PowerContainer) : List<Triad<PowerContainerVm>> {
		var l = mutableListOf<Pair<Double, Triad<PowerContainerVm>>>()
		for(vm in vmList) {
			l.add(Pair(getVmEuclideanMetric(c, vm), vm))
		}
		l.sortBy { it.first }
		return l.map { it.second }
	}

	override fun isCorrelationConatiner1Greater2(c1 : PowerContainer, c2 : PowerContainer, vm : Triad<PowerContainerVm>) : Boolean {
		return getVmEuclideanMetric(c1, vm) < getVmEuclideanMetric(c2, vm)
	}

	private fun getVmEuclideanMetric(c : PowerContainer, vm : Triad<PowerContainerVm>) : Double {
		return euclideanMetric(c.currentRequestedTotalMips, c.currentRequestedRam.toDouble(), vm.mips, vm.ram)
	}

	private fun euclideanMetric(x1 : Double, y1 : Double, x2 : Double, y2 : Double) : Double {
		return Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0))
	}
}

class TanimotoCorrelation : Correlation() {
	override fun getVmListSorted(vmList : List<Triad<PowerContainerVm>>, c : PowerContainer) : List<Triad<PowerContainerVm>> {
		var l = mutableListOf<Pair<Double, Triad<PowerContainerVm>>>()
		for(vm in vmList) {
			l.add(Pair(getVmTanimoto(c, vm), vm))
		}
		l.sortedByDescending { it.first }

		return l.map { it.second }
	}

	override fun isCorrelationConatiner1Greater2(c1 : PowerContainer, c2 : PowerContainer, vm : Triad<PowerContainerVm>) : Boolean {
		return getVmTanimoto(c1, vm) > getVmTanimoto(c2, vm)
	}

	private fun getVmTanimoto(c : PowerContainer, vm : Triad<PowerContainerVm>) : Double {
		return tanimoto(c.currentRequestedTotalMips, vm.mips) + tanimoto(c.currentRequestedRam.toDouble(), vm.ram)
	}

	private fun tanimoto(x : Double, y : Double) : Double {
		return x*y / Math.abs(x + y - x*y)
	}
}

class CosCorrelation : Correlation() {
	override fun getVmListSorted(vmList : List<Triad<PowerContainerVm>>, c : PowerContainer) : List<Triad<PowerContainerVm>> {
		var l = mutableListOf<Pair<Double, Triad<PowerContainerVm>>>()
		for(vm in vmList) {
			l.add(Pair(getVmCos(c, vm), vm))
		}
		l.sortedBy{ it.first }

		return l.map { it.second }
	}

	override fun isCorrelationConatiner1Greater2(c1 : PowerContainer, c2 : PowerContainer, vm : Triad<PowerContainerVm>) : Boolean {
		return getVmCos(c1, vm) < getVmCos(c2, vm)
	}

	private fun getVmCos(c : PowerContainer, vm : Triad<PowerContainerVm>) : Double {
		return cos(c.currentRequestedTotalMips, c.currentRequestedRam.toDouble(), vm.mips, vm.ram)
	}

	private fun cos(x1 : Double, y1 : Double, x2 : Double, y2 : Double) : Double {
		return (x1*y1+x2*y2) / (Math.sqrt(x1*x1 + x2*x2) * Math.sqrt(y1*y1 + y2*y2))
	}
}
package cloudsim.container.resourceAllocators;

import cloudsim.container.core.ContainerDatacenter;
import cloudsim.container.core.ContainerHost;
import cloudsim.container.core.ContainerVm;

import java.util.List;
import java.util.Map;

/**
 * Created by sareh on 14/07/15.
 */
public class PowerContainerVmAllocationSimple extends PowerContainerVmAllocationAbstract {

    public PowerContainerVmAllocationSimple(List<? extends ContainerHost> list) {
        super(list);
    }

    @Override

    public List<Map<String, Object>> optimizeAllocation(List<? extends ContainerVm> vmList) {
        return null;
    }

    @Override
    public void setDatacenter(ContainerDatacenter datacenter) {

    }
}

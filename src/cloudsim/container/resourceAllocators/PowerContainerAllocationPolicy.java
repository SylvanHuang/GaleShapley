package cloudsim.container.resourceAllocators;

import cloudsim.Log;
import cloudsim.container.core.Container;
import cloudsim.container.core.ContainerVm;
import cloudsim.core.CloudSim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sareh on 16/07/15.
 */
public abstract class PowerContainerAllocationPolicy extends ContainerAllocationPolicy {

    /**
     * The container table.
     */
    private final Map<String, ContainerVm> containerTable = new HashMap<>();

    /**
     * Instantiates a new power vm allocation policy abstract.
     */
    public PowerContainerAllocationPolicy() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see cloudsim.VmAllocationPolicy#allocateHostForVm(cloudsim.Vm)
     */
    @Override
    public boolean allocateVmForContainer(Container container, List<ContainerVm> containerVmList) {
        setContainerVmList(containerVmList);
        return allocateVmForContainer(container, findVmForContainer(container));
    }

    /*
     * (non-Javadoc)
     * @see cloudsim.VmAllocationPolicy#allocateHostForVm(cloudsim.Vm,
     * cloudsim.Host)
     */
    @Override
    public boolean allocateVmForContainer(Container container, ContainerVm containerVm) {
        if (containerVm == null) {
            Log.formatLine("%.2f: No suitable VM found for Container#" + container.getId() + "\n", CloudSim.clock());
            return false;
        }
        if (containerVm.containerCreate(container)) {
            getContainerTable().put(container.getUid(), containerVm);
            Log.formatLine(
                    "%.2f: Container #" + container.getId() + " has been allocated to the VM #" + containerVm.getId(),
                    CloudSim.clock());
            return true;
        }
        Log.formatLine(
                "%.2f: Creation of Container #" + container.getId() + " on the Vm #" + containerVm.getId() + " failed\n",
                CloudSim.clock());
        return false;
    }

    /**
     * Find host for vm.
     *
     * @param container the vm
     * @return the power host
     */
    public ContainerVm findVmForContainer(Container container) {
        for (ContainerVm containerVm : getContainerVmList()) {
            if (containerVm.isSuitableForContainer(container)) {
                return containerVm;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see cloudsim.VmAllocationPolicy#deallocateHostForVm(cloudsim.Vm)
     */
    @Override
    public void deallocateVmForContainer(Container container) {
        ContainerVm containerVm = getContainerTable().remove(container.getUid());
        if (containerVm != null) {
            containerVm.containerDestroy(container);
        }
    }

    /*
     * (non-Javadoc)
     * @see cloudsim.VmAllocationPolicy#getHost(cloudsim.Vm)
     */
    @Override
    public ContainerVm getContainerVm(Container container) {
        return getContainerTable().get(container.getUid());
    }

    /*
     * (non-Javadoc)
     * @see cloudsim.VmAllocationPolicy#getHost(int, int)
     */
    @Override
    public ContainerVm getContainerVm(int containerId, int userId) {
        return getContainerTable().get(Container.getUid(userId, containerId));
    }

    /**
     * Gets the vm table.
     *
     * @return the vm table
     */
    public Map<String, ContainerVm> getContainerTable() {
        return containerTable;
    }

}




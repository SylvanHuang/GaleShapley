package cloudsim.container.vmSelectionPolicies;


import cloudsim.container.core.ContainerVm;
import cloudsim.container.core.PowerContainerHost;
import cloudsim.container.core.PowerContainerVm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sareh on 28/07/15.
 */
public abstract class PowerContainerVmSelectionPolicy {

    /**
     * Gets the vms to migrate.
     *
     * @param host the host
     * @return the vms to migrate
     */
    public abstract ContainerVm getVmToMigrate(PowerContainerHost host);

    /**
     * Gets the migratable vms.
     *
     * @param host the host
     * @return the migratable vms
     */
    protected List<PowerContainerVm> getMigratableVms(PowerContainerHost host) {
        List<PowerContainerVm> migratableVms = new ArrayList<>();
        for (PowerContainerVm vm : host.<PowerContainerVm>getVmList()) {
            if (!vm.isInMigration()) {
                migratableVms.add(vm);
            }
        }
        return migratableVms;
    }

}


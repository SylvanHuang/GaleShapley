package examples.container;

import cloudsim.*;
import cloudsim.container.containerProvisioners.ContainerBwProvisionerSimple;
import cloudsim.container.containerProvisioners.ContainerPe;
import cloudsim.container.containerProvisioners.ContainerRamProvisionerSimple;
import cloudsim.container.containerProvisioners.CotainerPeProvisionerSimple;
import cloudsim.container.containerVmProvisioners.ContainerVmBwProvisionerSimple;
import cloudsim.container.containerVmProvisioners.ContainerVmPe;
import cloudsim.container.containerVmProvisioners.ContainerVmPeProvisionerSimple;
import cloudsim.container.containerVmProvisioners.ContainerVmRamProvisionerSimple;
import cloudsim.container.core.*;
import cloudsim.container.hostSelectionPolicies.HostSelectionPolicy;
import cloudsim.container.hostSelectionPolicies.HostSelectionPolicyFirstFit;
import cloudsim.container.resourceAllocatorMigrationEnabled.PowerContainerVmAllocationPolicyMigrationAbstractHostSelection;
import cloudsim.container.resourceAllocators.ContainerAllocationPolicy;
import cloudsim.container.resourceAllocators.ContainerVmAllocationPolicy;
import cloudsim.container.resourceAllocators.PowerContainerAllocationPolicySimple;
import cloudsim.container.schedulers.ContainerCloudletSchedulerDynamicWorkload;
import cloudsim.container.schedulers.ContainerSchedulerTimeSharedOverSubscription;
import cloudsim.container.schedulers.ContainerVmSchedulerTimeSharedOverSubscription;
import cloudsim.container.utils.IDs;
import cloudsim.container.vmSelectionPolicies.PowerContainerVmSelectionPolicy;
import cloudsim.container.vmSelectionPolicies.PowerContainerVmSelectionPolicyMaximumUsage;
import cloudsim.core.CloudSim;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple example showing how to create a data center with one host, one VM, one container and run one cloudlet on it.
 */
public class ContainerCloudSimExample1 {

    /**
     * The cloudlet list.
     */
    private static List<ContainerCloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private static List<ContainerVm> vmList;

    /**
     * The vmlist.
     */

    private static List<Container> containerList;

    /**
     * The hostList.
     */

    private static List<ContainerHost> hostList;

    /**
     * Creates main() to run this example.
     *
     * @param args the args
     */

    public static void main(String[] args) {
        Log.printLine("Starting ContainerCloudSimExample1...");

        try {
            /**
             * number of cloud Users
             */
            int num_user = 1;
            /**
             *  The fields of calender have been initialized with the current date and time.
             */
            Calendar calendar = Calendar.getInstance();
            /**
             * Deactivating the event tracing
             */
            boolean trace_flag = false;
            /**
             * 1- Like CloudSim the first step is initializing the CloudSim Package before creating any entities.
             *
             */


            CloudSim.init(num_user, calendar, trace_flag);
            /**
             * 2-  Defining the container allocation Policy. This policy determines how Containers are
             * allocated to VMs in the data center.
             *
             */


            ContainerAllocationPolicy containerAllocationPolicy = new PowerContainerAllocationPolicySimple();

            /**
             * 3-  Defining the VM selection Policy. This policy determines which VMs should be selected for migration
             * when a host is identified as over-loaded.
             *
             */

            PowerContainerVmSelectionPolicy vmSelectionPolicy = new PowerContainerVmSelectionPolicyMaximumUsage();


            /**
             * 4-  Defining the host selection Policy. This policy determines which hosts should be selected as
             * migration destination.
             *
             */
            HostSelectionPolicy hostSelectionPolicy = new HostSelectionPolicyFirstFit();
            /**
             * 5- Defining the thresholds for selecting the under-utilized and over-utilized hosts.
             */

            double overUtilizationThreshold = 0.80;
            double underUtilizationThreshold = 0.70;
            /**
             * 6- The host list is created considering the number of hosts, and host types which are specified
             * in the {@link ConstantsExamples}.
             */
            hostList = new ArrayList<ContainerHost>();
            hostList = createHostList(ConstantsExamples.NUMBER_HOSTS);
            cloudletList = new ArrayList<ContainerCloudlet>();
            vmList = new ArrayList<ContainerVm>();
            /**
             * 7- The container allocation policy  which defines the allocation of VMs to containers.
             */
            ContainerVmAllocationPolicy vmAllocationPolicy = new
                    PowerContainerVmAllocationPolicyMigrationAbstractHostSelection(hostList, vmSelectionPolicy,
                    hostSelectionPolicy, overUtilizationThreshold, underUtilizationThreshold);

            /**
             * 8- The overbooking factor for allocating containers to VMs. This factor is used by the broker for the
             * allocation process.
             */
            int overBookingFactor = 80;
            ContainerDatacenterBroker broker = createBroker(overBookingFactor);
            int brokerId = broker.getId();
            /**
             * 9- Creating the cloudlet, container and VM lists for submitting to the broker.
             */
            cloudletList = createContainerCloudletList(brokerId, ConstantsExamples.NUMBER_CLOUDLETS);
            containerList = createContainerList(brokerId, ConstantsExamples.NUMBER_CLOUDLETS);
            vmList = createVmList(brokerId, ConstantsExamples.NUMBER_VMS);
            /**
             * 10- The address for logging the statistics of the VMs, containers in the data center.
             */
            String logAddress = "~/Results";

            @SuppressWarnings("unused")
            PowerContainerDatacenter e = createDatacenter(
                    "datacenter",
                    hostList, vmAllocationPolicy, containerAllocationPolicy,
                    getExperimentName("ContainerCloudSimExample-1", String.valueOf(overBookingFactor)),
                    ConstantsExamples.SCHEDULING_INTERVAL, logAddress,
                    ConstantsExamples.VM_STARTTUP_DELAY, ConstantsExamples.CONTAINER_STARTTUP_DELAY);


            /**
             * 11- Submitting the cloudlet's , container's , and VM's lists to the broker.
             */
            broker.submitCloudletList(cloudletList.subList(0, containerList.size()));
            broker.submitContainerList(containerList);
            broker.submitVmList(vmList);
            /**
             * 12- Determining the simulation termination time according to the cloudlet's workload.
             */
            CloudSim.terminateSimulation(86400.00);
            /**
             * 13- Starting the simualtion.
             */
            CloudSim.startSimulation();
            /**
             * 14- Stopping the simualtion.
             */
            CloudSim.stopSimulation();
            /**
             * 15- Printing the results when the simulation is finished.
             */
            List<ContainerCloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);

            Log.printLine("ContainerCloudSimExample1 finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }


    /**
     * It creates a specific name for the experiment which is used for creating the Log address folder.
     */

    private static String getExperimentName(String... args) {
        StringBuilder experimentName = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            if (!args[i].isEmpty()) {
                if (i != 0) {
                    experimentName.append("_");
                }
                experimentName.append(args[i]);
            }
        }
        return experimentName.toString();
    }

    /**
     * Creates the broker.
     *
     * @param overBookingFactor
     * @return the datacenter broker
     */
    private static ContainerDatacenterBroker createBroker(int overBookingFactor) {

        ContainerDatacenterBroker broker = null;

        try {
            broker = new ContainerDatacenterBroker("Broker", overBookingFactor);
        } catch (Exception var2) {
            var2.printStackTrace();
            System.exit(0);
        }

        return broker;
    }

    /**
     * Prints the Cloudlet objects.
     *
     * @param list list of Cloudlets
     */
    private static void printCloudletList(List<ContainerCloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
                + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatusString() == "Success") {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId()
                        + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent
                        + dft.format(cloudlet.getActualCPUTime()) + indent
                        + indent + dft.format(cloudlet.getExecStartTime())
                        + indent + indent
                        + dft.format(cloudlet.getFinishTime()));
            }
        }
    }

    /**
     * Create the Virtual machines and add them to the list
     *
     * @param brokerId
     * @param containerVmsNumber
     */
    private static ArrayList<ContainerVm> createVmList(int brokerId, int containerVmsNumber) {
        ArrayList<ContainerVm> containerVms = new ArrayList<ContainerVm>();

        for (int i = 0; i < containerVmsNumber; ++i) {
            ArrayList<ContainerPe> peList = new ArrayList<ContainerPe>();
            int vmType = i / (int) Math.ceil((double) containerVmsNumber / 4.0D);
            for (int j = 0; j < ConstantsExamples.VM_PES[vmType]; ++j) {
                peList.add(new ContainerPe(j,
                        new CotainerPeProvisionerSimple((double) ConstantsExamples.VM_MIPS[vmType])));
            }
            containerVms.add(new PowerContainerVm(IDs.pollId(ContainerVm.class), brokerId,
                    (double) ConstantsExamples.VM_MIPS[vmType], (float) ConstantsExamples.VM_RAM[vmType],
                    ConstantsExamples.VM_BW, ConstantsExamples.VM_SIZE, "Xen",
                    new ContainerSchedulerTimeSharedOverSubscription(peList),
                    new ContainerRamProvisionerSimple(ConstantsExamples.VM_RAM[vmType]),
                    new ContainerBwProvisionerSimple(ConstantsExamples.VM_BW),
                    peList, ConstantsExamples.SCHEDULING_INTERVAL));


        }

        return containerVms;
    }

    /**
     * Create the host list considering the specs listed in the {@link ConstantsExamples}.
     *
     * @param hostsNumber
     * @return
     */


    public static List<ContainerHost> createHostList(int hostsNumber) {
        ArrayList<ContainerHost> hostList = new ArrayList<ContainerHost>();
        for (int i = 0; i < hostsNumber; ++i) {
            int hostType = i / (int) Math.ceil((double) hostsNumber / 3.0D);
            ArrayList<ContainerVmPe> peList = new ArrayList<ContainerVmPe>();
            for (int j = 0; j < ConstantsExamples.HOST_PES[hostType]; ++j) {
                peList.add(new ContainerVmPe(j,
                        new ContainerVmPeProvisionerSimple((double) ConstantsExamples.HOST_MIPS[hostType])));
            }

            hostList.add(
                    new PowerContainerHostUtilizationHistory(
                            IDs.pollId(ContainerHost.class),
                            new ContainerVmRamProvisionerSimple(ConstantsExamples.HOST_RAM[hostType]),
                            new ContainerVmBwProvisionerSimple(1000000L),
                            1000000L,
                            peList,
                            new ContainerVmSchedulerTimeSharedOverSubscription(peList),
                            ConstantsExamples.HOST_POWER[hostType]
                    )
            );
        }

        return hostList;
    }


    /**
     * Create the data center
     *
     * @param name
     * @param hostList
     * @param vmAllocationPolicy
     * @param containerAllocationPolicy
     * @param experimentName
     * @param logAddress
     * @return
     * @throws Exception
     */

    public static PowerContainerDatacenter createDatacenter(String name,
                                                            List<ContainerHost> hostList,
                                                            ContainerVmAllocationPolicy vmAllocationPolicy,
                                                            ContainerAllocationPolicy containerAllocationPolicy,
                                                            String experimentName, double schedulingInterval, String logAddress, double VMStartupDelay,
                                                            double ContainerStartupDelay) throws Exception {
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0D;
        double cost = 3.0D;
        double costPerMem = 0.05D;
        double costPerStorage = 0.001D;
        double costPerBw = 0.0D;
        ContainerDatacenterCharacteristics characteristics = new
                ContainerDatacenterCharacteristics(arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage,
                costPerBw);

        return new PowerContainerDatacenter(name, characteristics, vmAllocationPolicy,
                containerAllocationPolicy, new LinkedList<Storage>(), schedulingInterval, experimentName, logAddress);
    }

    /**
     * create the containers for hosting the cloudlets and binding them together.
     *
     * @param brokerId
     * @param containersNumber
     * @return
     */

    public static List<Container> createContainerList(int brokerId, int containersNumber) {
        ArrayList<Container> containers = new ArrayList<Container>();

        for (int i = 0; i < containersNumber; ++i) {
            int containerType = i / (int) Math.ceil((double) containersNumber / 3.0D);

            containers.add(
                    new PowerContainer(
                            IDs.pollId(Container.class),
                            brokerId,
                            (double) ConstantsExamples.CONTAINER_MIPS[containerType],
                            ConstantsExamples.CONTAINER_PES[containerType],
                            ConstantsExamples.CONTAINER_RAM[containerType],
                            ConstantsExamples.CONTAINER_BW,
                            0L, "Xen",
                            //new ContainerCloudletSchedulerTimeShared(),
                            new ContainerCloudletSchedulerDynamicWorkload(ConstantsExamples.CONTAINER_MIPS[containerType], ConstantsExamples.CONTAINER_PES[containerType]),
                            ConstantsExamples.SCHEDULING_INTERVAL
                    ));
        }

        return containers;
    }

    /**
     * Creating the cloudlet list that are going to run on containers
     *
     * @param brokerId
     * @param numberOfCloudlets
     * @return
     * @throws FileNotFoundException
     */
    public static List<ContainerCloudlet> createContainerCloudletList(int brokerId, int numberOfCloudlets)
            throws FileNotFoundException {
        String inputFolderName = ContainerCloudSimExample1.class.getClassLoader().getResource("workload/planetlab").getPath();
        ArrayList<ContainerCloudlet> cloudletList = new ArrayList<ContainerCloudlet>();
        long fileSize = 300L;
        long outputSize = 300L;
        UtilizationModelNull utilizationModelNull = new UtilizationModelNull();
        //UtilizationModelFull utilizationModelFull = new UtilizationModelFull();
        java.io.File inputFolder1 = new java.io.File(inputFolderName);
        java.io.File[] files1 = inputFolder1.listFiles();
        int createdCloudlets = 0;
        for (java.io.File aFiles1 : files1) {
            java.io.File inputFolder = new java.io.File(aFiles1.toString());
            java.io.File[] files = inputFolder.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (createdCloudlets < numberOfCloudlets) {
                    ContainerCloudlet cloudlet = null;

                    try {
                        cloudlet = new ContainerCloudlet(IDs.pollId(ContainerCloudlet.class), ConstantsExamples.CLOUDLET_LENGTH, 1,
                                fileSize, outputSize,
                                new UtilizationModelPlanetLabInMemory(files[i].getAbsolutePath(), 300.0D),
                                //new UtilizationModelPlanetLabInMemoryExtended(files[i].getAbsolutePath(), 300.0D),
                                utilizationModelNull, utilizationModelNull);
                    } catch (Exception var13) {
                        var13.printStackTrace();
                        System.exit(0);
                    }

                    cloudlet.setUserId(brokerId);
                    cloudletList.add(cloudlet);
                    createdCloudlets += 1;
                } else {

                    return cloudletList;
                }
            }

        }
        return cloudletList;
    }

}

package examples.power.random;

import cloudsim.Log;
import cloudsim.core.CloudSim;
import examples.power.Helper;
import examples.power.RunnerAbstract;

import java.util.Calendar;

/**
 * The example runner for the random workload.
 * <p>
 * If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * <p>
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 *
 * @author Anton Beloglazov
 * @since Jan 5, 2012
 */
public class RandomRunner extends RunnerAbstract {

    /**
     * @param enableOutput
     * @param outputToFile
     * @param inputFolder
     * @param outputFolder
     * @param workload
     * @param vmAllocationPolicy
     * @param vmSelectionPolicy
     * @param parameter
     */
    public RandomRunner(
            boolean enableOutput,
            boolean outputToFile,
            String inputFolder,
            String outputFolder,
            String workload,
            String vmAllocationPolicy,
            String vmSelectionPolicy,
            String parameter) {
        super(
                enableOutput,
                outputToFile,
                inputFolder,
                outputFolder,
                workload,
                vmAllocationPolicy,
                vmSelectionPolicy,
                parameter);
    }

    /*
     * (non-Javadoc)
     *
     * @see examples.power.RunnerAbstract#init(java.lang.String)
     */
    @Override
    protected void init(String inputFolder) {
        try {
            CloudSim.init(1, Calendar.getInstance(), false);

            broker = Helper.createBroker();
            int brokerId = broker.getId();

            cloudletList = RandomHelper.createCloudletList(brokerId, RandomConstants.NUMBER_OF_VMS);
            vmList = Helper.createVmList(brokerId, cloudletList.size());
            hostList = Helper.createHostList(RandomConstants.NUMBER_OF_HOSTS);
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
            System.exit(0);
        }
    }

}

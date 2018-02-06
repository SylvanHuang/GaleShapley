/*
 *
 */
package examples.power.random;

import cloudsim.Cloudlet;
import cloudsim.UtilizationModel;
import cloudsim.UtilizationModelNull;
import cloudsim.UtilizationModelStochastic;
import examples.power.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * The Helper class for the random workload.
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
public class RandomHelper {

    /**
     * Creates the cloudlet list.
     *
     * @param brokerId        the broker id
     * @param cloudletsNumber the cloudlets number
     * @return the list< cloudlet>
     */
    public static List<Cloudlet> createCloudletList(int brokerId, int cloudletsNumber) {
        List<Cloudlet> list = new ArrayList<Cloudlet>();

        long fileSize = 300;
        long outputSize = 300;
        long seed = RandomConstants.CLOUDLET_UTILIZATION_SEED;
        UtilizationModel utilizationModelNull = new UtilizationModelNull();

        for (int i = 0; i < cloudletsNumber; i++) {
            Cloudlet cloudlet = null;
            if (seed == -1) {
                cloudlet = new Cloudlet(
                        i,
                        Constants.CLOUDLET_LENGTH,
                        Constants.CLOUDLET_PES,
                        fileSize,
                        outputSize,
                        new UtilizationModelStochastic(),
                        utilizationModelNull,
                        utilizationModelNull);
            } else {
                cloudlet = new Cloudlet(
                        i,
                        Constants.CLOUDLET_LENGTH,
                        Constants.CLOUDLET_PES,
                        fileSize,
                        outputSize,
                        new UtilizationModelStochastic(seed * i),
                        utilizationModelNull,
                        utilizationModelNull);
            }
            cloudlet.setUserId(brokerId);
            cloudlet.setVmId(i);
            list.add(cloudlet);
        }

        return list;
    }

}

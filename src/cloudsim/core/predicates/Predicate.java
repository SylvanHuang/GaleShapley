/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package cloudsim.core.predicates;

import cloudsim.core.SimEvent;

/**
 * Predicates are used to select events from the deferred queue, according to
 * required criteria.
 * They are used internally the by {@link cloudsim.core.CloudSim} class
 * and aren't intended to be used directly by the user.
 * <p>
 * This class is abstract and must be
 * extended when writing a new predicate. Each subclass define
 * the criteria to select received events.
 * <p>
 * Some standard predicates are provided.<br/>
 * The idea of simulation predicates was copied from SimJava 2.
 *
 * @author Marcos Dias de Assuncao
 * @todo It would be an interface, since it doesn't have any attributes, just
 * abstract methods.
 * @todo There already is a native java {@link java.util.function.Predicate} interface.
 * Maybe it was introduced with Java 8 (due to Stream and Lambda functions).
 * @see PredicateType
 * @see PredicateFrom
 * @see PredicateAny
 * @see PredicateNone
 * @see Simulation
 * @since CloudSim Toolkit 1.0
 */
public abstract class Predicate {

    /**
     * Verifies if a given event matches the required criteria.
     * The method is called for each event in the deferred queue when a method such as
     * {@link cloudsim.core.CloudSim#select(int, cloudsim.core.predicates.Predicate) }
     * is called.
     *
     * @param event The event to test for a match.
     * @return <code>true</code> if the event matches and should be
     * selected, or <code>false</code> if it does not match the predicate.
     */
    public abstract boolean match(SimEvent event);

}

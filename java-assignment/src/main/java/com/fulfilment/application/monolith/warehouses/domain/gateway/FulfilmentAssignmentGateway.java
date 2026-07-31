package com.fulfilment.application.monolith.warehouses.domain.gateway;

import com.fulfilment.application.monolith.warehouses.domain.models.FulfilmentAssignment;
import com.fulfilment.application.monolith.warehouses.domain.ports.FulfilmentAssignmentResolver;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FulfilmentAssignmentGateway implements FulfilmentAssignmentResolver {
    private static final Logger LOGGER = Logger.getLogger(FulfilmentAssignmentGateway.class);

    private static final List<FulfilmentAssignment> assignments = new ArrayList<>();

    static {
        assignments.add(new FulfilmentAssignment(1L, 1L, "MWH.001"));
        assignments.add(new FulfilmentAssignment(1L, 1L, "MWH.012"));
        assignments.add(new FulfilmentAssignment(2L, 2L, "MWH.023"));
    }

    public List<FulfilmentAssignment> getAssignments() {

        LOGGER.infof("Fetching assignments (total=%d)", assignments.size());
        return List.copyOf(assignments);
    }

    public void add(FulfilmentAssignment assignment) {
        LOGGER.infof("Adding new assignment: %s", assignment);

        assignments.add(assignment);

        LOGGER.infof("Assignment added successfully. newTotal=%d", assignments.size());
    }
}

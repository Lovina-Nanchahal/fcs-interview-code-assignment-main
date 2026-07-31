package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.FulfilmentAssignment;
import java.util.List;

public interface FulfilmentAssignmentResolver {

    List<FulfilmentAssignment> getAssignments();

    void add(FulfilmentAssignment assignment);
}
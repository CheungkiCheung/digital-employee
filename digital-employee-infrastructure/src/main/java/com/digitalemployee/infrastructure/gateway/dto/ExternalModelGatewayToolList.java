package com.digitalemployee.infrastructure.gateway.dto;

import java.util.ArrayList;
import java.util.Collection;

public class ExternalModelGatewayToolList extends ArrayList<ExternalModelGatewayToolDTO> {

    private static final long serialVersionUID = -2911455887011138834L;

    public ExternalModelGatewayToolList(Collection<ExternalModelGatewayToolDTO> tools) {
        super(tools);
    }

    @Override
    public boolean contains(Object target) {
        if (target instanceof String) {
            return stream().anyMatch(tool -> target.equals(tool.getName()));
        }
        return super.contains(target);
    }

}

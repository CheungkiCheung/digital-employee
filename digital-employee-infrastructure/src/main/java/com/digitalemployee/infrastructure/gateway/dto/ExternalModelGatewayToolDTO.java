package com.digitalemployee.infrastructure.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ExternalModelGatewayToolDTO implements Serializable {

    private static final long serialVersionUID = -4505250468429032213L;

    private String name;
    private String description;
    private String defaultPermissionBehavior;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof String) {
            return name != null && name.equals(other);
        }
        if (!(other instanceof ExternalModelGatewayToolDTO)) {
            return false;
        }
        ExternalModelGatewayToolDTO that = (ExternalModelGatewayToolDTO) other;
        return equalsValue(name, that.name)
                && equalsValue(description, that.description)
                && equalsValue(defaultPermissionBehavior, that.defaultPermissionBehavior);
    }

    @Override
    public int hashCode() {
        int result = name == null ? 0 : name.hashCode();
        result = 31 * result + (description == null ? 0 : description.hashCode());
        result = 31 * result + (defaultPermissionBehavior == null ? 0 : defaultPermissionBehavior.hashCode());
        return result;
    }

    private boolean equalsValue(String left, String right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }

}

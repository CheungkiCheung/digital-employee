package com.digitalemployee.domain.conversation.service;

import com.digitalemployee.domain.conversation.model.valobj.PermissionBehaviorVO;
import com.digitalemployee.domain.conversation.model.valobj.ToolDescriptorVO;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ToolRegistryDomainService {

    private static final String FILE_READ_TOOL_NAME = "file_read";
    private static final String FILE_WRITE_TOOL_NAME = "file_write";
    private static final String FILE_EDIT_TOOL_NAME = "file_edit";
    private static final String BASH_TOOL_NAME = "bash";

    private final Map<String, ToolDescriptorVO> descriptors;

    private ToolRegistryDomainService(Map<String, ToolDescriptorVO> descriptors) {
        this.descriptors = Collections.unmodifiableMap(descriptors);
    }

    public static ToolRegistryDomainService defaultRegistry() {
        ToolDescriptorVO fileRead = ToolDescriptorVO.builder()
                .name(FILE_READ_TOOL_NAME)
                .description("Read a UTF-8 workspace file after permission evaluation.")
                .defaultPermissionBehavior(PermissionBehaviorVO.ASK)
                .build();
        ToolDescriptorVO fileWrite = ToolDescriptorVO.builder()
                .name(FILE_WRITE_TOOL_NAME)
                .description("Write UTF-8 content to a workspace file after permission evaluation.")
                .defaultPermissionBehavior(PermissionBehaviorVO.ASK)
                .build();
        ToolDescriptorVO fileEdit = ToolDescriptorVO.builder()
                .name(FILE_EDIT_TOOL_NAME)
                .description("Edit a UTF-8 workspace file by replacing expected text after permission evaluation.")
                .defaultPermissionBehavior(PermissionBehaviorVO.ASK)
                .build();
        ToolDescriptorVO bash = ToolDescriptorVO.builder()
                .name(BASH_TOOL_NAME)
                .description("Evaluate a workspace-safe bash command through Domain permission policy.")
                .defaultPermissionBehavior(PermissionBehaviorVO.ASK)
                .build();
        return new ToolRegistryDomainService(Map.of(
                FILE_READ_TOOL_NAME, fileRead,
                FILE_WRITE_TOOL_NAME, fileWrite,
                FILE_EDIT_TOOL_NAME, fileEdit,
                BASH_TOOL_NAME, bash
        ));
    }

    public boolean isRegistered(String toolName) {
        return descriptors.containsKey(toolName);
    }

    public ToolDescriptorVO getDescriptor(String toolName) {
        return descriptors.get(toolName);
    }

    public List<ToolDescriptorVO> listDescriptors() {
        return List.copyOf(descriptors.values());
    }

}

package com.digitalemployee.domain.conversation.service;

import com.digitalemployee.domain.conversation.model.valobj.PermissionBehaviorVO;
import com.digitalemployee.domain.conversation.model.valobj.ToolDescriptorVO;
import org.junit.Assert;
import org.junit.Test;

public class ToolRegistryDomainServiceTest {

    @Test
    public void shouldRegisterFileReadToolByDefault() {
        ToolRegistryDomainService registry = ToolRegistryDomainService.defaultRegistry();

        Assert.assertTrue(registry.isRegistered("file_read"));
    }

    @Test
    public void shouldExposeFileReadToolDescriptor() {
        ToolRegistryDomainService registry = ToolRegistryDomainService.defaultRegistry();

        ToolDescriptorVO descriptor = registry.getDescriptor("file_read");

        Assert.assertEquals("file_read", descriptor.getName());
        Assert.assertTrue(descriptor.getDescription().contains("workspace file"));
        Assert.assertEquals(PermissionBehaviorVO.ASK, descriptor.getDefaultPermissionBehavior());
    }

    @Test
    public void shouldExposeFileWriteToolDescriptor() {
        ToolRegistryDomainService registry = ToolRegistryDomainService.defaultRegistry();

        ToolDescriptorVO descriptor = registry.getDescriptor("file_write");

        Assert.assertNotNull(descriptor);
        Assert.assertTrue(registry.isRegistered("file_write"));
        Assert.assertEquals("file_write", descriptor.getName());
        Assert.assertTrue(descriptor.getDescription().contains("Write"));
        Assert.assertEquals(PermissionBehaviorVO.ASK, descriptor.getDefaultPermissionBehavior());
    }

    @Test
    public void shouldExposeFileEditToolDescriptor() {
        ToolRegistryDomainService registry = ToolRegistryDomainService.defaultRegistry();

        ToolDescriptorVO descriptor = registry.getDescriptor("file_edit");

        Assert.assertNotNull(descriptor);
        Assert.assertTrue(registry.isRegistered("file_edit"));
        Assert.assertEquals("file_edit", descriptor.getName());
        Assert.assertTrue(descriptor.getDescription().contains("Edit"));
        Assert.assertEquals(PermissionBehaviorVO.ASK, descriptor.getDefaultPermissionBehavior());
    }

    @Test
    public void shouldExposeBashToolDescriptor() {
        ToolRegistryDomainService registry = ToolRegistryDomainService.defaultRegistry();

        ToolDescriptorVO descriptor = registry.getDescriptor("bash");

        Assert.assertNotNull(descriptor);
        Assert.assertTrue(registry.isRegistered("bash"));
        Assert.assertEquals("bash", descriptor.getName());
        Assert.assertTrue(descriptor.getDescription().contains("command"));
        Assert.assertEquals(PermissionBehaviorVO.ASK, descriptor.getDefaultPermissionBehavior());
    }

}

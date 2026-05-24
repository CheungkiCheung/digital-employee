package com.digitalemployee.domain.task.service;

import com.digitalemployee.domain.task.model.entity.DigitalEmployeeTaskEntity;
import com.digitalemployee.domain.task.model.valobj.TaskStatusVO;
import com.digitalemployee.domain.task.model.valobj.TaskTypeVO;
import org.junit.Assert;
import org.junit.Test;

public class TaskLifecycleDomainServiceTest {

    @Test
    public void shouldCreateLocalAgentTaskWithAgentPrefix() {
        TaskLifecycleDomainService service = new TaskLifecycleDomainService();

        DigitalEmployeeTaskEntity task = service.createTask(TaskTypeVO.LOCAL_AGENT, "inspect repository");

        Assert.assertTrue(task.getId().startsWith("a"));
        Assert.assertEquals(TaskTypeVO.LOCAL_AGENT, task.getType());
        Assert.assertEquals(TaskStatusVO.PENDING, task.getStatus());
        Assert.assertEquals("inspect repository", task.getDescription());
        Assert.assertTrue(task.getStartTime() > 0);
    }

    @Test
    public void shouldIdentifyTerminalTaskStatuses() {
        TaskLifecycleDomainService service = new TaskLifecycleDomainService();

        Assert.assertTrue(service.isTerminalStatus(TaskStatusVO.COMPLETED));
        Assert.assertTrue(service.isTerminalStatus(TaskStatusVO.FAILED));
        Assert.assertTrue(service.isTerminalStatus(TaskStatusVO.KILLED));
        Assert.assertFalse(service.isTerminalStatus(TaskStatusVO.PENDING));
        Assert.assertFalse(service.isTerminalStatus(TaskStatusVO.RUNNING));
    }

    @Test
    public void shouldRejectTransitionFromTerminalStatus() {
        TaskLifecycleDomainService service = new TaskLifecycleDomainService();
        DigitalEmployeeTaskEntity completed = service.complete(
                service.start(service.createTask(TaskTypeVO.LOCAL_BASH, "run tests"))
        );

        try {
            service.start(completed);
            Assert.fail("terminal task transition should be rejected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("terminal task cannot transition", e.getMessage());
        }
    }

}

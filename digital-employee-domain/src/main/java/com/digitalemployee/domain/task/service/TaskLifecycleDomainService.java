package com.digitalemployee.domain.task.service;

import com.digitalemployee.domain.task.model.entity.DigitalEmployeeTaskEntity;
import com.digitalemployee.domain.task.model.valobj.TaskStatusVO;
import com.digitalemployee.domain.task.model.valobj.TaskTypeVO;

import java.security.SecureRandom;

public class TaskLifecycleDomainService {

    private static final String TASK_ID_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    public DigitalEmployeeTaskEntity createTask(TaskTypeVO type, String description) {
        if (type == null) {
            throw new IllegalArgumentException("task type is required");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("task description is required");
        }
        String taskId = generateTaskId(type);
        return DigitalEmployeeTaskEntity.builder()
                .id(taskId)
                .type(type)
                .status(TaskStatusVO.PENDING)
                .description(description)
                .startTime(System.currentTimeMillis())
                .outputFile(".digital-employee/tasks/" + taskId + ".log")
                .outputOffset(0)
                .notified(false)
                .build();
    }

    public DigitalEmployeeTaskEntity start(DigitalEmployeeTaskEntity task) {
        assertCanTransition(task);
        return task.withStatus(TaskStatusVO.RUNNING, null);
    }

    public DigitalEmployeeTaskEntity complete(DigitalEmployeeTaskEntity task) {
        assertCanTransition(task);
        return task.withStatus(TaskStatusVO.COMPLETED, System.currentTimeMillis());
    }

    public boolean isTerminalStatus(TaskStatusVO status) {
        return status == TaskStatusVO.COMPLETED
                || status == TaskStatusVO.FAILED
                || status == TaskStatusVO.KILLED;
    }

    private void assertCanTransition(DigitalEmployeeTaskEntity task) {
        if (task == null) {
            throw new IllegalArgumentException("task is required");
        }
        if (isTerminalStatus(task.getStatus())) {
            throw new IllegalStateException("terminal task cannot transition");
        }
    }

    private String generateTaskId(TaskTypeVO type) {
        StringBuilder builder = new StringBuilder(type.getIdPrefix());
        for (int i = 0; i < 8; i++) {
            builder.append(TASK_ID_ALPHABET.charAt(RANDOM.nextInt(TASK_ID_ALPHABET.length())));
        }
        return builder.toString();
    }

}

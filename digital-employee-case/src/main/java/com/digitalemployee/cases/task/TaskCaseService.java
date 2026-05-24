package com.digitalemployee.cases.task;

import com.digitalemployee.cases.ITaskCaseService;
import com.digitalemployee.domain.task.adapter.repository.ITaskRepository;
import com.digitalemployee.domain.task.model.entity.DigitalEmployeeTaskEntity;
import com.digitalemployee.domain.task.model.valobj.TaskTypeVO;
import com.digitalemployee.domain.task.service.TaskLifecycleDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskCaseService implements ITaskCaseService {

    private final ITaskRepository taskRepository;
    private final TaskLifecycleDomainService taskLifecycleDomainService;

    public TaskCaseService(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        this.taskLifecycleDomainService = new TaskLifecycleDomainService();
    }

    @Override
    public DigitalEmployeeTaskEntity createTask(TaskTypeVO type, String description) {
        DigitalEmployeeTaskEntity task = taskLifecycleDomainService.createTask(type, description);
        taskRepository.save(task);
        return task;
    }

    @Override
    public DigitalEmployeeTaskEntity findTask(String taskId) {
        return requireTask(taskId);
    }

    @Override
    public List<DigitalEmployeeTaskEntity> listTasks() {
        return taskRepository.findAll();
    }

    @Override
    public DigitalEmployeeTaskEntity startTask(String taskId) {
        DigitalEmployeeTaskEntity task = taskLifecycleDomainService.start(requireTask(taskId));
        taskRepository.save(task);
        return task;
    }

    @Override
    public DigitalEmployeeTaskEntity completeTask(String taskId) {
        DigitalEmployeeTaskEntity task = taskLifecycleDomainService.complete(requireTask(taskId));
        taskRepository.save(task);
        return task;
    }

    private DigitalEmployeeTaskEntity requireTask(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("task id is required");
        }
        DigitalEmployeeTaskEntity task = taskRepository.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("task not found: " + taskId);
        }
        return task;
    }

}

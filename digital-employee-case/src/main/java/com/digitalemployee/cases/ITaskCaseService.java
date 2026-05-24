package com.digitalemployee.cases;

import com.digitalemployee.domain.task.model.entity.DigitalEmployeeTaskEntity;
import com.digitalemployee.domain.task.model.valobj.TaskTypeVO;

import java.util.List;

public interface ITaskCaseService {

    DigitalEmployeeTaskEntity createTask(TaskTypeVO type, String description);

    DigitalEmployeeTaskEntity findTask(String taskId);

    List<DigitalEmployeeTaskEntity> listTasks();

    DigitalEmployeeTaskEntity startTask(String taskId);

    DigitalEmployeeTaskEntity completeTask(String taskId);

}

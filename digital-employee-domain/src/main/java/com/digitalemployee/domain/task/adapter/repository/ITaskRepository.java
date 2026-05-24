package com.digitalemployee.domain.task.adapter.repository;

import com.digitalemployee.domain.task.model.entity.DigitalEmployeeTaskEntity;

import java.util.List;

public interface ITaskRepository {

    void save(DigitalEmployeeTaskEntity task);

    DigitalEmployeeTaskEntity findById(String taskId);

    List<DigitalEmployeeTaskEntity> findAll();

}

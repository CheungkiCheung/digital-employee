package com.digitalemployee.infrastructure.adapter.repository;

import com.digitalemployee.domain.task.adapter.repository.ITaskRepository;
import com.digitalemployee.domain.task.model.entity.DigitalEmployeeTaskEntity;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTaskRepository implements ITaskRepository {

    private final Map<String, DigitalEmployeeTaskEntity> tasks = new ConcurrentHashMap<>();

    @Override
    public void save(DigitalEmployeeTaskEntity task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public DigitalEmployeeTaskEntity findById(String taskId) {
        return tasks.get(taskId);
    }

    @Override
    public List<DigitalEmployeeTaskEntity> findAll() {
        return tasks.values().stream()
                .sorted(Comparator.comparingLong(DigitalEmployeeTaskEntity::getStartTime))
                .toList();
    }

}

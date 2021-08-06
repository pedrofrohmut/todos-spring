package com.pedrofrohmut.todos.domain.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.pedrofrohmut.todos.domain.dtos.TaskDto;
import com.pedrofrohmut.todos.domain.entities.Task;

public class TaskMapper {

  public static TaskDto mapEntityToTaskDto(Task task) {
    if (task == null) {
      return null;
    }
    final var taskDto = new TaskDto();
    taskDto.id = task.getId();
    taskDto.name = task.getName();
    taskDto.description = task.getDescription() == null ? "" : task.getDescription();
    taskDto.userId = task.getUserId();
    return taskDto;
  }

  public static List<TaskDto> mapEntityListToTaskDtoList(List<Task> tasks) {
    return
      tasks
        .stream()
        .map(task -> TaskMapper.mapEntityToTaskDto(task))
        .toList();
  }

}

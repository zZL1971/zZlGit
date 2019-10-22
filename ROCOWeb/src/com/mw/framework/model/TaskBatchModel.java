package com.mw.framework.model;

import java.util.List;

public class TaskBatchModel {

	private String assignee;
	
	private List<TaskModel> tasks;

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public List<TaskModel> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskModel> tasks) {
		this.tasks = tasks;
	}
	
	
}

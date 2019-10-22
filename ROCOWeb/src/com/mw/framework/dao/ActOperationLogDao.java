package com.mw.framework.dao;

import com.mw.framework.domain.ActOperationLog;
import com.mw.framework.support.dao.GenericRepository;

import org.springframework.data.jpa.repository.Query;

public interface ActOperationLogDao extends
		GenericRepository<ActOperationLog, String> {
	@Query("from ActOperationLog where pid=?1 and actId=?2")
	public ActOperationLog findByActIdAndPid(String pid, String actId);

	@Query("from ActOperationLog where taskId=?1")
	public ActOperationLog findByTaskId(String taskId);
}

package com.digitalemployee.domain.conversation.adapter.port;

import com.digitalemployee.domain.conversation.model.valobj.WorkspaceFileVO;

public interface IWorkspaceFilePort {

    WorkspaceFileVO readFile(String relativePath);

}

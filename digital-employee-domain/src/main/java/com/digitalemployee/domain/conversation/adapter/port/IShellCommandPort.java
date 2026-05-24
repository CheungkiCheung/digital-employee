package com.digitalemployee.domain.conversation.adapter.port;

import com.digitalemployee.domain.conversation.model.valobj.ShellCommandResultVO;

public interface IShellCommandPort {

    ShellCommandResultVO execute(String command);

}

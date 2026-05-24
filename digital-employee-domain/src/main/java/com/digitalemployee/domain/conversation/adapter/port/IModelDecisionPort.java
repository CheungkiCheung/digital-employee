package com.digitalemployee.domain.conversation.adapter.port;

import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionRequestVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelProviderVO;

public interface IModelDecisionPort {

    default ModelProviderVO provider() {
        return ModelProviderVO.builder()
                .provider("unspecified")
                .model("unspecified")
                .external(false)
                .apiKeyEnvName("")
                .build();
    }

    ModelDecisionVO decideNextAction(ModelDecisionRequestVO request);

}

package com.digitalemployee.domain.conversation.service;

import com.digitalemployee.domain.conversation.model.valobj.PermissionBehaviorVO;
import com.digitalemployee.domain.conversation.model.valobj.PermissionDecisionVO;
import org.junit.Assert;
import org.junit.Test;

public class PermissionDomainServiceTest {

    @Test
    public void shouldDenyFileReadWhenPathContainsParentDirectorySegment() {
        PermissionDomainService service = new PermissionDomainService();

        PermissionDecisionVO decision = service.decideFileRead("AGENTS.md/..");

        Assert.assertEquals(PermissionBehaviorVO.DENY, decision.getBehavior());
        Assert.assertEquals("file path must stay inside the workspace", decision.getReason());
    }

    @Test
    public void shouldDenyFileWriteWhenPathContainsParentDirectorySegment() {
        PermissionDomainService service = new PermissionDomainService();

        PermissionDecisionVO decision = service.decideFileWrite("notes/../secret.txt");

        Assert.assertEquals(PermissionBehaviorVO.DENY, decision.getBehavior());
        Assert.assertEquals("file path must stay inside the workspace", decision.getReason());
    }

    @Test
    public void shouldAllowSafeBashCommandsOnly() {
        PermissionDomainService service = new PermissionDomainService();

        PermissionDecisionVO pwdDecision = service.decideBashCommand("pwd");
        PermissionDecisionVO lsDecision = service.decideBashCommand("ls docs");
        PermissionDecisionVO catDecision = service.decideBashCommand("cat AGENTS.md");

        Assert.assertEquals(PermissionBehaviorVO.ALLOW, pwdDecision.getBehavior());
        Assert.assertEquals(PermissionBehaviorVO.ALLOW, lsDecision.getBehavior());
        Assert.assertEquals(PermissionBehaviorVO.ALLOW, catDecision.getBehavior());
    }

    @Test
    public void shouldDenyUnsafeBashCommands() {
        PermissionDomainService service = new PermissionDomainService();

        Assert.assertEquals(PermissionBehaviorVO.DENY, service.decideBashCommand("rm -rf target").getBehavior());
        Assert.assertEquals(PermissionBehaviorVO.DENY, service.decideBashCommand("ls && cat AGENTS.md").getBehavior());
        Assert.assertEquals(PermissionBehaviorVO.DENY, service.decideBashCommand("cat ../secret.txt").getBehavior());
        Assert.assertEquals(PermissionBehaviorVO.DENY, service.decideBashCommand("cat AGENTS.md > copy.txt").getBehavior());
    }

}

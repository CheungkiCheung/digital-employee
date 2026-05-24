# Digital Employee Progress

## Current State

**Last Updated:** 2026-05-24 16:53 Asia/Shanghai  
**Active Feature:** none  
**Repository:** `/Users/zhangqixiang/0_2实习/DDDAGENT`  
**Target Remote:** `https://github.com/CheungkiCheung/digital-employee.git`

## What's Done

- [x] Generated the Java 17 Maven DDD hexagonal scaffold with artifact `digital-employee`.
- [x] Moved scaffold contents into the repository root.
- [x] Fixed scaffold build issues for current Java/Maven by configuring Lombok annotation processing in the parent POM.
- [x] Replaced Lombok constructor generation on `ResponseCode` with an explicit enum constructor and getters.
- [x] Verified the scaffold with `mvn clean install -DskipTests`.
- [x] Created repository-local harness constraints based on learn-harness-engineering L01-L12.
- [x] Verified the full harness with `./init.sh`.
- [x] Implemented and verified `feat-004`, the first Claude Code domain runtime slice.
- [x] Implemented and verified `feat-005`, the Domain-defined model decision port with deterministic Infrastructure adapter.
- [x] Implemented and verified `feat-006`, workspace traversal denial in Domain permissions.
- [x] Implemented and verified `feat-007`, the Domain tool registry model.
- [x] Implemented and verified `feat-008`, immutable tool descriptor metadata.
- [x] Implemented and verified `feat-009`, the task lifecycle Domain model.
- [x] Implemented and verified `feat-010`, task creation API vertical slice.
- [x] Implemented and verified `feat-011`, structured model decision request context with tool descriptors.
- [x] Implemented and verified `feat-012`, memory context Domain slice.
- [x] Implemented and verified `feat-013`, runtime acceptance checkpoint for the current backend stage.
- [x] Implemented and verified `feat-014`, task query/list/start/complete API vertical slice.
- [x] Implemented and verified `feat-015`, memory context injection into model decision requests.
- [x] Implemented and verified `feat-016`, model provider metadata boundary.
- [x] Implemented and verified `feat-017`, file_write tool permission slice with API regression.
- [x] Implemented and verified `feat-018`, file_edit tool permission slice with API regression.
- [x] Implemented and verified `feat-019`, bash command permission policy boundary without real shell execution.
- [x] Implemented and verified `feat-020`, shell command execution port skeleton with default execution disabled.
- [x] Implemented and verified `feat-021`, workspace-safe shell command Infrastructure adapter for read-only commands.
- [x] Implemented and verified `feat-022`, shell command output summary limit.
- [x] Implemented and verified `feat-023`, unsafe shell command API denial regression.
- [x] Implemented and verified `feat-024`, conversation history repository API.
- [x] Implemented and verified `feat-025`, conversation history isolation regression.
- [x] Implemented and verified `feat-026`, conversation history decision context.
- [x] Implemented and verified `feat-027`, external model provider configuration boundary.
- [x] Implemented and verified `feat-028`, external model decision port stub.
- [x] Implemented and verified `feat-029`, external model gateway DTO mapping.
- [x] Implemented and verified `feat-030`, external model gateway service boundary.
- [x] Implemented and verified `feat-031`, external model gateway request validation.
- [x] Implemented and verified `feat-032`, external model tool descriptor mapping.
- [x] Implemented and verified `feat-033`, file conversation history repository adapter.
- [x] Implemented and verified `feat-034`, configurable conversation history repository selection.
- [x] Implemented and verified `feat-035`, file-backed conversation API persistence slice.
- [x] Implemented and verified `feat-036`, OpenAI-compatible chat completions request mapping.
- [x] Implemented and verified `feat-037`, external model base URL configuration boundary.

## What's In Progress

- None. WIP is clear for the next Goal-mode slice.

## What's Next

1. Continue deeper runtime slices in Goal mode.
2. Recommended next feature: add external gateway request execution guardrails around timeout/retry metadata before real network calls.
3. Keep WIP=1 and run `./init.sh` before claiming completion.

## Decisions Made

- **Project name:** Use `digital-employee` as the English name for "数字员工".
  - Context: User requested replacing `ddd-agent` with the English for digital employee.
- **Harness style:** Keep `AGENTS.md` as a short router and put detailed rules in `docs/harness/`.
  - Context: L04 warns against one giant instruction file.
- **Feature state:** Use `not-started`, `active`, `blocked`, `passing`.
  - Context: L08 treats feature lists as a harness primitive with state-gated completion.
- **Verification entrypoint:** Use `./init.sh` as the standard startup and completion check.
  - Context: L06 requires initialization as its own phase.

## Verification Evidence

- [x] Scaffold build: `mvn clean install -DskipTests` succeeded on 2026-05-23.
- [x] Harness verification: `./init.sh` succeeded on 2026-05-23.
- [x] `feat-004` red verification: `mvn -pl digital-employee-app test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest` failed because `ConversationMessageRequestDTO` did not exist.
- [x] `feat-004` feature verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 1 test, 0 failures, 0 errors.
- [x] `feat-004` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-004` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-004` manual runtime verification: started `digital-employee-app` on port 8091 and posted `{ "message": "请读取 AGENTS.md" }` to `/api/v1/conversations/manual-check/messages`; response contained `code=0000`, `file_read`, `allow`, and AGENTS.md content.
- [x] `feat-005` red verification: natural prompt `{ "message": "帮我看一下 AGENTS.md" }` initially returned no tool execution before extracting a model decision port.
- [x] `feat-005` feature verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 2 tests, 0 failures, 0 errors.
- [x] `feat-005` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-005` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-006` red verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=PermissionDomainServiceTest` failed with `expected:<DENY> but was:<ALLOW>` for `AGENTS.md/..`.
- [x] `feat-006` feature verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=PermissionDomainServiceTest` passed with 1 test, 0 failures, 0 errors.
- [x] `feat-006` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-006` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-007` red verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` first failed because `ToolRegistryDomainService` did not exist, then failed with unsupported tool answer `null`.
- [x] `feat-007` feature verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` passed with 2 tests, 0 failures, 0 errors.
- [x] `feat-007` app regression verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 2 tests, 0 failures, 0 errors.
- [x] `feat-007` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-007` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-008` red verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ToolRegistryDomainServiceTest` failed because `ToolDescriptorVO` did not exist.
- [x] `feat-008` feature verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ToolRegistryDomainServiceTest` passed with 2 tests, 0 failures, 0 errors.
- [x] `feat-008` domain regression verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest,PermissionDomainServiceTest` passed with 4 tests, 0 failures, 0 errors.
- [x] `feat-008` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-008` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-009` red verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=TaskLifecycleDomainServiceTest` failed because task Domain packages did not exist.
- [x] `feat-009` feature verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=TaskLifecycleDomainServiceTest` passed with 3 tests, 0 failures, 0 errors.
- [x] `feat-009` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-009` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-010` red verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeTaskApiTest -Dsurefire.failIfNoSpecifiedTests=false` failed because `TaskCreateRequestDTO` did not exist.
- [x] `feat-010` feature verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeTaskApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 1 test, 0 failures, 0 errors.
- [x] `feat-010` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-010` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-011` red verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ConversationRuntimeServiceTest` failed because `ModelDecisionRequestVO` did not exist.
- [x] `feat-011` feature verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ConversationRuntimeServiceTest,ToolRegistryDomainServiceTest` passed with 4 tests, 0 failures, 0 errors.
- [x] `feat-011` conversation regression verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 2 tests, 0 failures, 0 errors.
- [x] `feat-011` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-011` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-012` red verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=MemoryContextDomainServiceTest` failed because memory Domain packages did not exist.
- [x] `feat-012` feature verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=MemoryContextDomainServiceTest` passed with 1 test, 0 failures, 0 errors.
- [x] `feat-012` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-012` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-013` runtime acceptance verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeTaskApiTest,DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 3 tests, 0 failures, 0 errors.
- [x] `feat-013` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-013` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-014` red verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeTaskApiTest -Dsurefire.failIfNoSpecifiedTests=false` failed as expected because `GET /api/v1/tasks/{taskId}` returned 404 before Task query/transition endpoints existed.
- [x] `feat-014` feature verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeTaskApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 2 tests, 0 failures, 0 errors.
- [x] `feat-014` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-014` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-015` red verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ConversationRuntimeServiceTest` failed because `ConversationRuntimeService` had no memory-context constructor and `ModelDecisionRequestVO` had no `getMemoryContext()`.
- [x] `feat-015` feature verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ConversationRuntimeServiceTest` passed with 3 tests, 0 failures, 0 errors.
- [x] `feat-015` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-015` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-016` red verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=DeterministicModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` failed because Domain had no `ModelProviderVO`.
- [x] `feat-016` feature verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=DeterministicModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 1 test, 0 failures, 0 errors.
- [x] `feat-016` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-016` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-017` red verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=PermissionDomainServiceTest,ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` failed because `IWorkspaceFileWritePort` did not exist.
- [x] `feat-017` API red verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` failed because deterministic model decisions returned a direct answer instead of choosing `file_write`.
- [x] `feat-017` feature verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=PermissionDomainServiceTest,ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` passed with 9 tests, 0 failures, 0 errors.
- [x] `feat-017` API regression verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 3 tests, 0 failures, 0 errors.
- [x] `feat-017` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-017` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-018` red verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` failed because `file_edit` was not registered and runtime did not write edited content.
- [x] `feat-018` API red verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` failed because deterministic model decisions returned a direct answer instead of choosing `file_edit`.
- [x] `feat-018` feature verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` passed with 9 tests, 0 failures, 0 errors.
- [x] `feat-018` API regression verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 4 tests, 0 failures, 0 errors.
- [x] `feat-018` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-018` harness verification: `./init.sh` passed with BUILD SUCCESS for all 8 modules.
- [x] `feat-019` red verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=PermissionDomainServiceTest,ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` failed because `PermissionDomainService` had no `decideBashCommand`.
- [x] `feat-019` API red verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` failed because deterministic model decisions returned a direct answer instead of choosing `bash`.
- [x] `feat-019` feature verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=PermissionDomainServiceTest,ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` passed with 16 tests, 0 failures, 0 errors.
- [x] `feat-019` API regression verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 5 tests, 0 failures, 0 errors.
- [x] `feat-019` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-020` red verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ConversationRuntimeServiceTest` failed because Domain had no `IShellCommandPort` and no `ShellCommandResultVO`.
- [x] `feat-020` feature verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ConversationRuntimeServiceTest` passed with 8 tests, 0 failures, 0 errors.
- [x] `feat-020` API regression verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 5 tests, 0 failures, 0 errors.
- [x] `feat-020` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-021` red verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=WorkspaceShellCommandPortTest -Dsurefire.failIfNoSpecifiedTests=false` failed because `WorkspaceShellCommandPort` did not exist.
- [x] `feat-021` app red verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` failed at Infrastructure test compilation because `WorkspaceShellCommandPort` did not exist.
- [x] `feat-021` feature verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=WorkspaceShellCommandPortTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 4 tests, 0 failures, 0 errors.
- [x] `feat-021` API regression verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 5 tests, 0 failures, 0 errors.
- [x] `feat-021` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-022` red verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ShellCommandResultVOTest` failed because `ShellCommandResultVO.summarize()` returned long stdout/stderr without truncation.
- [x] `feat-022` feature verification: `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ShellCommandResultVOTest,ConversationRuntimeServiceTest` passed with 10 tests, 0 failures, 0 errors.
- [x] `feat-022` API regression verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 5 tests, 0 failures, 0 errors.
- [x] `feat-022` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-023` regression verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 7 tests, 0 failures, 0 errors; unsafe bash prompts returned permission denial.
- [x] `feat-023` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-024` red verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` failed because `GET /api/v1/conversations/{conversationId}/messages` returned 405.
- [x] `feat-024` feature verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 8 tests, 0 failures, 0 errors.
- [x] `feat-024` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-025` regression verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 9 tests, 0 failures, 0 errors; `history-alpha` and `history-beta` remained isolated by conversation id.
- [x] `feat-025` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-025` harness verification: `./init.sh` passed with feature_list.json valid (25 features, 0 active), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.
- [x] `feat-026` red verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` failed as expected because `上一条用户消息是什么` returned the default deterministic response instead of prior conversation history.
- [x] `feat-026` feature verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 10 tests, 0 failures, 0 errors.
- [x] `feat-026` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-026` harness verification: `./init.sh` passed with feature_list.json valid (26 features, 0 active), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.
- [x] `feat-027` red verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=DeterministicModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` failed as expected because `ModelProviderVO` had no `apiKeyEnvName` getter or builder field.
- [x] `feat-027` feature verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=DeterministicModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 2 tests, 0 failures, 0 errors.
- [x] `feat-027` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-027` harness verification: `./init.sh` passed with feature_list.json valid (27 features, 0 active), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.
- [x] `feat-028` red verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ModelDecisionPortSelectionTest,ExternalModelDecisionPortTest,DeterministicModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` failed as expected because `ExternalModelDecisionPort` and `ModelDecisionPortConfiguration` did not exist.
- [x] `feat-028` feature verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ModelDecisionPortSelectionTest,ExternalModelDecisionPortTest,DeterministicModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 6 tests, 0 failures, 0 errors.
- [x] `feat-028` conversation regression verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 10 tests, 0 failures, 0 errors.
- [x] `feat-028` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-028` harness verification: `./init.sh` passed with feature_list.json valid (28 features, 0 active), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.
- [x] `feat-029` red verification: `./init.sh` failed during Infrastructure test compilation because `ExternalModelGatewayRequestDTO`, `ExternalModelGatewayResponseDTO`, and `ExternalModelGatewayMapper` did not exist, and `ExternalModelGatewayMapperTest` imported the wrong permission enum.
- [x] `feat-029` feature verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayMapperTest,ExternalModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 4 tests, 0 failures, 0 errors.
- [x] `feat-029` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-029` harness verification: `./init.sh` passed with feature_list.json valid (29 features, 1 active before closure), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.
- [x] `feat-030` red verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelDecisionPortTest,ExternalModelGatewayServiceTest,ExternalModelGatewayMapperTest -Dsurefire.failIfNoSpecifiedTests=false` failed because `ExternalModelGatewayService` did not exist.
- [x] `feat-030` feature verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelDecisionPortTest,ExternalModelGatewayServiceTest,ExternalModelGatewayMapperTest,ModelDecisionPortSelectionTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 7 tests, 0 failures, 0 errors.
- [x] `feat-030` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-030` harness verification: `./init.sh` passed with feature_list.json valid (30 features, 1 active before closure), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.
- [x] `feat-031` red verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayServiceTest -Dsurefire.failIfNoSpecifiedTests=false` failed with 3 expected assertion failures because blank provider, blank model, and blank input were not rejected.
- [x] `feat-031` feature verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayServiceTest,ExternalModelDecisionPortTest,ExternalModelGatewayMapperTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 8 tests, 0 failures, 0 errors.
- [x] `feat-031` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-031` harness verification: `./init.sh` passed with feature_list.json valid (31 features, 1 active before closure), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.
- [x] `feat-032` red verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayMapperTest -Dsurefire.failIfNoSpecifiedTests=false` failed because gateway request tools were still plain strings without structured descriptor getters.
- [x] `feat-032` feature verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayMapperTest,ExternalModelGatewayServiceTest,ExternalModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 8 tests, 0 failures, 0 errors.
- [x] `feat-032` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-032` harness verification: `./init.sh` passed with feature_list.json valid (32 features, 1 active before closure), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.
- [x] `feat-033` red verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=FileConversationTurnRepositoryTest -Dsurefire.failIfNoSpecifiedTests=false` failed because `FileConversationTurnRepository` did not exist.
- [x] `feat-033` feature verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=FileConversationTurnRepositoryTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 1 test, 0 failures, 0 errors.
- [x] `feat-033` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-033` harness verification: `./init.sh` passed with feature_list.json valid (33 features, 1 active before closure), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.
- [x] `feat-034` red verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ConversationTurnRepositoryConfigurationTest,FileConversationTurnRepositoryTest -Dsurefire.failIfNoSpecifiedTests=false` failed because `ConversationTurnRepositoryConfiguration` did not exist.
- [x] `feat-034` feature verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ConversationTurnRepositoryConfigurationTest,FileConversationTurnRepositoryTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 3 tests, 0 failures, 0 errors.
- [x] `feat-034` conversation regression verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 10 tests, 0 failures, 0 errors.
- [x] `feat-034` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-034` harness verification: `./init.sh` passed with feature_list.json valid (34 features, 1 active before closure), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.
- [x] `feat-035` red/setup verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeFileConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` first failed during test compilation because `javax.annotation.Resource` was unavailable in the Spring Boot 3 test classpath.
- [x] `feat-035` behavioral red verification: after switching to `@Autowired`, the same test failed because the assertion expected a non-existent English deterministic response string while the persisted assistant message used the existing Chinese default response.
- [x] `feat-035` feature verification: `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeFileConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 1 test, 0 failures, 0 errors.
- [x] `feat-035` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-035` harness verification: `./init.sh` passed with feature_list.json valid (35 features, 1 active before closure), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.
- [x] `feat-036` red verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayMapperTest -Dsurefire.failIfNoSpecifiedTests=false` failed because `OpenAiChatCompletionRequestDTO` did not exist.
- [x] `feat-036` feature verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayMapperTest,ExternalModelGatewayServiceTest,ExternalModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 9 tests, 0 failures, 0 errors.
- [x] `feat-036` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-036` harness verification: `./init.sh` passed with feature_list.json valid (36 features, 1 active before closure), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.
- [x] `feat-037` red verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ModelDecisionPortSelectionTest,ExternalModelGatewayServiceTest,ExternalModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` failed because `ModelDecisionPortConfiguration`, `ExternalModelDecisionPort`, and `ExternalModelGatewayService` did not accept a base URL parameter.
- [x] `feat-037` feature verification: `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ModelDecisionPortSelectionTest,ExternalModelGatewayServiceTest,ExternalModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` passed with 9 tests, 0 failures, 0 errors.
- [x] `feat-037` architecture verification: `bash scripts/check-architecture.sh` passed.
- [x] `feat-037` harness verification: `./init.sh` passed with feature_list.json valid (37 features, 1 active before closure), DDD boundaries verified, and BUILD SUCCESS for all 8 modules.

## Sprint Contract: feat-004 - Claude Code Domain Runtime Slice

### Scope

- Add API DTOs for conversation message requests and responses.
- Add Domain model for conversation turn messages, tool calls, tool results, permission decisions, and file-read execution.
- Add Case service to orchestrate one conversation turn.
- Add Infrastructure file-system port adapter constrained to the workspace root.
- Add Trigger HTTP endpoint for conversation messages.
- Add integration test that proves an allowed project file can be read through the HTTP API.

### Verification

- `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false`
- `./init.sh`

### Exclusions

- No terminal TUI clone.
- No Anthropic API integration.
- No Bash, write, edit, MCP, subagent, or background-task execution.

### Result

- Implemented and verified on 2026-05-23.
- HTTP endpoint: `POST /api/v1/conversations/{conversationId}/messages`.
- Demo request: `{ "message": "请读取 AGENTS.md" }`.
- Response includes `conversationId`, answer text, recorded messages, and `file_read` tool execution with `allow` permission behavior.

## Sprint Contract: feat-005 - Model Decision Port

### Scope

- Add a Domain port for agent/model next-action decisions.
- Add Domain value objects for direct responses and tool-call decisions.
- Refactor `ConversationRuntimeService` so it consumes the decision port instead of parsing the user prompt.
- Add an Infrastructure deterministic adapter that maps natural file-inspection prompts such as `帮我看一下 AGENTS.md` to `file_read`.
- Extend the HTTP integration test to prove natural file-inspection prompts still execute through the tool path.

### Verification

- `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false`
- `./init.sh`

### Exclusions

- No real model API call.
- No prompt engineering layer.
- No multi-tool or chained tool planning.

### Result

- Implemented and verified on 2026-05-23.
- Added Domain port `IModelDecisionPort`.
- Added decision value objects for direct response vs tool call.
- Refactored `ConversationRuntimeService` to consume model decisions instead of parsing prompts directly.
- Added deterministic Infrastructure adapter `DeterministicModelDecisionPort` that maps natural file-inspection prompts to `file_read`.
- Extended the HTTP integration test to prove `帮我看一下 AGENTS.md` executes through the tool path.

## Sprint Contract: feat-006 - Workspace Traversal Denial

### Scope

- Add a Domain permission unit test for parent-directory traversal.
- Deny any `file_read` path containing a path segment equal to `..`.
- Keep Infrastructure workspace-root normalization as a second guard.

### Verification

- `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=PermissionDomainServiceTest`
- `bash scripts/check-architecture.sh`
- `./init.sh`

### Exclusions

- No new tool types.
- No tool registry.
- No HTTP API shape changes.

### Result

- Implemented and verified on 2026-05-23.
- Added `PermissionDomainServiceTest`.
- Updated `PermissionDomainService` so `AGENTS.md/..` and any other `..` segment is denied before Infrastructure can read.

## Sprint Contract: feat-007 - Tool Registry Domain Model

### Scope

- Add a Domain registry for enabled tools.
- Register the currently supported `file_read` tool.
- Route model-requested tool calls through the registry before execution.
- Return an explicit unsupported-tool answer when the model asks for a tool that is not registered.

### Verification

- `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest`
- `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false`
- `bash scripts/check-architecture.sh`
- `./init.sh`

### Exclusions

- No Bash/Edit/MCP tool implementations.
- No external model API calls.
- No HTTP API shape changes.

### Result

- Implemented and verified on 2026-05-23.
- Added `ToolRegistryDomainService.defaultRegistry()` with `file_read` registered.
- Updated `ConversationRuntimeService` to distinguish direct responses, registered tool calls, and unsupported tool calls.
- Added `ToolRegistryDomainServiceTest` and `ConversationRuntimeServiceTest`.

## Sprint Contract: feat-008 - Tool Descriptor Metadata

### Scope

- Add immutable Domain value object for tool descriptors.
- Include name, description, and default permission behavior.
- Update the Domain registry to expose descriptors while preserving `isRegistered`.

### Verification

- `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ToolRegistryDomainServiceTest`
- `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest,PermissionDomainServiceTest`
- `bash scripts/check-architecture.sh`
- `./init.sh`

### Exclusions

- No new tool execution.
- No prompt rendering.
- No HTTP API changes.

### Result

- Implemented and verified on 2026-05-23.
- Added `ToolDescriptorVO`.
- Updated `ToolRegistryDomainService` to store immutable descriptor metadata for `file_read`.

## Notes For Next Session

- The project now has a minimal usable agent runtime backend: conversation API, model decision port, permission decision, workspace file-read tool, tool registry metadata, and vertical-slice tests.
- The project also has task lifecycle Domain modeling, task creation/query/list/start/complete API, model decision request context, memory context injection, model provider metadata, and read/write/edit workspace file tools with permission checks.
- Continue migrating Claude Code concepts in small slices before adding product-specific Digital Employee business capabilities.
- Always run `./init.sh` before claiming completion.

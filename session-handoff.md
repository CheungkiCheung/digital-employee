# Session Handoff

## Current Objective

- Goal: Continue the Claude Code runtime migration into the Digital Employee DDD framework.
- Current status: `feat-001` through `feat-036` are implemented and verified, except `feat-003` remains intentionally blocked until the user gives product-specific business requirements.
- Active feature: none.
- Branch / commit at original scaffold point: `5de09fd chore: initialize digital employee harness`.

## Completed Runtime Slices

- [x] DDD Java 17 Maven scaffold and repository-local harness.
- [x] Claude Code domain migration decision: migrate backend runtime core first, not the terminal TUI.
- [x] Conversation API with deterministic model decision, workspace `file_read`, permission decision, and recorded tool execution.
- [x] Tool registry with immutable descriptors passed into model decision requests.
- [x] Task lifecycle Domain model with Claude Code-style task type id prefixes.
- [x] Task creation/query/list/start/complete HTTP vertical slice backed by an in-memory repository.
- [x] Memory context Domain model and memory-context injection into the model decision request boundary.
- [x] Model provider metadata boundary on the Domain model decision port, with deterministic local provider metadata in Infrastructure.
- [x] Registered `file_write` tool with Domain permission checks and Infrastructure workspace write adapter.
- [x] Registered `file_edit` tool with Domain runtime replace semantics and deterministic prompt parsing.
- [x] Registered `bash` tool boundary with conservative Domain permission policy and explicit non-execution response.
- [x] Added Domain-owned shell command execution port and command result value object; default runtime execution remains disabled.
- [x] Added Infrastructure workspace-safe shell command adapter for `pwd`, `ls`, and `cat`, wired through the Case layer.
- [x] Added bounded shell stdout/stderr summaries to avoid unbounded conversation responses.
- [x] Added API regression coverage proving unsafe shell prompts return Domain permission denial.
- [x] Added conversation history repository port, in-memory adapter, and GET history endpoint.
- [x] Added a file-backed conversation history repository adapter that can reload messages from disk.
- [x] Added conversation history isolation regression coverage so separate conversation ids cannot leak messages into each other.
- [x] Injected bounded same-conversation history into the model decision request context.
- [x] Extended model provider metadata to carry API-key environment variable names without storing secret values.
- [x] Added an external model decision port stub and configuration-based provider selection while keeping deterministic as the default.
- [x] Added external model gateway request/response DTO mapping without network calls or secret values.
- [x] Routed the external model decision port through a no-network external model gateway service boundary.
- [x] Added external model gateway request validation for provider, model, and input before future network execution.
- [x] Added structured external model tool descriptor mapping with name, description, and default permission behavior.
- [x] Added Spring Infrastructure configuration to select the in-memory conversation history repository by default or the file-backed repository when configured.
- [x] Added an app-level API regression proving `digital-employee.conversation.repository=file` writes conversation history to disk and a new file repository instance can reload it.
- [x] Added OpenAI-compatible chat completions request DTO mapping without network calls or secret values.
- [x] Runtime acceptance checkpoint proving task creation and conversation file-read paths.

## Latest Verification Evidence

| Check | Command | Result | Notes |
|---|---|---|---|
| Startup baseline | `./init.sh` | Passing | Validated feature state, DDD boundaries, and Maven build before continuing. |
| feat-014 red test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeTaskApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | `GET /api/v1/tasks/{taskId}` returned 404 before Task query/transition endpoints existed. |
| feat-014 feature test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeTaskApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 2 tests, 0 failures, 0 errors. |
| feat-014 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-014 harness check | `./init.sh` | Passing | BUILD SUCCESS for all 8 modules while feat-014 was active. |
| feat-015 red test | `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ConversationRuntimeServiceTest` | Failed as expected | Missing memory-context constructor and `ModelDecisionRequestVO#getMemoryContext()`. |
| feat-015 feature test | `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ConversationRuntimeServiceTest` | Passing | 3 tests, 0 failures, 0 errors. |
| feat-015 architecture and harness | `bash scripts/check-architecture.sh && ./init.sh` | Passing | DDD boundaries verified; BUILD SUCCESS for all 8 modules while feat-015 was active. |
| feat-016 red test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=DeterministicModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | Domain had no `ModelProviderVO`. |
| feat-016 feature test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=DeterministicModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 1 test, 0 failures, 0 errors. |
| feat-016 architecture and harness | `bash scripts/check-architecture.sh && ./init.sh` | Passing | DDD boundaries verified; BUILD SUCCESS for all 8 modules while feat-016 was active. |
| feat-017 red test | `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=PermissionDomainServiceTest,ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` | Failed as expected | Domain had no `IWorkspaceFileWritePort`. |
| feat-017 API red test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | Deterministic model decisions did not yet choose `file_write`. |
| feat-017 feature test | `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=PermissionDomainServiceTest,ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` | Passing | 9 tests, 0 failures, 0 errors. |
| feat-017 API regression | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 3 tests, 0 failures, 0 errors; wrote `target/test-workspace/generated/agent-note.txt`. |
| feat-017 architecture and harness | `bash scripts/check-architecture.sh && ./init.sh` | Passing | DDD boundaries verified; BUILD SUCCESS for all 8 modules while feat-017 was active. |
| feat-018 red test | `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` | Failed as expected | `file_edit` was not registered and runtime did not write edited content. |
| feat-018 API red test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | Deterministic model decisions did not yet choose `file_edit`. |
| feat-018 feature test | `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` | Passing | 9 tests, 0 failures, 0 errors. |
| feat-018 API regression | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 4 tests, 0 failures, 0 errors; edited `target/test-workspace/generated/edit-note.txt`. |
| feat-018 architecture and harness | `bash scripts/check-architecture.sh && ./init.sh` | Passing | DDD boundaries verified; BUILD SUCCESS for all 8 modules while feat-018 was active. |
| feat-019 red test | `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=PermissionDomainServiceTest,ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` | Failed as expected | `PermissionDomainService` had no `decideBashCommand`. |
| feat-019 API red test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | Deterministic model decisions returned a direct answer instead of choosing `bash` for `运行 ls docs`. |
| feat-019 feature test | `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=PermissionDomainServiceTest,ToolRegistryDomainServiceTest,ConversationRuntimeServiceTest` | Passing | 16 tests, 0 failures, 0 errors. |
| feat-019 API regression | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 5 tests, 0 failures, 0 errors; bash command was permission-evaluated but not executed. |
| feat-019 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-020 red test | `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ConversationRuntimeServiceTest` | Failed as expected | Domain had no `IShellCommandPort` and no `ShellCommandResultVO`. |
| feat-020 feature test | `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ConversationRuntimeServiceTest` | Passing | 8 tests, 0 failures, 0 errors. |
| feat-020 API regression | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 5 tests, 0 failures, 0 errors; default bash path still reports execution disabled. |
| feat-020 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-021 red test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=WorkspaceShellCommandPortTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | `WorkspaceShellCommandPort` did not exist. |
| feat-021 app red test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | Build stopped at missing Infrastructure adapter compilation. |
| feat-021 feature test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=WorkspaceShellCommandPortTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 4 tests, 0 failures, 0 errors. |
| feat-021 API regression | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 5 tests, 0 failures, 0 errors; `运行 ls docs` returns captured shell output. |
| feat-021 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-022 red test | `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ShellCommandResultVOTest` | Failed as expected | `ShellCommandResultVO.summarize()` returned long stdout/stderr without truncation. |
| feat-022 feature test | `mvn -pl digital-employee-domain test -DskipTests=false -Dtest=ShellCommandResultVOTest,ConversationRuntimeServiceTest` | Passing | 10 tests, 0 failures, 0 errors. |
| feat-022 API regression | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 5 tests, 0 failures, 0 errors. |
| feat-022 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-023 API regression | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 7 tests, 0 failures, 0 errors; `rm -rf` and `cat ../secret.txt` prompts return permission denial. |
| feat-023 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-024 red test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | `GET /api/v1/conversations/{conversationId}/messages` returned 405 before history endpoint existed. |
| feat-024 feature test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 8 tests, 0 failures, 0 errors. |
| feat-024 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-025 regression test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 9 tests, 0 failures, 0 errors; `history-alpha` and `history-beta` histories remained isolated. |
| feat-025 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-025 harness check | `./init.sh` | Passing | feature_list.json valid (25 features, 0 active), DDD boundaries verified, BUILD SUCCESS for all 8 modules. |
| feat-026 red test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | `上一条用户消息是什么` returned the default deterministic response before history context injection. |
| feat-026 feature test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 10 tests, 0 failures, 0 errors; response included the prior user message `我的代号是 alpha-memory`. |
| feat-026 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-026 harness check | `./init.sh` | Passing | feature_list.json valid (26 features, 0 active), DDD boundaries verified, BUILD SUCCESS for all 8 modules. |
| feat-027 red test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=DeterministicModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | `ModelProviderVO` had no `apiKeyEnvName` getter or builder field. |
| feat-027 feature test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=DeterministicModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 2 tests, 0 failures, 0 errors. |
| feat-027 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-027 harness check | `./init.sh` | Passing | feature_list.json valid (27 features, 0 active), DDD boundaries verified, BUILD SUCCESS for all 8 modules. |
| feat-028 red test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ModelDecisionPortSelectionTest,ExternalModelDecisionPortTest,DeterministicModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | `ExternalModelDecisionPort` and `ModelDecisionPortConfiguration` did not exist. |
| feat-028 feature test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ModelDecisionPortSelectionTest,ExternalModelDecisionPortTest,DeterministicModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 6 tests, 0 failures, 0 errors. |
| feat-028 conversation regression | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 10 tests, 0 failures, 0 errors; default provider selection still supports deterministic tool decisions. |
| feat-028 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-028 harness check | `./init.sh` | Passing | feature_list.json valid (28 features, 0 active), DDD boundaries verified, BUILD SUCCESS for all 8 modules. |
| feat-029 red test | `./init.sh` | Failed as expected | Infrastructure test compilation failed because external model gateway DTOs and mapper did not exist, and the test imported the wrong permission enum. |
| feat-029 feature test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayMapperTest,ExternalModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 4 tests, 0 failures, 0 errors. |
| feat-029 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-029 harness check | `./init.sh` | Passing | feature_list.json valid (29 features, 1 active before closure), DDD boundaries verified, BUILD SUCCESS for all 8 modules. |
| feat-030 red test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelDecisionPortTest,ExternalModelGatewayServiceTest,ExternalModelGatewayMapperTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | `ExternalModelGatewayService` did not exist before the gateway service boundary was added. |
| feat-030 feature test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelDecisionPortTest,ExternalModelGatewayServiceTest,ExternalModelGatewayMapperTest,ModelDecisionPortSelectionTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 7 tests, 0 failures, 0 errors. |
| feat-030 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-030 harness check | `./init.sh` | Passing | feature_list.json valid (30 features, 1 active before closure), DDD boundaries verified, BUILD SUCCESS for all 8 modules. |
| feat-031 red test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayServiceTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | 3 assertion failures showed blank provider, blank model, and blank input were not rejected. |
| feat-031 feature test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayServiceTest,ExternalModelDecisionPortTest,ExternalModelGatewayMapperTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 8 tests, 0 failures, 0 errors. |
| feat-031 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-031 harness check | `./init.sh` | Passing | feature_list.json valid (31 features, 1 active before closure), DDD boundaries verified, BUILD SUCCESS for all 8 modules. |
| feat-032 red test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayMapperTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | Gateway request tools were still plain strings without structured descriptor getters. |
| feat-032 feature test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayMapperTest,ExternalModelGatewayServiceTest,ExternalModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 8 tests, 0 failures, 0 errors. |
| feat-032 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-032 harness check | `./init.sh` | Passing | feature_list.json valid (32 features, 1 active before closure), DDD boundaries verified, BUILD SUCCESS for all 8 modules. |
| feat-033 red test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=FileConversationTurnRepositoryTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | `FileConversationTurnRepository` did not exist before the file-backed adapter was added. |
| feat-033 feature test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=FileConversationTurnRepositoryTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 1 test, 0 failures, 0 errors. |
| feat-033 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-033 harness check | `./init.sh` | Passing | feature_list.json valid (33 features, 1 active before closure), DDD boundaries verified, BUILD SUCCESS for all 8 modules. |
| feat-034 red test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ConversationTurnRepositoryConfigurationTest,FileConversationTurnRepositoryTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | `ConversationTurnRepositoryConfiguration` did not exist before repository selection was configurable. |
| feat-034 feature test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ConversationTurnRepositoryConfigurationTest,FileConversationTurnRepositoryTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 3 tests, 0 failures, 0 errors. |
| feat-034 conversation regression | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 10 tests, 0 failures, 0 errors; default in-memory repository still supports conversation API behavior. |
| feat-034 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-034 harness check | `./init.sh` | Passing | feature_list.json valid (34 features, 1 active before closure), DDD boundaries verified, BUILD SUCCESS for all 8 modules. |
| feat-035 red/setup test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeFileConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | New test initially used `javax.annotation.Resource`, which is unavailable in the Spring Boot 3 test classpath. |
| feat-035 behavioral red test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeFileConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | After switching to `@Autowired`, the test assertion expected a non-existent English deterministic response string while the API persisted the existing Chinese default response. |
| feat-035 feature test | `mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeFileConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 1 test, 0 failures, 0 errors; file-configured API history was reloaded by a new `FileConversationTurnRepository` instance. |
| feat-035 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-035 harness check | `./init.sh` | Passing | feature_list.json valid (35 features, 1 active before closure), DDD boundaries verified, BUILD SUCCESS for all 8 modules. |
| feat-036 red test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayMapperTest -Dsurefire.failIfNoSpecifiedTests=false` | Failed as expected | `OpenAiChatCompletionRequestDTO` did not exist before OpenAI-compatible request mapping was added. |
| feat-036 feature test | `mvn -pl digital-employee-infrastructure -am test -DskipTests=false -Dtest=ExternalModelGatewayMapperTest,ExternalModelGatewayServiceTest,ExternalModelDecisionPortTest -Dsurefire.failIfNoSpecifiedTests=false` | Passing | 9 tests, 0 failures, 0 errors. |
| feat-036 architecture check | `bash scripts/check-architecture.sh` | Passing | DDD boundaries verified. |
| feat-036 harness check | `./init.sh` | Passing | feature_list.json valid (36 features, 1 active before closure), DDD boundaries verified, BUILD SUCCESS for all 8 modules. |

## Important Files

- `feature_list.json` - authoritative feature state; WIP must stay at most one `active`.
- `progress.md` - human-readable progress and verification evidence.
- `docs/migration/claude-code-domain-migration.md` - migration decision and bounded-context mapping.
- `digital-employee-domain/src/main/java/com/digitalemployee/domain/conversation/*` - conversation runtime, tool registry, permission, and model decision request.
- `digital-employee-domain/src/main/java/com/digitalemployee/domain/conversation/adapter/port/IMemoryContextPort.java` - memory-context boundary for conversation decisions.
- `digital-employee-domain/src/main/java/com/digitalemployee/domain/conversation/adapter/port/IShellCommandPort.java` - Domain-owned shell execution boundary.
- `digital-employee-domain/src/main/java/com/digitalemployee/domain/conversation/adapter/repository/IConversationTurnRepository.java` - Domain-owned conversation history repository boundary.
- `digital-employee-domain/src/main/java/com/digitalemployee/domain/conversation/model/valobj/ModelProviderVO.java` - provider metadata for model decision adapters, including API-key environment variable name only.
- `digital-employee-domain/src/main/java/com/digitalemployee/domain/conversation/model/valobj/ShellCommandResultVO.java` - shell command output result value object with bounded summaries.
- `digital-employee-domain/src/main/java/com/digitalemployee/domain/conversation/adapter/port/IWorkspaceFileWritePort.java` - Domain write-file port.
- `digital-employee-domain/src/main/java/com/digitalemployee/domain/conversation/service/PermissionDomainService.java` - workspace file and bash command permission policy.
- `digital-employee-case/src/main/java/com/digitalemployee/cases/conversation/ConversationCaseService.java` - use-case orchestration and bounded same-conversation memory context injection.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/adapter/port/WorkspaceFileWritePort.java` - workspace-root constrained file writer.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/adapter/port/WorkspaceShellCommandPort.java` - workspace-root constrained read-only shell command adapter.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/adapter/port/ExternalModelDecisionPort.java` - external provider stub that does not call a network.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/gateway/ExternalModelGatewayMapper.java` - external gateway DTO mapper.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/gateway/ExternalModelGatewayService.java` - no-network external model gateway service boundary.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/gateway/dto/ExternalModelGatewayRequestDTO.java` - external model request DTO without secret values.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/gateway/dto/ExternalModelGatewayResponseDTO.java` - external model response DTO.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/gateway/dto/ExternalModelGatewayToolDTO.java` - structured external tool descriptor DTO.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/gateway/dto/ExternalModelGatewayToolList.java` - tool list helper that preserves name-based contains checks.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/gateway/dto/OpenAiChatCompletionRequestDTO.java` - OpenAI-compatible chat completions request DTO.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/config/ModelDecisionPortConfiguration.java` - configuration-based model decision port selection.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/adapter/repository/InMemoryConversationTurnRepository.java` - in-memory conversation history adapter.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/adapter/repository/FileConversationTurnRepository.java` - file-backed conversation history adapter.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/config/ConversationTurnRepositoryConfiguration.java` - configuration-based conversation history repository selection.
- `digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure/adapter/port/DeterministicModelDecisionPort.java` - deterministic read/write/edit/bash prompt parsing.
- `digital-employee-infrastructure/src/test/java/com/digitalemployee/infrastructure/adapter/port/DeterministicModelDecisionPortTest.java` - deterministic provider metadata test.
- `digital-employee-domain/src/main/java/com/digitalemployee/domain/task/*` - task lifecycle Domain model and repository port.
- `digital-employee-case/src/main/java/com/digitalemployee/cases/task/*` - task use-case orchestration.
- `digital-employee-trigger/src/main/java/com/digitalemployee/trigger/http/TaskController.java` - task HTTP API.
- `digital-employee-app/src/test/java/com/digitalemployee/test/DigitalEmployeeTaskApiTest.java` - task vertical-slice API tests.
- `digital-employee-app/src/test/java/com/digitalemployee/test/DigitalEmployeeFileConversationApiTest.java` - file-backed conversation repository API persistence regression.
- `digital-employee-domain/src/test/java/com/digitalemployee/domain/conversation/service/ConversationRuntimeServiceTest.java` - model request boundary tests.

## Current API Surface

- `POST /api/v1/conversations/{conversationId}/messages`
- `GET /api/v1/conversations/{conversationId}/messages`
- `POST /api/v1/tasks`
- `GET /api/v1/tasks/{taskId}`
- `GET /api/v1/tasks`
- `POST /api/v1/tasks/{taskId}/start`
- `POST /api/v1/tasks/{taskId}/complete`

## Decisions Made

- Use `digital-employee` as the English name for “数字员工”.
- Keep Option B migration scope: build the DDD backend runtime foundation before attempting a UI/TUI clone.
- Keep the model decision port Domain-owned and let Infrastructure provide deterministic or real model adapters.
- Keep memory context behind a Conversation Domain port so the runtime can evolve without importing Infrastructure or persistence details.
- Expose model provider metadata from the model decision port while preserving deterministic local execution as the default; provider metadata may name an API key env var but must not hold secret values.
- Treat file writes as permission-gated tool executions, not direct Infrastructure behavior.
- Treat file edits as read-then-write tool executions using the same workspace guards.
- Treat bash as a registered permission boundary first; real command execution remains disabled until a dedicated execution port and semantics are added.
- Shell execution is now wired through Infrastructure, but only for Domain-allowed `pwd`, `ls`, and `cat` commands; Infrastructure also performs a second allowlist and workspace-path check.
- Shell command summaries are capped at 1000 characters per stdout/stderr stream with a truncation marker.
- Conversation history is grouped by conversation id behind `IConversationTurnRepository`; Infrastructure defaults to in-memory storage and can select file-backed storage with `digital-employee.conversation.repository=file`.
- Keep tasks in-memory for now; persistence can be added later behind `ITaskRepository`.

## Blockers / Risks

- `feat-003` remains blocked until the user gives concrete Digital Employee business capability requirements.
- Current model behavior is still deterministic/local by default. External provider selection, structured gateway DTO mapping, no-network gateway service boundary, and provider/model/input validation exist; no Anthropic/OpenAI network gateway has been wired yet.

## Recommended Next Step

Start the next Goal-mode slice with WIP=1. Good next choices:

- Persistence route: add an app-level vertical slice proving `digital-employee.conversation.repository=file` persists conversation history through the configured Spring runtime.
- Model route: add OpenAI-compatible chat-completions request mapping for future external provider execution, without network calls or secrets.
- Runtime route: add another Claude Code-style tool boundary if needed by the next acceptance milestone.

## Next Session Startup

1. Read `AGENTS.md`.
2. Run `./init.sh`.
3. Read `feature_list.json`, `progress.md`, and this file.
4. Work on exactly one active or newly selected unblocked feature.

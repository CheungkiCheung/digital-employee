# Claude Code Domain Migration

## Decision

Use option B: migrate the core agent runtime concepts from `claudecode` into this Java DDD system, without copying the terminal TUI as the first milestone.

## Source Understanding

The downloaded source lives at `/tmp/cheungki-claudecode/claudecode`. It is a TypeScript terminal agent runtime with these core domains:

- conversation lifecycle: `QueryEngine.ts`, `query.ts`, message history, model usage, streaming turns
- tool runtime: `Tool.ts`, `tools.ts`, `services/tools/*`, tool schemas, tool results, concurrency rules
- permissions: `types/permissions.ts`, allow, deny, ask, working directory and tool rules
- tasks: `Task.ts`, `src/tasks/*`, background task state and output
- integrations: Anthropic API, MCP, LSP, plugins, skills, remote bridge

The Java migration must preserve the domain behavior, not the TypeScript file layout.

## First Vertical Slice

The first slice proves the system can act like a digital employee backend:

1. Accept a user message through HTTP.
2. Create or continue a conversation.
3. Decide that a file-read tool is needed for explicit read-file requests.
4. Check permissions against the configured workspace root.
5. Execute the file read through a Domain-defined port implemented in Infrastructure.
6. Record the user message, tool call, tool result, and assistant answer.
7. Return the answer and execution trace to the caller.

## DDD Mapping

- Domain owns `conversation`, `tool`, and `permission` model behavior.
- Case orchestrates the conversation turn.
- Infrastructure implements file system access through `infrastructure/adapter/port`.
- Trigger exposes HTTP endpoints and maps API DTOs.
- API module contains request and response DTO contracts.

## Explicit Non-Goals

- Do not copy React Ink terminal UI in this slice.
- Do not implement Anthropic API integration in this slice.
- Do not implement Bash, file write, file edit, MCP, subagents, or background task execution in this slice.
- Do not place business decisions in Infrastructure.

## Acceptance

The slice is accepted when `POST /api/v1/conversations/{conversationId}/messages` can read an allowed project file and return a structured response containing:

- `conversationId`
- final assistant answer
- recorded messages
- tool execution trace

Verification command:

```bash
mvn -pl digital-employee-app -am test -DskipTests=false -Dtest=DigitalEmployeeConversationApiTest -Dsurefire.failIfNoSpecifiedTests=false
./init.sh
```

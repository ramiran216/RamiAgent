## Context

`UserProfileService` currently refreshes profiles by iterating conversation IDs discovered from chat memory. However, the service logic is not explicit about bootstrapping profiles when the `USER_PROFILE` table is empty, and the method naming and flow make the behavior unclear.

The application uses Spring Boot, Spring AI, and JDBC-backed chat memory. The service must safely handle an empty `USER_PROFILE` table and still initialize user profiles from existing chat history.

## Goals / Non-Goals

**Goals:**
- Make profile bootstrapping explicit and reliable when `USER_PROFILE` starts empty
- Ensure `refreshAllProfiles()` discovers conversation IDs from chat memory consistently
- Preserve existing refresh semantics for conversations with existing profiles
- Add a clear separation between profile discovery and profile refresh logic

**Non-Goals:**
- Changing chat memory schema or Deepseek prompt behavior beyond summarization logic
- Adding new persistence storage outside the existing HSQLDB setup

## Decisions

- Keep the `SPRING_AI_CHAT_MEMORY` table as the source of truth for conversation discovery. This ensures new conversations are bootstrapped even before any `USER_PROFILE` row exists.
- Refactor `refreshAllProfiles()` into two steps: discover eligible conversation IDs, then refresh or bootstrap each profile. This improves readability and testability.
- Preserve the existing strategy of skipping profile creation when the generated summary is blank or null.
- Rename internal helper methods if necessary to make the distinction between memory-based discovery and profile persistence clear.

## Risks / Trade-offs

- [Risk] Relying on chat memory as the only source of conversation IDs could miss conversations if memory cleanup or archiving is introduced.
  → Mitigation: The service is explicitly scoped to current chat memory and can be updated if archival support is added.
- [Risk] Existing scheduled refresh behavior may change if conversation discovery is altered.
  → Mitigation: Keep the refresh loop semantics intact and add test coverage for both empty and populated `USER_PROFILE` scenarios.

## Migration Plan

- Implement the refactor in `UserProfileService` without changing its external scheduling contract.
- Add targeted tests for empty table bootstrapping and existing-profile refresh paths.
- Run existing Maven tests to verify no regressions.

## Open Questions

- Should profile creation be limited to conversations with more than a minimum number of messages? (Not in scope for this change.)

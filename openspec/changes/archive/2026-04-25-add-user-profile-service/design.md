## Context

RamiAgent currently uses Spring Boot, Spring AI, and DeepSeek to process chat interactions, storing session history in an HSQLDB-backed JDBC repository. The goal is to augment the assistant with a user profile layer that extracts user labels from conversation history and surfaces them as system prompt context for DeepSeek.

## Goals / Non-Goals

**Goals:**
- Introduce a `UserProfileService` responsible for periodic profile extraction from chat history.
- Persist user profile summaries in a new `USER_PROFILE` table keyed by `conversationId`.
- Add profile context into DeepSeek requests from `HelloController` when available.
- Keep the implementation compatible with existing Spring Boot and HSQLDB setup.

**Non-Goals:**
- Do not change Java, Maven, Spring Boot, Spring AI dependency versions.
- Do not alter the existing high-level assistant flow beyond adding profile context.
- Do not introduce a separate external service beyond the existing DeepSeek integration.

## Decisions

- `UserProfileService` will be a Spring-managed service that fetches the earliest 10 chat records and sends them to DeepSeek for label extraction.
- The user profile model will be persisted in a new `USER_PROFILE` table using `conversationId` as the primary key.
- Existing repository code will be extended with methods for reading and writing `USER_PROFILE` entries.
- `HelloController` will retrieve the current conversation's profile and include it as a system prompt when invoking DeepSeek.
- If no profile exists, the DeepSeek request will proceed normally without profile context.

## Risks / Trade-offs

- [Profile freshness] → The profile may lag behind user behavior if extraction is not frequent enough. Mitigation: schedule regular extraction or trigger profile refresh on conversation start.
- [Prompt size] → Extra profile context could lengthen DeepSeek prompts. Mitigation: store a concise summary of labels rather than full conversation content.
- [Schema migration] → Adding a `USER_PROFILE` table requires repository schema updates. Mitigation: keep the model simple and document the table schema clearly.

## Migration Plan

1. Add the `USER_PROFILE` table and repository mapping.
2. Implement profile extraction and persistence logic.
3. Update `HelloController` to load profile context for DeepSeek.
4. Validate behavior with existing and new conversations.
5. Deploy with verification that DeepSeek still works when no profile exists.

## Open Questions

- Should profile extraction be scheduled periodically or triggered by conversation activity?
- What exact format should the DeepSeek prompt use to return structured labels?

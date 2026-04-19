## Why

The application needs a dedicated user profile service to summarize user interests and basic attributes from recent conversation history, so DeepSeek can provide more personalized responses and context-aware prompts.

## What Changes

- Add a new `USER_PROFILE` database table keyed by `conversationId`.
- Implement a new `UserProfileService` that periodically queries the earliest 10 chat records from the JDBC repository, sends them to DeepSeek, and extracts user labels such as interests, hobbies, and basic profile details.
- Persist the generated profile metadata back into the JDBC repository under `USER_PROFILE`.
- Extend `HelloController` to load the current `conversationId` user profile and include it as a system prompt when invoking DeepSeek.

## Capabilities

### New Capabilities
- `user-profile-service`: Manage user profile extraction and persistence from chat history to enable personalized DeepSeek prompt context.

### Modified Capabilities
- `RamiAgent`: No existing requirement changes; this is a new capability extension that fits the current assistant behavior.

## Impact

- New database table: `USER_PROFILE`.
- New Java service class: `UserProfileService`.
- Updates to `HelloController` request handling for DeepSeek prompts.
- Changes to JDBC repository layer for user profile CRUD operations.
- May require schema updates in HSQLDB scripts or repository configuration.

## 1. Data Model and Repository

- [ ] 1.1 Define the `USER_PROFILE` table schema and add the HSQLDB migration/script entry.
- [ ] 1.2 Add repository methods for reading and writing user profile entries by `conversationId`.

## 2. UserProfileService Implementation

- [ ] 2.1 Create `UserProfileService` to query the earliest 10 chat records from the JDBC repository.
- [ ] 2.2 Implement DeepSeek invocation to summarize profile labels from the fetched chat records.
- [ ] 2.3 Persist the extracted profile labels into `USER_PROFILE` using the repository layer.

## 3. Controller Integration

- [ ] 3.1 Update `HelloController` to fetch the current conversation's user profile.
- [ ] 3.2 Include the loaded profile summary as a system prompt when invoking DeepSeek.
- [ ] 3.3 Ensure DeepSeek requests still work when no profile exists for the conversation.

## 4. Validation and Testing

- [ ] 4.1 Add tests for `UserProfileService` profile extraction and persistence logic.
- [ ] 4.2 Add tests for `HelloController` prompt construction with and without profile context.
- [ ] 4.3 Verify existing chat flow behavior remains unchanged when profile data is absent.

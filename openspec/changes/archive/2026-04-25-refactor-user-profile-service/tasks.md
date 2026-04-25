## 1. Analyze and document

- [x] 1.1 Review current `UserProfileService` refresh logic and identify where conversation discovery is performed
- [x] 1.2 Confirm the bootstrap path when `USER_PROFILE` is empty and document the expected behavior

## 2. Implement refactor

- [x] 2.1 Refactor `UserProfileService.refreshAllProfiles()` to discover conversation IDs through `ChatMemoryRepository` instead of direct table access
- [x] 2.2 Ensure `refreshAllProfiles()` creates a profile for new conversations when `USER_PROFILE` is empty and the chat memory contains conversation history
- [x] 2.3 Maintain refresh behavior for existing `USER_PROFILE` rows
- [x] 2.4 Remove direct chat memory deletion and improve helper methods to separate discovery, summarization, and persistence logic

## 3. Add tests and verify

- [x] 3.1 Add test coverage for initial bootstrap when `USER_PROFILE` is empty but chat memory contains conversations
- [x] 3.2 Add test coverage for no-op behavior when chat memory is empty
- [x] 3.3 Run Maven tests and ensure the change does not break existing behavior

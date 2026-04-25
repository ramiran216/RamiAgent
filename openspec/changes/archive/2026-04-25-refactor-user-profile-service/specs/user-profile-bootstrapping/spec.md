## ADDED Requirements

### Requirement: Bootstrap user profiles from chat memory
The system SHALL generate and refresh `USER_PROFILE` summaries for each conversation recorded in `SPRING_AI_CHAT_MEMORY`, even when the `USER_PROFILE` table is initially empty.

#### Scenario: First run with empty USER_PROFILE
- **WHEN** the application starts and `USER_PROFILE` is empty while `SPRING_AI_CHAT_MEMORY` contains conversation history
- **THEN** `refreshAllProfiles()` processes each conversation ID from `SPRING_AI_CHAT_MEMORY` and creates a `USER_PROFILE` entry when a valid summary is produced

#### Scenario: No chat history present
- **WHEN** `SPRING_AI_CHAT_MEMORY` contains no conversation rows
- **THEN** `refreshAllProfiles()` completes without creating any profiles and without throwing an exception

#### Scenario: Existing profile present
- **WHEN** a conversation already has a `USER_PROFILE` entry and there is message history in `SPRING_AI_CHAT_MEMORY`
- **THEN** `refreshAllProfiles()` refreshes the existing profile summary based on the relevant message history

### Requirement: Discover conversation IDs from chat memory
The system SHALL determine conversation IDs from `SPRING_AI_CHAT_MEMORY` and not from the `USER_PROFILE` table when deciding which profiles to refresh or bootstrap.

#### Scenario: Conversation exists only in chat memory
- **WHEN** a conversation ID appears in `SPRING_AI_CHAT_MEMORY` but has no corresponding `USER_PROFILE` row
- **THEN** the service still treats it as eligible for profile generation

### Requirement: Preserve behavior for blank summaries
The system SHALL skip profile creation or updates when the generated summary is null or blank.

#### Scenario: Summary generation returns blank
- **WHEN** the profile summarization result is null or blank
- **THEN** `refreshAllProfiles()` does not insert or update a `USER_PROFILE` row for that conversation

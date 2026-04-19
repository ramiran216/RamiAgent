## ADDED Requirements

### Requirement: User profile extraction service
The system SHALL periodically extract the earliest 10 chat records for each conversation from the JDBC repository and summarize user traits such as interests, hobbies, and basic profile information using DeepSeek.

#### Scenario: Extract profile labels from chat history
- **WHEN** the profile extraction service runs for a conversation with at least 10 chat entries
- **THEN** it sends the earliest 10 messages to DeepSeek and stores the returned user labels in the repository

### Requirement: Persist user profile by conversation
The system SHALL store generated user profile data in a `USER_PROFILE` table using `conversationId` as the primary key.

#### Scenario: Save user profile data
- **WHEN** DeepSeek returns profile metadata for a conversation
- **THEN** the system SHALL persist the profile into the `USER_PROFILE` table keyed by `conversationId`

### Requirement: Provide profile context to DeepSeek prompt
The system SHALL load the current conversation's user profile from `USER_PROFILE` and include it as a system prompt when calling DeepSeek in `HelloController`.

#### Scenario: Use profile data for DeepSeek prompt
- **WHEN** `HelloController` triggers a DeepSeek request for a conversation
- **THEN** the system SHALL retrieve the corresponding `USER_PROFILE` and include the profile summary as part of the system prompt

### Requirement: Handle missing profile gracefully
The system SHALL continue to work even if no profile exists for the current `conversationId`, without failing the DeepSeek request.

#### Scenario: Missing profile fallback
- **WHEN** the current conversation has no associated `USER_PROFILE` entry
- **THEN** `HelloController` SHALL still invoke DeepSeek without profile context

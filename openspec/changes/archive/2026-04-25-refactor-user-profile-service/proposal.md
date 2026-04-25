## Why

The current `UserProfileService` refresh workflow assumes user profile data already exists in the `USER_PROFILE` table. Because the table starts empty, the existing scheduled refresh logic does not bootstrap profiles from message history correctly and may leave conversations unanalyzed.

## What Changes

- Refactor `UserProfileService.refreshAllProfiles()` to discover active conversation IDs from chat memory and bootstrap profile creation for new conversations.
- Adjust the profile refresh strategy so it can generate the first profile summary when `USER_PROFILE` is empty or when a conversation has no existing profile.
- Improve error handling and logging around profile refresh operations.
- Add a new specification for `user-profile-bootstrapping` describing how profile generation should behave when no prior profile exists.

## Capabilities

### New Capabilities
- `user-profile-bootstrapping`: Define how the system initializes and refreshes user profiles from chat memory when the profile table is empty or incomplete.

### Modified Capabilities
- 

## Impact

- `UserProfileService` and its scheduled refresh flow
- `UserProfileRepository` profile persistence logic
- Database initialization and `USER_PROFILE` table lifecycle
- Potential changes to chat memory read semantics and prompt construction

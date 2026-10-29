# Backend Request Flow

## Application startup

1. `OakApplication` starts the Spring Boot application.
2. Spring discovers controllers, repositories, entities, validation, and exception advice.
3. Spring Data JPA configures persistence against the PostgreSQL URL in `src/main/resources/application.properties`.
4. `PasswordEncoderConfig` provides a BCrypt password encoder; `CorsConfig` reads allowed browser origins from `app.cors.allowed-origins`.
5. Mutable entities receive `created_at` and `updated_at` timestamps from Hibernate timestamp annotations; clients do not set these fields through request DTOs.

## Standard HTTP request

1. A request enters a controller under `/api/users`, `/api/repositories`, `/api/issues`, `/api/comments`, `/api/labels`, `/api/issue-labels`, or `/api/pull-requests`.
2. For create and update bodies, Jackson deserializes into a request record from `com.oak.request`. `@Valid` triggers Jakarta Bean Validation before controller logic runs.
3. The controller maps allowed request fields to an entity. For relationships, it loads the referenced `User`, `Repository`, `Issues`, or `Labels` through the relevant Spring Data repository rather than persisting client-supplied nested entities.
4. The controller calls a Spring Data repository to read, save, or delete the entity. Response mappers convert persisted entities to DTOs in `com.oak.response`.
5. Successful results retain the `{ "message": "...", "data": ... }` envelope. Creates return 201; other successful operations return 200.
6. Unfiltered collection endpoints accept `page` and `size` (default size 20) and return Spring Data page metadata inside `data`.

## Error flow

- Bean Validation failures are converted by `GlobalExceptionHandler` to HTTP 400, with field messages in `data`.
- `BadRequestException` becomes HTTP 400; `ResourceNotFoundException` becomes HTTP 404.
- `DataIntegrityViolationException` becomes HTTP 409.
- Other exceptions become HTTP 500 with a generic message and no stack trace in the response.

## Domain paths

- **Issue creation:** validate the create request, load poster and repository, map request fields to a new issue, save it, then return an `IssuesResponse`.
- **Comment creation:** validate the request, load issue and poster, map content and type, save the comment, then return a `CommentResponse`.
- **Label creation:** validate the request, load the repository, map label fields, save, then return a `LabelsResponse`.
- **Issue-label creation:** validate the request, load issue and label, create the association, save, then return an `IssuesLabelsResponse`.
- **Pull-request creation:** validate the request, load issue, head repository, and base repository, map branch fields, save, then return a `PullRequestResponse`.
- **Issue or repository update:** load the existing entity first, apply only supplied non-null mutable fields, resolve any supplied relationship IDs, save, and return the corresponding response DTO.
- **Issue numbering:** issue creation locks the target repository within a transaction, reads the maximum existing issue number for that repository, assigns the next number, then saves. The database's `(repo_id, index)` unique constraint is a final integrity guard.
- **Issue-label creation:** after loading both records, the controller verifies their repositories match before inserting the association.
- **Pull-request merge:** the merge endpoint rejects an already-merged pull request with HTTP 409; otherwise it marks the pull request merged and closes its linked issue in the same transaction.
- **Deletion:** repository deletion removes its issues and labels; issue deletion removes its comments, issue-label associations, and pull request. A user's dependent repositories, posted issues, and comments are not cascaded, so deleting such a user is rejected by foreign-key integrity and returned as HTTP 409.
- **Comment type:** request and response types use the `CommentType` enum; JPA stores its enum name as a string.
- **User passwords:** create/update requests carry plain `password`; the controller BCrypt-hashes supplied values before saving. Hashes are not included in response DTOs and are not logged.
- **CORS:** the global MVC configuration applies configured origins to `/api/**`; the old wildcard CORS annotation on the user controller has been removed.

## Current boundary

The application has no authentication middleware or caller identity context yet. Read endpoints for repositories and their issues, labels, comments, issue-label links, and pull requests contain TODOs for owner-only filtering. This is not access control: private repository data remains readable through those endpoints until authentication and authorization are implemented. JWT is the recommended minimal approach for this REST API, pending owner confirmation.

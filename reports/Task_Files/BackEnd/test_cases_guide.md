# Test Cases Guide for Trainees

This guide provides a comprehensive list of test cases that trainees should implement to ensure robust testing of the application. Each test case includes the proper test name following the pattern: `methodName_scenario_expectedResult` and a clear description of what it should verify.

## Service Layer Tests

### UserService Tests

#### createUser_validInput_returnsUserDTO
**Description**: Should successfully create a new user when valid UserCreateDTO is provided
- Test should mock UserRepository.save() and UserMapper.toDTO()
- Verify that the returned UserDTO has the expected ID
- Ensure UserRepository.save() is called once

#### getUserById_found_returnsUserDTO
**Description**: Should return UserDTO when user exists in database
- Mock UserRepository.findById() to return a user entity
- Verify the returned UserDTO contains correct data
- Ensure mapping from entity to DTO works correctly

#### getUserById_notFound_throwsResourceNotFoundException
**Description**: Should throw ResourceNotFoundException when user doesn't exist
- Mock UserRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure the exception message is appropriate

#### getUsersByRole_validRole_returnsUserDTOList
**Description**: Should return list of UserDTOs filtered by user role
- Create multiple users with different roles
- Mock repository to return users with specific role
- Verify returned list contains only users with the requested role
- Test with different roles: USER, ADMIN, MANAGER

#### updateUser_validUpdate_returnsUpdatedUserDTO
**Description**: Should successfully update user information
- Mock findById() to return existing user
- Mock save() to return updated user
- Verify the update operation modifies correct fields
- Ensure UserMapper.toDTO() is called for response

#### updateUser_notFound_throwsResourceNotFoundException
**Description**: Should throw ResourceNotFoundException when updating non-existent user
- Mock UserRepository.findById() to return empty
- Use assertThrows to verify exception
- Ensure repository save() is never called

#### deleteUser_validId_completesSuccessfully
**Description**: Should successfully delete user when user exists
- Mock findById() to return existing user
- Mock deleteById() with any() argument matcher
- Verify delete operation is called
- Ensure no exception is thrown

#### deleteUser_notFound_throwsResourceNotFoundException
**Description**: Should throw ResourceNotFoundException when deleting non-existent user
- Mock UserRepository.findById() to return empty
- Use assertThrows to verify exception
- Ensure deleteById() is never called

### ProjectService Tests

#### createProject_withOwnerId_savesAndReturnsDTO
**Description**: Should create project and set owner relationship when valid ProjectCreateDTO and ownerId provided
- Mock ProjectMapper.toEntity() to return project entity
- Mock UserRepository.findById() to return owner user
- Mock ProjectRepository.save() to return project with ID
- Verify owner relationship is properly set
- Ensure project is saved with correct owner

#### getProjectById_found_returnsDTO
**Description**: Should return ProjectDTO when project exists
- Mock ProjectRepository.findById() to return project entity
- Verify returned ProjectDTO contains correct project data
- Ensure ProjectMapper.toDTO() is called

#### getProjectsByOwner_returnsList
**Description**: Should return list of projects owned by specific user
- Create multiple projects with same owner
- Mock ProjectRepository.findByOwnerId() to return project list
- Verify returned list size matches expected
- Ensure each project has correct owner relationship

#### updateProject_withOwnerChange_updatesAndReturns
**Description**: Should update project and change owner when new ownerId provided
- Mock findById() to return existing project
- Mock UserRepository.findById() to return new owner
- Mock save() to return updated project
- Verify owner is changed to new owner
- Ensure other project fields remain unchanged

#### updateProject_nameAndDescription_updatesAndReturns
**Description**: Should update project name and description without changing owner
- Create update DTO with new name and description
- Mock findById() to return existing project
- Mock save() to return updated project
- Verify name and description are updated
- Ensure owner remains unchanged

#### deleteProject_validId_completesSuccessfully
**Description**: Should successfully delete project when project exists
- Mock findById() to return existing project
- Mock delete() with any() argument matcher
- Verify delete operation is called
- Ensure project is removed from database

#### getProjectsByStatus_validStatus_returnsFilteredList
**Description**: Should return projects filtered by status
- Create projects with different statuses
- Mock repository to return projects with specific status
- Verify returned list contains only projects with requested status
- Test with all possible project statuses

#### createProject_ownerNotFound_throws
**Description**: Should throw ResourceNotFoundException when specified owner doesn't exist
- Mock ProjectMapper.toEntity() to return project entity
- Mock UserRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure project is not saved

#### getProjectById_notFound_throws
**Description**: Should throw ResourceNotFoundException when project doesn't exist
- Mock ProjectRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure appropriate error message

#### updateProject_projectNotFound_throws
**Description**: Should throw ResourceNotFoundException when updating non-existent project
- Mock ProjectRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure repository save() is never called

#### updateProject_ownerNotFound_throws
**Description**: Should throw ResourceNotFoundException when new owner doesn't exist
- Mock findById() to return existing project
- Mock UserRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure project is not updated

### TaskService Tests

#### createTask_setsRelationshipsAndReturnsDTO
**Description**: Should create task and set all relationships (project, assignee) when valid TaskCreateDTO provided
- Mock TaskMapper.toEntity() to return task entity
- Mock ProjectRepository.findById() to return project
- Mock UserRepository.findById() to return assignee
- Mock TaskRepository.save() to return task with ID
- Verify all relationships are properly set
- Ensure task is saved with correct associations

#### getTaskById_found_returnsDTO
**Description**: Should return TaskDTO when task exists in database
- Mock TaskRepository.findById() to return task entity
- Verify returned TaskDTO contains all task data
- Ensure mapping includes project and assignee information

#### getTasksByProject_returnsList
**Description**: Should return all tasks belonging to specific project
- Create multiple tasks for same project
- Mock TaskRepository.findByProjectId() to return task list
- Verify returned list contains all project tasks
- Ensure each task has correct project relationship

#### getTasksByAssignee_validAssigneeId_returnsTaskList
**Description**: Should return all tasks assigned to specific user
- Create tasks with different assignees
- Mock repository to return tasks for specific user
- Verify returned list contains only tasks assigned to that user
- Ensure assignee information is correctly mapped

#### updateTaskStatus_validUpdate_returnsUpdatedDTO
**Description**: Should update task status and return updated TaskDTO
- Create task with initial status
- Mock findById() to return existing task
- Mock save() to return updated task
- Verify status is changed to new status
- Ensure other task fields remain unchanged

#### updateTaskPriority_validUpdate_returnsUpdatedDTO
**Description**: Should update task priority and return updated TaskDTO
- Create task with initial priority
- Mock findById() to return existing task
- Mock save() to return updated task
- Verify priority is changed to new priority
- Test all possible priority levels

#### deleteTask_validId_completesSuccessfully
**Description**: Should successfully delete task when task exists
- Mock findById() to return existing task
- Mock delete() with any() argument matcher
- Verify delete operation is called
- Ensure task is removed from database

#### getOverdueTasks_validQuery_returnsOverdueTaskList
**Description**: Should return tasks that are past their due date
- Create tasks with different due dates (some overdue, some not)
- Mock repository to return only overdue tasks
- Verify returned tasks have due dates in the past
- Ensure tasks are filtered correctly

#### createTask_projectNotFound_throws
**Description**: Should throw ResourceNotFoundException when specified project doesn't exist
- Mock TaskMapper.toEntity() to return task entity
- Mock ProjectRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure task is not saved

#### createTask_assigneeNotFound_throws
**Description**: Should throw ResourceNotFoundException when specified assignee doesn't exist
- Mock TaskMapper.toEntity() to return task entity
- Mock ProjectRepository.findById() to return project
- Mock UserRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure task is not saved

#### getTaskById_notFound_throws
**Description**: Should throw ResourceNotFoundException when task doesn't exist
- Mock TaskRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure appropriate error message

#### updateTaskStatus_taskNotFound_throws
**Description**: Should throw ResourceNotFoundException when updating non-existent task
- Mock TaskRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure repository save() is never called

### CommentService Tests

#### createComment_validInput_returnsCommentDTO
**Description**: Should create comment for task when valid CommentCreateDTO provided
- Mock CommentMapper.toEntity() to return comment entity
- Mock TaskRepository.findById() to return task
- Mock CommentRepository.save() to return comment with ID
- Verify comment is associated with correct task
- Ensure comment text and other fields are set correctly

#### getCommentsByTask_validTaskId_returnsCommentList
**Description**: Should return all comments for specific task
- Create multiple comments for same task
- Mock CommentRepository.findByTaskId() to return comment list
- Verify returned list contains all task comments
- Ensure comments are ordered by creation date (newest first)

#### updateComment_validUpdate_returnsUpdatedCommentDTO
**Description**: Should update comment text when comment exists
- Mock findById() to return existing comment
- Mock save() to return updated comment
- Verify comment text is updated
- Ensure other comment fields remain unchanged

#### deleteComment_validId_completesSuccessfully
**Description**: Should successfully delete comment when comment exists
- Mock findById() to return existing comment
- Mock delete() with any() argument matcher
- Verify delete operation is called
- Ensure comment is removed from database

#### createComment_taskNotFound_throws
**Description**: Should throw ResourceNotFoundException when specified task doesn't exist
- Mock CommentMapper.toEntity() to return comment entity
- Mock TaskRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure comment is not saved

#### getCommentsByTask_taskNotFound_throws
**Description**: Should throw ResourceNotFoundException when task doesn't exist
- Mock TaskRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure appropriate error message

#### updateComment_notFound_throws
**Description**: Should throw ResourceNotFoundException when updating non-existent comment
- Mock CommentRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure repository save() is never called

#### deleteComment_notFound_throws
**Description**: Should throw ResourceNotFoundException when deleting non-existent comment
- Mock CommentRepository.findById() to return empty
- Use assertThrows to verify exception is thrown
- Ensure delete operation is never called

## Repository Layer Tests (Integration Tests)

### UserRepository Tests

#### save_and_findById_validData_returnsSavedUser
**Description**: Should save user to database and retrieve it by ID
- Save user entity using repository
- Retrieve user by ID
- Verify all fields match saved user
- Test with complete user data including relationships

#### findByEmail_validEmail_returnsUser
**Description**: Should find user by email address
- Save user with specific email
- Query repository using email
- Verify returned user matches saved user
- Test with different email formats

#### findByRole_validRole_returnsUserList
**Description**: Should find all users with specific role
- Save users with different roles
- Query repository for specific role
- Verify returned list contains only users with that role
- Test with each role type

#### deleteById_validId_removesUser
**Description**: Should delete user from database
- Save user to database
- Delete user by ID
- Verify user no longer exists in database
- Ensure database row is removed

#### findByEmail_emailNotFound_returnsEmpty
**Description**: Should return empty when email doesn't exist
- Query repository for non-existent email
- Verify returned result is empty
- Ensure no exception is thrown

### ProjectRepository Tests

#### save_and_findById_validData_returnsSavedProject
**Description**: Should save project and retrieve it by ID
- Save project with owner relationship
- Retrieve project by ID
- Verify all project fields and owner relationship
- Ensure bidirectional relationship works

#### findByOwnerId_validOwnerId_returnsProjectList
**Description**: Should find all projects owned by specific user
- Save multiple projects with same owner
- Query repository using owner ID
- Verify all returned projects belong to that owner
- Ensure owner relationship is loaded

#### findByStatus_validStatus_returnsProjectList
**Description**: Should find projects by status
- Save projects with different statuses
- Query repository for specific status
- Verify returned projects have correct status
- Test with all project statuses

#### deleteById_validId_removesProject
**Description**: Should delete project from database
- Save project to database
- Delete project by ID
- Verify project no longer exists
- Ensure cascading delete works for related tasks

#### findByOwnerId_ownerNotFound_returnsEmpty
**Description**: Should return empty when owner doesn't have projects
- Query repository for non-existent owner
- Verify returned result is empty
- Ensure no exception is thrown

### TaskRepository Tests

#### save_and_findById_validData_returnsSavedTask
**Description**: Should save task and retrieve it by ID
- Save task with project and assignee relationships
- Retrieve task by ID
- Verify all task fields and relationships
- Ensure all foreign key relationships are correct

#### findByProjectId_validProjectId_returnsTaskList
**Description**: Should find all tasks for specific project
- Save multiple tasks for same project
- Query repository using project ID
- Verify all returned tasks belong to that project
- Ensure project relationship is loaded

#### findByAssigneeId_validAssigneeId_returnsTaskList
**Description**: Should find all tasks assigned to specific user
- Save tasks with different assignees
- Query repository using assignee ID
- Verify all returned tasks assigned to that user
- Ensure assignee relationship is loaded

#### findByStatus_validStatus_returnsTaskList
**Description**: Should find tasks by status
- Save tasks with different statuses
- Query repository for specific status
- Verify returned tasks have correct status
- Test with all task statuses

#### findByPriority_validPriority_returnsTaskList
**Description**: Should find tasks by priority level
- Save tasks with different priorities
- Query repository for specific priority
- Verify returned tasks have correct priority
- Test with all priority levels

#### findByStatusAndPriority_validFilters_returnsFilteredList
**Description**: Should find tasks filtered by both status and priority
- Save tasks with various status and priority combinations
- Query repository with both filters
- Verify returned tasks match both criteria
- Test multiple combinations

#### findOverdueTasks_validQuery_returnsOverdueTaskList
**Description**: Should find tasks that are past their due date
- Save tasks with different due dates
- Query for overdue tasks
- Verify returned tasks have past due dates
- Ensure current tasks are not included

#### findByProjectIdAndStatus_validFilters_returnsFilteredList
**Description**: Should find tasks filtered by project and status
- Save tasks for multiple projects with different statuses
- Query repository with project ID and status
- Verify returned tasks match both criteria
- Test with various combinations

#### countByProjectId_validProjectId_returnsTaskCount
**Description**: Should return count of tasks for specific project
- Save known number of tasks for a project
- Query repository for count
- Verify returned count matches actual number
- Test with zero tasks, one task, and multiple tasks

#### findByProjectId_projectNotFound_returnsEmpty
**Description**: Should return empty when project has no tasks
- Query repository for non-existent project
- Verify returned result is empty
- Ensure no exception is thrown

### CommentRepository Tests

#### save_and_findById_validData_returnsSavedComment
**Description**: Should save comment and retrieve it by ID
- Save comment associated with task
- Retrieve comment by ID
- Verify all comment fields and task relationship
- Ensure foreign key relationship is correct

#### findByTaskId_validTaskId_returnsCommentList
**Description**: Should find all comments for specific task
- Save multiple comments for same task
- Query repository using task ID
- Verify all returned comments belong to that task
- Ensure comments are ordered by creation date

#### deleteByTaskId_validTaskId_removesAllComments
**Description**: Should delete all comments for specific task
- Save multiple comments for a task
- Delete comments by task ID
- Verify no comments remain for that task
- Ensure cascading delete works correctly

#### findByTaskIdOrderByCreatedAtDesc_validTaskId_returnsOrderedComments
**Description**: Should find comments for task ordered by creation date (newest first)
- Save comments at different times
- Query repository with order specification
- Verify comments are returned in descending order
- Ensure creation timestamps are used for ordering

#### findByTaskId_taskNotFound_returnsEmpty
**Description**: Should return empty when task has no comments
- Query repository for non-existent task
- Verify returned result is empty
- Ensure no exception is thrown

## Mapper Layer Tests

### UserMapper Tests

#### toEntity_validCreateDTO_returnsUserEntity
**Description**: Should convert UserCreateDTO to User entity
- Create UserCreateDTO with sample data
- Map to User entity using mapper
- Verify all fields are correctly mapped
- Ensure no null pointer exceptions

#### toDTO_validUser_returnsUserDTO
**Description**: Should convert User entity to UserDTO
- Create User entity with sample data
- Map to UserDTO using mapper
- Verify all fields are correctly mapped
- Ensure sensitive fields (password) are excluded

#### toDTOList_validUserList_returnsUserDTOList
**Description**: Should convert list of User entities to UserDTOs
- Create list of User entities
- Map to list of UserDTOs using mapper
- Verify all entities are converted
- Ensure list size matches original

#### updateEntityFromDTO_validUpdate_returnsUpdatedUser
**Description**: Should update User entity fields from UserUpdateDTO
- Create existing User entity
- Create UserUpdateDTO with new data
- Update entity using mapper
- Verify specified fields are updated
- Ensure other fields remain unchanged

### ProjectMapper Tests

#### toEntity_validCreateDTO_returnsProjectEntity
**Description**: Should convert ProjectCreateDTO to Project entity
- Create ProjectCreateDTO with sample data
- Map to Project entity using mapper
- Verify all fields are correctly mapped
- Ensure relationships are handled properly

#### toDTO_validProject_returnsProjectDTO
**Description**: Should convert Project entity to ProjectDTO
- Create Project entity with relationships
- Map to ProjectDTO using mapper
- Verify all fields are correctly mapped
- Ensure owner relationship is included

#### toDTOList_validProjectList_returnsProjectDTOList
**Description**: Should convert list of Project entities to ProjectDTOs
- Create list of Project entities
- Map to list of ProjectDTOs using mapper
- Verify all entities are converted
- Ensure list size matches original

#### updateEntityFromDTO_validUpdate_returnsUpdatedProject
**Description**: Should update Project entity fields from ProjectUpdateDTO
- Create existing Project entity
- Create ProjectUpdateDTO with new data
- Update entity using mapper
- Verify specified fields are updated
- Ensure ID and other immutable fields remain unchanged

### TaskMapper Tests

#### toEntity_validCreateDTO_returnsTaskEntity
**Description**: Should convert TaskCreateDTO to Task entity
- Create TaskCreateDTO with sample data
- Map to Task entity using mapper
- Verify all fields are correctly mapped
- Ensure relationships are handled properly

#### toDTO_validTask_returnsTaskDTO
**Description**: Should convert Task entity to TaskDTO
- Create Task entity with relationships
- Map to TaskDTO using mapper
- Verify all fields are correctly mapped
- Ensure project and assignee relationships are included

#### toDTOList_validTaskList_returnsTaskDTOList
**Description**: Should convert list of Task entities to TaskDTOs
- Create list of Task entities
- Map to list of TaskDTOs using mapper
- Verify all entities are converted
- Ensure list size matches original

#### updateEntityFromDTO_validUpdate_returnsUpdatedTask
**Description**: Should update Task entity fields from TaskUpdateDTO
- Create existing Task entity
- Create TaskUpdateDTO with new data
- Update entity using mapper
- Verify specified fields are updated
- Ensure ID and creation date remain unchanged

### CommentMapper Tests

#### toEntity_validCreateDTO_returnsCommentEntity
**Description**: Should convert CommentCreateDTO to Comment entity
- Create CommentCreateDTO with sample data
- Map to Comment entity using mapper
- Verify all fields are correctly mapped
- Ensure task relationship is handled

#### toDTO_validComment_returnsCommentDTO
**Description**: Should convert Comment entity to CommentDTO
- Create Comment entity with task relationship
- Map to CommentDTO using mapper
- Verify all fields are correctly mapped
- Ensure task relationship is included

#### toDTOList_validCommentList_returnsCommentDTOList
**Description**: Should convert list of Comment entities to CommentDTOs
- Create list of Comment entities
- Map to list of CommentDTOs using mapper
- Verify all entities are converted
- Ensure list size matches original

## Controller Layer Tests

### UserController Tests

#### createUser_validInput_returnsCreatedStatus
**Description**: Should create user and return 201 status with UserDTO
- Mock UserService.createUser() to return UserDTO
- Create UserCreateDTO with valid data
- Call controller endpoint
- Verify HTTP status is 201 (Created)
- Verify returned UserDTO matches expected data

#### getUserById_validId_returnsUserDTO
**Description**: Should return user data when user exists
- Mock UserService.getUserById() to return UserDTO
- Call controller endpoint with user ID
- Verify HTTP status is 200 (OK)
- Verify returned UserDTO contains correct data

#### getUserById_notFound_returnsNotFoundStatus
**Description**: Should return 404 when user doesn't exist
- Mock UserService.getUserById() to throw ResourceNotFoundException
- Call controller endpoint with non-existent user ID
- Verify HTTP status is 404 (Not Found)
- Verify error response structure is correct

#### getUsersByRole_validRole_returnsUserList
**Description**: Should return users filtered by role
- Mock UserService.getUsersByRole() to return UserDTO list
- Call controller endpoint with role parameter
- Verify HTTP status is 200 (OK)
- Verify returned list contains users with specified role

#### updateUser_validUpdate_returnsUpdatedUserDTO
**Description**: Should update user and return updated UserDTO
- Mock UserService.updateUser() to return UserDTO
- Create UserUpdateDTO with new data
- Call controller endpoint
- Verify HTTP status is 200 (OK)
- Verify returned UserDTO contains updated data

#### deleteUser_validId_returnsNoContentStatus
**Description**: Should delete user and return 204 status
- Mock UserService.deleteUser() to complete successfully
- Call controller endpoint with user ID
- Verify HTTP status is 204 (No Content)
- Verify no content is returned

### ProjectController Tests

#### createProject_validInput_returnsCreatedStatus
**Description**: Should create project and return 201 status with ProjectDTO
- Mock ProjectService.createProject() to return ProjectDTO
- Create ProjectCreateDTO with valid data
- Call controller endpoint
- Verify HTTP status is 201 (Created)
- Verify returned ProjectDTO matches expected data

#### getProjectById_validId_returnsProjectDTO
**Description**: Should return project data when project exists
- Mock ProjectService.getProjectById() to return ProjectDTO
- Call controller endpoint with project ID
- Verify HTTP status is 200 (OK)
- Verify returned ProjectDTO contains correct data

#### getProjectsByOwner_validOwnerId_returnsProjectList
**Description**: Should return projects owned by specific user
- Mock ProjectService.getProjectsByOwner() to return ProjectDTO list
- Call controller endpoint with owner ID
- Verify HTTP status is 200 (OK)
- Verify returned list contains only projects owned by that user

#### updateProject_validUpdate_returnsUpdatedProjectDTO
**Description**: Should update project and return updated ProjectDTO
- Mock ProjectService.updateProject() to return ProjectDTO
- Create ProjectUpdateDTO with new data
- Call controller endpoint
- Verify HTTP status is 200 (OK)
- Verify returned ProjectDTO contains updated data

#### deleteProject_validId_returnsNoContentStatus
**Description**: Should delete project and return 204 status
- Mock ProjectService.deleteProject() to complete successfully
- Call controller endpoint with project ID
- Verify HTTP status is 204 (No Content)
- Verify no content is returned

### TaskController Tests

#### createTask_validInput_returnsCreatedStatus
**Description**: Should create task and return 201 status with TaskDTO
- Mock TaskService.createTask() to return TaskDTO
- Create TaskCreateDTO with valid data
- Call controller endpoint
- Verify HTTP status is 201 (Created)
- Verify returned TaskDTO matches expected data

#### getTaskById_validId_returnsTaskDTO
**Description**: Should return task data when task exists
- Mock TaskService.getTaskById() to return TaskDTO
- Call controller endpoint with task ID
- Verify HTTP status is 200 (OK)
- Verify returned TaskDTO contains correct data

#### getTasksByProject_validProjectId_returnsTaskList
**Description**: Should return tasks for specific project
- Mock TaskService.getTasksByProject() to return TaskDTO list
- Call controller endpoint with project ID
- Verify HTTP status is 200 (OK)
- Verify returned list contains only tasks for that project

#### updateTaskStatus_validUpdate_returnsUpdatedTaskDTO
**Description**: Should update task status and return updated TaskDTO
- Mock TaskService.updateTaskStatus() to return TaskDTO
- Call controller endpoint with task ID and new status
- Verify HTTP status is 200 (OK)
- Verify returned TaskDTO contains updated status

#### deleteTask_validId_returnsNoContentStatus
**Description**: Should delete task and return 204 status
- Mock TaskService.deleteTask() to complete successfully
- Call controller endpoint with task ID
- Verify HTTP status is 204 (No Content)
- Verify no content is returned

### CommentController Tests

#### createComment_validInput_returnsCreatedStatus
**Description**: Should create comment and return 201 status with CommentDTO
- Mock CommentService.createComment() to return CommentDTO
- Create CommentCreateDTO with valid data
- Call controller endpoint
- Verify HTTP status is 201 (Created)
- Verify returned CommentDTO matches expected data

#### getCommentsByTask_validTaskId_returnsCommentList
**Description**: Should return comments for specific task
- Mock CommentService.getCommentsByTask() to return CommentDTO list
- Call controller endpoint with task ID
- Verify HTTP status is 200 (OK)
- Verify returned list contains only comments for that task

#### updateComment_validUpdate_returnsUpdatedCommentDTO
**Description**: Should update comment and return updated CommentDTO
- Mock CommentService.updateComment() to return CommentDTO
- Create CommentUpdateDTO with new data
- Call controller endpoint
- Verify HTTP status is 200 (OK)
- Verify returned CommentDTO contains updated data

#### deleteComment_validId_returnsNoContentStatus
**Description**: Should delete comment and return 204 status
- Mock CommentService.deleteComment() to complete successfully
- Call controller endpoint with comment ID
- Verify HTTP status is 204 (No Content)
- Verify no content is returned

## Exception Handling Tests

### GlobalExceptionHandler Tests

#### handleResourceNotFoundException_validException_returnsNotFoundResponse
**Description**: Should return 404 status with error response when ResourceNotFoundException is thrown
- Create ResourceNotFoundException with custom message
- Call exception handler method
- Verify HTTP status is 404 (Not Found)
- Verify error response contains correct error code and message

#### handleValidationException_validException_returnsBadRequestResponse
**Description**: Should return 400 status with validation error details when ValidationException is thrown
- Create ValidationException with field errors
- Call exception handler method
- Verify HTTP status is 400 (Bad Request)
- Verify error response contains field validation errors

#### handleBusinessException_validException_returnsBadRequestResponse
**Description**: Should return 400 status with business error when BusinessException is thrown
- Create BusinessException with custom message
- Call exception handler method
- Verify HTTP status is 400 (Bad Request)
- Verify error response contains business error details

#### handleAccessDeniedException_validException_returnsForbiddenResponse
**Description**: Should return 403 status with access denied error when AccessDeniedException is thrown
- Create AccessDeniedException with custom message
- Call exception handler method
- Verify HTTP status is 403 (Forbidden)
- Verify error response contains access denied message

#### handleGenericException_unexpectedException_returnsInternalServerError
**Description**: Should return 500 status with internal server error for unexpected exceptions
- Create generic RuntimeException
- Call exception handler method
- Verify HTTP status is 500 (Internal Server Error)
- Verify error response contains generic error message

## Edge Cases and Boundary Tests

### Data Validation Tests

#### createUser_invalidEmail_throwsValidationException
**Description**: Should throw ValidationException when email format is invalid
- Create UserCreateDTO with malformed email
- Mock UserService to throw ValidationException
- Call controller endpoint
- Verify HTTP status is 400 (Bad Request)
- Verify validation error details are returned

#### createProject_emptyName_throwsValidationException
**Description**: Should throw ValidationException when project name is empty
- Create ProjectCreateDTO with empty name
- Mock ProjectService to throw ValidationException
- Call controller endpoint
- Verify HTTP status is 400 (Bad Request)
- Verify field error for name is returned

#### createTask_invalidPriority_throwsValidationException
**Description**: Should throw ValidationException when priority value is invalid
- Create TaskCreateDTO with invalid priority enum
- Mock TaskService to throw ValidationException
- Call controller endpoint
- Verify HTTP status is 400 (Bad Request)
- Verify validation error for priority field

#### createComment_emptyText_throwsValidationException
**Description**: Should throw ValidationException when comment text is empty
- Create CommentCreateDTO with empty text
- Mock CommentService to throw ValidationException
- Call controller endpoint
- Verify HTTP status is 400 (Bad Request)
- Verify validation error for text field

### Null and Empty Tests

#### getUserById_nullId_returnsBadRequest
**Description**: Should handle null ID parameter gracefully
- Call controller endpoint with null user ID
- Verify HTTP status is 400 (Bad Request) or handle appropriately
- Ensure no NullPointerException is thrown

#### getTasksByProject_nonExistentProject_returnsEmptyList
**Description**: Should return empty list when project has no tasks
- Mock TaskService to return empty list
- Call controller endpoint with project ID
- Verify HTTP status is 200 (OK)
- Verify returned list is empty

#### createTask_nonExistentProject_throwsResourceNotFoundException
**Description**: Should throw ResourceNotFoundException when project doesn't exist
- Mock TaskService to throw ResourceNotFoundException
- Create TaskCreateDTO with non-existent project ID
- Call controller endpoint
- Verify HTTP status is 404 (Not Found)
- Verify error response structure

#### createTask_nonExistentAssignee_throwsResourceNotFoundException
**Description**: Should throw ResourceNotFoundException when assignee doesn't exist
- Mock TaskService to throw ResourceNotFoundException
- Create TaskCreateDTO with non-existent assignee ID
- Call controller endpoint
- Verify HTTP status is 404 (Not Found)
- Verify error response structure

### Performance and Load Tests

#### getUsers_pagination_validParams_returnsPaginatedResults
**Description**: Should return paginated results when requesting large user lists
- Mock UserService to return paginated results
- Call controller endpoint with page and size parameters
- Verify HTTP status is 200 (OK)
- Verify pagination metadata is returned
- Ensure only requested page size is returned

#### getProjects_largeDataset_returnsResults
**Description**: Should handle queries with large result sets efficiently
- Mock ProjectService to return large project list
- Call controller endpoint
- Verify HTTP status is 200 (OK)
- Ensure response time is acceptable
- Verify data integrity in returned results

## Testing Best Practices

### Test Naming Convention
- Use descriptive test method names that explain what is being tested
- Follow pattern: `methodName_scenario_expectedResult`
- Examples: `createUser_validInput_returnsUserDTO`, `getUserById_notFound_throwsResourceNotFoundException`

### Test Data Setup
- Use meaningful test data instead of generic values
- Create reusable test data builders or factories
- Use constants for repeated test values
- Ensure test data doesn't conflict between tests

### Mocking Strategy
- Mock external dependencies (repositories, services, mappers)
- Use argument matchers appropriately (any(), eq(), etc.)
- Verify method calls with verify() to ensure interactions
- Don't over-mock - use real objects when appropriate

### Assertion Guidelines
- Use specific assertions that clearly indicate what is being tested
- Include meaningful assertion messages
- Test both positive and negative scenarios
- Verify not just the result but the side effects (method calls, state changes)

### Test Independence
- Each test should be independent and not rely on other tests
- Use @BeforeEach for common setup, @BeforeAll for expensive setup
- Clean up test data after each test
- Avoid test order dependency

### Coverage Goals
- Aim for high code coverage (>80%)
- Include edge cases and error scenarios
- Test all public methods and critical paths
- Don't sacrifice test quality for coverage metrics

## Implementation Notes

### Dependencies for Testing
- JUnit 5 for testing framework
- Mockito for mocking dependencies
- AssertJ for fluent assertions
- Spring Boot Test for integration tests
- TestContainers for database testing (if needed)

### Test Configuration
- Use separate test application.properties/yaml
- Configure in-memory database for tests
- Set appropriate logging levels for tests
- Use @ActiveProfiles("test") for integration tests

### Running Tests
- Use `mvn test` to run all tests
- Use `mvn test -Dtest=ClassName` to run specific test class
- Use `mvn test -Dtest="*Service*Test"` to run service tests
- Generate test reports with `mvn surefire-report:report`

This guide provides a comprehensive foundation for implementing robust tests across all layers of the application. Trainees should prioritize understanding the testing patterns and then implement tests systematically, starting with service layer tests and progressing to more complex integration and controller tests.

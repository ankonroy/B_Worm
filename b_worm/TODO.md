# Book Exchange Platform - Implementation Steps

## Current Progress: Starting Step 1/12

### Step 1: Project Setup [DONE]
- [x] Create `BookExchangeApplication.java` with DataLoader
- [ ] Fix test class package/name (move file)
- [x] Update this TODO

### Step 3: Entities (4 files) [DONE]
- [x] User.java, Book.java, Request.java, Notification.java

### Step 4: Repositories (4 files) [DONE]
- [x] UserRepository.java, BookRepository.java, RequestRepository.java, NotificationRepository.java

### Step 5: DTOs (7 files) [DONE]
- [x] RegisterRequest.java, LoginRequest.java, BookRequest.java
- [x] UserResponse.java, BookResponse.java, RequestResponse.java, NotificationResponse.java

### Step 6: Exceptions (4 files) [DONE]
- [x] ResourceNotFoundException.java, UnauthorizedException.java, BadRequestException.java
- [x] GlobalExceptionHandler.java

### Step 7: Security (4 files) [DONE]
- [x] CustomUserDetails.java, CustomUserDetailsService.java
- [x] SecurityConfig.java, CustomAuthenticationSuccessHandler.java

### Step 8: Services (6 files) [DONE]
- [x] UserService.java/impl, BookService.java/impl, NotificationService.java/impl

### Step 9: Controllers (6 files) [DONE]
- [x] AuthController.java, HomeController.java, BookController.java
- [x] ProfileController.java, NotificationController.java, AdminController.java

### Step 10: Templates (14+ files) [DONE]

### Step 2: Enums (5 files)
- Role.java, BookStatus.java, BookCondition.java, RequestStatus.java, NotificationType.java

### Step 3: Entities (4 files)
- User.java, Book.java, Request.java, Notification.java

### Step 4: Repositories (4 files)
- UserRepository.java, BookRepository.java, RequestRepository.java, NotificationRepository.java

### Step 5: DTOs (7 files)
- RegisterRequest.java, LoginRequest.java, BookRequest.java
- UserResponse.java, BookResponse.java, RequestResponse.java, NotificationResponse.java

### Step 6: Exceptions (4 files)
- ResourceNotFoundException.java, UnauthorizedException.java, BadRequestException.java
- GlobalExceptionHandler.java

### Step 7: Security (4 files)
- CustomUserDetails.java, CustomUserDetailsService.java, SecurityConfig.java, CustomAuthenticationSuccessHandler.java

### Step 8: Services (6 files)
- UserService.java/impl, BookService.java/impl, NotificationService.java/impl

### Step 9: Controllers (6 files)
- AuthController.java, HomeController.java, BookController.java, ProfileController.java, NotificationController.java, AdminController.java

### Step 10: Templates (14+ files)
- layouts/fragments, welcome.html, auth/, books/, profile/, notifications/, admin/

### Step 11: Tests (20+ files)
- Move/rename existing test
- 15+ service unit tests
- 3+ controller integration tests

### Step 12: Final Polish
- Verify structure
- Manual test instructions

**Next: Complete Step 1 → Step 2**

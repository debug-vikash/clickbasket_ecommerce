package site.clickbasketecom.ClickBasket.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
                        EmailAlreadyExistsException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Conflict")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        @ExceptionHandler(RoleNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleRoleNotFound(
                        RoleNotFoundException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("Internal Server Error")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserNotFound(
                        UserNotFoundException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(VendorNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleVendorNotFound(
                        VendorNotFoundException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(VendorNotApprovedException.class)
        public ResponseEntity<ErrorResponse> handleVendorNotApproved(
                        VendorNotApprovedException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.FORBIDDEN.value())
                                .error("Forbidden")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        @ExceptionHandler(CategoryNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleCategoryNotFound(
                        CategoryNotFoundException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(
                        IllegalArgumentException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Bad Request")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<ErrorResponse> handleIllegalState(
                        IllegalStateException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Bad Request")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleProductNotFound(
                        ProductNotFoundException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(OrderNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleOrderNotFound(
                        OrderNotFoundException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(PaymentNotFoundException.class)
        public ResponseEntity<ErrorResponse> handlePaymentNotFound(
                        PaymentNotFoundException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDenied(
                        org.springframework.security.access.AccessDeniedException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.FORBIDDEN.value())
                                .error("Forbidden")
                                .message("Access denied")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentials(
                        BadCredentialsException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Unauthorized")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthentication(
                        AuthenticationException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Unauthorized")
                                .message("Authentication failed")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                Map<String, String> validationErrors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        validationErrors.put(fieldName, errorMessage);
                });

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Bad Request")
                                .message("Validation failed")
                                .path(request.getRequestURI())
                                .validationErrors(validationErrors)
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("Internal Server Error")
                                .message("An unexpected error occurred")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
}

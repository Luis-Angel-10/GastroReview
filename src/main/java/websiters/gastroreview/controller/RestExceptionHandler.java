package websiters.gastroreview.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.HibernateException;
import org.hibernate.LazyInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.SignatureException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler implements ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ModelAttribute
    public void logRequest(HttpServletRequest req) {
        log.debug("➡️  Incoming Request: {} {}", req.getMethod(), req.getRequestURI());
    }

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (request instanceof ServletServerHttpRequest req &&
                response instanceof ServletServerHttpResponse res) {

            var method = req.getServletRequest().getMethod();
            var uri = req.getServletRequest().getRequestURI();
            var status = res.getServletResponse().getStatus();

            log.debug("✅ Completed [{} {}] with status {} | bodyType=[{}]",
                    method, uri, status, body != null ? body.getClass().getSimpleName() : "null");
        }

        return body;
    }

    private Map<String, Object> base(HttpStatus status, String message, HttpServletRequest req) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (req != null) body.put("path", req.getRequestURI());
        return body;
    }

    private ResponseEntity<Map<String, Object>> respond(HttpStatus status, String message, HttpServletRequest req) {
        log.debug("⚠️  Responding with status {} {}", status.value(), status.getReasonPhrase());
        return ResponseEntity.status(status).body(base(status, message, req));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> badJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        log.debug("Bad JSON", ex);
        return respond(HttpStatus.BAD_REQUEST, "JSON mal formado o incompatible", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> badRequest(MethodArgumentNotValidException ex, HttpServletRequest req) {
        log.debug("Bean Validation failed", ex);
        Map<String, Object> body = base(HttpStatus.BAD_REQUEST, "Validación fallida", req);
        Map<String, String> details = ex.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        e -> (e instanceof FieldError fe) ? fe.getField() : e.getObjectName(),
                        e -> e.getDefaultMessage(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        body.put("details", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> constraint(ConstraintViolationException ex, HttpServletRequest req) {
        log.debug("Constraint violation", ex);
        Map<String, Object> body = base(HttpStatus.BAD_REQUEST, "Validación fallida", req);
        Map<String, String> details = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        v -> v.getMessage(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        body.put("details", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> typeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        log.debug("Type mismatch", ex);
        Map<String, Object> body = base(HttpStatus.BAD_REQUEST, "Tipo de parámetro inválido", req);
        body.put("details", Map.of(
                "param", ex.getName(),
                "requiredType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido",
                "value", String.valueOf(ex.getValue())
        ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> missingParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        log.debug("Missing request parameter", ex);
        Map<String, Object> body = base(HttpStatus.BAD_REQUEST, "Falta un parámetro requerido", req);
        body.put("details", Map.of("param", ex.getParameterName()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler( MissingPathVariableException.class)
    public ResponseEntity<Map<String, Object>> missingPathVar(MissingPathVariableException ex, HttpServletRequest req) {
        log.debug("Missing path variable", ex);
        Map<String, Object> body = base(HttpStatus.BAD_REQUEST, "Falta un path variable requerido", req);
        body.put("details", Map.of("variable", ex.getVariableName()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(EntityNotFoundException ex, HttpServletRequest req) {
        log.debug("Entity not found", ex);
        return respond(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> noHandler(NoHandlerFoundException ex, HttpServletRequest req) {
        log.debug("No handler found", ex);
        return respond(HttpStatus.NOT_FOUND, "Recurso no encontrado", req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> methodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        log.debug("Method not allowed", ex);
        return respond(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), req);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> unsupportedMedia(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        log.debug("Unsupported media type", ex);
        return respond(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> conflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        String msg = "Conflicto con los datos (p.ej., valor duplicado)";
        Throwable cause = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause() : ex;
        String c = cause.getMessage() != null ? cause.getMessage() : "";
        if (c.contains("23505") || c.toLowerCase().contains("duplicate") || c.toLowerCase().contains("unique")) {
            msg = "Ya existe un registro con ese valor único";
        }
        log.debug("Data integrity violation: {}", c, ex);
        return respond(HttpStatus.CONFLICT, msg, req);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, Object>> duplicate(DuplicateKeyException ex, HttpServletRequest req) {
        log.debug("Duplicate key", ex);
        return respond(HttpStatus.CONFLICT, "Clave duplicada", req);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> rse(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String msg = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        log.debug("ResponseStatusException {} {}", status.value(), msg, ex);
        return ResponseEntity.status(status).body(base(status, msg, req));
    }

    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<Map<String, Object>> forbidden(HttpClientErrorException.Forbidden ex, HttpServletRequest req) {
        String msg = ex.getResponseBodyAsString();
        if (msg == null || msg.isBlank()) msg = ex.getStatusText();
        log.debug("Forbidden from downstream", ex);
        return respond(HttpStatus.FORBIDDEN, msg, req);
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<Map<String, Object>> notFoundClient(HttpClientErrorException.NotFound ex, HttpServletRequest req) {
        String msg = ex.getResponseBodyAsString();
        if (msg == null || msg.isBlank()) msg = "Recurso no encontrado";
        log.debug("NotFound from downstream", ex);
        return respond(HttpStatus.NOT_FOUND, msg, req);
    }

    @ExceptionHandler(HttpClientErrorException.Conflict.class)
    public ResponseEntity<Map<String, Object>> conflictClient(HttpClientErrorException.Conflict ex, HttpServletRequest req) {
        String msg = ex.getResponseBodyAsString();
        if (msg == null || msg.isBlank()) msg = ex.getStatusText();
        log.debug("Conflict from downstream", ex);
        return respond(HttpStatus.CONFLICT, msg, req);
    }


    @ExceptionHandler({ LazyInitializationException.class, HibernateException.class })
    public ResponseEntity<Map<String, Object>> hibernateErrors(Exception ex, HttpServletRequest req) {
        log.error("Hibernate error", ex);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> generic(Exception ex, HttpServletRequest req) {
        if (ex instanceof ResponseStatusException rse) return rse(rse, req);
        if (ex.getCause() instanceof ResponseStatusException rse2) return rse(rse2, req);

        log.error("Unhandled exception", ex);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", req);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> invalidJwt(SignatureException ex, HttpServletRequest req) {
        log.warn("JWT inválido: firma no coincide o token manipulado", ex);
        return respond(HttpStatus.UNAUTHORIZED, "Token inválido o manipulado. Inicie sesión nuevamente.", req);
    }


}

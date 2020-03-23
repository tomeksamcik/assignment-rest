package com.roche.assignment;

import com.roche.assignment.model.Error;
import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class Validator {

    @ExceptionHandler(RollbackException.class)
    public ResponseEntity<?> badRequest(RollbackException e, HttpServletRequest req) {
        if (e.getCause() instanceof ConstraintViolationException) {
            return new ResponseEntity<>(Error.builder().status(HttpStatus.BAD_REQUEST.value()).error("Bad Request")
                    .message(e.getCause().getMessage()).path(req.getServletPath()).build(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(
                Error.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).error("Rollback Exception")
                        .message(e.getMessage()).path(req.getServletPath()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
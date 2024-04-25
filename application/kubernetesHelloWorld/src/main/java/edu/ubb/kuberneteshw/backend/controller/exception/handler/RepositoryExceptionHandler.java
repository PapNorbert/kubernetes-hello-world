package edu.ubb.kuberneteshw.backend.controller.exception.handler;

import edu.ubb.kuberneteshw.backend.repository.exception.RepositoryException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RepositoryExceptionHandler {
    @ExceptionHandler(RepositoryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleFailedConstraintException(RepositoryException exception) {
        return exception.getMessage();
    }

}

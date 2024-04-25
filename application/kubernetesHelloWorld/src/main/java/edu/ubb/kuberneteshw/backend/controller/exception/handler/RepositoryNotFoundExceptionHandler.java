package edu.ubb.kuberneteshw.backend.controller.exception.handler;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class RepositoryNotFoundExceptionHandler {
    @ExceptionHandler(edu.ubb.kuberneteshw.backend.repository.exception.RepositoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleFailedConstraintException(edu.ubb.kuberneteshw.backend.repository.exception.RepositoryNotFoundException exception) {
        return exception.getMessage();
    }
}

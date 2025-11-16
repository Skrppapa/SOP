package edu.rutmiit.demo.fit_contract.exeption;

public class CodeAlreadyExistsException extends RuntimeException {
    public CodeAlreadyExistsException(String code) {
        super("Subscription with code=" + code + " already exists");
    }
}

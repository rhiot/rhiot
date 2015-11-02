package io.rhiot.utils.install.exception;

public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException() {
    }

    public PermissionDeniedException(String message) {
        super(message);
    }
}


package fhi360.it.assetverify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class EmailExistsException extends Exception {
    private static final long serialVersionUID = 1L;

    public EmailExistsException(final String message) {
        super(message);
    }
}

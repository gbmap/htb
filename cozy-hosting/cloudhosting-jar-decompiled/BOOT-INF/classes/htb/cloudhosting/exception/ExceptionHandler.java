package htb.cloudhosting.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
   @org.springframework.web.bind.annotation.ExceptionHandler({IllegalArgumentException.class})
   public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception) {
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("message", exception.getMessage());
      return new ResponseEntity(body, HttpStatus.BAD_REQUEST);
   }
}

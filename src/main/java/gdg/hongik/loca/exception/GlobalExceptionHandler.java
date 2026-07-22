package gdg.hongik.loca.exception;

import gdg.hongik.loca.dto.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 전역 예외 처리
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 장소 미존재 - 404
    @ExceptionHandler(PlaceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlaceNotFound(PlaceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    // kakaoPlaceId 중복 - 409
    @ExceptionHandler(DuplicateKakaoPlaceIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKakaoPlaceId(DuplicateKakaoPlaceIdException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    /* 태그 미존재 - 404
    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTagNotFound(TagNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 태그 이름 중복 - 409
    @ExceptionHandler(DuplicateTagNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateTagName(DuplicateTagNameException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }
    */

    // 방문 기록 미존재/삭제됨/소유자 불일치 - 404
    @ExceptionHandler(VisitRecordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVisitRecordNotFound(VisitRecordNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 추천 요청 파라미터 오류(tagIds 미선택 등) - 400
    @ExceptionHandler(InvalidRecommendationRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRecommendationRequest(InvalidRecommendationRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 요청 본문 검증 실패 - 400
    // - 첫 번째 필드 에러 메시지 사용
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("잘못된 요청입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }
}

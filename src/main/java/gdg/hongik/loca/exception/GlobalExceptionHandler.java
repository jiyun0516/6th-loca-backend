package gdg.hongik.loca.exception;

import gdg.hongik.loca.dto.common.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// 전역 예외 처리
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 장소 예외 처리

    // 장소 미존재 - 404
    @ExceptionHandler(PlaceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlaceNotFound(PlaceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 장소 kakaoPlaceId 중복 - 409
    @ExceptionHandler(DuplicateKakaoPlaceIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKakaoPlaceId(DuplicateKakaoPlaceIdException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 장소 목록 예외 처리

    // 목록 미존재 / 타인 소유 - 404
    @ExceptionHandler(PlaceListNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlaceListNotFound(PlaceListNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 목록 항목 미존재 - 404
    @ExceptionHandler(PlaceListItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlaceListItemNotFound(PlaceListItemNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 이미 담긴 장소 - 409
    @ExceptionHandler(DuplicateListItemException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateListItem(DuplicateListItemException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 태그 예외 처리

    // 태그 미존재 - 404
    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTagNotFound(TagNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 태그 생성 시 이름 중복 - 409
    @ExceptionHandler(DuplicateTagNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateTagName(DuplicateTagNameException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 리뷰에 사용 중인 태그 삭제 시도 - 409
    @ExceptionHandler(TagInUseException.class)
    public ResponseEntity<ErrorResponse> handleTagInUse(TagInUseException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 리뷰 예외 처리

    // 리뷰 검색 시 미존재/소유자 불일치 - 404
    @ExceptionHandler(VisitRecordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVisitRecordNotFound(VisitRecordNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 추천 예외 처리

    // 추천 요청 파라미터 오류(tagIds 미선택 등) - 400
    @ExceptionHandler(InvalidRecommendationRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRecommendationRequest(InvalidRecommendationRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    // ForYou 잠금 상태 - 403
    @ExceptionHandler(ForYouLockedException.class)
    public ResponseEntity<ErrorResponse> handleForYouLocked(
            ForYouLockedException e
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 요청 예외 처리

    // 요청 본문 파싱(JSON -> DTO) 실패 - 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("요청 본문의 형식이 올바르지 않습니다."));
    }

    // URL 쿼리 파라미터 누락 - 400
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException e) {
        String message = e.getParameterName() + " 파라미터는 필수입니다.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    // URL 경로/쿼리 파라미터 타입 변환 실패 - 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = e.getName() + " 파라미터의 형식이 올바르지 않습니다.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    // 요청 본문 검증 실패 - 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("잘못된 요청입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    // 폴백 예외 처리

    // 데이터 무결성 위반 - 400
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("요청 데이터가 제약 조건을 위반했습니다."));
    }

    // 회원가입, 로그인 예외 응답 처리

    // 이메일 중복 - 409
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(
            DuplicateEmailException e
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 로그인 인증 실패 - 401
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException e
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
    }
}
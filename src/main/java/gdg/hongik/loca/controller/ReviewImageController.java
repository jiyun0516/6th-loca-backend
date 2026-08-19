package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.image.ImageUploadResponse;
import gdg.hongik.loca.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/me/review-images")
@RequiredArgsConstructor
public class ReviewImageController {

    private final ImageStorageService imageStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageUploadResponse upload(
            @AuthenticationPrincipal Integer userId,
            @RequestPart("file") MultipartFile file
    ) {
        return imageStorageService.upload(
                file,
                "reviews/" + userId
        );
    }
}
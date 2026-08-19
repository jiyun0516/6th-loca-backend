package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.image.ImageUploadResponse;
import gdg.hongik.loca.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/public-place-images")
@RequiredArgsConstructor
public class AdminPublicPlaceImageController {

    private final ImageStorageService imageStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageUploadResponse upload(
            @RequestPart("file") MultipartFile file
    ) {
        return imageStorageService.upload(
                file,
                "public-places"
        );
    }
}
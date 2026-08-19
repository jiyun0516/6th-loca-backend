package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.image.ImageUploadResponse;
import gdg.hongik.loca.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    private static final Map<String, String> EXTENSIONS = Map.of(
            MediaType.IMAGE_JPEG_VALUE, ".jpg",
            MediaType.IMAGE_PNG_VALUE, ".png",
            "image/webp", ".webp"
    );

    private final RestClient supabaseStorageRestClient;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.storage.bucket}")
    private String bucket;

    public ImageUploadResponse upload(
            MultipartFile file,
            String directory
    ) {
        validate(file, directory);

        String contentType = file.getContentType();
        String extension = EXTENSIONS.get(contentType);
        String objectPath = directory + "/"
                + UUID.randomUUID() + extension;

        try {
            URI uploadUri = buildUploadUri(objectPath);

            supabaseStorageRestClient.post()
                    .uri(uploadUri)
                    .contentType(MediaType.parseMediaType(contentType))
                    .header("x-upsert", "false")
                    .body(file.getBytes())
                    .retrieve()
                    .toBodilessEntity();

            return new ImageUploadResponse(buildPublicUrl(objectPath));
        } catch (Exception e) {
            throw new ImageUploadException(
                    "이미지 업로드에 실패했습니다.",
                    e
            );
        }
    }

    private void validate(
            MultipartFile file,
            String directory
    ) {
        if (file == null || file.isEmpty()) {
            throw new ImageUploadException(
                    "업로드할 이미지 파일이 필요합니다."
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ImageUploadException(
                    "이미지 파일은 최대 5MB까지 업로드할 수 있습니다."
            );
        }

        if (!EXTENSIONS.containsKey(file.getContentType())) {
            throw new ImageUploadException(
                    "JPEG, PNG, WebP 형식만 업로드할 수 있습니다."
            );
        }

        if (!StringUtils.hasText(directory)) {
            throw new ImageUploadException(
                    "이미지 저장 경로가 필요합니다."
            );
        }
    }

    private URI buildUploadUri(String objectPath) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath("/storage/v1/object")
                .pathSegment(bucket);

        for (String segment : objectPath.split("/")) {
            builder.pathSegment(segment);
        }

        return builder.build()
                .encode()
                .toUri();
    }

    private String buildPublicUrl(String objectPath) {
        String baseUrl = supabaseUrl.endsWith("/")
                ? supabaseUrl.substring(0, supabaseUrl.length() - 1)
                : supabaseUrl;

        return baseUrl
                + "/storage/v1/object/public/"
                + bucket
                + "/"
                + objectPath;
    }

    public void deleteByUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String publicUrlPrefix = buildPublicUrl("");

        // 우리 Storage 파일이 아닌 외부 URL은 삭제하지 않음
        if (!imageUrl.startsWith(publicUrlPrefix)) {
            return;
        }

        String objectPath = imageUrl.substring(publicUrlPrefix.length());

        try {
            supabaseStorageRestClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/storage/v1/object/")
                            .path(bucket)
                            .path("/")
                            .path(objectPath)
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new ImageUploadException("이미지 파일 삭제에 실패했습니다.");
        }
    }
}

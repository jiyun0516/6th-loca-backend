package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.tag.TagCreateRequest;
import gdg.hongik.loca.dto.tag.TagResponse;
import gdg.hongik.loca.entity.Tag;
import gdg.hongik.loca.exception.DuplicateTagNameException;
import gdg.hongik.loca.exception.TagNotFoundException;
import gdg.hongik.loca.repository.TagRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

// 태그 도메인 서비스 계층
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    // 전체 태그 목록 조회
    public List<TagResponse> getTags() {
        return tagRepository.findAll().stream()
                .map(TagResponse::from)
                .toList();
    }

    /**
     * 태그를 생성한다.
     * name이 이미 존재하면 {@link DuplicateTagNameException}을 던진다.
     */
    @Transactional
    public TagResponse createTag(@Valid TagCreateRequest request) {
        tagRepository.findByName(request.name())
                .ifPresent(existing -> {
                    throw new DuplicateTagNameException(request.name());
                });

        Tag tag = Tag.builder()
                .name(request.name())
                .build();

        return TagResponse.from(tagRepository.save(tag));
    }

    /**
     * 태그를 삭제한다(하드 삭제).
     * 없으면 {@link TagNotFoundException}을 던진다.
     */
    @Transactional
    public void deleteTag(Integer tagId) {
        Tag tag = findById(tagId);
        tagRepository.delete(tag);
    }

    private Tag findById(Integer tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
    }
}

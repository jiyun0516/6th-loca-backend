package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.tag.TagCreateRequest;
import gdg.hongik.loca.dto.tag.TagResponse;
import gdg.hongik.loca.entity.Tag;
import gdg.hongik.loca.exception.DuplicateTagNameException;
import gdg.hongik.loca.exception.TagInUseException;
import gdg.hongik.loca.exception.TagNotFoundException;
import gdg.hongik.loca.repository.TagRepository;
import gdg.hongik.loca.repository.VisitTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 태그 도메인 서비스 계층
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;
    private final VisitTagRepository visitTagRepository;

    // 전체 태그 목록 조회
    public List<TagResponse> getTags() {
        return tagRepository.findAll().stream()
                .map(TagResponse::from)
                .toList();
    }

    // 태그를 생성한다.
    // 같은 이름이 존재하면 DuplicateTagNameException 발생
    @Transactional
    public TagResponse createTag(TagCreateRequest request) {
        tagRepository.findByName(request.name())
                .ifPresent(existing -> {
                    throw new DuplicateTagNameException(request.name());
                });

        Tag tag = Tag.builder()
                .name(request.name())
                .build();

        return TagResponse.from(tagRepository.save(tag));
    }

    // 태그 삭제(하드 삭제)
    // 없으면 TagNotFoundException 발생
    // 리뷰에 사용 중이면 TagInUseException 발생
    @Transactional
    public void deleteTag(Integer tagId) {
        Tag tag = findById(tagId);

        if (visitTagRepository.existsByTagId(tagId)) {
            throw new TagInUseException(tagId);
        }

        tagRepository.delete(tag);
    }

    private Tag findById(Integer tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
    }
}

package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.tag.TagResponse;
import gdg.hongik.loca.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 태그 도메인 서비스 계층
// - 조회 전용
@Service
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
}

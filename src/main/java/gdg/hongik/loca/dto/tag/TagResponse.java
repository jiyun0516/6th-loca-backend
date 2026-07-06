package gdg.hongik.loca.dto.tag;

import gdg.hongik.loca.entity.Tag;

// 태그 응답 DTO
public record TagResponse(
        Integer tagId,
        String name
) {

    // 엔티티 -> 응답 변환
    public static TagResponse from(Tag tag) {
        return new TagResponse(tag.getTagId(), tag.getName());
    }
}

package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.user.IconIdResponse;
import gdg.hongik.loca.dto.user.IconIdUpdateRequest;
import gdg.hongik.loca.entity.User;
import gdg.hongik.loca.exception.UserNotFoundException;
import gdg.hongik.loca.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public IconIdResponse getIconId(Integer userId) {
        User user = findActiveUser(userId);
        return new IconIdResponse(user.getIconId());
    }

    @Transactional
    public IconIdResponse updateIconId(
            Integer userId,
            IconIdUpdateRequest request
    ) {
        User user = findActiveUser(userId);
        user.setIconId(request.iconId());

        return new IconIdResponse(user.getIconId());
    }

    private User findActiveUser(Integer userId) {
        return userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.user.IconIdResponse;
import gdg.hongik.loca.dto.user.IconIdUpdateRequest;
import gdg.hongik.loca.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/icon")
    public IconIdResponse getIconId(
            @AuthenticationPrincipal Integer userId
    ) {
        return userService.getIconId(userId);
    }

    @PutMapping("/icon")
    public IconIdResponse updateIconId(
            @AuthenticationPrincipal Integer userId,
            @Valid @RequestBody IconIdUpdateRequest request
    ) {
        return userService.updateIconId(userId, request);
    }
}
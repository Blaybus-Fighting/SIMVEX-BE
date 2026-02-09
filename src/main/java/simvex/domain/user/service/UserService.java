package simvex.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import simvex.domain.user.entity.User;
import simvex.domain.user.repository.UserRepository;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}

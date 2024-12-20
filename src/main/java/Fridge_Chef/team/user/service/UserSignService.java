package Fridge_Chef.team.user.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.fridge.domain.Fridge;
import Fridge_Chef.team.fridge.repository.FridgeRepository;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.repository.ImageRepository;
import Fridge_Chef.team.security.service.dto.OAuthAttributes;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.Social;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserHistory;
import Fridge_Chef.team.user.repository.UserHistoryRepository;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSignService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final FridgeRepository fridgeRepository;

    @Transactional
    public User saveOrUpdate(OAuthAttributes attributes) {
        log.info(attributes.registrationId() + attributes.email() + " : 로그인 시도");
        Social loginType = Social.valueOf(attributes.registrationId().toUpperCase());

        User user = userRepository.findByProfileEmailAndProfileSocial(attributes.email(), loginType)
                .orElseGet(() -> registerNewUser(attributes, loginType));

        withdrawalAccountRecovery(user);

        return user;
    }

    private User signup(OAuthAttributes attributes) {
        log.info(attributes.registrationId() + attributes.email() + " : 회원가입 시도");
        Social social = Social.signupOf(attributes.registrationId().toUpperCase());
        User user = User.createSocialUser(
                attributes.email(),
                attributes.name(),
                Role.USER,
                social);

        user.updatePicture(imageRepository.save(Image.outUri(attributes.picture())));

        try {
            User signup = userRepository.save(user);
            fridgeRepository.save(Fridge.setup(signup));
            return signup;
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.SIGNUP_USER_FAIL_SNS_EMAIL_UNIQUE);
        }
    }

    private User registerNewUser(OAuthAttributes attributes, Social social) {
        if (userRepository.existsByProfileEmailAndProfileSocial(attributes.email(), social)) {
            throw new ApiException(ErrorCode.SIGNUP_USER_FAIL_SNS_EMAIL_UNIQUE);
        }
        User user = signup(attributes);
        userHistoryRepository.save(new UserHistory(user));
        return user;
    }

    private void withdrawalAccountRecovery(User user) {
        if (user.isDeleteStatus()) {
            log.info("휴먼 계정 복구 : "+user.getEmail());
            user.accountDelete(false);
        }
    }
}

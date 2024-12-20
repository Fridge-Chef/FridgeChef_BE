package Fridge_Chef.team.security.service.factory;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OAuthAttributesAdapterFactory {
    private final List<OAuthAttributesAdapter> oAuthAttributesAdapters;

    public OAuthAttributesAdapter factory(String registrationId) {
        return oAuthAttributesAdapters.stream()
                .filter(oAuthAttributesAdapter -> oAuthAttributesAdapter.supports(registrationId))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.SIGNUP_SNS_NOT_SUPPORT));
    }
}

package Fridge_Chef.team.security.service.factory;

import Fridge_Chef.team.security.service.factory.adapter.NotSupportedOAuthVendorException;
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
                .orElseThrow(() -> new NotSupportedOAuthVendorException("해당 OAuth2 벤더는 지원되지 않습니다."));
    }
}

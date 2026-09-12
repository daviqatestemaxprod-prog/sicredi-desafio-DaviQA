package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginResponse(
        Integer id,
        String username,
        String email,
        String firstName,
        String lastName,
        String gender,
        String image,
        String token,
        String accessToken,
        String refreshToken
) {
}

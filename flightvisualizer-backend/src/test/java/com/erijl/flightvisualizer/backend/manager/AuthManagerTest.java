package com.erijl.flightvisualizer.backend.manager;

import com.erijl.flightvisualizer.backend.dto.AccessToken;
import com.erijl.flightvisualizer.backend.util.RestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthManagerTest {

    @Mock
    private RestUtil restUtil;

    @InjectMocks
    private AuthManager authManager;

    @Test
    public void testGetBearerAccessToken() {
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken("testToken");
        accessToken.setExpiresIn(3600);

        ResponseEntity<String> responseEntity = new ResponseEntity<>(
                "{\"access_token\":\"testToken\",\"expires_in\":26000}",
                HttpStatus.OK);

        when(restUtil.exchangeRequest(any(String.class), eq(HttpMethod.POST), any(), any(MultiValueMap.class)))
                .thenReturn(responseEntity);

        String bearerToken = authManager.getBearerAccessToken();

        assertEquals("Bearer testToken", bearerToken);

        bearerToken = authManager.getBearerAccessToken();

        assertEquals("Bearer testToken", bearerToken);

        verify(restUtil, times(1))
                .exchangeRequest(any(String.class), eq(HttpMethod.POST), any(), any(MultiValueMap.class));
    }
}

package com.greenhouse.auth.client.fallback;

import com.greenhouse.auth.client.feign.UserClient;
import com.greenhouse.auth.exception.ErrorCodes;
import com.greenhouse.auth.exception.RemoteException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author keets
 * @date 2017/9/25
 */
@Component
public class UserFallbackClient implements UserClient {

    @Override
    public Map checkUsernameAndPassword(Map userMap) {
        throw new RemoteException(ErrorCodes.USER_SERVICE_CALL_FAILURE);
    }

    @Override
    public Map getUserInformation(Map userCheck) {
        throw new RemoteException(ErrorCodes.USER_SERVICE_CALL_FAILURE);
    }

}

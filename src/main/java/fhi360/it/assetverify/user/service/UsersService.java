
package fhi360.it.assetverify.user.service;

import fhi360.it.assetverify.user.dto.UserLoginDto;
import fhi360.it.assetverify.exception.ResourceNotFoundException;
import fhi360.it.assetverify.user.model.Users;
import fhi360.it.assetverify.response.ApiResponse;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public interface UsersService {
    ApiResponse login(final UserLoginDto loginDto) throws ResourceNotFoundException;
    boolean isUserAlreadyPresent(final Users users);
    Users save(final Users users);
}

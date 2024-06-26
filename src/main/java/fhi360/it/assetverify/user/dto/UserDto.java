
package fhi360.it.assetverify.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private long id;
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @NotBlank
    private String userType;
    private String project;
    @NotBlank
    private String email;
    private String password;
}

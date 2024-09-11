package Fridge_Chef.team.common.auth;

import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.UserId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsCustom implements UserDetails {

    private final String userId;
    private final String email;
    private final String username;
    private final List<Role> role = new ArrayList<>();

    public UserDetailsCustom(String userId,String email,String username, Role role) {
        this.userId =userId;
        this.email =email;
        this.username =username;
        this.role.add(role);
    }
    public UserDetailsCustom(String userId, String email,String username, List<Role> role) {

        this.email =email;
        this.username =username;
        this.userId =userId;
        this.role.addAll(role);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}

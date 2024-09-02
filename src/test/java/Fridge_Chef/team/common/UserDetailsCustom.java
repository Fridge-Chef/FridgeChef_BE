package Fridge_Chef.team.common;

import Fridge_Chef.team.user.domain.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsCustom implements UserDetails {

    private final String name;
    private final String password;
    private final List<Role> role = new ArrayList<>();

    public UserDetailsCustom(String username, String password, Role role) {
        this.name=username;
        this.password=password;
        this.role.add(role);
    }
    public UserDetailsCustom(String username, String password, List<Role> role) {
        this.name=username;
        this.password=password;
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

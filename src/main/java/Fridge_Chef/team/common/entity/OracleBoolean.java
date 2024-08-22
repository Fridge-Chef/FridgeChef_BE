package Fridge_Chef.team.common.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum OracleBoolean {
    T(true),
    F(false);
    private final boolean bool;

    public static OracleBoolean of(boolean bool){
        if(bool) return T;
        return F;
    }

    public int jdbcInsert(){
        return this == T ? 1 : 0;
    }
}

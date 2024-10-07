package Fridge_Chef.team.common.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseEntity {

    @Column(name = "create_time")
    @CreatedDate
    private LocalDateTime createTime;
    @Column(name = "update_time")
    @LastModifiedDate
    private LocalDateTime updateTime;
    private OracleBoolean isDelete;

    protected void updateIsDelete(boolean isDelete){
        this.isDelete = OracleBoolean.of(isDelete);
    }
}
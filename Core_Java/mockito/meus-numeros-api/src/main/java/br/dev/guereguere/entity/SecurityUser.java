package br.dev.guereguere.entity;

import br.dev.guereguere.entity.enums.SecurityUserCredentialEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "sw000core_vw_security_user")
@Data
@NoArgsConstructor
public class SecurityUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="user_id")
    private Integer id;

    @Column(unique = true,name="user_mail")
    private String email;

    @JsonIgnore
    @Column(name="user_password")
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "sw000core_vw_security_user_credential"
                        ,joinColumns=@JoinColumn(name = "user_id", referencedColumnName = "user_id"))
    @Column(name="user_credential")
    private Set<Integer> credentials = new HashSet<>();

    @Column(name="user_enabled")
    private boolean enabled;

    @Column(name="user_account_non_expired")
    private boolean accountNonExpired;

    @Column(name="user_account_non_locked")
    private boolean accountNonLocked;

    @Column(name="user_credentials_non_expired")
    private boolean credentialsNonExpired;

    public <E> SecurityUser(String username, String password, ArrayList<E> es) {
    }


    public Set<SecurityUserCredentialEnum> getCredentials() {
        return credentials.stream().map(x -> SecurityUserCredentialEnum.toEnum(x)).collect(Collectors.toSet());
    }

}
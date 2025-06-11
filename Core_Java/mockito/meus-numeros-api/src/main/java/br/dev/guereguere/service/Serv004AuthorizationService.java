package br.dev.guereguere.service;

import br.dev.guereguere.dto.SecurityUserAuthorizationDTO;


public interface Serv004AuthorizationService {

    SecurityUserAuthorizationDTO getAuthorization(String token);

}

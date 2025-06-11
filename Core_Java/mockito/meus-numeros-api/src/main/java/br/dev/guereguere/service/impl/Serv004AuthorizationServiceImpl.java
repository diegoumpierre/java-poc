package br.dev.guereguere.service.impl;

import br.dev.guereguere.service.Serv004AuthorizationService;
import br.dev.guereguere.dto.SecurityUserAuthorizationDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class Serv004AuthorizationServiceImpl implements Serv004AuthorizationService {

//    @Autowired
//    Serv004AuthorizationClient serv004AuthorizationClient;

    @Override
    public SecurityUserAuthorizationDTO getAuthorization(String token) {

//        return serv004AuthorizationClient.getAuthorization(token);
    return null;
    }
}

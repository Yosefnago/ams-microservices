package com.ams.ui.api.clientside;

import com.ams.dtos.clientDto.*;

public interface ClientHttpService {

    LoadNumOfClientsResponse loadNumOfClients(String token);
    GrantAccessResponse grantAccess(GrantAccessRequestDto request, String token);
    LoadClientDetailsCaseResponse loadClientDetails(String token, String clientId);
}

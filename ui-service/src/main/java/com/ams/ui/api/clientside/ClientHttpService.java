package com.ams.ui.api.clientside;

import com.ams.dtos.clientDto.*;

public interface ClientHttpService {


    LoadClientDetailsCaseResponse loadClientDetails(String token, String clientId);
}

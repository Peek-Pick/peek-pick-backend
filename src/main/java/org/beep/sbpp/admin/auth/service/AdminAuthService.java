package org.beep.sbpp.admin.auth.service;

import org.beep.sbpp.admin.auth.entities.AdminEntity;

public interface AdminAuthService {

    AdminEntity authenticate(String accountId, String password);
}

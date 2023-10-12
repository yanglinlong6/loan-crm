package com.help.loan.distribute.service.session;

import com.loan.cps.entity.Session;

import java.util.List;

public interface SessionService {

    Session getSession(String userId);

    Session initSession(String userId, String domain2);

    Session setUpstreamTime(Session session);

    Session getSessionForProcess(String userId, String domain2);

    void saveSession(Session session);

    void delSession(String userId);

    List<Session> getSessionByDate(Integer dateNum);
}

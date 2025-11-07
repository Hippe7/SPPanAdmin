package net.sppan.base.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sppan.base.entity.Role;
import net.sppan.base.entity.User;
import net.sppan.base.service.ISessionService;

/**
 * 会话管理服务实现类
 * 
 * @author SPPan
 *
 */
@Service
public class SessionServiceImpl implements ISessionService {

    @Autowired
    private SessionDAO sessionDAO;

    @Override
    public List<Session> getSessionsByUsername(String username) {
        List<Session> result = new ArrayList<>();
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        for (Session session : sessions) {
            Object principal = session.getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY");
            if (principal instanceof User) {
                User user = (User) principal;
                if (username.equals(user.getUserName())) {
                    result.add(session);
                }
            }
        }
        return result;
    }

    @Override
    public List<Session> getSessionsByUserId(Integer userId) {
        List<Session> result = new ArrayList<>();
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        for (Session session : sessions) {
            Object principal = session.getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY");
            if (principal instanceof User) {
                User user = (User) principal;
                if (userId.equals(user.getId())) {
                    result.add(session);
                }
            }
        }
        return result;
    }

    @Override
    public void invalidateSession(Session session) {
        if (session != null) {
            sessionDAO.delete(session);
        }
    }

    @Override
    public void invalidateSession(String sessionId) {
        Session session = sessionDAO.readSession(sessionId);
        if (session != null) {
            invalidateSession(session);
        }
    }

    @Override
    public void invalidateSessionsByUsername(String username) {
        List<Session> sessions = getSessionsByUsername(username);
        for (Session session : sessions) {
            invalidateSession(session);
        }
    }

    @Override
    public void invalidateSessionsByUserId(Integer userId) {
        List<Session> sessions = getSessionsByUserId(userId);
        for (Session session : sessions) {
            invalidateSession(session);
        }
    }

    @Override
    public List<Session> getAllSessions() {
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        return new ArrayList<>(sessions);
    }

    @Override
    public List<Session> getSessionsByRoleId(Integer roleId) {
        List<Session> result = new ArrayList<>();
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        for (Session session : sessions) {
            Object principal = session.getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY");
            if (principal instanceof User) {
                User user = (User) principal;
                // 检查用户是否拥有该角色
                if (user.getRoles() != null) {
                    for (Role role : user.getRoles()) {
                        if (roleId.equals(role.getId())) {
                            result.add(session);
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void invalidateSessionsByRoleId(Integer roleId) {
        List<Session> sessions = getSessionsByRoleId(roleId);
        for (Session session : sessions) {
            invalidateSession(session);
        }
    }
}
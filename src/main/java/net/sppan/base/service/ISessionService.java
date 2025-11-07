package net.sppan.base.service;

import java.util.List;

import org.apache.shiro.session.Session;

/**
 * 会话管理服务接口
 * 
 * @author SPPan
 *
 */
public interface ISessionService {

    /**
     * 根据用户名获取所有会话
     * @param username
     * @return
     */
    List<Session> getSessionsByUsername(String username);
    
    /**
     * 根据用户ID获取所有会话
     * @param userId
     * @return
     */
    List<Session> getSessionsByUserId(Integer userId);
    
    /**
     * 失效指定会话
     * @param session
     */
    void invalidateSession(Session session);
    
    /**
     * 根据会话ID失效会话
     * @param sessionId
     */
    void invalidateSession(String sessionId);
    
    /**
     * 失效指定用户的所有会话
     * @param username
     */
    void invalidateSessionsByUsername(String username);
    
    /**
     * 失效指定用户的所有会话
     * @param userId
     */
    void invalidateSessionsByUserId(Integer userId);
    
    /**
     * 获取所有在线会话
     * @return
     */
    List<Session> getAllSessions();
    
    /**
     * 根据角色ID获取所有会话
     * @param roleId
     * @return
     */
    List<Session> getSessionsByRoleId(Integer roleId);
    
    /**
     * 失效指定角色的所有用户会话
     * @param roleId
     */
    void invalidateSessionsByRoleId(Integer roleId);
}
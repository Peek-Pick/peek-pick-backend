package org.beep.sbpp.chatbot.service;

import jakarta.servlet.http.HttpSession;

public interface ChatbotService {

    String handleUserQuery(String userQuery, HttpSession session);

}

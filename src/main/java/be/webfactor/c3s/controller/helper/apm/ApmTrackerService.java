package be.webfactor.c3s.controller.helper.apm;

import jakarta.servlet.http.HttpServletRequest;
import org.glowroot.agent.api.Glowroot;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

@Service
public class ApmTrackerService {

    public void setTransactionName(HttpServletRequest request) {
        Glowroot.setTransactionName((String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
    }
}

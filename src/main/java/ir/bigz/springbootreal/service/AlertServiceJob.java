package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.viewmodel.UserModelRequest;
import ir.bigz.springbootreal.viewmodel.UserModelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AlertServiceJob implements Runnable {

    private final static Logger LOG = LoggerFactory.getLogger(AlertServiceJob.class);
    private final UserService userService;

    @Autowired
    public AlertServiceJob(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run() {
        List<UserModelResponse> all = userService.getAll();
        LOG.info("Job call getAll service method result: count {}",
                all.size());
    }
}

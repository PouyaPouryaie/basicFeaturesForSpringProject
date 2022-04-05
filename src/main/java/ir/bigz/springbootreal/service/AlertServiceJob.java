package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.viewmodel.UserModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AlertServiceJob implements Runnable {

    private final UserService userService;

    @Autowired
    public AlertServiceJob(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run() {
        List<UserModel> all = userService.getAll();
        System.out.println("count of data is " + all.size());
    }
}
